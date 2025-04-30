package com.hellodoc.healthcaresystem.user.personal.otherusercolumn

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import com.hellodoc.healthcaresystem.R
import com.hellodoc.healthcaresystem.responsemodel.GetDoctorResponse
import com.hellodoc.healthcaresystem.responsemodel.modeluser.ContentTitle
import com.hellodoc.healthcaresystem.responsemodel.modeluser.Contents
import com.hellodoc.healthcaresystem.responsemodel.modeluser.Images
import java.text.DecimalFormat
import java.text.NumberFormat

fun formatPrice(price: Int): String {
    val formatter: NumberFormat = DecimalFormat("#,###")
    return formatter.format(price) + "đ"
}
@Composable
fun ViewIntroduce(
    doctor: GetDoctorResponse?
){
    Introduce(
        contentTitle = ContentTitle(
            introduce = "Giới thiệu",
            certificate = "Bằng cấp & chứng chỉ",
            workplace = "Nơi làm việc",
            service = "Dịch vụ & Giá cả",
        ),
        contents = Contents(
            introduce = doctor?.description ?: "Chưa cập nhật giới thiệu",
            certificate1 = doctor?.certificates?.getOrNull(0) ?: "Chưa cập nhật bằng cấp 1",
            certificate2 = doctor?.certificates?.getOrNull(1) ?: "Chưa cập nhật bằng cấp 2",
            workplace = doctor?.hospital ?: "Chưa cập nhật nơi làm việc",
            services = doctor?.services?.map {
                (it.name ?: "Dịch vụ chưa đặt tên") to (it.price ?: 0)
            } ?: listOf(
                "Dịch vụ khám cơ bản" to 500000
            )
        ),
        images = Images(
            image1 = R.drawable.image_certif,
            image2 = R.drawable.image_certif_2,
            image3 = R.drawable.clarifymanage
        )
    )
}
@Composable
fun Introduce(
    contentTitle: ContentTitle,
    contents: Contents,
    images: Images,
    modifier: Modifier = Modifier
) {
    val backgroundColor = Color.White
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(color = backgroundColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 80.dp)
        ) {
            ConstraintLayout(
                modifier = modifier
                    .padding(top = 5.dp, end = 5.dp)
                    .background(color = backgroundColor, shape = RectangleShape)
                    .fillMaxWidth()
            ) {
                val (iconImage1, iconImage2, tvIntroduce, tvCIntroduce, tvCertif, tvC1Certifi, tvC2Certifi, tvWorkplace, tvCWorkplace, tvService) = createRefs()
                Text(
                    text = contentTitle.introduce,
                    style = TextStyle(
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 24.sp,
                        color = Color(0xFF242760)
                    ),
                    modifier = Modifier.constrainAs(tvIntroduce) {
                        top.linkTo(parent.top, margin = 10.dp)
                        start.linkTo(parent.start, margin = 15.dp)
                    }
                )
                Text(
                    text = contents.introduce,
                    style = TextStyle(
                        fontWeight = FontWeight.Medium,
                        fontSize = 15.sp,
                        color = Color.Black,
                    ),
                    modifier = Modifier
                        .constrainAs(tvCIntroduce) {
                            top.linkTo(tvIntroduce.bottom, margin = 10.dp)
                            start.linkTo(tvIntroduce.start, margin = 10.dp)
                            end.linkTo(parent.end)
                        }
                )
                Text(
                    text = contentTitle.certificate,
                    style = TextStyle(
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 24.sp,
                        color = Color(0xFF242760)
                    ),
                    modifier = Modifier.constrainAs(tvCertif) {
                        top.linkTo(tvCIntroduce.bottom, margin = 10.dp)
                        start.linkTo(tvIntroduce.start)
                    }
                )
                Image(
                    painter = painterResource(id = images.image1),
                    contentDescription = null,
                    modifier = Modifier
                        .size(27.dp)
                        .constrainAs(iconImage1) {
                            start.linkTo(tvIntroduce.start)
                            top.linkTo(tvCertif.bottom, margin = 10.dp)
                        },
                )
                Image(
                    painter = painterResource(id = images.image2),
                    contentDescription = null,
                    modifier = Modifier
                        .size(27.dp)
                        .constrainAs(iconImage2) {
                            start.linkTo(tvIntroduce.start)
                            top.linkTo(iconImage1.bottom, margin = 5.dp)
                        },
                )
                Text(
                    text = contents.certificate1,
                    style = TextStyle(
                        fontWeight = FontWeight.Medium,
                        fontSize = 15.sp,
                        color = Color(0xFF242760)
                    ),
                    modifier = Modifier.constrainAs(tvC1Certifi) {
                        top.linkTo(iconImage1.top)
                        bottom.linkTo(iconImage1.bottom)
                        start.linkTo(iconImage1.end, margin = 10.dp)
                    }
                )
                Text(
                    text = contents.certificate2,
                    style = TextStyle(
                        fontWeight = FontWeight.Medium,
                        fontSize = 15.sp,
                        color = Color(0xFF242760)
                    ),
                    modifier = Modifier.constrainAs(tvC2Certifi) {
                        top.linkTo(iconImage2.top)
                        bottom.linkTo(iconImage2.bottom, margin = 3.dp)
                        start.linkTo(iconImage2.end, margin = 10.dp)
                    }
                )
                Text(
                    text = contentTitle.workplace,
                    style = TextStyle(
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 24.sp,
                        color = Color(0xFF242760)
                    ),
                    modifier = Modifier.constrainAs(tvWorkplace) {
                        top.linkTo(iconImage2.bottom, margin = 10.dp)
                        start.linkTo(tvIntroduce.start)
                    }
                )
                Text(
                    text = contents.workplace,
                    style = TextStyle(
                        fontWeight = FontWeight.Medium,
                        fontSize = 15.sp,
                        color = Color.Black,
                        textAlign = TextAlign.Justify,
                    ),
                    modifier = Modifier.constrainAs(tvCWorkplace) {
                        top.linkTo(tvWorkplace.bottom, margin = 10.dp)
                        start.linkTo(tvIntroduce.start)
                    }
                )
                Text(
                    text = contentTitle.service,
                    style = TextStyle(
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 24.sp,
                        color = Color(0xFF242760)
                    ),
                    modifier = Modifier.constrainAs(tvService) {
                        top.linkTo(tvCWorkplace.bottom, margin = 10.dp)
                        start.linkTo(tvIntroduce.start)
                    }
                )
                val serviceRefs = contents.services.mapIndexed { index, _ ->
                    val imageRef = createRef()
                    val serviceRef = createRef()
                    val priceRef = createRef()
                    Triple(imageRef, serviceRef, priceRef)
                }

                contents.services.forEachIndexed { index, (serviceName, servicePrice) ->
                    val (imageRef, serviceRef, priceRef) = serviceRefs[index]
                    val topAnchor =
                        if (index == 0) tvService else serviceRefs[index - 1].first

                    Image(
                        painter = painterResource(id = images.image3),
                        contentDescription = null,
                        modifier = Modifier
                            .size(27.dp)
                            .constrainAs(imageRef) {
                                start.linkTo(tvIntroduce.start)
                                top.linkTo(topAnchor.bottom, margin = 10.dp)
                            }
                    )

                    Text(
                        text = serviceName,
                        style = TextStyle(
                            fontWeight = FontWeight.Medium,
                            fontSize = 15.sp,
                            color = Color(0xFF242760)
                        ),
                        modifier = Modifier.constrainAs(serviceRef) {
                            top.linkTo(imageRef.top)
                            bottom.linkTo(imageRef.bottom)
                            start.linkTo(imageRef.end, margin = 5.dp)
                        }
                    )

                    Text(
                        text = formatPrice(servicePrice),
                        style = TextStyle(
                            fontWeight = FontWeight.Medium,
                            fontSize = 15.sp,
                            color = Color(0xFF242760)
                        ),
                        modifier = Modifier.constrainAs(priceRef) {
                            top.linkTo(imageRef.top)
                            bottom.linkTo(imageRef.bottom)
                            end.linkTo(parent.end)
                        }
                    )
                }
            }
        }
    }
}
@Preview(showBackground = true)
@Composable
fun IntroducePreview() {
    Introduce(
        contentTitle = ContentTitle(
            introduce = "Giới thiệu",
            certificate = "Bằng cấp & chứng chỉ",
            workplace = "Nơi làm việc",
            service = "Dịch vụ & Giá cả",
        ),
        contents = Contents(
            introduce = "Bác sĩ với 10 năm kinh nghiệm trong các vấn đề sức khỏe sinh sản và sinh lý. Chuyên điều trị rối loạn sinh lý, thoát dương sớm và các vấn đề sức khỏe của bạn.",
            certificate1 = "Bằng Y khoa, Đại học Y Sài Gòn",
            certificate2 = "Chứng nhận Sản khoa",
            workplace = "Bệnh viện Đại học Y dược TP. HCM",
            services = listOf(
                "Tư vấn sức khỏe sinh sản" to 1500000,
                "Điều trị rối loạn sinh lý" to 1800000,
                "Thăm khám định kỳ" to 1000000
            )
        ),
        images = Images(
            image1 = R.drawable.image_certif,
            image2 = R.drawable.image_certif_2,
            image3 = R.drawable.clarifymanage
        )
    )
}

