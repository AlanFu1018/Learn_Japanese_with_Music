# 實作 Sudachi 分詞模式 (SplitMode) 切換功能

本計畫旨在為歌詞頁面新增一個切換按鈕，允許使用者在 Sudachi 的三種分詞模式（Mode A, B, C）之間切換，並即時更新歌詞的拆分效果。

## 建議的變更

### 1. 資料模型更新

#### [MODIFY] [LyricsModels.kt](file:///C:/Users/fuala/AndroidStudioProjects/Learn_Japanese_with_Music/app/src/main/java/com/learn_japanese_with_music/features/lyrics/model/LyricsModels.kt)
- 在 `SongData` 中新增 `rawLyrics: List<String>` 欄位，用於儲存原始未處理的歌詞行，以便在切換模式時重新處理。

### 2. 分詞處理器優化

#### [MODIFY] [JapaneseProcessor.kt](file:///C:/Users/fuala/AndroidStudioProjects/Learn_Japanese_with_Music/app/src/main/java/com/learn_japanese_with_music/features/lyrics/processor/JapaneseProcessor.kt)
- 更新 `processLine` 方法，新增 `mode: Tokenizer.SplitMode` 參數（預設為 `SplitMode.C`）。
- 在呼叫 `currentTokenizer.tokenize(text)` 時傳入該模式。

### 3. Repository 更新

#### [MODIFY] [LyricsRepository.kt](file:///C:/Users/fuala/AndroidStudioProjects/Learn_Japanese_with_Music/app/src/main/java/com/learn_japanese_with_music/features/lyrics/repository/LyricsRepository.kt)
- 更新 `fetchLyricsFromUrl`，在建立 `SongData` 時存入過濾後的原始歌詞行。

### 4. UI 介面更新

#### [MODIFY] [LyricPage.kt](file:///C:/Users/fuala/AndroidStudioProjects/Learn_Japanese_with_Music/app/src/main/java/com/learn_japanese_with_music/features/lyrics/ui/LyricPage.kt)
- 新增 `selectedMode` 狀態（預設為 `Tokenizer.SplitMode.C`）。
- 在歌詞顯示區域上方新增一個切換控制項（如 `TabRow` 或 `SegmentedButton`）。
- 當 `selectedMode` 改變時，使用 `JapaneseProcessor` 重新處理 `rawLyrics` 並更新顯示內容。

## 驗證計畫

### 手動驗證
1. 搜尋並開啟一首歌（如「前前前世」）。
2. 在歌詞上方切換 A、B、C 模式。
3. 觀察「國立國會圖書館」或「前前前世」等複合詞的拆分變化：
   - **Mode A**: 拆分最細。
   - **Mode C**: 保持完整長單詞。
4. 確認點擊新拆分出的單字仍能正確彈出單字卡。
