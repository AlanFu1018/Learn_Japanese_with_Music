# 程式碼架構重構與邏輯分類計畫

本計畫旨在透過分層架構（Layered Architecture）重新組織專案結構，提升程式碼的可讀性、可維護性及擴充性。

## 重構目標

將目前的程式碼依據邏輯職責分為 **Data (資料層)**, **Domain (領域層)** 與 **UI (表示層)**。

## 預計調整後的目錄結構

```text
com.learn_japanese_with_music
├── data/                       (資料存取與網路通訊)
│   ├── api/                    <- GeniusService.kt (包含 RetrofitClient)
│   ├── model/                  <- API 專用資料模型 (如 GeniusResponse)
│   └── repository/             <- LyricsRepository.kt
├── domain/                     (核心業務邏輯與通用模型)
│   ├── model/                  <- 核心資料模型 (SongData, LyricLine, LyricSegment)
│   └── processor/              <- JapaneseProcessor.kt (分詞處理邏輯)
├── ui/                         (使用者介面與元件)
│   ├── lyrics/                 <- 歌詞顯示相關 UI (LyricsDisplay)
│   ├── search/                 <- 搜尋頁面 UI (LyricPage)
│   ├── components/             <- 通用自定義元件 (HomeButton)
│   └── theme/                  <- 樣式、顏色與字型設定
└── MainActivity.kt             (應用程式進入點)
```

## 建議的變更內容

### 1. 模型遷移與合併
- 將分散在各檔案的 `data class` 依照職責重新歸類。
- 例如：`SongData`, `LyricLine`, `LyricSegment` 搬移至 `domain.model`。

### 2. 功能元件歸類
- `GeniusService` 與其相關網路設定遷移至 `data.api`。
- `JapaneseProcessor` 遷移至 `domain.processor`。
- `LyricsRepository` 遷移至 `data.repository`。

### 3. UI 元件組織
- 將 `Lyrics_ui.kt` 內容重命名或遷移至 `ui.lyrics`。
- 將 `LyricPage.kt` 遷移至 `ui.search`。

### 4. 全域 Import 更新
- 修改所有受影響檔案的 `package` 宣告與 `import` 路徑。

## 驗證計畫

### 自動化測試
- 執行 `gradle_build("app:assembleDebug")` 確保重構後專案仍能正確編譯。

### 手動驗證
- 啟動 App，確認搜尋與歌詞顯示功能與重構前一致。
- 檢查專案目錄結構是否符合計畫。
