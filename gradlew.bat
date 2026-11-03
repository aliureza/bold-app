@ECHO OFF
SETLOCAL
SET APP_HOME=%~dp0
SET WRAPPER_DIR=%APP_HOME%gradle\wrapper
SET JAR=%WRAPPER_DIR%\gradle-wrapper.jar
SET JAR_URL=https://services.gradle.org/distributions/gradle-8.0-wrapper.jar

IF NOT EXIST "%JAR%" (
  ECHO Gradle Wrapper JAR not found; downloading Gradle 8.0 wrapper...
  IF NOT EXIST "%WRAPPER_DIR%" MKDIR "%WRAPPER_DIR%"
  powershell -NoProfile -ExecutionPolicy Bypass -Command "Invoke-WebRequest -UseBasicParsing -Uri '%JAR_URL%' -OutFile '%JAR%'"
  IF ERRORLEVEL 1 EXIT /B 1
)

java -jar "%JAR%" %*
ENDLOCAL
