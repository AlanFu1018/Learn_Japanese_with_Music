# 修復閃退與分詞模式同步問題

本計畫旨在修復 `MainActivity` 的初始化閃退問題，並確保分詞模式的變更能精確反映在 UI 上。

## 發現的問題

> [!CAUTION]
> **1. 初始化閃退 (Crash)**
> 在 `MainActivity` 中，`repository` 屬性在建構時就嘗試存取尚未初始化的 `lateinit var settingsManager`，導致 `UninitializedPropertyAccessException`。

> [!WARNING]
> **2. 模式同步不完全**
> 雖然 UI 傳遞了 `currentMode`，但 `LyricPage` 的重繪邏輯仍依賴讀取 `SettingsManager` 的屬性，且 `LyricsRepository` 內部也持有自己的狀態，這可能導致競爭條件或讀取到舊值。

## 建議的變更內容

### 1. 修復 MainActivity 閃退

#### [MODIFY] [MainActivity.kt](file:///C:/Users/fuala/AndroidStudioProjects/Learn_Japanese_with_Music/app/src/main/java/com/learn_japanese_with_music/MainActivity.kt)
- 將 `repository` 改為 `by lazy` 初始化，確保它在 `settingsManager` 於 `onCreate` 初始化後才被建立。
- 優化 `SettingsPage` 的回呼，確保狀態同步。

### 2. 精確同步分詞模式

#### [MODIFY] [LyricsRepository.kt](file:///C:/Users/fuala/AndroidStudioProjects/Learn_Japanese_with_Music/app/src/main/java/com/learn_japanese_with_music/features/lyrics/repository/LyricsRepository.kt)
- 從建構子中移除 `settingsManager`。
- 修改 `fetchLyricsFromUrl`，讓其顯式接受 `mode: Tokenizer.SplitMode` 參數。

#### [MODIFY] [LyricPage.kt](file:///C:/Users/fuala/AndroidStudioProjects/Learn_Japanese_with_Music/app/src/main/java/com/learn_japanese_with_music/features/lyrics/ui/LyricPage.kt)
- 在 `LaunchedEffect` 中使用傳入的 `currentMode` 進行分詞，而非再次讀取 `settingsManager`。
- 在搜尋點擊處，將 `currentMode` 傳遞給 `repository.fetchLyricsFromUrl`。

## 驗證計畫

### 手動驗證
1. **閃退測試**：啟動 App，確認不再發生閃退並能正常進入搜尋頁面。
2. **模式即時反映**：
   - 搜尋一首歌。
   - 進入 Settings 切換 Mode A。
   - 返回 Search，確認歌詞讀音與拆分已立即改變。
3. **新歌曲抓取**：在 Mode A 下搜尋新歌曲，確認抓取後的初始顯示即為 Mode A 的結果。
