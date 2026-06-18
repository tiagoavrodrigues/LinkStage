package turmaA.grupoB.LinkStage.ui.aluno.notifications

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.NotificationsOff
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import turmaA.grupoB.LinkStage.R
import turmaA.grupoB.LinkStage.ui.common.CommonTopBar
import turmaA.grupoB.LinkStage.ui.theme.BackgroundLight
import turmaA.grupoB.LinkStage.ui.theme.BorderGrey
import turmaA.grupoB.LinkStage.ui.theme.DarkBlue
import turmaA.grupoB.LinkStage.ui.theme.DarkGrey
import turmaA.grupoB.LinkStage.ui.theme.Fade2
import turmaA.grupoB.LinkStage.ui.theme.LightBlue
import turmaA.grupoB.LinkStage.viewmodel.settings.SettingsViewModel

@Composable
fun NotificationsAlunoScreen(
    onBack: () -> Unit,
    settingsViewModel: SettingsViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    val enabled by settingsViewModel.notificationsEnabled.collectAsState()
    val candidaturas by settingsViewModel.notifCandidaturas.collectAsState()
    val mensagens by settingsViewModel.notifMensagens.collectAsState()
    val lembretes by settingsViewModel.notifLembretes.collectAsState()
    val orientador by settingsViewModel.notifOrientador.collectAsState()
    val avaliacao by settingsViewModel.notifAvaliacao.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundLight)
    ) {
        CommonTopBar()

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 8.dp, end = 20.dp, top = 0.dp, bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.common_back_content_desc),
                    tint = DarkBlue
                )
            }
            Text(
                text = stringResource(R.string.notifications_title),
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = DarkBlue
                )
            )
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Master Toggle Section
            NotificationSection(
                containerColor = DarkBlue.copy(alpha = 0.05f),
                borderColor = DarkBlue.copy(alpha = 0.1f)
            ) {
                NotificationRow(
                    title = stringResource(R.string.notif_disable_all),
                    subtitle = stringResource(R.string.notif_disable_subtitle),
                    checked = !enabled,
                    onCheckedChange = { settingsViewModel.toggleNotifications(!it) }
                )
            }

            if (enabled) {
                // Alertas de Candidaturas Section
                Column {
                    Text(
                        stringResource(R.string.notif_section_applications),
                        style = MaterialTheme.typography.labelSmall,
                        color = DarkGrey,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
                    )
                    NotificationSection {
                        NotificationRow(
                            title = stringResource(R.string.notif_application_status),
                            subtitle = stringResource(R.string.notif_application_status_sub),
                            checked = candidaturas,
                            onCheckedChange = { settingsViewModel.toggleNotifCandidaturas(it) }
                        )
                        HorizontalDivider(color = BorderGrey.copy(alpha = 0.5f), modifier = Modifier.padding(horizontal = 16.dp))
                        NotificationRow(
                            title = stringResource(R.string.notif_advisor_assigned),
                            subtitle = stringResource(R.string.notif_advisor_assigned_sub),
                            checked = orientador, 
                            onCheckedChange = { settingsViewModel.toggleNotifOrientador(it) }
                        )
                    }
                }

                // Comunicação e Progresso Section
                Column {
                    Text(
                        stringResource(R.string.notif_section_communication),
                        style = MaterialTheme.typography.labelSmall,
                        color = DarkGrey,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
                    )
                    NotificationSection {
                        NotificationRow(
                            title = stringResource(R.string.notif_new_messages),
                            subtitle = stringResource(R.string.notif_new_messages_sub),
                            checked = mensagens,
                            onCheckedChange = { settingsViewModel.toggleNotifMensagens(it) }
                        )
                        HorizontalDivider(color = BorderGrey.copy(alpha = 0.5f), modifier = Modifier.padding(horizontal = 16.dp))
                        NotificationRow(
                            title = stringResource(R.string.notif_evaluation_completed),
                            subtitle = stringResource(R.string.notif_evaluation_completed_sub),
                            checked = avaliacao,
                            onCheckedChange = { settingsViewModel.toggleNotifAvaliacao(it) }
                        )
                    }
                }

                // Atividade Section
                Column {
                    Text(
                        stringResource(R.string.notif_section_activity),
                        style = MaterialTheme.typography.labelSmall,
                        color = DarkGrey,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
                    )
                    NotificationSection {
                        NotificationRow(
                            title = stringResource(R.string.notif_daily_reminders),
                            subtitle = stringResource(R.string.notif_daily_reminders_sub),
                            checked = lembretes,
                            onCheckedChange = { settingsViewModel.toggleNotifLembretes(it) }
                        )
                    }
                }
            } else {
                // Info when disabled
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 40.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.NotificationsOff,
                        contentDescription = null,
                        tint = DarkGrey.copy(alpha = 0.3f),
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        stringResource(R.string.notif_silenced),
                        style = MaterialTheme.typography.bodyMedium,
                        color = DarkGrey
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Save button with Fade2 Gradient
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Fade2)
                    .clickable { onBack() },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(R.string.notif_save_changes),
                    color = Color.White,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun NotificationSection(
    containerColor: Color = Color.White,
    borderColor: Color = BorderGrey,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(containerColor)
            .border(1.dp, borderColor, RoundedCornerShape(12.dp))
            .padding(vertical = 4.dp),
        content = content
    )
}

@Composable
private fun NotificationRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    enabled: Boolean = true
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                color = if (enabled) DarkBlue else DarkBlue.copy(alpha = 0.4f),
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall,
                color = if (enabled) DarkGrey else DarkGrey.copy(alpha = 0.4f)
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            enabled = enabled,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = LightBlue,
                uncheckedThumbColor = Color.White,
                uncheckedTrackColor = DarkGrey.copy(alpha = 0.3f)
            )
        )
    }
}

