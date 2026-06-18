package turmaA.grupoB.LinkStage.ui.instituicao.offers

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import turmaA.grupoB.LinkStage.R
import turmaA.grupoB.LinkStage.data.repository.institution.InstitutionRepository
import turmaA.grupoB.LinkStage.data.repository.offer.OfferRepository
import turmaA.grupoB.LinkStage.ui.common.CommonTopBar
import turmaA.grupoB.LinkStage.ui.common.LinkStageButton
import turmaA.grupoB.LinkStage.ui.instituicao.InstituicaoRoutes
import turmaA.grupoB.LinkStage.ui.theme.BackgroundLight
import turmaA.grupoB.LinkStage.ui.theme.DarkBlue
import turmaA.grupoB.LinkStage.ui.theme.LightBlue
import turmaA.grupoB.LinkStage.viewmodel.offer.OfferUiState
import turmaA.grupoB.LinkStage.viewmodel.offer.OfferViewModel
import turmaA.grupoB.LinkStage.viewmodel.offer.OfferViewModelFactory

@Composable
fun OfferSuccessInstituicaoScreen(
    offerId: String,
    navController: NavController,
    isNew: Boolean = true,
    offerLogoColor: Color = Color(0xFF1565C0),
    offerViewModel: OfferViewModel = viewModel(
        factory = OfferViewModelFactory(
            offerRepository = OfferRepository(),
            institutionRepository = InstitutionRepository(),
        )
    ),
) {
    var animationStarted by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (animationStarted) 1f else 0f,
        animationSpec = tween(durationMillis = 500),
        label = "check_scale",
    )

    val offerUiState by offerViewModel.uiState.collectAsState()

    LaunchedEffect(offerId) {
        animationStarted = true
        offerViewModel.loadOfferDetailsById(offerId)
    }

    val details = offerUiState as? OfferUiState.SuccessDetails
    val offerTitle = details?.offer?.title ?: stringResource(R.string.offer_form_create_title)
    val offerCompany = details?.institution?.name.orEmpty()
    val offerLogoInitial = offerCompany.firstOrNull()?.uppercaseChar()?.toString() ?: "?"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundLight),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        CommonTopBar()

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // Mini card da oferta
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(Color.White)
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(offerLogoColor),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    offerLogoInitial,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    offerTitle,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = DarkBlue,
                )
                Text(
                    offerCompany,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = LightBlue,
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Animated check icon
        Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = null,
            tint = LightBlue,
            modifier = Modifier
                .size(64.dp)
                .scale(scale),
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = if (isNew) stringResource(R.string.offer_success_created) else stringResource(R.string.offer_success_updated),
            color = DarkBlue,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            textAlign = TextAlign.Center,
            lineHeight = 28.sp,
        )

        Spacer(modifier = Modifier.height(48.dp))

            LinkStageButton(
                text = stringResource(R.string.offer_success_back),
                onClick = {
                    navController.navigate(InstituicaoRoutes.OFFERS) {
                        popUpTo(InstituicaoRoutes.HOME) { inclusive = false }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                height = 50.dp,
            )
        }
    }
}

@Preview(showSystemUi = true)
@Composable
private fun OfferSuccessInstituicaoScreenPreview() {
    MaterialTheme {
        OfferSuccessInstituicaoScreen(
            offerId = "new",
            navController = rememberNavController(),
        )
    }
}

@Preview(showSystemUi = true, device = "spec:width=411dp,height=891dp,orientation=landscape")
@Composable
private fun OfferSuccessInstituicaoScreenLandscapePreview() {
    MaterialTheme {
        OfferSuccessInstituicaoScreen(
            offerId = "new",
            navController = rememberNavController(),
        )
    }
}
