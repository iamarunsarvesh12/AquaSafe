package com.aquasafe.controller;

import com.aquasafe.dto.DatasetUploadResponseDTO;
import com.aquasafe.entity.DatasetImport;
import com.aquasafe.service.DatasetImportService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/datasets")
public class DatasetController {

    private final DatasetImportService importService;

    public DatasetController(DatasetImportService importService) {
        this.importService = importService;
    }

    @PostMapping("/upload")
    public ResponseEntity<DatasetUploadResponseDTO> uploadDataset(
            @RequestParam("file") MultipartFile file,
            @RequestParam("datasetType") String datasetType) throws Exception {
        DatasetUploadResponseDTO response = importService.processCsvUpload(file, datasetType);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<DatasetImport>> getRecentImports() {
        return ResponseEntity.ok(importService.getRecentImports());
    }

    @GetMapping("/history")
    public ResponseEntity<Page<DatasetImport>> getAllImports(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(importService.getAllImports(page, size));
    }
}
