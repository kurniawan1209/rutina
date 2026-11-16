@echo off
setlocal enabledelayedexpansion

echo ==================================================
echo          Todo App Android Installer (CMD)
echo ==================================================

set "PACKAGE=com.example.todoapp"
set "ACTIVITY=com.example.todoapp.MainActivity"
set "APK_FILE=todo-app.apk"

:: 1. Locate ADB
where adb >nul 2>nul
if %errorlevel% equ 0 (
    set "ADB_CMD=adb"
) else (
    echo [ERROR] 'adb' command not found in PATH.
    pause
    exit /b 1
)

:: 2. Check APK
if not exist "%APK_FILE%" (
    if exist "app\build\outputs\apk\debug\app-debug.apk" (
        set "APK_FILE=app\build\outputs\apk\debug\app-debug.apk"
    ) else (
        echo [INFO] Building APK with gradlew...
        call gradlew.bat assembleDebug
        set "APK_FILE=app\build\outputs\apk\debug\app-debug.apk"
    )
)

echo [INFO] Using APK: %APK_FILE%

:: 3. Check Devices
echo [INFO] Checking connected Android devices...
%ADB_CMD% devices

:: 4. Install
echo [INFO] Installing APK...
echo [TIP] If a prompt appears on your phone screen, tap 'Install' or 'Allow'.
%ADB_CMD% install -r -d "%APK_FILE%"

if %errorlevel% neq 0 (
    echo [NOTICE] If restricted by Xiaomi / MIUI, pushing to phone storage directly...
    %ADB_CMD% push "%APK_FILE%" /sdcard/Download/todo-app.apk
    echo [DONE] APK pushed to /sdcard/Download/todo-app.apk
    echo Please open File Manager on your phone and tap todo-app.apk to install.
) else (
    echo [SUCCESS] App installed! Launching...
    %ADB_CMD% shell am start -n %PACKAGE%/%ACTIVITY%
)

pause
