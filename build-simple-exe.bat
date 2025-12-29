@echo off
echo ========================================
echo Building Simple Executable Package
echo ========================================
echo.

REM Check if Java is installed
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo ERROR: Java is not installed or not in PATH!
    pause
    exit /b 1
)

echo Step 1: Building fat JAR...
call mvn clean package
if %errorlevel% neq 0 (
    echo ERROR: Build failed!
    pause
    exit /b %errorlevel%
)

echo.
echo Step 2: Creating distribution package...
echo.

REM Create distribution folder
if not exist "dist\RestaurantManager" mkdir dist\RestaurantManager

REM Copy JAR
copy target\restaurant-manager-1.0.0.jar dist\RestaurantManager\RestaurantManager.jar >nul

REM Create launcher batch file
(
echo @echo off
echo title Restaurant Manager
echo cd /d "%%~dp0"
echo.
echo echo Starting Restaurant Manager...
echo echo.
echo java -jar RestaurantManager.jar
echo.
echo if errorlevel 1 ^(
echo     echo.
echo     echo ERROR: Failed to start application!
echo     echo.
echo     echo Please make sure Java 17 or later is installed.
echo     echo Download from: https://adoptium.net/
echo     echo.
echo     pause
echo ^)
) > dist\RestaurantManager\RestaurantManager.bat

REM Create README
(
echo Restaurant Manager - Installation Guide
echo ======================================
echo.
echo QUICK START:
echo   1. Make sure Java 17+ is installed
echo   2. Double-click RestaurantManager.bat
echo.
echo REQUIREMENTS:
echo   - Java 17 or later
echo   - Download from: https://adoptium.net/
echo.
echo INSTALLATION:
echo   1. Check if Java is installed:
echo        Open Command Prompt and type: java -version
echo.
echo   2. If Java is not installed:
echo        - Go to https://adoptium.net/
echo        - Download Java 17 or later
echo        - Install it
echo.
echo   3. Run the application:
echo        - Double-click RestaurantManager.bat
echo.
echo TROUBLESHOOTING:
echo   - If you see "Java is not recognized":
echo     * Java is not installed or not in PATH
echo     * Install Java from https://adoptium.net/
echo.
echo   - If the window closes immediately:
echo     * Check if Java is installed: java -version
echo     * Make sure you're using Java 17 or later
echo.
echo SUPPORT:
echo   For issues, make sure Java 17+ is installed and try again.
) > dist\RestaurantManager\README.txt

echo.
echo ========================================
echo SUCCESS! Distribution package created!
echo ========================================
echo.
echo Location: dist\RestaurantManager\
echo.
echo Package contents:
echo   - RestaurantManager.jar (the application)
echo   - RestaurantManager.bat (launcher)
echo   - README.txt (instructions)
echo.
echo To distribute:
echo   1. Zip the entire 'dist\RestaurantManager' folder
echo   2. Share the zip file with users
echo   3. Users extract and run RestaurantManager.bat
echo.
echo Note: Users need Java 17+ installed.
echo.
pause

