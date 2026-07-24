# Walkthrough - Genius 搜尋優化與無限捲動功能

本任務已完成 Genius API 搜尋結果的品質優化，並實作了分頁加載（無限捲動）功能，提供更流暢的找歌體驗。

## 變更內容

### 1. 搜尋結果品質提升
- **過濾雜訊**：在 [LyricsRepository.kt](file:///C:/Users/fuala/AndroidStudioProjects/Learn_Japanese_with_Music/app/src/main/java/com/learn_japanese_with_music/features/lyrics/repository/LyricsRepository.kt) 中實作了過濾邏輯，自動排除 `Genius Romanizations` 帳號發布的純拼音歌詞結果，讓搜尋清單更精準。
- **日文優先排序**：新增了日文字元偵測演算法。現在系統會自動將標題或歌手包含**日文（假名或漢字）**的歌曲排列在搜尋結果的最前方。

### 2. 無限捲動 (Infinite Scroll)
- **API 分頁支援**：更新了 [GeniusService.kt](file:///C:/Users/fuala/AndroidStudioProjects/Learn_Japanese_with_Music/app/src/main/java/com/learn_japanese_with_music/features/lyrics/api/GeniusService.kt)，讓搜尋請求支援 `page` 參數。
- **自動加載邏輯**：在 [LyricPage.kt](file:///C:/Users/fuala/AndroidStudioProjects/Learn_Japanese_with_Music/app/src/main/java/com/learn_japanese_with_music/features/lyrics/ui/LyricPage.kt) 中監測滑動位置。當使用者滑動到接近列表底部時，系統會自動在背景發起下一頁的請求，並將新結果附加到清單中。
- **UI 狀態同步**：加入了 `currentPage` 與 `isFullListLoaded` 狀態管理，確保加載過程平滑且不會重複請求。

### 3. 視覺反饋
- 在搜尋結果底部新增了載入指示器（CircularProgressIndicator），讓使用者在分頁加載時獲得明確的視覺反饋。

## 驗證結果
- [x] Gradle 編譯成功。
- [x] 搜尋知名歌曲（如 "Radwimps"）時，日文原文版本會排在最前面。
- [x] 向下滑動可持續載入更多歌曲，直到沒有更多結果。
- [x] 成功排除拼音版的無效結果。

> [!TIP]
> **搜尋小秘訣**
> 如果您搜尋不到特定的日文歌，可以嘗試輸入日文關鍵字，由於現在有了分頁功能，您可以更輕鬆地在長清單中找到目標！
