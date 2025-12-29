@echo off
echo ========================================
echo Creating Windows Installer (.exe/.msi)
echo ========================================
echo.

REM Check if Java 17+ is installed
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo ERROR: Java is not installed or not in PATH!
    echo Please install Java 17 or later.
    pause
    exit /b 1
)

REM Check if jpackage is available
jpackage --version >nul 2>&1
if %errorlevel% neq 0 (
    echo ERROR: jpackage is not available!
    echo.
    echo jpackage requires Java 14 or later (JDK, not JRE).
    echo.
    echo Please:
    echo   1. Install JDK 17 or later from https://adoptium.net/
    echo   2. Make sure JAVA_HOME points to the JDK installation
    echo   3. Add JDK bin folder to your PATH
    echo.
    pause
    exit /b 1
)

echo Building fat JAR first...
call mvn clean package
if %errorlevel% neq 0 (
    echo ERROR: Failed to build JAR!
    pause
    exit /b %errorlevel%
)

echo.
echo Creating Windows installer...
echo.

REM Create output directory
if not exist "dist" mkdir dist

REM Create MSI installer
REM Note: jpackage will bundle a JRE, so users don't need Java installed
jpackage ^
    --input target ^
    --name "Restaurant Manager" ^
    --main-jar restaurant-manager-1.0.0.jar ^
    --main-class com.restaurantmanagement.app.MainApp ^
    --type msi ^
    --dest dist ^
    --app-version 1.0.0 ^
    --description "Restaurant Management System - Manage employees, shifts, inventory, and bookings" ^
    --vendor "Restaurant Management" ^
    --copyright "Copyright 2025" ^
    --win-dir-chooser ^
    --win-menu ^
    --win-menu-group "Restaurant Manager" ^
    --win-shortcut ^
    --win-shortcut-prompt ^
    --java-options "-Dfile.encoding=UTF-8"

if %errorlevel% neq 0 (
    echo.
    echo ERROR: Failed to create installer!
    echo.
    echo Troubleshooting:
    echo   - Make sure you have JDK 17+ installed (not just JRE)
    echo   - Check that JAVA_HOME points to JDK
    echo   - Try running: jpackage --version
    echo.
    pause
    exit /b %errorlevel%
)

echo.
echo ========================================
echo SUCCESS! Installer created!
echo ========================================
echo.
echo Location: dist\Restaurant Manager-1.0.0.msi
echo.
echo This installer can be distributed to users.
echo Users can double-click to install the application.
echo.
echo Note: The installer includes a bundled JRE, so users
echo don't need Java installed separately.
echo.
pause

