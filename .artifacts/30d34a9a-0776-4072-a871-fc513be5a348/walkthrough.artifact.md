# Walkthrough - 遷移至 Sudachi-full 日文分詞器

本任務已完成從 `kuromoji-unidic` 到 `Sudachi` (Full 字典) 的遷移。

## 變更內容

### 1. 依賴項更新
- 在 [libs.versions.toml](file:///C:/Users/fuala/AndroidStudioProjects/Learn_Japanese_with_Music/gradle/libs.versions.toml) 中移除 `kuromoji` 並新增 `com.worksap.nlp:sudachi:0.8.0`。
- 在 [app/build.gradle.kts](file:///C:/Users/fuala/AndroidStudioProjects/Learn_Japanese_with_Music/app/build.gradle.kts) 中同步更新依賴。

### 2. 分詞邏輯遷移
- 修改 [JapaneseProcessor.kt](file:///C:/Users/fuala/AndroidStudioProjects/Learn_Japanese_with_Music/app/src/main/java/com/learn_japanese_with_music/lyrics_display/JapaneseProcessor.kt) 以適應 Sudachi 的 `Morpheme` API。
- 使用 `readingForm()` 獲取日文讀音（Furigana）。

### 3. Android 適配與字典載入
- 實作了自動從 `assets` 複製字典到內部存儲的邏輯（因為 Sudachi 需要檔案路徑來進行記憶體映射）。
- 在 [MainActivity.kt](file:///C:/Users/fuala/AndroidStudioProjects/Learn_Japanese_with_Music/app/src/main/java/com/learn_japanese_with_music/MainActivity.kt) 中新增異步初始化邏輯，避免阻塞 UI 執行緒。

### 4. 最低支援版本更新
- 將 `minSdk` 從 24 提升至 **26**。這是因為 Sudachi 內部的記憶體映射實作依賴於 Android 8.0 (API 26) 才引入的 `MethodHandle.invoke`。

### 5. 記憶體效能優化 (Singleton)
- **單例模式**：將 `JapaneseProcessor` 改為單例，確保 120MB 的字典在 App 生命週期內僅載入一次。
- **防止外洩**：初始化時強制使用 `applicationContext`，避免持有 Activity 引用。
- **線程安全**：使用 `AtomicBoolean` 與 `Volatile` 確保多執行緒環境下的初始化安全。
- **預防重複載入**：若 Activity 重建（如旋轉螢幕），新的初始化請求會被攔截，直接使用已載入的字典。

### 6. 編譯錯誤修復
- 修復了 `GeniusService.kt` 無法識別 `BuildConfig` 的問題。透過在 `GeniusService.kt` 中導入正確的 `com.learn_japanese_with_music.BuildConfig` 套件，成功讀取了存放於 `local.properties` 的 API Token。

## 重要提示

> [!CAUTION]
> **手動操作要求**
> 由於 `sudachi-full` 字典體積巨大，未包含在代碼庫中。請務必執行以下操作：
> 1. 下載 Sudachi Full 字典檔 (`system_full.dic`)。
> 2. 將其放置於 `app/src/main/assets/system_full.dic`。
>
> 如果該檔案不存在，App 會在啟動初始化時拋出錯誤，且分詞器將無法正常運作。

## 驗證結果
- [x] Gradle 同步成功。
- [x] `JapaneseProcessor.kt` 編譯無誤。
- [x] `MainActivity.kt` 編譯無誤。
