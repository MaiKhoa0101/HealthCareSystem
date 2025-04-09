package com.hellodoc.healthcaresystem.user.post

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hellodoc.healthcaresystem.R
import com.hellodoc.healthcaresystem.admin.ui.theme.MutedBlack
import com.hellodoc.healthcaresystem.ui.theme.HealthCareSystemTheme


data class Publisher(
    val name: String,
    @DrawableRes val image: Int,
    val job: String
)

@Composable
fun BoxPostCard(){
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.8f)), contentAlignment = Alignment.Center){
        PostCard(
            modifier = Modifier.fillMaxWidth(0.8f),
            image = R.drawable.avarta,
            title = "Shadows & Lightnings",
            text = " Bla bla bla bla  Bla bla bla bla Bla bla bla bla Bla bla bla bla Bla bla bla bla Bla bla bla bla Bla bla bla bla Bla bla bla bla Bla bla bla bla Bla bla bla bla Bla bla bla bla Bla bla bla bla Bla bla bla bla Bla bla bla bla",
            publisher = Publisher(
                name = "Chau Tinh Chi",
                job = "movie actor",
                image = R.drawable.img
            )
        )
    }
}

@Composable
fun PostCard(
    modifier: Modifier = Modifier,
    @DrawableRes image: Int,
    title: String,
    text: String,
    publisher: Publisher
    ) {
    var showFullText by remember {
        mutableStateOf(false)
    }
    Card(
        modifier = modifier,
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = MutedBlack
        )
    ) {
        Column {
            Image(
                modifier = Modifier.fillMaxWidth().height(200.dp),
                painter = painterResource(id= image),
                contentDescription = null,
                contentScale = ContentScale.Crop
            )
            Column(modifier = Modifier.padding(vertical = 20.dp, horizontal = 15.dp)) {
                Text(text = title, color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    modifier = Modifier.clickable {
                        showFullText = !showFullText
                    },
                    text = text,
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    maxLines = if (showFullText) 100 else 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(30.dp))

                Row {
                    Image(
                        modifier = Modifier.size(42.dp).clip(CircleShape),
                        painter = painterResource(id = publisher.image),
                        contentDescription = null
                    )

                    Spacer(modifier = Modifier.width(10.dp))
                    val annotatedString = buildAnnotatedString {
                        withStyle(
                            style = SpanStyle(
                                color = Color.White,
                                fontSize = 18.sp
                            )
                        ){
                            append(publisher.name)
                        }
                        append("\n")
                        withStyle(
                            style = SpanStyle(
                                color = Color.White.copy(alpha = 0.7f),
                                fontSize = 16.sp,
                            )
                        ){
                            append(publisher.job)
                        }
                    }
                    Text(text=annotatedString)
                }
            }
        }
    }
}
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun BoxPostCardPreview() {
    HealthCareSystemTheme {
        BoxPostCard()
    }
}