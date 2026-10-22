.PHONY: all run build test spark seed clean help

all: run

help:
	@echo "AquaSafe - Command Runner"
	@echo "Targets:"
	@echo "  make run    - Run the entire application (single command)"
	@echo "  make build  - Compile and package backend JAR"
	@echo "  make test   - Run test suite"
	@echo "  make spark  - Execute PySpark big data analytics job"
	@echo "  make seed   - Generate synthetic training & surveillance datasets"
	@echo "  make clean  - Clean build artifacts"

run:
	@python run.py

build:
	@cd backend && mvn clean package -DskipTests

test:
	@cd backend && mvn test

spark:
	@python spark/spark_processor.py local[*] data

seed:
	@python generate_demo_data.py

clean:
	@cd backend && mvn clean
