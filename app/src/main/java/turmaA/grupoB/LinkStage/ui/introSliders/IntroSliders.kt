package turmaA.grupoB.LinkStage.ui.introSliders

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import turmaA.grupoB.LinkStage.R
import turmaA.grupoB.LinkStage.ui.theme.*

data class SliderData(
    val titlePart1: String,
    val titleHighlight: String,
    val description: String,
    @param:DrawableRes val imageRes: Int
)

@Composable
fun IntroSlidersScreen(
    onFinish: () -> Unit = {}
) {
    val sliders = listOf(
        SliderData(
            stringResource(R.string.intro_slide1_title),
            stringResource(R.string.intro_slide1_highlight),
            stringResource(R.string.intro_slide1_description),
            R.drawable.logo_link_stage1
        ),
        SliderData(
            stringResource(R.string.intro_slide2_title),
            stringResource(R.string.intro_slide2_highlight),
            stringResource(R.string.intro_slide2_description),
            R.drawable.sliders1
        ),
        SliderData(
            stringResource(R.string.intro_slide3_title),
            stringResource(R.string.intro_slide3_highlight),
            stringResource(R.string.intro_slide3_description),
            R.drawable.sliders2
        ),
        SliderData(
            stringResource(R.string.intro_slide4_title),
            stringResource(R.string.intro_slide4_highlight),
            stringResource(R.string.intro_slide4_description),
            R.drawable.sliders3
        ),
    )

    val pagerState = rememberPagerState(pageCount = { sliders.size })

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.White
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            TextButton(
                onClick = onFinish,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 16.dp, end = 16.dp)
            ) {
                Text(
                    text = stringResource(R.string.intro_skip),
                    color = Color.Gray,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(60.dp))
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) { page ->
                    SliderContent(sliders[page])
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 32.dp, end = 32.dp, bottom = 48.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        Modifier
                            .height(20.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        repeat(sliders.size) { iteration ->
                            val isSelected = pagerState.currentPage == iteration
                            val width = if (isSelected) 24.dp else 8.dp
                            val color = if (isSelected) DarkBlue else Color.LightGray.copy(alpha = 0.5f)
                            
                            Box(
                                modifier = Modifier
                                    .padding(4.dp)
                                    .clip(CircleShape)
                                    .background(color)
                                    .width(width)
                                    .height(8.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    Box(
                        modifier = Modifier.fillMaxWidth().height(56.dp)
                    ) {
                        if (pagerState.currentPage == sliders.size - 1) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(56.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(Fade2)
                                    .clickable { onFinish() },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = stringResource(R.string.intro_start),
                                    color = Color.White,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SliderContent(data: SliderData) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(300.dp)
                .background(DarkBlue.copy(alpha = 0.03f), CircleShape)
                .border(2.dp, DarkBlue.copy(alpha = 0.1f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = data.imageRes),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
        }

        Spacer(modifier = Modifier.height(50.dp))

        Text(
            text = buildAnnotatedString {
                withStyle(style = SpanStyle(color = DarkBlue, fontWeight = FontWeight.Bold)) {
                    append(data.titlePart1)
                }
                withStyle(style = SpanStyle(color = LightBlue, fontWeight = FontWeight.Bold)) {
                    append(data.titleHighlight)
                }
            },
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = data.description,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = Color.Gray,
            lineHeight = 24.sp
        )
    }
}

@Preview(showBackground = true)
@Composable
fun IntroSlidersPreview() {
    LinkStageTheme {
        IntroSlidersScreen()
    }
}
