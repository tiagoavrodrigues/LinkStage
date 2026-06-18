package turmaA.grupoB.LinkStage.ui.common

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import turmaA.grupoB.LinkStage.R
import turmaA.grupoB.LinkStage.ui.theme.DarkBlue
import turmaA.grupoB.LinkStage.ui.theme.DarkGrey
import turmaA.grupoB.LinkStage.ui.theme.Fade2
import turmaA.grupoB.LinkStage.ui.theme.Red

@Composable
fun ConfirmationDialog(
    title: String,
    body: String,
    confirmLabel: String = stringResource(R.string.common_confirm),
    confirmBrush: Brush = Fade2,
    isDanger: Boolean = false,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                color = if (isDanger) Red else DarkBlue,
                fontSize = 18.sp,
            )
        },
        text = {
            Text(
                text = body,
                color = DarkGrey,
                lineHeight = 20.sp,
                fontSize = 14.sp,
            )
        },
        confirmButton = {
            LinkStageButton(
                text = confirmLabel,
                onClick = onConfirm,
                modifier = Modifier.width(120.dp),
                height = 40.dp,
                brush = if (isDanger) SolidColor(Red) else confirmBrush
            )
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.height(40.dp)
            ) {
                Text(
                    text = stringResource(R.string.dialog_cancel),
                    color = DarkGrey,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp
                )
            }
        },
        containerColor = Color.White,
        shape = RoundedCornerShape(16.dp),
    )
}
