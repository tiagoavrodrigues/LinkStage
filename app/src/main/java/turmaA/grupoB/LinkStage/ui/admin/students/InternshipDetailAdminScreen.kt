package turmaA.grupoB.LinkStage.ui.admin.students

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.Work
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import turmaA.grupoB.LinkStage.R
import turmaA.grupoB.LinkStage.ui.aluno.offers.BenefitChip
import turmaA.grupoB.LinkStage.ui.aluno.offers.MetaChip
import turmaA.grupoB.LinkStage.ui.aluno.offers.OfferDetail
import turmaA.grupoB.LinkStage.ui.aluno.offers.ResponsibilityItem
import turmaA.grupoB.LinkStage.ui.common.CheckItem
import turmaA.grupoB.LinkStage.ui.common.ContentSection
import turmaA.grupoB.LinkStage.ui.common.ContentSectionColored
import turmaA.grupoB.LinkStage.ui.common.SecondaryTopBar
import turmaA.grupoB.LinkStage.viewmodel.admin.AdminInternshipDetailUiState
import turmaA.grupoB.LinkStage.viewmodel.admin.AdminInternshipDetailViewModel
import turmaA.grupoB.LinkStage.viewmodel.admin.AdminInternshipDetailViewModelFactory
import turmaA.grupoB.LinkStage.viewmodel.admin.fallbackInternshipDetail
import turmaA.grupoB.LinkStage.ui.theme.BackgroundLight
import turmaA.grupoB.LinkStage.ui.theme.DarkBlue
import turmaA.grupoB.LinkStage.ui.theme.DarkGrey
import turmaA.grupoB.LinkStage.ui.theme.LightBlue

@Composable
fun InternshipDetailAdminScreen(
    internshipId: String,
    onBack: () -> Unit,
) {
    val internshipDetailViewModel: AdminInternshipDetailViewModel = viewModel(factory = AdminInternshipDetailViewModelFactory())
    val internshipDetailUiState by internshipDetailViewModel.uiState.collectAsState()
    val offer = when (val state = internshipDetailUiState) {
        is AdminInternshipDetailUiState.Success -> state.data.offerDetail
        else -> fallbackInternshipDetail(internshipId).offerDetail
    }

    LaunchedEffect(internshipId) {
        internshipDetailViewModel.loadInternship(internshipId)
    }

    Scaffold(
        topBar = { SecondaryTopBar(title = stringResource(R.string.admin_internship_detail_title), onBack = onBack) },
        containerColor = BackgroundLight,
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            InternshipOfferHeader(offer = offer)

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
            ) {
                Spacer(modifier = Modifier.height(12.dp))

                InternshipMetaChips(offer = offer)

                Spacer(modifier = Modifier.height(16.dp))

                ContentSection(title = stringResource(R.string.offer_about_company)) {
                    Text(
                        text = offer.aboutCompany,
                        fontSize = 14.sp,
                        color = DarkGrey,
                        lineHeight = 22.sp,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                ContentSection(title = stringResource(R.string.offer_responsibilities)) {
                    offer.responsibilities.forEach { item ->
                        ResponsibilityItem(text = item)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }

                Spacer(modifier = Modifier.height(12.dp))

                ContentSectionColored(title = stringResource(R.string.offer_requirements)) {
                    offer.requirements.forEach { item ->
                        CheckItem(text = item)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                ContentSection(title = stringResource(R.string.offer_benefits)) {
                    Row(
                        modifier = Modifier
                            .horizontalScroll(rememberScrollState())
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        offer.benefits.forEach { benefit ->
                            BenefitChip(text = benefit)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun InternshipOfferHeader(offer: OfferDetail) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(bottom = 12.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(BackgroundLight)
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(offer.logoColor),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = offer.logoInitial,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = offer.title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = DarkBlue,
                )
                Text(
                    text = offer.company,
                    fontSize = 13.sp,
                    color = LightBlue,
                )
            }
        }
    }
}

@Composable
private fun InternshipMetaChips(offer: OfferDetail) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .height(IntrinsicSize.Min),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        MetaChip(
            icon = Icons.Outlined.LocationOn,
            label = stringResource(R.string.offer_location_label),
            value = offer.location,
            modifier = Modifier.weight(1f).fillMaxHeight(),
        )
        MetaChip(
            icon = Icons.Outlined.Schedule,
            label = stringResource(R.string.offer_duration_label),
            value = offer.duration,
            modifier = Modifier.weight(1f).fillMaxHeight(),
        )
        MetaChip(
            icon = Icons.Outlined.Work,
            label = stringResource(R.string.offer_type_label),
            value = offer.type,
            modifier = Modifier.weight(1f).fillMaxHeight(),
        )
    }
}

@Preview(showSystemUi = true)
@Composable
private fun InternshipDetailAdminPreview() {
    MaterialTheme {
        InternshipDetailAdminScreen(internshipId = "i1", onBack = {})
    }
}
