@echo off
REM ------------------------------------------------------------------------------
REM Gradle start up script for Windows
REM ------------------------------------------------------------------------------
set DIRNAME=%~dp0
set APP_HOME=%DIRNAME:~0,-1%
set CLASSPATH=%APP_HOME%\gradle\wrapper\gradle-wrapper.jar
if not exist "%CLASSPATH%" (
  echo ERROR: Gradle wrapper jar not found. Run 'gradle wrapper' or install Gradle.
  exit /b 1
)
java -classpath "%CLASSPATH%" org.gradle.wrapper.GradleWrapperMain %*
