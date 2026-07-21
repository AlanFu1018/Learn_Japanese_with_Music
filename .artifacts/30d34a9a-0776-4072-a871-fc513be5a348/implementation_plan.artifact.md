# 修復編譯錯誤：Unresolved reference 'BuildConfig'

本計畫旨在修復 `GeniusService.kt` 中無法找到 `BuildConfig` 的編譯錯誤。

## 發現的問題

`GeniusService.kt` 位於 `com.learn_japanese_with_music.lyrics_display` 套件中，而自動生成的 `BuildConfig` 位於 `com.learn_japanese_with_music` 套件下。因此需要明確導入才能存取 `GENIUS_API_TOKEN`。

## 建議的變更

#### [MODIFY] [GeniusService.kt](file:///C:/Users/fuala/AndroidStudioProjects/Learn_Japanese_with_Music/app/src/main/java/com/learn_japanese_with_music/lyrics_display/GeniusService.kt)
- 新增 `import com.learn_japanese_with_music.BuildConfig`。

## 驗證計畫

### 自動化測試
- 執行 `gradle_build("app:assembleDebug")` 確認編譯成功。

### 手動驗證
- 部署 App 並測試歌詞搜尋功能，確認 API Token 正確注入。
