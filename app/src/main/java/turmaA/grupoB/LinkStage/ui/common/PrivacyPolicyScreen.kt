package turmaA.grupoB.LinkStage.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.sp
import turmaA.grupoB.LinkStage.R
import turmaA.grupoB.LinkStage.ui.theme.BackgroundLight
import turmaA.grupoB.LinkStage.ui.theme.DarkBlue
import turmaA.grupoB.LinkStage.ui.theme.DarkGrey

@Composable
fun PrivacyPolicyScreen(
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            SecondaryTopBar(
                title = stringResource(R.string.privacy_policy_title),
                onBack = onBack
            )
        },
        containerColor = BackgroundLight
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
        ) {
            PrivacySection(
                title = stringResource(R.string.privacy_section_1_title),
                content = stringResource(R.string.privacy_section_1_content)
            )

            PrivacySection(
                title = stringResource(R.string.privacy_section_2_title),
                content = stringResource(R.string.privacy_section_2_content)
            )

            PrivacySection(
                title = stringResource(R.string.privacy_section_3_title),
                content = stringResource(R.string.privacy_section_3_content)
            )

            PrivacySection(
                title = stringResource(R.string.privacy_section_4_title),
                content = stringResource(R.string.privacy_section_4_content)
            )

            PrivacySection(
                title = stringResource(R.string.privacy_section_5_title),
                content = stringResource(R.string.privacy_section_5_content)
            )

            PrivacySection(
                title = stringResource(R.string.privacy_section_6_title),
                content = stringResource(R.string.privacy_section_6_content)
            )

            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                text = stringResource(R.string.privacy_policy_updated),
                style = MaterialTheme.typography.bodySmall,
                color = DarkGrey,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun PrivacySection(title: String, content: String) {
    Column(modifier = Modifier.padding(bottom = 20.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = DarkBlue
            ),
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Text(
            text = content,
            style = MaterialTheme.typography.bodyMedium.copy(
                color = Color.DarkGray,
                lineHeight = 20.sp
            )
        )
    }
}
