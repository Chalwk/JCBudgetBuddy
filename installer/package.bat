@echo off
setlocal enabledelayedexpansion

if "%1"=="" (
    echo Usage: package.bat ^<build-dir^>
    exit /b 1
)

set BUILD_DIR=%~1
set INSTALL_DIR=%BUILD_DIR%\package

if exist "%INSTALL_DIR%" rmdir /s /q "%INSTALL_DIR%"
mkdir "%INSTALL_DIR%"

cmake --install "%BUILD_DIR%" --prefix "%INSTALL_DIR%"
if errorlevel 1 exit /b 1

set EXE=%INSTALL_DIR%\JCBudgetBuddy.exe

where windeployqt >nul 2>nul
if not errorlevel 1 (
    echo Running windeployqt...
    windeployqt "%EXE%"
) else (
    echo windeployqt not found on PATH. Copy Qt runtime DLLs manually before building an installer.
)

echo.
echo Package staged in %INSTALL_DIR%
echo.
