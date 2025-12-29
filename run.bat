@echo off
echo Compiling project...
call mvn clean compile
if %errorlevel% neq 0 (
    echo Compilation failed!
    pause
    exit /b %errorlevel%
)

echo Running application...
cd target
java --module-path "dependency" --add-modules javafx.controls,javafx.fxml -cp "classes;dependency\javafx-base-17.0.2.jar;dependency\javafx-controls-17.0.2.jar;dependency\javafx-graphics-17.0.2.jar;dependency\javafx-fxml-17.0.2.jar" com.restaurantmanagement.app.MainApp
cd ..
pause

