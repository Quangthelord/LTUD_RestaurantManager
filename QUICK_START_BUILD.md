# Quick Start - Build Executable

## 🚀 Fastest Way to Create Executable

### Option 1: Windows Installer (Best for End Users) ⭐

**Creates a professional installer - users don't need Java!**

```batch
create-exe-installer.bat
```

**Result:** `dist\Restaurant Manager-1.0.0.msi`

- Users double-click to install
- No Java required on user's computer
- Creates Start Menu shortcuts
- Professional installation experience

---

### Option 2: Simple Package (Easiest) 

**Creates a simple launcher package**

```batch
build-simple-exe.bat
```

**Result:** `dist\RestaurantManager\` folder

- Contains JAR + launcher batch file
- Users need Java 17+ installed
- Just double-click `RestaurantManager.bat` to run
- Easy to zip and distribute

---

### Option 3: Just the JAR

**For technical users**

```batch
build-fat-jar.bat
```

**Result:** `target\restaurant-manager-1.0.0.jar`

- Users run: `java -jar restaurant-manager-1.0.0.jar`
- Requires Java 17+ on user's machine

---

## 📋 Requirements

- **Java JDK 17+** (for building)
- **Maven** (should already be installed)
- **Windows** (for creating Windows executables)

---

## ✅ Recommended Workflow

1. **For best user experience:**
   ```batch
   create-exe-installer.bat
   ```
   Share the `.msi` file - users install like any Windows program!

2. **For quick distribution:**
   ```batch
   build-simple-exe.bat
   ```
   Zip the `dist\RestaurantManager` folder and share!

---

## 🎯 What Each Method Creates

| Method | Output | Java Required? | Best For |
|--------|--------|----------------|----------|
| `create-exe-installer.bat` | `.msi` installer | ❌ No | End users |
| `build-simple-exe.bat` | Folder with launcher | ✅ Yes | Quick distribution |
| `build-fat-jar.bat` | JAR file | ✅ Yes | Technical users |

---

## 💡 Tips

- **First time?** Try `build-simple-exe.bat` - it's the easiest!
- **Professional distribution?** Use `create-exe-installer.bat`
- **Users have Java?** Use `build-fat-jar.bat`

---

## 🆘 Troubleshooting

**"jpackage not found"**
- Install JDK 17+ (not just JRE)
- Set JAVA_HOME environment variable

**"Maven not found"**
- Install Maven or use IDE's built-in Maven

**Build fails**
- Run: `mvn clean package` manually
- Check Java version: `java -version`

---

**Ready? Run one of the build scripts above!** 🚀

