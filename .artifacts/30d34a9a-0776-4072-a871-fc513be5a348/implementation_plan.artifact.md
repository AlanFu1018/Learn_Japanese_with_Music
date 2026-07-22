# 新增單字卡功能計畫 (Feature: Vocabulary Card)

本計畫旨在新增單字卡功能。當使用者點擊歌詞中的某個單字時，會彈出一個顯示該單字詳細資訊（目前僅顯示單字本身）的卡片。

## 建議的變更

### 1. 建立新功能目錄：`features/vocabulary`

#### [NEW] [VocabularyCard.kt](file:///C:/Users/fuala/AndroidStudioProjects/Learn_Japanese_with_Music/app/src/main/java/com/learn_japanese_with_music/features/vocabulary/ui/VocabularyCard.kt)
- 實作一個 `VocabularyCard` Composable。
- 目前僅包含一個顯示所選單字與其讀音的 UI (可以使用 `ModalBottomSheet`)。

### 2. 更新歌詞顯示邏輯以支援點擊

#### [MODIFY] [LyricsDisplay.kt](file:///C:/Users/fuala/AndroidStudioProjects/Learn_Japanese_with_Music/app/src/main/java/com/learn_japanese_with_music/features/lyrics/ui/LyricsDisplay.kt)
- 在 `LyricLineDisplay` 中，為每個單字（`LyricSegment`）新增點擊事件。
- 透過回呼（Callback）將點擊的單字傳遞給上層。
- 在 `LyricsDisplay` 中接收此回呼並繼續向上傳遞。

### 3. 在主頁面整合單字卡

#### [MODIFY] [LyricPage.kt](file:///C:/Users/fuala/AndroidStudioProjects/Learn_Japanese_with_Music/app/src/main/java/com/learn_japanese_with_music/features/lyrics/ui/LyricPage.kt)
- 新增狀態管理以追蹤目前被點擊的單字 (`selectedSegment`)。
- 使用 Material 3 的 `ModalBottomSheet` 或簡單的介面切換來顯示 `VocabularyCard`。

## 驗證計畫

### 手動驗證
1. 進入歌詞顯示畫面。
2. 點擊歌詞中的任意單字（例如「君」）。
3. 確認底部彈出單字卡，並正確顯示「君」及其讀音。
4. 點擊背景或收合按鈕，確認單字卡能正常收起。
