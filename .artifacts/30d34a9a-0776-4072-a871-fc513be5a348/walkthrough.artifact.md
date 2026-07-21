# Walkthrough - 擴充 Genius API 資料模型與搜尋 UI 優化

本任務已完成 `GeniusService` 資料模型的擴充，並將搜尋結果介面優化為 2 欄位的網格佈局。

## 變更內容

### 1. 資料模型更新
- 在 [GeniusService.kt](file:///C:/Users/fuala/AndroidStudioProjects/Learn_Japanese_with_Music/app/src/main/java/com/learn_japanese_with_music/lyrics_display/GeniusService.kt) 中更新了 `GeniusSong` 資料類別，新增了 `song_art_image_thumbnail_url` 欄位。這使得我們能直接從 API 取得歌曲的縮圖網址。

### 2. 搜尋結果 UI 優化
- 在 [LyricPage.kt](file:///C:/Users/fuala/AndroidStudioProjects/Learn_Japanese_with_Music/app/src/main/java/com/learn_japanese_with_music/ui/pages/LyricPage.kt) 中：
    - 將原本的 `LazyColumn` 替換為 `LazyVerticalGrid`，設定為 **1 列顯示 2 個卡片** (`GridCells.Fixed(2)`)。
    - 重新設計了 `SearchResultItem`：
        - 使用 **歌曲縮圖作為卡片背景**。
        - 底部疊加了 **半透明漸層遮罩**，確保在各種背景下文字都能清晰顯示。
        - 卡片呈現 **1:1 的正方形比例**，外觀更加現代化且一致。
        - 顯示歌曲標題（粗體）與歌手名稱。

## 驗證結果
- [x] Gradle 編譯成功。
- [x] App 成功部署至裝置。
- [x] 搜尋功能正常，搜尋結果以 2 欄網格顯示並帶有歌曲圖片。

> [!TIP]
> **UI 體驗提升**
> 現在搜尋歌曲時，視覺上能更直觀地透過封面圖識別歌曲，且網格佈局提升了單一畫面能顯示的資訊量。
