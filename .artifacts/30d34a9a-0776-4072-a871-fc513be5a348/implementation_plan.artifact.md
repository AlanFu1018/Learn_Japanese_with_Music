# 單字卡收藏功能與單字管理頁面實作計畫

本計畫旨在實作單字卡的「永久收藏」功能，並建立一個專屬的「Word Card」頁面，讓使用者可以分類、搜尋並編輯已收藏的單字與筆記。

## 建議的變更內容

### 1. 資料持久化層 (`core/data/database`)

#### [NEW] [SavedWord.kt](file:///C:/Users/fuala/AndroidStudioProjects/Learn_Japanese_with_Music/app/src/main/java/com/learn_japanese_with_music/core/data/database/SavedWord.kt)
- 建立 `SavedWord` Entity，儲存永久收藏的單字。
- 欄位包含：`word`, `contextLine`, `splitMode` (主鍵組合), `reading`, `partOfSpeech` (JSON), `jlptLevel`, `generalMeaning`, `contextualMeaning`, `commonUsages` (JSON), `notes` (筆記), `songTitle`, `songArtist`, `timestamp`。

#### [NEW] [SavedWordDao.kt](file:///C:/Users/fuala/AndroidStudioProjects/Learn_Japanese_with_Music/app/src/main/java/com/learn_japanese_with_music/core/data/database/SavedWordDao.kt)
- 提供 CRUD 操作：插入、刪除、讀取單一單字、讀取全部、更新筆記。

#### [MODIFY] [AppDatabase.kt](file:///C:/Users/fuala/AndroidStudioProjects/Learn_Japanese_with_Music/app/src/main/java/com/learn_japanese_with_music/core/data/database/AppDatabase.kt)
- 將 `SavedWord` 加入資料庫實體列表。

### 2. 單字卡 UI 優化 (`features/vocabulary/ui`)

#### [MODIFY] [VocabularyCard.kt](file:///C:/Users/fuala/AndroidStudioProjects/Learn_Japanese_with_Music/app/src/main/java/com/learn_japanese_with_music/features/vocabulary/ui/VocabularyCard.kt)
- **載入優先級**：`SavedWord` > `WordCache` > Gemini API。
- **右上角操作**：
    - 若未收藏：顯示 (+) 按鈕，點擊後將資料（含當前歌曲資訊）存入 `SavedWord`。
    - 若已收藏：顯示 (Delete) 按鈕，點擊後移除收藏。
- **筆記功能**：若單字處於已收藏狀態，底部顯示一個可編輯的 `OutlinedTextField` 供輸入筆記，並實作自動儲存。
- **參數擴充**：`VocabularyCardContent` 需額外接收 `songTitle` 與 `artist`。

### 3. Word Card 管理頁面 (`features/vocabulary/ui`)

#### [NEW] [WordCardPage.kt](file:///C:/Users/fuala/AndroidStudioProjects/Learn_Japanese_with_Music/app/src/main/java/com/learn_japanese_with_music/features/vocabulary/ui/WordCardPage.kt)
- **頂部欄**：包含導覽按鈕與搜尋框（支援日文或中文意檢索）。
- **分類選擇器**：提供「全部」、「詞性」、「所屬歌曲」切換按鈕。
- **內容展示**：
    - **全部**：使用 `LazyColumn` 顯示所有單字。
    - **詞性/歌曲**：先以網格卡片（效仿 SearchResult）顯示各個類別。點擊類別後，顯示該類別下的單字清單。
- **點擊行為**：點擊單字卡片應能彈出相同風格的詳情彈窗進行編輯。

### 4. 全域導航更新 (`MainActivity`)

#### [MODIFY] [MainActivity.kt](file:///C:/Users/fuala/AndroidStudioProjects/Learn_Japanese_with_Music/app/src/main/java/com/learn_japanese_with_music/MainActivity.kt)
- 更新 `Screen` enum 加入 `WordCard`。
- 在 Drawer 中新增 "Word Card" 項目與對應圖示。

## 驗證計畫

### 手動驗證
1.  **收藏功能**：在歌詞頁點擊單字分析後，點擊右上方 (+)，確認按鈕切換為刪除圖示。
2.  **筆記驗證**：輸入筆記並關閉彈窗，再次開啟該單字，確認筆記內容已正確讀取。
3.  **頁面分類**：進入 Word Card 頁面，切換不同分類方式，確認歌曲與詞性分組正確。
4.  **搜尋驗證**：在 Word Card 頁面搜尋單字或翻譯關鍵字，確認過濾功能正常。
5.  **跨歌曲引用**：在同一首歌點擊已收藏單字，確認直接載入本地收藏內容（含筆記），不顯示分析動畫。

## Open Questions

> [!IMPORTANT]
> **關於「類別卡片」的圖片**
> 在「所屬歌曲」分類中，是否要顯示該歌曲的封面圖作為類別卡片的背景？
