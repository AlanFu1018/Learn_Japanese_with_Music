# Walkthrough - 修復 Gemini 解析錯誤與 Prompt 優化

本任務解決了 Gemini API 回傳 JSON 時因為結構不匹配導致的解析錯誤，並透過優化 Prompt 與資料流，提升了單字分析的品質。

## 變更內容

### 1. 修復 JSON 解析錯誤 (Fix JSON Mismatch)
- **錯誤原因**：原本代碼預期 `common_usages` 是字串列表 (`List<String>`)，但 Gemini 回傳的是包含日文、讀音、翻譯的物件列表。
- **修正**：
    - 更新了 [WordAnalysis.kt](file:///C:/Users/fuala/AndroidStudioProjects/Learn_Japanese_with_Music/app/src/main/java/com/learn_japanese_with_music/features/vocabulary/model/WordAnalysis.kt)，新增了 `UsageExample` 資料類別。
    - 在 [VocabularyCard.kt](file:///C:/Users/fuala/AndroidStudioProjects/Learn_Japanese_with_Music/app/src/main/java/com/learn_japanese_with_music/features/vocabulary/ui/VocabularyCard.kt) 中使用 Gson 進行正確的序列化與反序列化。

### 2. Prompt 工程與詞性整合 (Prompt Optimization)
- **本地詞性優先**：修改了 [JapaneseProcessor.kt](file:///C:/Users/fuala/AndroidStudioProjects/Learn_Japanese_with_Music/app/src/main/java/com/learn_japanese_with_music/features/lyrics/processor/JapaneseProcessor.kt)，從 Sudachi 直接提取準確的詞性列表。
- **優化提示詞**：在 [GeminiAnalyzer.kt](file:///C:/Users/fuala/AndroidStudioProjects/Learn_Japanese_with_Music/app/src/main/java/com/learn_japanese_with_music/features/vocabulary/api/GeminiAnalyzer.kt) 中，將 Sudachi 詞性作為上下文傳遞給 Gemini，並要求：
    - 釋義必須貼合歌詞語境。
    - 例句必須與該單字在歌詞中的用法相關。
    - 不再要求 Gemini 解釋詞性，改由本地直接顯示。

### 3. 設定功能擴充 (Dynamic Model Selection)
- **自定義模型**：在 [SettingsPage.kt](file:///C:/Users/fuala/AndroidStudioProjects/Learn_Japanese_with_Music/app/src/main/java/com/learn_japanese_with_music/features/settings/ui/SettingsPage.kt) 新增了 Gemini Model 名稱輸入框。
- **預設值更新**：預設模型設為 `gemini-3.1-flash-lite`，並透過 [SettingsManager.kt](file:///C:/Users/fuala/AndroidStudioProjects/Learn_Japanese_with_Music/app/src/main/java/com/learn_japanese_with_music/core/data/SettingsManager.kt) 進行儲存。

### 4. 資料庫架構同步
- 更新了 [WordCache.kt](file:///C:/Users/fuala/AndroidStudioProjects/Learn_Japanese_with_Music/app/src/main/java/com/learn_japanese_with_music/core/data/database/WordCache.kt)，將結構化的例句存儲為 JSON 字串，確保快取讀取後的資料完整性。

## 驗證結果
- [x] 成功修復 `Expected a string but was BEGIN_OBJECT` 報錯。
- [x] 單字卡現在能漂亮地顯示帶有讀音的例句。
- [x] 詞性顯示與 Sudachi 分詞結果保持一致。
- [x] 在 Settings 修改模型名稱後，分析請求能正確發送到指定模型。

> [!TIP]
> **分析品質提升**
> 由於我們傳遞了 Sudachi 提供的精準詞性給 Gemini，現在 AI 產出的「一般意思」與「歌詞用法」會更加精確，不會再出現詞性判斷錯誤的情況。
