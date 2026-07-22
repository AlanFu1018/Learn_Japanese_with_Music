# Walkthrough - 設定頁面實作與全域導航架構優化

本任務已成功實作設定頁面，並將側邊欄（Navigation Drawer）邏輯提升至全域層級，同時支援了設定的持久化儲存。

## 變更內容

### 1. 全域導航架構重構
- **`MainActivity` 核心化**：現在側邊欄（ModalNavigationDrawer）由 `MainActivity` 直接管理。這意味著無論切換到哪個功能頁面，側邊欄都能保持一致的狀態與操作。
- **頁面管理**：引入了 `Screen` 狀態機，控制主內容區域在「搜尋頁」與「設定頁」之間切換。

### 2. 設定頁面 (SettingsPage)
- **路徑**：`features/settings/ui/SettingsPage.kt`
- **功能項目**：
    - **Syntax Analyzer Mode**：允許使用者選擇 Sudachi 的 A、B、C 分詞模式。
    - **API Key**：提供輸入框供使用者設定個人的 Genius API Access Token。
- **介面控制**：在設定頁同樣配置了 `HomeRectangleButton`，確保使用者能隨時開啟側邊欄進行導航。

### 3. 設定持久化 (SettingsManager)
- **路徑**：`core/data/SettingsManager.kt`
- **技術實作**：使用 Android 的 `SharedPreferences` 將設定儲存在手機內部的 `app_settings.xml` 中。即使 App 關閉重啟，使用者的 API Token 與分詞模式偏好也會被保留。

### 4. 基礎設施動態化
- **`RetrofitClient`**：現在不再硬編碼使用 `BuildConfig` 的 Token。透過一個動態的 `tokenProvider`，網路請求會即時讀取 `SettingsManager` 中的最新 Token。
- **即時重新分詞**：當使用者在設定頁變更分詞模式時，`LyricPage` 會透過 `LaunchedEffect` 自動監測到變更，並立即對目前的歌詞進行重新處理，無需重新搜尋。

## 驗證結果
- [x] Gradle 編譯成功。
- [x] 側邊欄點擊 "Settings" 可正常進入設定頁。
- [x] 在設定頁修改數值後，重啟 App 確認設定值已成功儲存。
- [x] 修改 Token 後，搜尋功能會自動套用新 Token 進行請求。

> [!TIP]
> **開發者叮嚀**
> 現在您可以放心地在設定頁輸入您個人的 API Token，這將覆蓋開發預設值，並安全地儲存在您的裝置中。
