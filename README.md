# LAUREN
knowLedge grAph sUmmarization foR quEstion aNswering


Note: The index files are not included due to size restriction. 

## How to run AGDISTIS in QANARY pipeline? 
1) Run the desired AGDISTIS service in first terminal. E.g. 
```
cd AGDISTIS-master_KG_SUM_HITS/
mvn clean install
mvn tomcat:run -Dmaven.tomcat.port=8181
```
The AGDISTIS service has now started on http://127.0.0.1:8181/AGDISTIS. 

2) Run the qanary-pipeline-template. Follow the tutorial given [here](https://github.com/WDAqua/Qanary/wiki/Qanary-tutorial:-How-to-build-a-trivial-Question-Answering-pipeline).
To be precise, 
- Install and run the stardog triple store. 
- Install and run the qanary-pipeline-template
```
cd evaluation-utilities/QANARY/qanary_pipeline-template
mvn clean install
cd ..
java -jar qanary_pipeline-template/target/qa.pipeline-X.Y.Z.jar
```
- Install and run the qanary-QA-components (each in a separate terminal tab). E.g.
```
cd evaluation-utilities/QANARY-QA-Components/qanary_component-NED-Dandelion
mvn clean install
cd ..
java -jar qanary_component-NER-Dandelion/target/qanary_component-NER-Dandelion-X.Y.Z.jar
```
Likewise, run other qanary-QA-componenets such as: qanary_component-NER-stanford, qanary_component-NED-AGDISTIS, qanary_component-REL-RELNLIOD, qanary_component-QueryBuilder. 
The qanary-QA pipeline is now ready to be tested. To test the web-UI, check http://localhost:8282/startquestionansweringwithtextquestion.
