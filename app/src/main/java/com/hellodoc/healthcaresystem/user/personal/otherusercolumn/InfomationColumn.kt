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
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.horizontalScroll
import coil.compose.rememberAsyncImagePainter
import com.hellodoc.healthcaresystem.responsemodel.ServiceOutput

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
            certificate = doctor?.certificates ?: "Chưa cập nhật bằng cấp",
            workplace = doctor?.hospital ?: "Chưa cập nhật nơi làm việc",
            services = doctor?.services ?: emptyList()
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
                    //text = contents.certificate1,
                    text = contents.certificate,
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
                    //text = contents.certificate2,
                    text = contents.certificate,
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
                val serviceConstraints = contents.services.map { createRef() }
                val (iconRef, textRef) = createRefs()

                contents.services.forEachIndexed { index, service ->
                    val currentRef = serviceConstraints[index]
                    val topAnchor = if (index == 0) tvService else serviceConstraints[index - 1]

                    Column(
                        modifier = Modifier
                            .constrainAs(currentRef) {
                                top.linkTo(topAnchor.bottom, margin = 16.dp)
                                start.linkTo(tvIntroduce.start, margin = 10.dp)
                                end.linkTo(parent.end)
                            }
                    ) {
                        ConstraintLayout(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 4.dp)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.clarifymanage),
                                contentDescription = null,
                                modifier = Modifier
                                    .size(18.dp)
                                    .constrainAs(iconRef) {
                                        start.linkTo(parent.start)
                                        top.linkTo(parent.top)
                                        bottom.linkTo(parent.bottom)
                                    }
                            )

                            Text(
                                text = service.specialtyName,
                                style = TextStyle(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = Color(0xFF242760)
                                ),
                                modifier = Modifier
                                    .constrainAs(textRef) {
                                        start.linkTo(iconRef.end, margin = 8.dp)
                                        top.linkTo(iconRef.top)
                                        bottom.linkTo(iconRef.bottom)
                                    }
                            )
                        }

                        LazyRow(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 5.dp)
                        ) {
                            items(listOf(service.imageService)) { imageUrl ->
                                Image(
                                    painter = rememberAsyncImagePainter(imageUrl),
                                    contentDescription = null,
                                    modifier = Modifier
                                        .padding(end = 8.dp)
                                        .size(120.dp)
                                )
                            }
                        }

                        Text(
                            text = "Giá: ${formatPrice(service.minPrice.toInt())} - ${formatPrice(service.maxPrice.toInt())}",
                            style = TextStyle(fontSize = 14.sp, color = Color.DarkGray)
                        )
                    }
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
            introduce = "Bác sĩ với hơn 10 năm kinh nghiệm chuyên điều trị các vấn đề sức khỏe nam giới.",
            certificate = "Bằng Y khoa, Đại học Y Dược TP.HCM",
            workplace = "Bệnh viện Đại học Y Dược TP.HCM",
            services = listOf(
                ServiceOutput(
                    specialtyID = "1",
                    specialtyName = "Khám nam khoa",
                    imageService = "https://via.placeholder.com/150",
                    minPrice = "500000",
                    maxPrice = "1500000",
                    description = "Khám và tư vấn sức khỏe sinh lý"
                ),
                ServiceOutput(
                    specialtyID = "2",
                    specialtyName = "Tư vấn sinh sản",
                    imageService = "https://via.placeholder.com/150",
                    minPrice = "700000",
                    maxPrice = "2000000",
                    description = "Tư vấn sinh sản toàn diện"
                )
            )
        ),
        images = Images(
            image1 = R.drawable.image_certif,
            image2 = R.drawable.image_certif_2,
            image3 = R.drawable.clarifymanage
        )
    )
}


