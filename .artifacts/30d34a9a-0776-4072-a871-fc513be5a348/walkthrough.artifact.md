# Walkthrough - 程式碼架構重構 (Feature-oriented)

本任務已完成將專案結構從簡單的目錄排列重構為以 **Feature (功能相關性)** 為核心的架構。

## 重構後的目錄結構

```text
com.learn_japanese_with_music
├── core/                       (跨功能通用的核心元件)
│   ├── network/                <- RetrofitClient (通用的連線設定)
│   ├── theme/                  <- appTheme, Color, Type
│   └── components/             <- 通用的 UI 組件 (如 HomeRectangleButton)
├── features/                   (獨立的功能模組)
│   └── lyrics/                 (歌詞功能模組)
│       ├── api/                <- GeniusService
│       ├── repository/         <- LyricsRepository
│       ├── processor/          <- JapaneseProcessor
│       ├── model/              <- LyricsModels.kt (包含 SongData, GeniusSong 等)
│       └── ui/                 <- LyricPage.kt, LyricsDisplay.kt
└── MainActivity.kt             (導航與進入點)
```

## 變更細節

### 1. 職責分離與封裝
- **`RetrofitClient`**：從 `GeniusService.kt` 中抽離，歸類到 `core/network` 下，方便未來其他功能模組共用連線邏輯。
- **資料模型統一**：將原本分散的歌詞相關資料模型統一整理至 `features/lyrics/model/LyricsModels.kt`，提升了資料結構的透明度。
- **UI 元件重命名**：將 `Lyrics_ui.kt` 重新命名為 `LyricsDisplay.kt`，更符合其 Composable 函數的名稱與職責。

### 2. 目錄整潔度提升
- 移除了原有的 `lyrics_display/`、`ui/pages/`、`ui/components/` 與 `ui/theme/` 目錄，改以更具邏輯性的 `core/` 與 `features/` 結構呈現。

### 3. 穩定性驗證
- 全域更新了所有受影響檔案的 `package` 宣告與 `import` 語句。
- 已通過 Gradle 編譯測試 (`app:assembleDebug`)，確認專案功能完好。

## 後續維護建議
- 若未來新增功能（例如：使用者播放清單），請在 `features/` 下建立新的目錄（如 `features/playlists/`），遵循相同的 `api`, `repository`, `model`, `ui` 結構。
