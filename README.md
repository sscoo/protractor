# 悬浮量角器 Android App

这是一个可以悬浮在其他应用上方的量角器示例：

- 一条基准边固定为水平线。
- 另一条可拖拽旋转，实时显示夹角（0° ~ 180°）。
- 整个量角器浮窗可拖动位置。
- 提供关闭按钮，或在主页面点击“关闭悬浮量角器”停止服务。

---

## 一、先安装 Android Studio（Windows / macOS 通用）

1. 打开 Android Studio 官网：<https://developer.android.com/studio>
2. 下载 **Android Studio（最新稳定版）**。
3. 安装时保持默认选项即可（包含：Android SDK、Android SDK Platform、Android Virtual Device）。
4. 首次启动会出现 Setup Wizard，选择 **Standard** 安装。

> 国内网络如果下载慢，可以给 SDK 配置镜像（见下方“常见问题”）。

---

## 二、Android Studio 必装组件（SDK Manager）

打开 Android Studio 后，进入：

`Settings / Preferences -> Appearance & Behavior -> System Settings -> Android SDK`

推荐安装：

- **SDK Platforms**
  - Android 14 (API 34)
- **SDK Tools**
  - Android SDK Build-Tools
  - Android SDK Command-line Tools (latest)
  - Android Emulator
  - Android SDK Platform-Tools

本项目使用：

- `compileSdk = 34`
- `targetSdk = 34`
- `minSdk = 26`

---

## 三、JDK 配置（非常关键）

本项目建议使用 **JDK 17**（AGP 8.x 通常最稳）。

在 Android Studio 中检查：

`Settings / Preferences -> Build, Execution, Deployment -> Build Tools -> Gradle`

将 **Gradle JDK** 设置为：

- `Embedded JDK (17)`，或
- 你本机安装的 `JDK 17`

> 如果你机器上是 JDK 21/25，可能会遇到插件或构建兼容问题，优先切回 JDK 17。

---

## 四、打开并运行本项目

1. 在 Android Studio 选择 **Open**，打开本仓库目录。
2. 等待 Gradle Sync 完成。
3. 准备运行设备：
   - 真机：打开开发者选项 + USB 调试
   - 模拟器：在 Device Manager 创建一个 Android 11+ 设备
4. 点击运行 `app` 模块。
5. 打开应用后：
   - 先授予“显示在其他应用上层”权限
   - 点击“启动悬浮量角器”

---

## 五、使用说明

- 拖动量角器外框：移动悬浮窗位置
- 拖动红色量角边：修改夹角
- 点击右上角关闭按钮：关闭悬浮窗
- 或回到主界面点击“关闭悬浮量角器”

---

## 六、常见问题（安装环境）

### 1) Gradle Sync 卡住 / 下载失败

- 检查网络是否可访问 `google()`、`mavenCentral()`。
- 在 `Settings -> HTTP Proxy` 设置代理（如有）。
- 必要时换网络再同步。

### 2) `Plugin [id: 'com.android.application'] was not found`

通常是网络无法访问 Google Maven，或代理配置问题。

处理：

- 确认根目录 `settings.gradle.kts` 里有：
  - `google()`
  - `mavenCentral()`
  - `gradlePluginPortal()`
- 配置代理后重新 Sync。

### 3) JDK 版本问题导致构建失败

- 报错中出现 Java 21/25 相关兼容提示时，切换到 **JDK 17** 再试。

### 4) 真机不显示悬浮窗

- 手动到系统设置给应用开启：
  - 显示在其他应用上层 / 悬浮窗权限
- 某些国产 ROM 还需要在“自启动/后台管理”中放行。

---

## 七、项目结构（快速理解）

- `MainActivity.kt`：权限申请 + 启动/停止悬浮服务
- `OverlayService.kt`：前台服务 + 创建悬浮窗 + 拖拽移动 + 关闭
- `ProtractorOverlayView.kt`：绘制基准线/量角边并计算角度

---

> 该实现使用 `TYPE_APPLICATION_OVERLAY` + 前台服务实现跨应用悬浮。
