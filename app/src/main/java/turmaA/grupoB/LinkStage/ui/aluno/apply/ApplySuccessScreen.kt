package turmaA.grupoB.LinkStage.ui.aluno.apply

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import turmaA.grupoB.LinkStage.R
import turmaA.grupoB.LinkStage.ui.common.LinkStageButton
import turmaA.grupoB.LinkStage.ui.theme.BackgroundLight
import turmaA.grupoB.LinkStage.ui.theme.DarkBlue
import turmaA.grupoB.LinkStage.ui.theme.LightBlue

@Composable
fun ApplySuccessScreen(
    offerId: String,
    offerTitle: String = "UI/UX Designer",
    offerCompany: String = "Viana S.T.Arts",
    offerLogoInitial: String = "V",
    offerLogoColor: Color = Color(0xFF212121),
    onNavigateBack: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundLight)
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // Offer mini card
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(Color.White)
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(offerLogoColor),
                contentAlignment = Alignment.Center,
            ) {
                Text(offerLogoInitial, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(offerTitle, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = DarkBlue)
                Text(offerCompany, fontSize = 13.sp, color = LightBlue)
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = null,
            tint = LightBlue,
            modifier = Modifier.size(64.dp),
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = stringResource(R.string.apply_success),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = DarkBlue,
            textAlign = TextAlign.Center,
            lineHeight = 28.sp,
        )

        Spacer(modifier = Modifier.height(48.dp))

        LinkStageButton(
            text = stringResource(R.string.common_back),
            onClick = onNavigateBack
        )
    }
}

@Preview(showSystemUi = true)
@Composable
private fun ApplySuccessScreenPreview() {
    MaterialTheme {
        ApplySuccessScreen(offerId = "2", onNavigateBack = {})
    }
}
