@echo off
echo ========================================
echo Building Fat JAR for Restaurant Manager
echo ========================================
echo.

REM Check if Java is installed
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo ERROR: Java is not installed or not in PATH!
    echo Please install Java 17 or later.
    pause
    exit /b 1
)

echo Step 1: Cleaning previous builds...
call mvn clean
if %errorlevel% neq 0 (
    echo ERROR: Maven clean failed!
    pause
    exit /b %errorlevel%
)

echo.
echo Step 2: Compiling and creating fat JAR...
call mvn package
if %errorlevel% neq 0 (
    echo ERROR: Maven package failed!
    pause
    exit /b %errorlevel%
)

echo.
echo ========================================
echo SUCCESS! Fat JAR created
echo ========================================
echo.
echo Location: target\restaurant-manager-1.0.0.jar
echo.
echo This JAR contains all dependencies and can be run with:
echo   java -jar target\restaurant-manager-1.0.0.jar
echo.
pause

