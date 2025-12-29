# Distribution Guide - Restaurant Manager

This guide explains how to create an executable file (.exe) for the Restaurant Manager application.

## Prerequisites

- **Java JDK 17 or later** (not just JRE) - Download from [Adoptium](https://adoptium.net/)
- **Maven** - Should already be installed if you can run `mvn` commands
- **Windows OS** - For creating Windows executables

## Method 1: Create Windows Installer (Recommended)

This creates a professional `.msi` installer that users can double-click to install.

### Steps:

1. **Open Command Prompt** in the project directory

2. **Run the installer creation script:**
   ```batch
   create-exe-installer.bat
   ```

3. **Wait for the build to complete** (may take a few minutes)

4. **Find your installer** in the `dist` folder:
   - `dist\Restaurant Manager-1.0.0.msi`

### What this creates:
- A Windows installer (.msi file)
- Includes bundled JRE (users don't need Java installed)
- Creates Start Menu shortcuts
- Professional installation experience

### Distribution:
- Share the `.msi` file with users
- Users double-click to install
- Application appears in Start Menu after installation

---

## Method 2: Create Standalone Launcher (Alternative)

If jpackage is not available, this creates a simple launcher that requires Java.

### Steps:

1. **Run the build script:**
   ```batch
   build-fat-jar.bat
   ```

2. **Copy files to distribution folder:**
   - Copy `target\restaurant-manager-1.0.0.jar` to `dist\launcher\RestaurantManager.jar`
   - The `RestaurantManager.bat` launcher is already in `dist\launcher\`

3. **Distribute the `dist\launcher` folder** to users

### What users need:
- Java 17 or later installed
- Download from [Adoptium](https://adoptium.net/)

### How users run it:
- Double-click `RestaurantManager.bat`

---

## Method 3: Simple JAR Distribution

For users who already have Java installed.

### Steps:

1. **Build the fat JAR:**
   ```batch
   build-fat-jar.bat
   ```

2. **Distribute the JAR file:**
   - `target\restaurant-manager-1.0.0.jar`

### How users run it:
```batch
java -jar restaurant-manager-1.0.0.jar
```

---

## Troubleshooting

### "jpackage is not available"
- Make sure you have **JDK** (not just JRE) installed
- Check `JAVA_HOME` environment variable points to JDK
- Verify with: `jpackage --version`

### "Java is not in PATH"
- Add Java bin folder to system PATH
- Or set `JAVA_HOME` environment variable

### Build fails
- Make sure Maven is installed: `mvn --version`
- Clean and rebuild: `mvn clean package`

### Application won't start for users
- Users need Java 17+ installed (unless using Method 1 installer)
- Check Java version: `java -version`
- Download from [Adoptium](https://adoptium.net/)

---

## File Structure After Build

```
Restaurant_manager/
├── dist/                          # Distribution folder
│   ├── Restaurant Manager-1.0.0.msi  # Windows installer (Method 1)
│   └── launcher/                  # Standalone launcher (Method 2)
│       ├── RestaurantManager.jar
│       ├── RestaurantManager.bat
│       └── README.txt
├── target/
│   └── restaurant-manager-1.0.0.jar  # Fat JAR (Method 3)
└── ...
```

---

## Quick Start (Recommended)

For the easiest distribution experience:

1. Run: `create-exe-installer.bat`
2. Wait for completion
3. Share `dist\Restaurant Manager-1.0.0.msi` with users
4. Users install and run - no Java needed!

---

## Notes

- **Method 1 (Installer)** is best for end users - no Java required
- **Method 2 (Launcher)** is simpler but requires Java on user's machine
- **Method 3 (JAR)** is for technical users who have Java

Choose the method that best fits your distribution needs!

