package turmaA.grupoB.LinkStage.ui.orientador.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import turmaA.grupoB.LinkStage.R
import turmaA.grupoB.LinkStage.data.remote.model.Imgs
import turmaA.grupoB.LinkStage.data.repository.flags.FlagsRepository
import turmaA.grupoB.LinkStage.ui.aluno.settings.ChangePasswordDialog
import turmaA.grupoB.LinkStage.ui.aluno.settings.LoggedUser
import turmaA.grupoB.LinkStage.ui.common.CommonTopBar
import turmaA.grupoB.LinkStage.ui.common.LinkStageButton
import turmaA.grupoB.LinkStage.ui.common.LinkStageDialog
import turmaA.grupoB.LinkStage.ui.theme.BackgroundLight
import turmaA.grupoB.LinkStage.ui.theme.BorderGrey
import turmaA.grupoB.LinkStage.ui.theme.DarkBlue
import turmaA.grupoB.LinkStage.ui.theme.DarkGrey
import turmaA.grupoB.LinkStage.ui.theme.Fade1
import turmaA.grupoB.LinkStage.ui.theme.LightBlue
import turmaA.grupoB.LinkStage.ui.theme.Red
import turmaA.grupoB.LinkStage.viewmodel.settings.PasswordChangeState
import turmaA.grupoB.LinkStage.viewmodel.settings.SettingsViewModel
import turmaA.grupoB.LinkStage.viewmodel.flags.FlagsUIState
import turmaA.grupoB.LinkStage.viewmodel.flags.FlagsViewModel
import turmaA.grupoB.LinkStage.viewmodel.flags.FlagsViewModelFactory

@Composable
fun SettingsOrientadorScreen(
    onLogout: () -> Unit = {},
    onNotificationsClick: () -> Unit = {},
    onPrivacyPolicyClick: () -> Unit = {},
    settingsViewModel: SettingsViewModel = viewModel(),
    flagsViewModel: FlagsViewModel = viewModel(factory = FlagsViewModelFactory(FlagsRepository())),
    modifier: Modifier = Modifier,
) {
    val user by settingsViewModel.user.collectAsState()
    val displayedUser = remember(user) {
        user.takeIf { it.name.isNotBlank() && it.email.isNotBlank() }
            ?: LoggedUser(name = "JJ", email = "jj@linkstage.pt")
    }
    val currentLanguage by settingsViewModel.currentLanguage.collectAsState()
    val flagsUIState by flagsViewModel.uiState.collectAsState()
    LaunchedEffect(Unit) {
        flagsViewModel.getImages(listOf("portugal", "gb"))
    }

    var showLogoutDialog by remember { mutableStateOf(false) }
    var showPasswordDialog by remember { mutableStateOf(false) }

    if (showLogoutDialog) {
        LogoutConfirmDialog(
            onConfirm = {
                showLogoutDialog = false
                settingsViewModel.logout()
                onLogout()
            },
            onDismiss = { showLogoutDialog = false },
        )
    }

    val passwordChangeState by settingsViewModel.passwordChangeState.collectAsState()

    LaunchedEffect(passwordChangeState) {
        if (passwordChangeState is PasswordChangeState.Success) {
            showPasswordDialog = false
            settingsViewModel.resetPasswordChangeState()
        }
    }

    if (showPasswordDialog) {
        ChangePasswordDialog(
            onDismiss = {
                showPasswordDialog = false
                settingsViewModel.resetPasswordChangeState()
            },
            onConfirm = { newPassword -> settingsViewModel.changePassword(newPassword) },
            isLoading = passwordChangeState is PasswordChangeState.Loading,
            errorMessage = (passwordChangeState as? PasswordChangeState.Error)?.let {
                stringResource(R.string.settings_password_change_error)
            },
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundLight),
    ) {
        CommonTopBar()

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.Start,
        ) {
            Text(
                text = stringResource(R.string.settings_title),
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = DarkBlue,
                ),
            )
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState()),
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Profile card
            SettingsCard(modifier = Modifier.padding(horizontal = 20.dp)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { }
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    UserAvatar(user = displayedUser, size = 48)
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = displayedUser.name,
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = DarkBlue,
                            ),
                        )
                        Text(
                            text = displayedUser.email,
                            style = MaterialTheme.typography.bodySmall,
                            color = DarkGrey,
                        )
                    }
                }

                HorizontalDivider(color = BorderGrey)

                SettingsRowItem(
                    label = stringResource(R.string.settings_privacy_policy),
                    onClick = onPrivacyPolicyClick,
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Settings section
            SettingsSectionHeader(title = stringResource(R.string.settings_section_config))

            SettingsCard(modifier = Modifier.padding(horizontal = 20.dp)) {
                SettingsRowItem(
                    icon = Icons.Outlined.Settings,
                    label = stringResource(R.string.settings_change_password),
                    onClick = { showPasswordDialog = true },
                )

                HorizontalDivider(
                    color = BorderGrey,
                    modifier = Modifier.padding(horizontal = 16.dp),
                )

                SettingsRowItem(
                    icon = Icons.Outlined.Settings,
                    label = stringResource(R.string.settings_notifications),
                    onClick = onNotificationsClick,
                )

                HorizontalDivider(
                    color = BorderGrey,
                    modifier = Modifier.padding(horizontal = 16.dp),
                )

                // Language toggle
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Settings,
                        contentDescription = null,
                        tint = DarkGrey,
                        modifier = Modifier.size(20.dp),
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = stringResource(R.string.settings_language),
                        style = MaterialTheme.typography.bodyLarge,
                        color = DarkBlue,
                        modifier = Modifier.weight(1f),
                    )
                    LanguageToggle(
                        selectedLang = currentLanguage,
                        onSelect = { settingsViewModel.changeLanguage(it) },
                        uiState = flagsUIState,
                        fallbackImages = fallbackFlagImages(),
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // App version section
            SettingsSectionHeader(title = stringResource(R.string.settings_section_version))

            SettingsCard(modifier = Modifier.padding(horizontal = 20.dp)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Settings,
                        contentDescription = null,
                        tint = DarkGrey,
                        modifier = Modifier.size(20.dp),
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = stringResource(R.string.settings_version),
                        style = MaterialTheme.typography.bodySmall,
                        color = DarkGrey,
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Logout button
            LinkStageButton(
                text = stringResource(R.string.settings_logout),
                onClick = { showLogoutDialog = true },
                modifier = Modifier.padding(horizontal = 20.dp),
                height = 52.dp,
                brush = SolidColor(Red)
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun UserAvatar(user: LoggedUser, size: Int = 48) {
    val initials = user.name
        .split(" ")
        .take(2)
        .joinToString("") { it.first().uppercase() }

    Box(
        modifier = Modifier
            .size(size.dp)
            .clip(CircleShape)
            .background(brush = Fade1),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = initials,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = (size / 3).sp,
        )
    }
}

@Composable
private fun SettingsSectionHeader(title: String) {
    Row(
        modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .width(4.dp)
                .height(20.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(brush = Fade1),
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.labelLarge.copy(
                fontWeight = FontWeight.SemiBold,
                color = LightBlue,
            ),
        )
    }
}

@Composable
private fun SettingsCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        content = content,
    )
}

@Composable
private fun SettingsRowItem(
    label: String,
    icon: ImageVector? = null,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = DarkGrey,
                modifier = Modifier.size(20.dp),
            )
            Spacer(modifier = Modifier.width(12.dp))
        }
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            color = DarkBlue,
            modifier = Modifier.weight(1f),
        )
        Icon(
            imageVector = Icons.AutoMirrored.Outlined.ArrowForward,
            contentDescription = null,
            tint = DarkGrey,
            modifier = Modifier.size(18.dp),
        )
    }
}

