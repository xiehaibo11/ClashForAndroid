@echo off
chcp 65001 >nul
echo ==========================================
echo   Clash for Android - 构建脚本
echo ==========================================
echo.

echo [1/4] 清理旧的构建文件...
call gradlew.bat clean

echo.
echo [2/4] 检查依赖...
call gradlew.bat dependencies

echo.
echo [3/4] 开始构建 Debug APK...
call gradlew.bat assembleDebug

echo.
echo [4/4] 构建完成！
echo.

if exist "app\build\outputs\apk\debug\app-debug.apk" (
    echo ✓ APK 文件生成成功！
    echo 位置: app\build\outputs\apk\debug\app-debug.apk
    echo.
    
    for %%I in ("app\build\outputs\apk\debug\app-debug.apk") do (
        echo 文件大小: %%~zI 字节
    )
    
    echo.
    echo ==========================================
    echo 安装到设备:
    echo   adb install -r app\build\outputs\apk\debug\app-debug.apk
    echo ==========================================
) else (
    echo ✗ 构建失败，请查看上方错误信息
)

echo.
pause

