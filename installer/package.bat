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
    echo Running windeployqt in release mode with compiler runtime...
    windeployqt --release --compiler-runtime "%EXE%"
    if errorlevel 1 (
        echo windeployqt failed. Please ensure Qt is correctly installed.
        exit /b 1
    )
) else (
    echo windeployqt not found on PATH. You must manually copy Qt DLLs and the VC++ redistributable.
    echo For VC++ runtime, copy the following from your compiler's bin folder:
    echo   msvcp140.dll, vcruntime140.dll, vcruntime140_1.dll, concrt140.dll (if used)
    echo Alternatively, bundle the VC++ redistributable installer and run it from the NSIS script.
)

if exist "..\LICENSE" (
    copy "..\LICENSE" "%INSTALL_DIR%\"
)

echo.
echo Package staged in %INSTALL_DIR%
echo.