@Composable
private fun LanguageToggle(
    selectedLang: String,
    onSelect: (String) -> Unit,
    uiState: FlagsUIState,
    fallbackImages: List<Imgs>,
) {
    val flagLangs = listOf("portugal", "gb")
    val labels = listOf("PT", "EN")

    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .border(1.dp, BorderGrey, RoundedCornerShape(8.dp)),
    ) {
        labels.forEachIndexed { index, label ->
            val lang = flagLangs[index]
            val isSelected = label == selectedLang || lang == selectedLang
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(7.dp))
                    .background(if (isSelected) DarkBlue else Color.Transparent)
                    .clickable { onSelect(label) }
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                contentAlignment = Alignment.Center,
            ) {
                when (uiState) {
                    is FlagsUIState.Error -> Text(
                        text = label,
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        ),
                        color = if (isSelected) Color.White else DarkGrey,
                    )
                    is FlagsUIState.Success -> {
                        val imageIndex = flagLangs.indexOf(lang)
                        val image = uiState.imgs.getOrNull(imageIndex) ?: fallbackImages.getOrNull(imageIndex)
                        if (image?.png != null) {
                            AsyncImage(
                                model = image.png,
                                contentDescription = label,
                                modifier = Modifier.size(24.dp),
                            )
                        } else {
                            Text(
                                text = label,
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                ),
                                color = if (isSelected) Color.White else DarkGrey,
                            )
                        }
                    }
                    else -> Text(
                        text = label,
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        ),
                        color = if (isSelected) Color.White else DarkGrey,
                    )
                }
            }
        }
    }
}

@Composable
private fun LogoutConfirmDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    LinkStageDialog(
        title = stringResource(R.string.settings_logout),
        onConfirm = onConfirm,
        onDismiss = onDismiss,
        confirmText = stringResource(R.string.settings_logout_button),
        dismissText = stringResource(R.string.common_cancel),
        content = {
            Text(
                text = stringResource(R.string.settings_logout_confirm),
                color = DarkGrey,
            )
        }
    )
}

private fun fallbackFlagImages(): List<Imgs> {
    return listOf(
        Imgs(png = "https://flagcdn.com/w320/pt.png"),
        Imgs(png = "https://flagcdn.com/w320/gb.png"),
    )
}

@Preview(showSystemUi = true)
@Composable
private fun SettingsOrientadorScreenPreview() {
    MaterialTheme {
        SettingsOrientadorScreen()
    }
}
