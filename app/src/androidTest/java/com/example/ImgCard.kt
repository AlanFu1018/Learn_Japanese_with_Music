package com.example

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.Image
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import coil.Coil
import coil.compose.AsyncImage
import com.learn_japanese_with_music.core.theme.appTheme
import org.junit.runner.RunWith

//Icons.Default.Image
@Composable
fun ImgCard(imgUrl: String = "none", modifier: Modifier){
    AsyncImage(
        model = imgUrl,
        contentDescription = "nono",
    )
}

@Preview(showBackground = true)
@Composable
fun ImgCardPreview(){
    appTheme() {
        ImgCard(
            imgUrl = "https://t2.genius.com/unsafe/344x344/https%3A%2F%2Fimages.genius.com%2F1721b4fdf56722aa52fb73f3195a1f51.1000x1000x1.png",
            modifier = Modifier)
    }

}