# 订阅格式转换功能说明

## 📝 概述

本次修改为 Clash for Android 添加了**自动订阅格式转换**功能，支持多种主流代理订阅格式。

## ✨ 新增功能

### 支持的订阅格式

1. **Clash 标准格式** - YAML 配置文件（原生支持）
2. **Base64 订阅** - 包含多个代理链接的 Base64 编码文本
3. **单个代理链接** - 支持以下协议：
   - Shadowsocks (ss://)
   - Hysteria2 (hysteria2://, hy2://)
   - Trojan (trojan://)
   - VMess (vmess://) - 基础支持

### 自动检测机制

应用会自动检测订阅内容的格式：
- 如果是标准 Clash YAML → 直接使用
- 如果是 Base64 编码 → 自动解码并转换
- 如果是单个代理链接 → 生成完整 Clash 配置
- 如果转换失败 → 尝试使用原始内容

## 🔧 修改的文件

### 1. 新增文件

**`app/src/main/java/com/github/kr328/clash/utils/SubscriptionConverter.kt`**
- 订阅格式转换核心工具类
- 支持多种代理协议解析
- 自动生成 Clash 配置模板

### 2. 修改文件

**`app/src/main/java/com/github/kr328/clash/ImportUrlActivity.kt`**
- 在下载订阅内容后添加格式转换步骤
- 自动调用 `SubscriptionConverter.convertToClash()`

## 📖 使用方法

### 导入 Base64 订阅

1. 打开应用，点击「配置」→「新配置」
2. 选择「URL」
3. 输入配置名称（如：我的订阅）
4. 输入订阅 URL：
   ```
   https://ckec.bebegenio.com/link/f45cfcbf5cef465efbfd5cf25605baf7
   ```
5. 点击保存

应用会自动：
- 下载 Base64 编码的内容
- 解码获取代理链接列表
- 解析每个代理链接（SS、Hysteria2 等）
- 生成完整的 Clash 配置文件
- 创建代理组（PROXY、自动选择）

### 生成的配置示例

转换后会包含：
- ✅ 所有解析成功的代理节点
- ✅ 自动选择组（URL 测试）
- ✅ 手动选择组
- ✅ 基础分流规则

## 🎯 支持的 URL 示例

```
# Base64 订阅
https://example.com/sub/xxxxxxxxxxxx

# 单个 Shadowsocks
ss://YWVzLTI1Ni1nY206password@server.com:8388#节点名称

# 单个 Hysteria2
hysteria2://password@server.com:443?insecure=1&sni=server.com#节点名称

# 单个 Trojan
trojan://password@server.com:443#节点名称
```

## 🚀 构建和测试

### 构建应用

```bash
# Windows
.\gradlew.bat assembleDebug

# Linux/Mac
./gradlew assembleDebug
```

### 安装到设备

```bash
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

### 测试步骤

1. 安装修改后的 APK
2. 打开应用，进入「配置」
3. 点击「+」按钮，选择「URL」
4. 输入您的订阅链接
5. 保存并查看是否成功导入

## ⚠️ 注意事项

### 第一个 URL 的问题

```
https://47.238.198.94/iv/verify_mode.htm?token=5deb6dce926526eda7974a73ffe38b4e
```

这个 URL 可能返回的是：
- 验证页面 HTML
- 需要认证的页面
- 非标准格式

**建议**：检查该 URL 是否需要特殊处理（如 Cookie、Token 验证）

### Base64 订阅注意事项

- 确保订阅服务返回的是纯文本 Base64
- 编码的内容应该是代理链接列表（每行一个）
- 支持的代理协议：ss://、hysteria2://、trojan://

### 已知限制

1. **VMess 支持不完整** - VMess 需要复杂的 JSON 解析，当前版本暂未完全实现
2. **VLESS 未支持** - 需要后续添加
3. **参数解析** - 某些代理的高级参数可能未完全支持

## 🔄 未来改进

- [ ] 完整支持 VMess 协议
- [ ] 添加 VLESS 支持
- [ ] 支持更多订阅格式（V2Ray、Quantumult X 等）
- [ ] 添加订阅更新功能
- [ ] 支持订阅转换 API

## 🐛 故障排查

### 问题：导入后提示 YAML 解析错误

**原因**：订阅内容格式不符合预期

**解决**：
1. 检查订阅 URL 是否正确
2. 在浏览器中打开订阅 URL，查看返回内容
3. 确认内容是 Base64 编码或代理链接

### 问题：部分节点未导入

**原因**：某些代理协议或参数不支持

**解决**：
1. 查看应用日志
2. 确认代理链接格式
3. 可能需要使用在线订阅转换服务

## 📞 技术支持

如有问题，请提供：
1. 订阅 URL（可隐藏敏感信息）
2. 错误截图
3. 应用日志

---

**修改时间**: 2025-11-01  
**适用版本**: Clash for Android 1.0.7-alpha+

