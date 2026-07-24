# Walkthrough - 單字卡收藏功能與管理頁面

本任務成功實作了單字卡的永久收藏功能，並建立了專屬的管理頁面，讓使用者能有效地組織與複習所學單字。

## 變更內容

### 1. 收藏與筆記功能
- **[VocabularyCard.kt](file:///C:/Users/fuala/AndroidStudioProjects/Learn_Japanese_with_Music/app/src/main/java/com/learn_japanese_with_music/features/vocabulary/ui/VocabularyCard.kt)**：
    - **收藏按鈕**：單字卡右上方新增了 (+) 按鈕。點擊後，該單字的分析結果、讀音以及當前歌曲資訊會永久儲存在本地資料庫。
    - **移除收藏**：已收藏的單字會顯示刪除圖示，點擊即可取消收藏。
    - **個人筆記**：收藏後的單字卡底部會出現一個輸入框。您輸入的任何筆記都會在修改時**自動儲存**。

### 2. Word Card 管理頁面
- **[WordCardPage.kt](file:///C:/Users/fuala/AndroidStudioProjects/Learn_Japanese_with_Music/app/src/main/java/com/learn_japanese_with_music/features/vocabulary/ui/WordCardPage.kt)**：
    - **智慧搜尋**：支援透過「日文單字」或「中文釋義」進行快速檢索。
    - **多維度分類**：
        - **全部**：以時間順序顯示所有收藏單字。
        - **詞性**：自動根據單字的詞性（如名詞、動詞）進行分組。
        - **歌曲**：根據單字來源的歌曲進行分類，類別卡片會顯示**歌曲封面圖**作為背景，美觀且易於識別。
    - **互動體驗**：在 Word Card 頁面點擊任何單字，會彈出完整的單字詳情與筆記編輯介面。

### 3. 資料庫架構升級
- **[SavedWord.kt](file:///C:/Users/fuala/AndroidStudioProjects/Learn_Japanese_with_Music/app/src/main/java/com/learn_japanese_with_music/core/data/database/SavedWord.kt)**：新增了永久存儲的資料表。
- **快取優先邏輯**：點擊單字時，系統會優先檢查是否已收藏。如果是已收藏的單字，將**瞬間載入**本地內容，完全不再消耗任何 Gemini API 額度。

### 4. 導航整合
- **側邊欄更新**：在側邊選單中新增了 "Word Card" 選項，方便隨時切換至單字本。

## 驗證結果
- [x] 成功實作單字收藏與移除邏輯。
- [x] 筆記功能可正常輸入並持久化。
- [x] Word Card 頁面分類與搜尋功能運作正常。
- [x] 跨頁面狀態同步（在單字卡收藏後，Word Card 頁面內容會同步更新）。

> [!TIP]
> **個人化學習**
> 建議在收藏單字後，隨手記下在歌詞中看到的特殊用法或感想。這些筆記會與單字緊密結合，成為您專屬的歌詞學習筆記本。
