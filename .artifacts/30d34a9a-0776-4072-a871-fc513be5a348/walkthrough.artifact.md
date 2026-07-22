# Walkthrough - 新增單字卡功能 (Vocabulary Card)

本任務已成功實作單字卡功能。使用者現在可以點擊歌詞中的單字來查看詳細資訊。

## 變更內容

### 1. 新功能模組：`features/vocabulary`
- 建立了 `features/vocabulary/ui/VocabularyCard.kt`，其中包含 `VocabularyCardContent` 組件。目前專注於清晰地顯示被點擊的單字及其讀音。

### 2. 歌詞互動優化
- 修改了 [LyricsDisplay.kt](file:///C:/Users/fuala/AndroidStudioProjects/Learn_Japanese_with_Music/app/src/main/java/com/learn_japanese_with_music/features/lyrics/ui/LyricsDisplay.kt)，為每個日文單字（`LyricSegment`）新增了 `clickable` 修飾符。
- 實作了回呼機制，將點擊事件從 `LyricLineDisplay` 向上傳遞至主頁面。

### 3. 單字卡彈出介面
- 在 [LyricPage.kt](file:///C:/Users/fuala/AndroidStudioProjects/Learn_Japanese_with_Music/app/src/main/java/com/learn_japanese_with_music/features/lyrics/ui/LyricPage.kt) 中整合了 Material 3 的 `ModalBottomSheet`。
- 使用 `selectedSegment` 狀態來追蹤目前被選中的單字，並在點擊時觸發單字卡的顯示。

## 驗證結果
- [x] Gradle 編譯成功。
- [x] 點擊歌詞單字可正常觸發底部彈窗。
- [x] 彈窗內能正確顯示該單字的漢字與讀音。

> [!TIP]
> **未來擴充**
> 目前單字卡僅顯示基本資訊。未來可以輕鬆地在 `VocabularyCardContent` 中加入釋義、例句或「加入單字本」的按鈕，而不需要改動歌詞顯示邏輯。
