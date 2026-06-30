@echo off
setlocal enabledelayedexpansion

set "QT_DIR=C:/Qt/6.11.1/msvc2022_64/lib/cmake/Qt6"

echo [1/5] Cleaning build...
rmdir /s /q build 2>nul
if errorlevel 1 (
    echo WARNING: Could not remove build folder (maybe it doesn't exist?)
)

echo [2/5] Configuring...
cmake -S . -B build -DQt6_DIR="%QT_DIR%"
if errorlevel 1 (
    echo ERROR: CMake configuration failed.
    exit /b 1
)

echo [3/5] Building...
cmake --build build --config Release
if errorlevel 1 (
    echo ERROR: Build failed.
    exit /b 1
)

echo [4/5] Packaging (custom package.bat)...
installer\package.bat build
if errorlevel 1 (
    echo ERROR: Packaging failed.
    exit /b 1
)

echo [5/5] Generating NSIS installer...
pushd installer || exit /b 1
makensis JCBudgetBuddy.nsi
if errorlevel 1 (
    popd
    echo ERROR: NSIS failed.
    exit /b 1
)
popd

echo All steps completed successfully.
exit /b 0