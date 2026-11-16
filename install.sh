#!/usr/bin/env bash
# ==============================================================================
# install.sh - Install and launch Todo App on an Android device via ADB
# ==============================================================================

set -e

PACKAGE="com.example.todoapp"
ACTIVITY="com.example.todoapp.MainActivity"
APK_FILE="todo-app.apk"
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
APK_PATH="${SCRIPT_DIR}/${APK_FILE}"

echo "=================================================="
echo "         Todo App Android Installer"
echo "=================================================="

# 1. Locate ADB
ADB_CMD=""
if command -v adb >/dev/null 2>&1; then
    ADB_CMD="adb"
elif [ -n "$ANDROID_HOME" ] && [ -x "$ANDROID_HOME/platform-tools/adb" ]; then
    ADB_CMD="$ANDROID_HOME/platform-tools/adb"
elif [ -n "$ANDROID_SDK_ROOT" ] && [ -x "$ANDROID_SDK_ROOT/platform-tools/adb" ]; then
    ADB_CMD="$ANDROID_SDK_ROOT/platform-tools/adb"
elif [ -n "$LOCALAPPDATA" ] && [ -x "$LOCALAPPDATA/Android/Sdk/platform-tools/adb.exe" ]; then
    ADB_CMD="$LOCALAPPDATA/Android/Sdk/platform-tools/adb.exe"
else
    # Search common Windows paths in Git Bash / MSYS
    WINGET_ADB=$(find /c/Users/$USER/AppData/Local/Microsoft/WinGet/Packages -name "adb.exe" 2>/dev/null | head -n 1 || true)
    if [ -n "$WINGET_ADB" ] && [ -x "$WINGET_ADB" ]; then
        ADB_CMD="$WINGET_ADB"
    fi
fi

if [ -z "$ADB_CMD" ]; then
    echo "[ERROR] 'adb' command not found."
    echo "Please ensure Android SDK platform-tools or adb is installed and in your PATH."
    exit 1
fi

echo "[INFO] Using ADB: $ADB_CMD"

# 2. Check APK existence
if [ ! -f "$APK_PATH" ]; then
    FALLBACK_APK="${SCRIPT_DIR}/app/build/outputs/apk/debug/app-debug.apk"
    if [ -f "$FALLBACK_APK" ]; then
        APK_PATH="$FALLBACK_APK"
    else
        echo "[INFO] APK not found. Building APK with Gradle..."
        if [ -f "${SCRIPT_DIR}/gradlew" ]; then
            (cd "$SCRIPT_DIR" && ./gradlew assembleDebug)
            APK_PATH="$FALLBACK_APK"
        elif [ -f "${SCRIPT_DIR}/gradlew.bat" ]; then
            (cd "$SCRIPT_DIR" && ./gradlew.bat assembleDebug)
            APK_PATH="$FALLBACK_APK"
        else
            echo "[ERROR] Cannot find $APK_FILE or gradlew to build it."
            exit 1
        fi
    fi
fi

echo "[INFO] Found APK: $APK_PATH"

# 3. Check connected devices
echo "[INFO] Detecting connected Android devices..."
DEVICES_OUTPUT=$("$ADB_CMD" devices | grep -v "List of devices" | grep -v "^$" || true)

if [ -z "$DEVICES_OUTPUT" ]; then
    echo ""
    echo "[WARNING] No Android device detected!"
    echo "  1. Connect your Android phone to this computer via USB cable."
    echo "  2. On your phone, enable USB Debugging in Settings > Developer Options."
    echo "  3. When prompted on the phone, choose 'File Transfer (MTP)' if needed."
    echo ""
    read -p "Press [Enter] after connecting your phone to try again..."
    DEVICES_OUTPUT=$("$ADB_CMD" devices | grep -v "List of devices" | grep -v "^$" || true)
    if [ -z "$DEVICES_OUTPUT" ]; then
        echo "[ERROR] Still no device found. Exiting."
        exit 1
    fi
fi

# Check for unauthorized devices
if echo "$DEVICES_OUTPUT" | grep -q "unauthorized"; then
    echo ""
    echo "[WARNING] Device is connected but unauthorized!"
    echo "Please check your phone screen and tap 'Allow USB debugging' (or 'Always allow from this computer')."
    echo ""
    read -p "Press [Enter] after authorizing on your phone..."
fi

# Pick device ID
DEVICE_ID=$(echo "$DEVICES_OUTPUT" | grep -w "device" | head -n 1 | awk '{print $1}')
if [ -z "$DEVICE_ID" ]; then
    DEVICE_ID=$(echo "$DEVICES_OUTPUT" | head -n 1 | awk '{print $1}')
fi

echo "[INFO] Target device: $DEVICE_ID"

# 4. Install APK
echo "[INFO] Installing APK onto device..."
echo "[TIP] If a prompt appears on your phone screen, tap 'Install' or 'Allow'."
INSTALL_OUTPUT=$("$ADB_CMD" -s "$DEVICE_ID" install -r -d "$APK_PATH" 2>&1 || true)
echo "$INSTALL_OUTPUT"

if echo "$INSTALL_OUTPUT" | grep -q "Success"; then
    echo "=================================================="
    echo "[SUCCESS] Todo App installed successfully!"
    echo "=================================================="
elif echo "$INSTALL_OUTPUT" | grep -q "INSTALL_FAILED_USER_RESTRICTED"; then
    echo ""
    echo "[NOTICE] Xiaomi / MIUI Security Restriction detected."
    echo "Xiaomi devices require:"
    echo "  1. Settings > Developer Options > Enable 'Install via USB'."
    echo "  2. Or tap 'Install' on the phone screen prompt within 10 seconds."
    echo ""
    echo "[INFO] Pushing APK directly to phone Downloads folder as instant fallback..."
    "$ADB_CMD" -s "$DEVICE_ID" push "$APK_PATH" /sdcard/Download/todo-app.apk
    echo "[DONE] APK saved to: /sdcard/Download/todo-app.apk"
    echo "👉 You can open 'File Manager' on your phone, go to 'Downloads', and tap 'todo-app.apk' to install!"
    exit 0
else
    echo "[ERROR] Installation failed. Output:"
    echo "$INSTALL_OUTPUT"
    exit 1
fi

# 5. Launch the App
echo "[INFO] Launching Todo App on device..."
"$ADB_CMD" -s "$DEVICE_ID" shell am start -n "${PACKAGE}/${ACTIVITY}"

echo ""
echo "Todo App is now running on your phone! Enjoy!"
