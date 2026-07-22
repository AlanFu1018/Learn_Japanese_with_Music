# 修正 UI 互動問題與歌詞點擊邏輯

本計畫旨在修復兩個 UI 相關的 Bug，提升使用者體驗。

## 建議的變更內容

### 1. 修正側邊欄開啟時鍵盤未收起問題

#### [MODIFY] [LyricPage.kt](file:///C:/Users/fuala/AndroidStudioProjects/Learn_Japanese_with_Music/app/src/main/java/com/learn_japanese_with_music/features/lyrics/ui/LyricPage.kt)
- 在 `HomeRectangleButton` 的 `onClick` 回調中，先呼叫 `focusManager.clearFocus()`。這將確保在側邊欄打開時，輸入框失去焦點並收起鍵盤。

#### [MODIFY] [SettingsPage.kt](file:///C:/Users/fuala/AndroidStudioProjects/Learn_Japanese_with_Music/app/src/main/java/com/learn_japanese_with_music/features/settings/ui/SettingsPage.kt)
- 同樣地，在 `SettingsPage` 引入 `LocalFocusManager` 並在點擊選單按鈕時清除焦點。

### 2. 修正歌詞空白字元可點擊問題

#### [MODIFY] [LyricsDisplay.kt](file:///C:/Users/fuala/AndroidStudioProjects/Learn_Japanese_with_Music/app/src/main/java/com/learn_japanese_with_music/features/lyrics/ui/LyricsDisplay.kt)
- 在 `LyricLineDisplay` 中，修改 `clickable` 修飾符。
- 設定 `enabled = segment.text.isNotBlank()`，確保純空白或空字元的區塊不會觸發點擊事件（不會彈出單字卡）。

## 驗證計畫

### 手動驗證
1. **輸入框與側邊欄**：
   - 點擊搜尋框啟動鍵盤。
   - 點擊左上角 Home 按鈕。
   - 確認鍵盤立即收起，且側邊欄正常開啟。
2. **歌詞空白點擊**：
   - 在歌詞顯示頁面，嘗試點擊單字之間的空白處。
   - 確認不會彈出單字卡。
   - 點擊有內容的單字，確認單字卡依然能正常顯示。
