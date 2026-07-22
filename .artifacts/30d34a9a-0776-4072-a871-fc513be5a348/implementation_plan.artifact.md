# 實作 Settings 頁面與全域導航架構重構

本計畫旨在建立設定功能，並將側邊欄（Drawer）邏輯從單一頁面提升至全域層級，提升 App 的架構整潔度。

## 建議的變更

### 1. 建立設定管理層 (`core/data`)

#### [NEW] [SettingsManager.kt](file:///C:/Users/fuala/AndroidStudioProjects/Learn_Japanese_with_Music/app/src/main/java/com/learn_japanese_with_music/core/data/SettingsManager.kt)
- 使用 `SharedPreferences` 儲存：
    - `genius_api_token` (預設為 `BuildConfig.GENIUS_API_TOKEN`)
    - `sudachi_split_mode` (預設為 `C`)

### 2. 重構導航與側邊欄架構 (`MainActivity`)

#### [MODIFY] [MainActivity.kt](file:///C:/Users/fuala/AndroidStudioProjects/Learn_Japanese_with_Music/app/src/main/java/com/learn_japanese_with_music/MainActivity.kt)
- 將 `ModalNavigationDrawer` 與 `DrawerState` 移入 `MainActivity`。
- 新增 `currentScreen` 狀態來控制顯示 `LyricPage` 還是 `SettingsPage`。
- 在 `DrawerContent` 中實作切換邏輯。

### 3. 建立與調整 UI 頁面 (`features`)

#### [NEW] [SettingsPage.kt](file:///C:/Users/fuala/AndroidStudioProjects/Learn_Japanese_with_Music/app/src/main/java/com/learn_japanese_with_music/features/settings/ui/SettingsPage.kt)
- 實作設定介面：
    - 頂部包含 `HomeRectangleButton` 用於開啟側邊欄。
    - **Syntax Analyzer Mode**：RadioButton 切換 A/B/C。
    - **API Key**：TextField 修改 Token。
    - 實作自動儲存至 `SettingsManager`。

#### [MODIFY] [LyricPage.kt](file:///C:/Users/fuala/AndroidStudioProjects/Learn_Japanese_with_Music/app/src/main/java/com/learn_japanese_with_music/features/lyrics/ui/LyricPage.kt)
- 移除 `ModalNavigationDrawer` 相關程式碼，使其轉變為純粹的「內容頁面」。
- 接受 `onMenuClick` 回呼，供其 `HomeRectangleButton` 呼叫。

### 4. 基礎設施整合 (`core/network`)

#### [MODIFY] [RetrofitClient.kt](file:///C:/Users/fuala/AndroidStudioProjects/Learn_Japanese_with_Music/app/src/main/java/com/learn_japanese_with_music/core/network/RetrofitClient.kt)
- 更新攔截器，使其在發起請求前從 `SettingsManager` 讀取最新的 Token。

## 驗證計畫

### 手動驗證
1.  **全域側邊欄**：確認在「搜尋頁」與「設定頁」都能透過左上角按鈕開啟同一個側邊欄。
2.  **頁面切換**：在側邊欄點擊「Search」或「Setting」，主畫面應正確切換且側邊欄自動關閉。
3.  **設定持久化**：在設定頁修改 Token 並重啟 App，確認設定值有被保留。
4.  **功能連動**：修改 Token 後，搜尋功能應使用新 Token 進行請求。
