# 在 GitHub Actions 上构建 ClashForAndroid

## 自动构建

代码已配置 GitHub Actions 自动构建流程。每次推送代码到 `master` 分支时，都会自动触发构建。

### 查看构建状态

1. 访问 GitHub 仓库页面：https://github.com/xiehaibo11/ClashForAndroid
2. 点击顶部的 **Actions** 标签
3. 查看最新的构建任务

### 下载构建的 APK

1. 在 Actions 页面，点击最新的成功构建
2. 向下滚动到 **Artifacts** 部分
3. 点击下载 `app-debug` 文件
4. 解压下载的 ZIP 文件，获取 APK

## 手动触发构建

如果需要手动触发构建：

1. 访问 GitHub 仓库的 Actions 页面
2. 点击左侧的 **Build APK** 工作流
3. 点击右侧的 **Run workflow** 按钮
4. 选择分支并确认

## 本地构建要求

如果要在本地构建，需要安装以下工具：

### 必需工具

1. **Java 17**
   - 下载：https://adoptium.net/

2. **Go 1.19+**
   - 下载：https://golang.org/dl/
   - 安装后，确保 `go` 命令在系统 PATH 中

3. **Android SDK**
   - 通过 Android Studio 安装
   - 或下载命令行工具：https://developer.android.com/studio#command-tools

4. **Android NDK 26.3.11579264**
   - 通过 Android Studio SDK Manager 安装
   - 或使用 sdkmanager：
     ```bash
     sdkmanager --install "ndk;26.3.11579264"
     ```

### 配置本地环境

1. 创建 `local.properties` 文件（已在 .gitignore 中）：
   ```properties
   sdk.dir=/path/to/android/sdk
   ndk.dir=/path/to/android/sdk/ndk/26.3.11579264
   ```

2. 设置环境变量：
   ```bash
   # Windows
   set JAVA_HOME=C:\Program Files\Eclipse Adoptium\jdk-17.0.16.8-hotspot
   
   # Linux/Mac
   export JAVA_HOME=/path/to/jdk-17
   export ANDROID_HOME=/path/to/android/sdk
   ```

### 本地构建命令

```bash
# 清理
./gradlew clean

# 构建 Debug APK
./gradlew assembleDebug

# 构建 Release APK（需要签名配置）
./gradlew assembleRelease
```

构建成功后，APK 文件位于：
- Debug: `app/build/outputs/apk/debug/app-debug.apk`
- Release: `app/build/outputs/apk/release/app-release.apk`

## 常见问题

### Q: 构建失败，提示"Android NDK not found"
**A:** 需要安装 Android NDK 26.3.11579264 版本，并在 `local.properties` 中配置路径。

### Q: 构建失败，提示"go.exe not found"
**A:** 需要安装 Go 语言（1.19 或更高版本），并确保在系统 PATH 中。

### Q: downloadMMDB 任务失败
**A:** 这是正常的，可以跳过此任务：
```bash
./gradlew assembleDebug -x :core:downloadMMDB
```

### Q: GitHub Actions 构建失败
**A:** 检查 Actions 日志，通常是由于：
- NDK 版本不匹配
- Go 模块依赖问题
- 网络连接问题（下载依赖）

## 项目升级说明

本次升级包括：

- ✅ Gradle 7.4
- ✅ Android Gradle Plugin 7.2.2
- ✅ Kotlin 1.6.21
- ✅ Kotlinx Serialization 1.3.3
- ✅ 各种 AndroidX 库更新
- ✅ 订阅格式自动转换支持
- ✅ Android 12+ 兼容性修复

详细信息请查看 `UPGRADE_SUMMARY.md` 和 `SUBSCRIPTION_SUPPORT.md`。

## 技术支持

如遇到问题，请在 GitHub Issues 中反馈。

