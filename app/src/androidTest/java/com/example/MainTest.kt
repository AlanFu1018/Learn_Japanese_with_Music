import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.ImgCard
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

// 1. 🌟 一樣使用 AndroidJUnit4 運行
@RunWith(AndroidJUnit4::class)
class MainTest {

    // 2. 🌟 宣告 Compose 測試規則 (這是取代 ComponentActivity 的關鍵)
    @get:Rule
    val composeTestRule = createComposeRule()

    // 3. 🌟 撰寫標準的測試函數
    @Test
    fun testImgCardDisplay() {

        // 4. 使用 composeTestRule 來設定 UI 內容
        composeTestRule.setContent {
            ImgCard(
                imgUrl = "https://static.vecteezy.com/system/resources/previews/029/796/931/non_2x/music-icon-in-trendy-flat-style-isolated-on-white-background-music-silhouette-symbol-for-your-website-design-logo-app-ui-illustration-eps10-free-vector.jpg",
                modifier = Modifier.padding(1.dp)
            )
        }

        // 🌟 小技巧：因為測試跑完會瞬間關閉，如果你想「用肉眼看到」模擬器上的畫面，
        // 可以暫時讓程式睡個 5 秒鐘 (僅限開發預覽用，正式測試請拿掉)
        Thread.sleep(5000)
    }
}