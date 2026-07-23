# 修復 Gemini 分析 JSON 錯誤與 Prompt 優化計畫

根據 `fix.txt` 的指示，本計畫將修復 JSON 解析錯誤，優化 Gemini 的分析提示詞，並擴充設定頁面以支援自定義 Gemini 模型。

## 建議的變更內容

### 1. 資料模型更新

#### [MODIFY] [LyricsModels.kt](file:///C:/Users/fuala/AndroidStudioProjects/Learn_Japanese_with_Music/app/src/main/java/com/learn_japanese_with_music/features/lyrics/model/LyricsModels.kt)
- **`LyricSegment`**：新增 `partOfSpeech: List<String>` 欄位，儲存從 Sudachi 取得的詞性。

#### [MODIFY] [WordAnalysis.kt](file:///C:/Users/fuala/AndroidStudioProjects/Learn_Japanese_with_music/app/src/main/java/com/learn_japanese_with_music/features/vocabulary/model/WordAnalysis.kt)
- 新增 **`UsageExample`** 資料類別，包含 `japanese`, `reading`, `translation`。
- 更新 **`WordAnalysis`**，將 `commonUsages` 的類型從 `List<String>` 改為 `List<UsageExample>`。
- 移除 `partOfSpeech` 欄位（改由 Sudachi 提供）。

### 2. 分詞邏輯優化

#### [MODIFY] [JapaneseProcessor.kt](file:///C:/Users/fuala/AndroidStudioProjects/Learn_Japanese_with_Music/app/src/main/java/com/learn_japanese_with_music/features/lyrics/processor/JapaneseProcessor.kt)
- 在 `processLine` 中，從 `morpheme.partOfSpeech()` 提取詞性列表並存入 `LyricSegment`。

### 3. 設定功能擴充

#### [MODIFY] [SettingsManager.kt](file:///C:/Users/fuala/AndroidStudioProjects/Learn_Japanese_with_Music/app/src/main/java/com/learn_japanese_with_music/core/data/SettingsManager.kt)
- 新增 `geminiModel` 屬性，預設值為 `gemini-1.5-flash` (或 `gemini-3.1-flash-lite`)。

#### [MODIFY] [SettingsPage.kt](file:///C:/Users/fuala/AndroidStudioProjects/Learn_Japanese_with_Music/app/src/main/java/com/learn_japanese_with_music/features/settings/ui/SettingsPage.kt)
- 新增一個 `OutlinedTextField` 供使用者輸入 **Gemini Model Name**。

### 4. Gemini 分析與快取層優化

#### [MODIFY] [GeminiAnalyzer.kt](file:///C:/Users/fuala/AndroidStudioProjects/Learn_Japanese_with_Music/app/src/main/java/com/learn_japanese_with_music/features/vocabulary/api/GeminiAnalyzer.kt)
- 建構子現在應接受 `modelName`。
- 更新 `analyzeWord` 方法：
    - 接收 `pos: List<String>` 作為參數。
    - **優化 Prompt**：
        - 告知 Gemini 該單字的詞性（由 Sudachi 提供）。
        - 要求 `general_meaning` 貼近歌詞意境。
        - 要求 `common_usages` 需與歌詞中的用法相關，且必須回傳包含 `japanese`, `reading`, `translation` 的 JSON 物件。
        - 移除 JSON 中的 `part_of_speech` 請求。

#### [MODIFY] [WordCache.kt](file:///C:/Users/fuala/AndroidStudioProjects/Learn_Japanese_with_Music/app/src/main/java/com/learn_japanese_with_music/core/data/database/WordCache.kt)
- 更新 Entity 結構，移除 `partOfSpeech`（由本地 `LyricSegment` 即時提供，不需快取）。

#### [MODIFY] [VocabularyCard.kt](file:///C:/Users/fuala/AndroidStudioProjects/Learn_Japanese_with_Music/app/src/main/java/com/learn_japanese_with_music/features/vocabulary/ui/VocabularyCard.kt)
- 修改 `VocabularyCardContent` 的顯示邏輯：
    - 從 `segment.partOfSpeech` 直接顯示詞性。
    - 適配新的 `UsageExample` 資料結構進行顯示。

## 驗證計畫

### 手動驗證
1.  **JSON 解析測試**：點擊單字分析，確認不再出現 `Expected a string but was BEGIN_OBJECT` 錯誤。
2.  **Prompt 準確度**：確認單字意思是否更貼合歌詞，且例句與歌詞用法相關。
3.  **模型切換**：在設定頁更改模型名稱，確認分析功能依然運作（或回傳對應模型的錯誤）。
4.  **詞性顯示**：確認顯示的是 Sudachi 原始的詞性分類。
