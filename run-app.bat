@echo off
echo Compiling project...
call mvn clean compile
if %errorlevel% neq 0 (
    echo Compilation failed!
    pause
    exit /b %errorlevel%
)

echo Copying dependencies...
call mvn dependency:copy-dependencies

echo Running application...
java --module-path "target/dependency" --add-modules javafx.controls,javafx.fxml -cp "target/classes" com.restaurantmanagement.app.MainApp

pause



















