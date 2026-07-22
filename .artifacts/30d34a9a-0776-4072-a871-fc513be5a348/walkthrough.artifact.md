# Walkthrough - Sudachi 分詞模式切換功能

本任務已成功實作 Sudachi 分詞模式（SplitMode A, B, C）的即時切換功能。使用者現在可以根據學習需求，調整歌詞單字的拆分細緻度。

## 變更內容

### 1. 資料模型與 Repository 優化
- **[LyricsModels.kt](file:///C:/Users/fuala/AndroidStudioProjects/Learn_Japanese_with_Music/app/src/main/java/com/learn_japanese_with_music/features/lyrics/model/LyricsModels.kt)**：在 `SongData` 中新增了 `rawLyrics` 欄位，保留過濾後的原始文字行。
- **[LyricsRepository.kt](file:///C:/Users/fuala/AndroidStudioProjects/Learn_Japanese_with_Music/app/src/main/java/com/learn_japanese_with_music/features/lyrics/repository/LyricsRepository.kt)**：抓取歌詞時會同步儲存這些原始文字，以便在不重新請求網路的情況下進行本地重新分詞。

### 2. 分詞處理器核心更新
- **[JapaneseProcessor.kt](file:///C:/Users/fuala/AndroidStudioProjects/Learn_Japanese_with_Music/app/src/main/java/com/learn_japanese_with_music/features/lyrics/processor/JapaneseProcessor.kt)**：`processLine` 方法現在支援傳入 `Tokenizer.SplitMode` 參數，允許呼叫端決定拆分邏輯。

### 3. UI 介面與互動
- **[LyricPage.kt](file:///C:/Users/fuala/AndroidStudioProjects/Learn_Japanese_with_Music/app/src/main/java/com/learn_japanese_with_music/features/lyrics/ui/LyricPage.kt)**：
    - 在歌詞顯示區域上方新增了 **`SingleChoiceSegmentedButtonRow`**，提供 A、B、C 三種模式的切換按鈕。
    - 使用 **`LaunchedEffect`** 監聽模式變化。一旦使用者切換模式，系統會立即使用 `JapaneseProcessor` 對 `rawLyrics` 進行重新處理，並平滑地更新 UI。

## 模式說明回顧
- **Mode A**: 拆分最細（如將「國立國會圖書館」拆為三個詞），適合精讀單字。
- **Mode B**: 中間長度。
- **Mode C**: 預設模式，儘量保持長單詞（如複合名詞不拆分）。

## 驗證結果
- [x] Gradle 編譯通過。
- [x] 成功實作 UI 切換控制項。
- [x] 切換模式後，歌詞內容會即時重新拆分，且點擊互動依然有效。

> [!TIP]
> **學習小撇步**
> 對於初學者，建議使用 **Mode A** 來學習構成長名詞的基礎單字；對於已經有基礎的學習者，**Mode C** 能提供更流暢的閱讀體驗。
