package turmaA.grupoB.LinkStage.ui.common

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.NotificationsActive
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.stringResource
import turmaA.grupoB.LinkStage.R
import turmaA.grupoB.LinkStage.ui.theme.DarkBlue
import turmaA.grupoB.LinkStage.ui.theme.DarkGrey
import turmaA.grupoB.LinkStage.ui.theme.LightBlue
import turmaA.grupoB.LinkStage.ui.theme.Red

@Composable
fun EvaluationPendingCard(
    title: String,
    message: String,
    actionLabel: String,
    isDanger: Boolean = false,
    onClick: () -> Unit,
) {
    val borderColor = if (isDanger) Red else LightBlue
    val bgColor = if (isDanger) Red.copy(alpha = 0.06f) else LightBlue.copy(alpha = 0.08f)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = bgColor),
        border = BorderStroke(1.dp, borderColor.copy(alpha = 0.5f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = Icons.Outlined.NotificationsActive,
                contentDescription = null,
                tint = borderColor,
                modifier = Modifier.size(24.dp),
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 12.dp),
            ) {
                Text(
                    text = title,
                    color = DarkBlue,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                )
                Text(
                    text = message,
                    color = DarkGrey,
                    fontSize = 12.sp,
                    lineHeight = 18.sp,
                )
            }
            Icon(
                imageVector = Icons.Outlined.ChevronRight,
                contentDescription = null,
                tint = borderColor,
                modifier = Modifier.size(20.dp),
            )
        }
    }
}

@Preview
@Composable
private fun EvaluationPendingCardPreview() {
    EvaluationPendingCard(
        title = stringResource(R.string.institution_home_eval_pending),
        message = stringResource(R.string.institution_home_eval_pending_message),
        actionLabel = stringResource(R.string.institution_eval_submit_grade),
        isDanger = false,
        onClick = {},
    )
}

@Preview
@Composable
private fun EvaluationPendingCardDangerPreview() {
    EvaluationPendingCard(
        title = stringResource(R.string.advisor_home_final_grade_pending),
        message = stringResource(R.string.student_detail_all_submitted),
        actionLabel = stringResource(R.string.student_detail_assign_final_button),
        isDanger = true,
        onClick = {},
    )
}
