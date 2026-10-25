@echo off
setlocal

set "PROJECT_DIR=%~dp0BACKEND\springboot"
pushd "%PROJECT_DIR%"
if errorlevel 1 (
    echo Could not find the Spring Boot project at "%PROJECT_DIR%".
    exit /b 1
)

call "%PROJECT_DIR%\mvnw.cmd" %*
set "EXIT_CODE=%ERRORLEVEL%"
popd
exit /b %EXIT_CODE%
