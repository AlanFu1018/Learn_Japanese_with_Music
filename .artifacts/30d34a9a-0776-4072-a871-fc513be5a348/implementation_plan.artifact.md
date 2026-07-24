# 優化 Genius API 搜尋結果與實作分頁加載計畫

根據 `fix.txt` 的指示，本計畫將優化搜尋結果的品質（優先顯示日文歌曲、過濾羅馬拼音帳號），並實作無限捲動（Infinite Scroll）功能以支援自動更新更多搜尋結果。

## 建議的變更內容

### 1. API 介面擴充

#### [MODIFY] [GeniusService.kt](file:///C:/Users/fuala/AndroidStudioProjects/Learn_Japanese_with_Music/app/src/main/java/com/learn_japanese_with_music/features/lyrics/api/GeniusService.kt)
- 更新 `searchSongs` 函式，新增 `page` (頁碼) 與 `per_page` (每頁數量) 參數。

### 2. 資料層邏輯優化

#### [MODIFY] [LyricsRepository.kt](file:///C:/Users/fuala/AndroidStudioProjects/Learn_Japanese_with_Music/app/src/main/java/com/learn_japanese_with_music/features/lyrics/repository/LyricsRepository.kt)
- **過濾邏輯**：過濾掉 `primary_artist.name == "Genius Romanizations"` 的結果。
- **優先級排序**：實作簡單的日文偵測邏輯。若標題或歌手包含日文字元（假名或漢字），則優先排在前面。
- **分頁支援**：`searchSongs` 現在應接受 `page` 參數。

### 3. UI 介面與導航優化

#### [MODIFY] [LyricPage.kt](file:///C:/Users/fuala/AndroidStudioProjects/Learn_Japanese_with_Music/app/src/main/java/com/learn_japanese_with_music/features/lyrics/ui/LyricPage.kt)
- **狀態管理**：
    - 新增 `currentPage` 追蹤目前載入到第幾頁。
    - 新增 `isFullListLoaded` 標記是否已無更多結果。
    - 新增 `isMoreLoading` 處理分頁載入時的讀取狀態。
- **無限捲動實作**：
    - 監測 `LazyVerticalGrid` 的捲動狀態。
    - 當使用者滑動到底部且尚未到達最後一頁時，自動觸發 `repository.searchSongs(query, currentPage + 1)`。
- **結果處理**：新抓取的結果應「附加（Append）」到現有的 `searchResults` 列表末尾，而非替換。

## 驗證計畫

### 手動驗證
1.  **過濾驗證**：搜尋知名日文歌（如 "Lemon"），確認搜尋結果中不會出現 "Genius Romanizations" 帳號發布的拼音版本。
2.  **優先級驗證**：搜尋關鍵字時，包含日文原文的結果應出現在列表最上方。
3.  **無限捲動驗證**：滑動到搜尋結果底部，確認會出現載入動畫並自動載入更多卡片。
4.  **穩定性測試**：快速捲動並頻繁搜尋，確認不會出現重複資料或 UI 崩潰。

> [!NOTE]
> **關於優先級排序**
> 由於 Genius API 本身不支援語言篩選，我們會對每一頁抓回來的 20 筆資料進行本地排序。這能保證每頁最相關的日文內容會優先顯示在使用者面前。
