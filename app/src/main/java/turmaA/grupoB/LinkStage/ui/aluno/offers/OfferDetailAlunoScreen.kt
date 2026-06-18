package turmaA.grupoB.LinkStage.ui.aluno.offers

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.TaskAlt
import androidx.compose.material.icons.outlined.Work
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import turmaA.grupoB.LinkStage.R
import turmaA.grupoB.LinkStage.data.remote.model.offer.InternshipOfferModel
import turmaA.grupoB.LinkStage.data.remote.model.institution.InstitutionModel
import turmaA.grupoB.LinkStage.data.repository.institution.InstitutionRepository
import turmaA.grupoB.LinkStage.data.repository.offer.OfferRepository
import turmaA.grupoB.LinkStage.ui.common.CheckItem
import turmaA.grupoB.LinkStage.ui.common.ContentSection
import turmaA.grupoB.LinkStage.ui.common.ContentSectionColored
import turmaA.grupoB.LinkStage.ui.common.LinkStageButton
import turmaA.grupoB.LinkStage.ui.common.SecondaryTopBar
import turmaA.grupoB.LinkStage.ui.common.LinkStageDialog
import turmaA.grupoB.LinkStage.ui.theme.BackgroundLight
import turmaA.grupoB.LinkStage.ui.theme.BorderGrey
import turmaA.grupoB.LinkStage.ui.theme.DarkBlue
import turmaA.grupoB.LinkStage.ui.theme.DarkGrey
import turmaA.grupoB.LinkStage.ui.theme.LightBlue
import turmaA.grupoB.LinkStage.ui.theme.Red
import turmaA.grupoB.LinkStage.viewmodel.offer.OfferUiState
import turmaA.grupoB.LinkStage.viewmodel.offer.OfferViewModel
import turmaA.grupoB.LinkStage.viewmodel.offer.OfferViewModelFactory
import java.time.LocalDate
import java.time.temporal.ChronoUnit

// region Data model

data class OfferDetail(
    val id: String,
    val title: String,
    val company: String,
    val logoInitial: String,
    val logoColor: Color,
    val location: String,
    val duration: String,
    val type: String,
    val aboutCompany: String,
    val responsibilities: List<String>,
    val requirements: List<String>,
    val benefits: List<String>,
    val deadlineDays: Int,
    val applicantsCount: Int,
    val isFavourite: Boolean = false,
    val hasApplied: Boolean = false,
)

// endregion

// region Main Screen

private fun InternshipOfferModel.toOfferDetail(
    institution: InstitutionModel?,
): OfferDetail {
    val requirementsList = requirements
        ?.split("\n", ";")
        ?.map { it.trim() }
        ?.filter { it.isNotBlank() }
        .orEmpty()

    return OfferDetail(
        id = id,
        title = title,
        company = institution?.name.orEmpty(),
        logoInitial = institution?.name?.firstOrNull()?.uppercaseChar()?.toString() ?: "?",
        logoColor = Color(0xFF212121),
        location = location.orEmpty(),
        duration = "",
        type = modality.orEmpty(),
        aboutCompany = institution?.description.orEmpty(),
        responsibilities = listOfNotNull(description.takeIf { it.isNotBlank() }),
        requirements = requirementsList,
        benefits = emptyList(),
        deadlineDays = deadline.toDeadlineDays(),
        applicantsCount = 0,
        isFavourite = false,
        hasApplied = false,
    )
}

private fun String?.toDeadlineDays(): Int {
    val deadlineDate = this?.let { runCatching { LocalDate.parse(it) }.getOrNull() }
        ?: return 0
    return ChronoUnit.DAYS.between(LocalDate.now(), deadlineDate)
        .coerceAtLeast(0)
        .toInt()
}

