# Walkthrough - UI 互動優化與歌詞點擊邏輯修正

本任務已完成對 UI 互動細節的優化，修復了側邊欄與鍵盤的同步問題，並微調了歌詞點擊的有效區域。

## 變更內容

### 1. 鍵盤與側邊欄同步優化
- 修改了 [LyricPage.kt](file:///C:/Users/fuala/AndroidStudioProjects/Learn_Japanese_with_Music/app/src/main/java/com/learn_japanese_with_music/features/lyrics/ui/LyricPage.kt) 與 [SettingsPage.kt](file:///C:/Users/fuala/AndroidStudioProjects/Learn_Japanese_with_Music/app/src/main/java/com/learn_japanese_with_music/features/settings/ui/SettingsPage.kt)。
- 在點擊「Home」按鈕開啟側邊欄之前，現在會先呼叫 `focusManager.clearFocus()`。
- **效果**：當您在輸入 Token 或搜尋關鍵字時，點擊選單會立即讓鍵盤收起、游標消失，畫面變得乾淨。

### 2. 歌詞點擊邏輯修正
- 修改了 [LyricsDisplay.kt](file:///C:/Users/fuala/AndroidStudioProjects/Learn_Japanese_with_Music/app/src/main/java/com/learn_japanese_with_music/features/lyrics/ui/LyricsDisplay.kt) 中的 `clickable` 參數。
- 設定 `enabled = segment.text.any { it.isLetterOrDigit() }`。
- **效果**：點擊單字之間的「空白區域」或「標點符號」（如 `。`、`、`、`！` 等）現在不會有任何反應，只有點擊到包含文字或數字的實際單字時才會彈出單字卡。

## 驗證結果
- [x] 點擊 Home 按鈕時，鍵盤與游標會同步消失。
- [x] 歌詞中的空格不再觸發單字卡彈窗。
- [x] 有文字的單字點擊功能依然運作正常。

> [!TIP]
> **體驗改進**
> 這些細小的互動修正能大幅減少使用者操作時的干擾感，特別是在頻繁切換設定與搜尋歌詞時。
