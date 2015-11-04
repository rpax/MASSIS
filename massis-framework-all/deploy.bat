@ECHO OFF
SET DEPLOY_PATH=C:\Users\Rafael Pax\Downloads\temp\maven-repo
RMDIR /S /Q "%DEPLOY_PATH%"
mvn clean install deploy -DaltDeploymentRepository=massis.github.repo::default::file://"%DEPLOY_PATH%"