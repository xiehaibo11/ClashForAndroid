@echo off
chcp 65001 >nul
echo ==========================================
echo   Clash for Android - 构建并安装
echo ==========================================
echo.

echo [1/3] 开始构建 Debug APK...
call gradlew.bat assembleDebug

if not exist "app\build\outputs\apk\debug\app-debug.apk" (
    echo ✗ 构建失败！
    pause
    exit /b 1
)

echo.
echo ✓ APK 构建成功！
echo.

echo [2/3] 检查 ADB 连接...
adb devices

echo.
echo [3/3] 安装 APK 到设备...
adb install -r app\build\outputs\apk\debug\app-debug.apk

if %ERRORLEVEL% EQU 0 (
    echo.
    echo ✓ 安装成功！
    echo.
    echo 启动应用:
    echo   adb shell am start -n com.github.kr328.clash/.MainActivity
) else (
    echo.
    echo ✗ 安装失败，请检查设备连接
)

echo.
pause

