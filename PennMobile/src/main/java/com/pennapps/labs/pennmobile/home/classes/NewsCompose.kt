package com.pennapps.labs.pennmobile.home.classes

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter

@Composable
fun NewsComposableComponent(
    article: Article,
    modifier: Modifier = Modifier,
) {
    val uriHandler = LocalUriHandler.current
    var url = "https://www.thedp.com/"
    article.articleUrl?.let {
        url = article.articleUrl
    }

    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .padding(16.dp)
                .clip(RoundedCornerShape(16.dp))
                .clickable { uriHandler.openUri(url) },
    ) {
        Box(
            modifier =
                Modifier
                    .weight(1f)
                    .fillMaxWidth(),
        ) {
            Image(
                painter = rememberAsyncImagePainter(article.imageUrl),
                contentDescription = "News Image",
                contentScale = ContentScale.Crop,
                modifier =
                    Modifier
                        .matchParentSize()
                        .fillMaxWidth(),
            )
        }
        Box(
            modifier =
                Modifier
                    .fillMaxWidth(),
        ) {
            Image(
                painter = rememberAsyncImagePainter(article.imageUrl),
                contentDescription = "News Image",
                contentScale = ContentScale.Crop,
                alignment = Alignment.TopCenter,
                modifier =
                    Modifier
                        .matchParentSize()
                        .blur(16.dp)
                        .fillMaxWidth(),
            )
            Box(
                modifier =
                    Modifier
                        .matchParentSize()
                        .background(Color.Black.copy(alpha = 0.5f)),
            )
            Box(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text(text = " THE DAILY PENNSYLVANIAN", color = Color.LightGray)
                        Spacer(Modifier.weight(1f))

                        var time = "1 hour ago"
                        article.timestamp?.let {
                            time = article.timestamp
                        }
                        Text(time, color = Color.LightGray)
                    }

                    var title = "sample title here"
                    article.title?.let {
                        title = article.title
                    }

                    Text(
                        text = title,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                    )

                    var description = "sample description here"
                    article.subtitle?.let {
                        description = article.subtitle
                    }
                    Text(text = description, color = Color.White)
                }
            }
        }
    }
}
