package com.aquasafe.ml;

import com.aquasafe.config.FilePathResolver;
import com.aquasafe.dto.ModelEvaluationDTO;
import com.aquasafe.dto.PredictionRequestDTO;
import com.aquasafe.dto.PredictionResponseDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import weka.classifiers.Evaluation;
import weka.classifiers.trees.RandomForest;
import weka.core.*;
import weka.core.converters.ConverterUtils.DataSource;

import jakarta.annotation.PostConstruct;
import java.io.File;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class WekaModelService {

    private static final Logger log = LoggerFactory.getLogger(WekaModelService.class);

    @Value("${aquasafe.ml.model-file:ml/models/random_forest_water_risk.model}")
    private String modelFilePath;

    @Value("${aquasafe.ml.training-file:ml/training/training_dataset.arff}")
    private String trainingFilePath;

    private RandomForest classifier;
    private Instances dataStructure;
    private ModelEvaluationDTO latestEvaluation;
    private boolean modelLoaded = false;

    private final List<String> districtList = Arrays.asList(
        "Chennai", "Madurai", "Coimbatore", "Tiruchirappalli", "Salem",
        "Tirunelveli", "Thanjavur", "Vellore", "Erode", "Kanyakumari"
    );

    @PostConstruct
    public void init() {
        try {
            File modelFile = FilePathResolver.resolve(modelFilePath);
            if (modelFile != null && modelFile.exists()) {
                log.info("Loading existing Weka Random Forest model from {}", modelFile.getAbsolutePath());
                loadModel(modelFile.getAbsolutePath());
            } else {
                File trainingFile = FilePathResolver.resolve(trainingFilePath);
                if (trainingFile != null && trainingFile.exists()) {
                    log.info("Training file found at {}. Training initial Random Forest model...", trainingFile.getAbsolutePath());
                    trainModelFromFile(trainingFile.getAbsolutePath());
                } else {
                    log.warn("Neither model nor training dataset found. Model will be trained upon dataset import.");
                }
            }
        } catch (Exception e) {
            log.error("Failed to initialize ML model on startup: {}", e.getMessage());
        }
    }

    public synchronized ModelEvaluationDTO trainModel() throws Exception {
        File trainingFile = FilePathResolver.resolve(trainingFilePath);
        if (trainingFile == null || !trainingFile.exists()) {
            throw new IllegalStateException("Training dataset ARFF file not found at " + trainingFilePath);
        }
        return trainModelFromFile(trainingFile.getAbsolutePath());
    }

    public synchronized ModelEvaluationDTO trainModelFromFile(String resolvedPath) throws Exception {
        log.info("Starting Weka Random Forest training from {}", resolvedPath);

        DataSource source = new DataSource(resolvedPath);
        Instances data = source.getDataSet();
        if (data.classIndex() == -1) {
            data.setClassIndex(data.numAttributes() - 1);
        }

        this.dataStructure = new Instances(data, 0); // copy empty header

        // Configure Weka Random Forest
        RandomForest rf = new RandomForest();
        rf.setNumIterations(100);
        rf.setSeed(42);
        rf.buildClassifier(data);

        // Perform 10-fold Cross-Validation for authentic metrics
        int folds = Math.min(10, Math.max(2, data.numInstances() / 10));
        Evaluation eval = new Evaluation(data);
        eval.crossValidateModel(rf, data, folds, new Random(42));

        // Save serialized model to designated target location
        File modelFile = FilePathResolver.resolve(modelFilePath);
        if (modelFile == null || !modelFile.exists()) {
            modelFile = new File(modelFilePath);
        }
        modelFile.getParentFile().mkdirs();
        SerializationHelper.write(modelFile.getAbsolutePath(), rf);
        this.classifier = rf;
        this.modelLoaded = true;

        // Extract authentic evaluation metrics
        ModelEvaluationDTO dto = new ModelEvaluationDTO();
        dto.setTrained(true);
        dto.setModelName("Weka Random Forest");
        dto.setModelVersion("RandomForest-v1.0");
        dto.setNumberOfTrees(100);
        dto.setTrainedDate(LocalDateTime.now());
        dto.setTotalInstances(data.numInstances());

        dto.setAccuracy(round(eval.pctCorrect()));
        dto.setPrecision(round(eval.weightedPrecision() * 100.0));
        dto.setRecall(round(eval.weightedRecall() * 100.0));
        dto.setF1Score(round(eval.weightedFMeasure() * 100.0));

        // Per class metrics
        Map<String, Map<String, Double>> classMetrics = new LinkedHashMap<>();
        List<String> classNames = new ArrayList<>();
        for (int i = 0; i < data.numClasses(); i++) {
            String className = data.classAttribute().value(i);
            classNames.add(className);
            Map<String, Double> m = new LinkedHashMap<>();
            m.put("precision", round(eval.precision(i) * 100.0));
            m.put("recall", round(eval.recall(i) * 100.0));
            m.put("f1Score", round(eval.fMeasure(i) * 100.0));
            classMetrics.put(className, m);
        }
        dto.setClassMetrics(classMetrics);
        dto.setClassNames(classNames);

        // Confusion Matrix
        double[][] matrix = eval.confusionMatrix();
        List<List<Integer>> confList = new ArrayList<>();
        for (int r = 0; r < matrix.length; r++) {
            List<Integer> row = new ArrayList<>();
            for (int c = 0; c < matrix[r].length; c++) {
                row.add((int) matrix[r][c]);
            }
            confList.add(row);
        }
        dto.setConfusionMatrix(confList);
        dto.setCrossValidationSummary(eval.toSummaryString("=== " + folds + "-Fold Cross-Validation ===", false));
        dto.setDatasetStatus("Evaluated on " + data.numInstances() + " demonstration records across 10 Tamil Nadu districts");
        dto.setNotes("Random Forest with 100 decision trees. Deterministic risk threshold ground truth applied.");

        this.latestEvaluation = dto;
        log.info("Model training successful. Actual 10-fold CV Accuracy: {}%", dto.getAccuracy());
        return dto;
    }

    public synchronized void loadModel() throws Exception {
        File file = FilePathResolver.resolve(modelFilePath);
        if (file == null || !file.exists()) {
            throw new IllegalStateException("Model file not found at " + modelFilePath);
        }
        loadModel(file.getAbsolutePath());
    }

    public synchronized void loadModel(String path) throws Exception {
        this.classifier = (RandomForest) SerializationHelper.read(path);

        // Load structure from ARFF
        File trainingFile = FilePathResolver.resolve(trainingFilePath);
        if (trainingFile != null && trainingFile.exists()) {
            DataSource source = new DataSource(trainingFile.getAbsolutePath());
            Instances data = source.getDataSet();
            if (data.classIndex() == -1) data.setClassIndex(data.numAttributes() - 1);
            this.dataStructure = new Instances(data, 0);
        } else {
            this.dataStructure = buildFallbackInstancesHeader();
        }
        this.modelLoaded = true;
    }

    public PredictionResponseDTO predict(PredictionRequestDTO req) throws Exception {
        if (!modelLoaded || classifier == null) {
            File modelFile = FilePathResolver.resolve(modelFilePath);
            if (modelFile != null && modelFile.exists()) {
                loadModel(modelFile.getAbsolutePath());
            } else {
                File trainingFile = FilePathResolver.resolve(trainingFilePath);
                if (trainingFile != null && trainingFile.exists()) {
                    trainModelFromFile(trainingFile.getAbsolutePath());
                } else {
                    throw new IllegalStateException("Model has not been trained yet. Please train the model via the Data/Model Management module.");
                }
            }
        }

        if (dataStructure == null) {
            dataStructure = buildFallbackInstancesHeader();
        }

        // Build Weka Instance
        DenseInstance instance = new DenseInstance(dataStructure.numAttributes());
        instance.setDataset(dataStructure);

        // Set district attribute
        Attribute districtAttr = dataStructure.attribute("district");
        if (districtAttr != null) {
            int dIndex = districtAttr.indexOfValue(req.getDistrict());
            if (dIndex >= 0) {
                instance.setValue(districtAttr, dIndex);
            } else {
                instance.setValue(districtAttr, 0); // fallback to first class
            }
        }

        instance.setValue(dataStructure.attribute("ph"), req.getPh());
        instance.setValue(dataStructure.attribute("turbidity"), req.getTurbidity());
        instance.setValue(dataStructure.attribute("tds"), req.getTds());
        instance.setValue(dataStructure.attribute("dissolved_oxygen"), req.getDissolvedOxygen());
        instance.setValue(dataStructure.attribute("temperature"), req.getTemperature());
        instance.setValue(dataStructure.attribute("rainfall"), req.getRainfall());
        instance.setValue(dataStructure.attribute("humidity"), req.getHumidity());
        instance.setValue(dataStructure.attribute("previous_disease_cases"), req.getPreviousDiseaseCases());
        instance.setValue(dataStructure.attribute("population_density"), req.getPopulationDensity());
        instance.setValue(dataStructure.attribute("sanitation_coverage"), req.getSanitationCoverage());

        // Get actual class probability distribution from the trained Weka Random Forest model
        double[] probs = classifier.distributionForInstance(instance);
        Attribute classAttr = dataStructure.classAttribute();

        Map<String, Double> classProbs = new LinkedHashMap<>();
        int maxIndex = 0;
        double maxProb = -1.0;
        for (int i = 0; i < probs.length; i++) {
            String cName = classAttr.value(i);
            double p = Math.round(probs[i] * 10000.0) / 10000.0;
            classProbs.put(cName, p);
            if (probs[i] > maxProb) {
                maxProb = probs[i];
                maxIndex = i;
            }
        }

        String predictedRisk = classAttr.value(maxIndex);
        double primaryProb = Math.round(maxProb * 10000.0) / 10000.0;

        String message;
        if ("HIGH".equalsIgnoreCase(predictedRisk)) {
            message = "Estimated High-Risk condition detected. Elevated environmental contamination or disease indicators observed.";
        } else if ("MEDIUM".equalsIgnoreCase(predictedRisk)) {
            message = "Moderate risk conditions detected based on current water quality and environmental thresholds.";
        } else {
            message = "Estimated Low-Risk condition based on available parameters.";
        }

        return new PredictionResponseDTO(
            req.getDistrict(),
            predictedRisk,
            primaryProb,
            LocalDateTime.now(),
            "RandomForest-v1.0",
            classProbs,
            message
        );
    }

    public ModelEvaluationDTO getLatestEvaluation() {
        if (latestEvaluation == null && modelLoaded) {
            try {
                return trainModel();
            } catch (Exception e) {
                log.error("Unable to generate evaluation: {}", e.getMessage());
            }
        }
        return latestEvaluation;
    }

    public boolean isModelLoaded() {
        return modelLoaded && classifier != null;
    }

    private Instances buildFallbackInstancesHeader() {
        ArrayList<Attribute> attrs = new ArrayList<>();
        attrs.add(new Attribute("district", districtList));
        attrs.add(new Attribute("ph"));
        attrs.add(new Attribute("turbidity"));
        attrs.add(new Attribute("tds"));
        attrs.add(new Attribute("dissolved_oxygen"));
        attrs.add(new Attribute("temperature"));
        attrs.add(new Attribute("rainfall"));
        attrs.add(new Attribute("humidity"));
        attrs.add(new Attribute("previous_disease_cases"));
        attrs.add(new Attribute("population_density"));
        attrs.add(new Attribute("sanitation_coverage"));

        ArrayList<String> classVals = new ArrayList<>(Arrays.asList("LOW", "MEDIUM", "HIGH"));
        attrs.add(new Attribute("risk_level", classVals));

        Instances inst = new Instances("aquasafe_disease_risk", attrs, 0);
        inst.setClassIndex(inst.numAttributes() - 1);
        return inst;
    }

    private double round(double val) {
        return Math.round(val * 100.0) / 100.0;
    }
}
