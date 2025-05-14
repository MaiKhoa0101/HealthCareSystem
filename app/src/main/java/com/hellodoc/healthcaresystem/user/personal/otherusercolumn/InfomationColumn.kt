package com.hellodoc.healthcaresystem.user.personal.otherusercolumn

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hellodoc.healthcaresystem.R
import com.hellodoc.healthcaresystem.responsemodel.GetDoctorResponse
import com.hellodoc.healthcaresystem.responsemodel.modeluser.ContentTitle
import com.hellodoc.healthcaresystem.responsemodel.modeluser.Contents
import com.hellodoc.healthcaresystem.responsemodel.modeluser.Images
import java.text.DecimalFormat
import java.text.NumberFormat
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import coil.compose.rememberAsyncImagePainter

fun formatPrice(price: Int): String {
    val formatter: NumberFormat = DecimalFormat("#,###")
    return formatter.format(price) + "đ"
}
@Composable
fun ViewIntroduce(
    doctor: GetDoctorResponse?,
    onImageClick: (String) -> Unit
){
    println("Đang ở trang thông tin với service là: ${doctor?.services ?: "ko co"}")
    Introduce(
        contentTitle = ContentTitle(
            introduce = "Giới thiệu",
            certificate = "Bằng cấp & chứng chỉ",
            workplace = "Địa chỉ",
            service = "Dịch vụ & Giá cả",
        ),
        contents = Contents(
            introduce = doctor?.description ?: "Chưa cập nhật giới thiệu",
            certificate = doctor?.certificates ?: "Chưa cập nhật bằng cấp",
            workplace = doctor?.address ?: "Chưa cập nhật nơi làm việc",
            services = doctor?.services ?: emptyList()
        ),
        images = Images(
            image1 = R.drawable.image_certif,
            image2 = R.drawable.image_certif_2,
            image3 = R.drawable.clarifymanage
        ),
        onImageClick
    )

}
@Composable
fun Introduce(
    contentTitle: ContentTitle,
    contents: Contents,
    images: Images,
    onImageClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor = Color.White

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(color = backgroundColor)
            .padding(16.dp)
    ) {
        Text(
            text = contentTitle.introduce,
            style = TextStyle(
                fontWeight = FontWeight.SemiBold,
                fontSize = 24.sp,
                color = Color(0xFF242760)
            )
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = contents.introduce,
            style = TextStyle(
                fontWeight = FontWeight.Medium,
                fontSize = 15.sp,
                color = Color.Black
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = contentTitle.certificate,
            style = TextStyle(
                fontWeight = FontWeight.SemiBold,
                fontSize = 24.sp,
                color = Color(0xFF242760)
            )
        )

        Spacer(modifier = Modifier.height(10.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painterResource(id = images.image1),
                contentDescription = null,
                modifier = Modifier.size(27.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = contents.certificate,
                style = TextStyle(
                    fontWeight = FontWeight.Medium,
                    fontSize = 15.sp,
                    color = Color(0xFF242760)
                )
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painterResource(id = images.image2),
                contentDescription = null,
                modifier = Modifier.size(27.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = contents.certificate,
                style = TextStyle(
                    fontWeight = FontWeight.Medium,
                    fontSize = 15.sp,
                    color = Color(0xFF242760)
                )
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = contentTitle.workplace,
            style = TextStyle(
                fontWeight = FontWeight.SemiBold,
                fontSize = 24.sp,
                color = Color(0xFF242760)
            )
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = contents.workplace,
            style = TextStyle(
                fontWeight = FontWeight.Medium,
                fontSize = 15.sp,
                color = Color.Black,
                textAlign = TextAlign.Justify
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = contentTitle.service,
            style = TextStyle(
                fontWeight = FontWeight.SemiBold,
                fontSize = 24.sp,
                color = Color(0xFF242760)
            )
        )

        Spacer(modifier = Modifier.height(10.dp))

        contents.services.forEach { service ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = R.drawable.clarifymanage),
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = service.specialtyName,
                            style = TextStyle(
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = Color(0xFF242760)
                            )
                        )
                    }

                    Text(
                        text = "Giá: ${formatPrice(service.minprice.toInt())} - ${formatPrice(service.maxprice.toInt())}",
                        style = TextStyle(fontSize = 14.sp, color = Color.DarkGray)
                    )
                }

                Spacer(modifier = Modifier.height(5.dp))

                LazyRow {
                    items(service.imageService) { imageUrl ->
                        Image(
                            painter = rememberAsyncImagePainter(imageUrl),
                            contentDescription = "Hinh anh dich vu",
                            modifier = Modifier
                                .padding(end = 8.dp)
                                .size(120.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .clickable {
                                    onImageClick(imageUrl)
                                },
                        )
                    }
                }
            }
        }
    }
}



