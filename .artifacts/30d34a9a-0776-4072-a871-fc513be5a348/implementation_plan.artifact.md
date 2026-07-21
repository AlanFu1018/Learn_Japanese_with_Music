# 擴充 Genius API 資料模型與搜尋結果 UI 優化

本計畫旨在更新 `GeniusService` 的資料模型以取得歌曲圖片，並重新設計搜尋結果介面為 1 列 2 個卡片的網格佈局。

## 建議的變更

### 1. 資料模型更新

#### [MODIFY] [GeniusService.kt](file:///C:/Users/fuala/AndroidStudioProjects/Learn_Japanese_with_Music/app/src/main/java/com/learn_japanese_with_music/lyrics_display/GeniusService.kt)
- 在 `GeniusSong` 資料類別中新增 `song_art_image_thumbnail_url: String`。

#### [MODIFY] [LyricsRepository.kt](file:///C:/Users/fuala/AndroidStudioProjects/Learn_Japanese_with_Music/app/src/main/java/com/learn_japanese_with_music/lyrics_display/LyricsRepository.kt)
- 更新 `searchSongs` 回傳的資料結構。
- 在 `fetchLyricsFromUrl` 中，若 Jsoup 抓取圖片失敗，可考慮使用 `GeniusSong` 中的網址作為備援（需調整參數傳遞）。

### 2. UI 介面優化

#### [MODIFY] [LyricPage.kt](file:///C:/Users/fuala/AndroidStudioProjects/Learn_Japanese_with_Music/app/src/main/java/com/learn_japanese_with_music/ui/pages/LyricPage.kt)
- **搜尋結果列表**：將 `LazyColumn` 替換為 `LazyVerticalGrid` (或使用 `FlowRow` 的替代方案)，設定為 1 列顯示 2 個卡片。
- **卡片設計 (`SearchResultItem`)**：
    - 使用 `Box` 佈局。
    - 背景為歌曲圖片 (使用 `AsyncImage`)。
    - 底部疊加半透明層，顯示歌曲標題與歌手。
    - 設定適當的 Aspect Ratio（如 1:1）。

## 驗證計畫

### 自動化測試
- 執行 `gradle_build("app:assembleDebug")` 確認編譯成功。

### 手動驗證
- 執行 App，搜尋「前前前世」。
- 確認搜尋結果以 2 行網格顯示，且每張卡片都有對應的歌曲封面圖。
- 點擊卡片確認能正確載入歌詞。