private fun emptyOfferDetail(offerId: String) = OfferDetail(
    id = offerId,
    title = "",
    company = "",
    logoInitial = "?",
    logoColor = Color(0xFF212121),
    location = "",
    duration = "",
    type = "",
    aboutCompany = "",
    responsibilities = emptyList(),
    requirements = emptyList(),
    benefits = emptyList(),
    deadlineDays = 0,
    applicantsCount = 0,
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun OfferDetailAlunoScreen(
    offerId: String,
    onBack: () -> Unit,
    onApply: (OfferDetail) -> Unit = {},
    offerViewModel: OfferViewModel = viewModel(
        factory = OfferViewModelFactory(
            OfferRepository(),
            InstitutionRepository(),
        )
    ),
) {
    val offerUiState by offerViewModel.uiState.collectAsState()

    LaunchedEffect(offerId) {
        offerViewModel.loadOfferDetailsById(offerId)
    }

    val offer = when (val state = offerUiState) {
        is OfferUiState.SuccessDetails -> state.offer.toOfferDetail(state.institution)
        is OfferUiState.Success -> state.offer.toOfferDetail(null)
        else -> emptyOfferDetail(offerId)
    }

    var isFavourite by remember(offer.id) { mutableStateOf(offer.isFavourite) }
    var hasApplied by remember(offer.id) { mutableStateOf(offer.hasApplied) }
    var showApplyDialog by remember { mutableStateOf(false) }

    if (showApplyDialog) {
        ApplyConfirmDialog(
            offerTitle = offer.title,
            onConfirm = {
                showApplyDialog = false
                onApply(offer)
            },
            onDismiss = { showApplyDialog = false },
        )
    }

    val errorMessage = when (val state = offerUiState) {
        OfferUiState.Idle,
        OfferUiState.Loading -> "A carregar oferta..."
        OfferUiState.Empty -> "Oferta não encontrada."
        is OfferUiState.Error -> state.message
        else -> null
    }

    Scaffold(
        topBar = { SecondaryTopBar(title = stringResource(R.string.offer_detail_title), onBack = onBack) },
        bottomBar = {
            OfferDetailBottomBar(
                hasApplied = hasApplied,
                isFavourite = isFavourite,
                deadlineDays = offer.deadlineDays,
                applicantsCount = offer.applicantsCount,
                onApply = {
                    if (offerUiState is OfferUiState.SuccessDetails ||
                        offerUiState is OfferUiState.Success
                    ) {
                        showApplyDialog = true
                    }
                },
                onFavouriteToggle = { isFavourite = !isFavourite },
            )
        },
        containerColor = BackgroundLight,
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            OfferDetailHeader(offer = offer)

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
            ) {
                Spacer(modifier = Modifier.height(12.dp))

                if (errorMessage != null) {
                    Text(
                        text = errorMessage,
                        color = Red,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                }

                OfferMetaChips(offer = offer)

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
                    Column(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        offer.benefits.forEach { benefit ->
                            BenefitChip(text = benefit)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

// endregion

// region Components

@Composable
private fun OfferDetailHeader(offer: OfferDetail) {
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
private fun OfferMetaChips(offer: OfferDetail) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .height(IntrinsicSize.Min),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        MetaChip(
            icon = Icons.Outlined.LocationOn,
            label = stringResource(R.string.filter_location),
            value = offer.location,
            modifier = Modifier.weight(1f).fillMaxHeight(),
        )
        MetaChip(
            icon = Icons.Outlined.Schedule,
            label = stringResource(R.string.filter_duration),
            value = offer.duration,
            modifier = Modifier.weight(1f).fillMaxHeight(),
        )
        MetaChip(
            icon = Icons.Outlined.Work,
            label = stringResource(R.string.offer_form_review_type),
            value = offer.type,
            modifier = Modifier.weight(1f).fillMaxHeight(),
        )
    }
}

@Composable
fun MetaChip(
    icon: ImageVector,
    label: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    ) {
        Column(
            modifier = Modifier
                .padding(10.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = LightBlue,
                modifier = Modifier.size(20.dp),
            )
            Text(
                text = label,
                fontSize = 11.sp,
                color = DarkGrey,
                textAlign = TextAlign.Center,
            )
            Text(
                text = value,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = DarkBlue,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
fun ResponsibilityItem(text: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.Top,
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(BackgroundLight),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Outlined.TaskAlt,
                contentDescription = null,
                tint = LightBlue,
                modifier = Modifier.size(20.dp),
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = text,
            fontSize = 14.sp,
            color = DarkGrey,
            lineHeight = 20.sp,
            modifier = Modifier
                .weight(1f)
                .padding(top = 8.dp),
        )
    }
}

@Composable
fun BenefitChip(text: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .border(1.dp, LightBlue, RoundedCornerShape(20.dp))
            .padding(horizontal = 14.dp, vertical = 8.dp),
    ) {
        Text(
            text = text,
            fontSize = 13.sp,
            color = DarkBlue,
            fontWeight = FontWeight.Medium,
        )
    }
}

@Composable
private fun OfferDetailBottomBar(
    hasApplied: Boolean,
    isFavourite: Boolean,
    deadlineDays: Int,
    applicantsCount: Int,
    onApply: () -> Unit,
    onFavouriteToggle: () -> Unit,
) {
    Surface(
        color = Color.White,
        shadowElevation = 8.dp,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 16.dp, vertical = 12.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                LinkStageButton(
                    text = if (hasApplied) stringResource(R.string.offer_applied) else stringResource(R.string.offer_apply),
                    onClick = { if (!hasApplied) onApply() },
                    enabled = !hasApplied,
                    modifier = Modifier.weight(1f),
                    height = 50.dp,
                    brush = if (hasApplied) turmaA.grupoB.LinkStage.ui.theme.Fade3 else turmaA.grupoB.LinkStage.ui.theme.Fade2
                )

                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .border(1.dp, BorderGrey, RoundedCornerShape(12.dp))
                        .background(Color.White),
                    contentAlignment = Alignment.Center,
                ) {
                    IconButton(onClick = onFavouriteToggle) {
                        Icon(
                            imageVector = Icons.Outlined.FavoriteBorder,
                            contentDescription = if (isFavourite) stringResource(R.string.offer_remove_favorite) else stringResource(R.string.offer_add_favorite),
                            tint = if (isFavourite) LightBlue else DarkGrey,
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(R.string.offer_deadline_info, deadlineDays, applicantsCount),
                    fontSize = 12.sp,
                    color = DarkGrey,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}

@Composable
private fun ApplyConfirmDialog(
    offerTitle: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    LinkStageDialog(
        title = stringResource(R.string.offer_confirm_apply_title),
        onConfirm = onConfirm,
        onDismiss = onDismiss,
        confirmText = stringResource(R.string.offer_apply),
        dismissText = stringResource(R.string.dialog_cancel),
        content = {
            Text(
                text = stringResource(R.string.offer_confirm_apply_message, offerTitle),
                color = DarkGrey,
                lineHeight = 22.sp,
            )
        }
    )
}

// endregion

// region Previews

@Preview(name = "Offer Detail", showSystemUi = true)
@Composable
private fun OfferDetailScreenPreview() {
    MaterialTheme {
        OfferDetailAlunoScreen(
            offerId = "2",
            onBack = {},
        )
    }
}

// endregion
