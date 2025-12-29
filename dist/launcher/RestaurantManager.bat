@echo off
title Restaurant Manager
cd /d "%~dp0"

REM Check if Java is installed
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo.
    echo ERROR: Java is not installed or not found in PATH!
    echo.
    echo Please install Java 17 or later from:
    echo https://adoptium.net/
    echo.
    pause
    exit /b 1
)

REM Run the application
java -jar RestaurantManager.jar

if %errorlevel% neq 0 (
    echo.
    echo Application encountered an error.
    pause
)

