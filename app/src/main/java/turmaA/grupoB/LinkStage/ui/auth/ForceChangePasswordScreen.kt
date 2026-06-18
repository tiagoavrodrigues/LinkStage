package turmaA.grupoB.LinkStage.ui.auth

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import turmaA.grupoB.LinkStage.R
import turmaA.grupoB.LinkStage.ui.common.ConfirmationDialog
import turmaA.grupoB.LinkStage.ui.common.LinkStageLogo
import turmaA.grupoB.LinkStage.ui.common.PasswordField
import turmaA.grupoB.LinkStage.ui.common.PasswordRequirementsIndicator
import turmaA.grupoB.LinkStage.ui.theme.BackgroundLight
import turmaA.grupoB.LinkStage.ui.theme.DarkBlue
import turmaA.grupoB.LinkStage.ui.theme.DarkGrey

@Composable
fun ForceChangePasswordScreen(
    navController: NavController,
    userDestination: String = "orientador",
) {
    BackHandler(enabled = true) { }

    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    var currentPasswordError by remember { mutableStateOf<String?>(null) }
    var newPasswordError by remember { mutableStateOf<String?>(null) }
    var confirmPasswordError by remember { mutableStateOf<String?>(null) }

    var showConfirmDialog by remember { mutableStateOf(false) }

    val context = LocalContext.current

    fun validateAndSubmit() {
        var isValid = true

        if (currentPassword.isBlank()) {
            currentPasswordError = context.getString(R.string.force_change_error_empty)
            isValid = false
        } else {
            currentPasswordError = null
        }

        if (newPassword.length < 8) {
            newPasswordError = context.getString(R.string.force_change_error_length)
            isValid = false
        } else if (!newPassword.any { it.isUpperCase() }) {
            newPasswordError = context.getString(R.string.force_change_error_uppercase)
            isValid = false
        } else if (!newPassword.any { it.isDigit() }) {
            newPasswordError = context.getString(R.string.force_change_error_number)
            isValid = false
        } else if (newPassword == currentPassword) {
            newPasswordError = context.getString(R.string.force_change_error_same)
            isValid = false
        } else {
            newPasswordError = null
        }

        if (confirmPassword != newPassword) {
            confirmPasswordError = context.getString(R.string.force_change_error_mismatch)
            isValid = false
        } else {
            confirmPasswordError = null
        }

        if (!isValid) return

        showConfirmDialog = true
    }

    if (showConfirmDialog) {
        ConfirmationDialog(
            title = stringResource(R.string.force_change_dialog_title),
            body = stringResource(R.string.force_change_dialog_body),
            confirmLabel = stringResource(R.string.force_change_dialog_confirm),
            isDanger = false,
            onConfirm = {
                showConfirmDialog = false
                navController.navigate(userDestination) {
                    popUpTo("force_change_password") { inclusive = true }
                }
            },
            onDismiss = { showConfirmDialog = false },
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundLight)
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            LinkStageLogo()

            Spacer(modifier = Modifier.height(24.dp))

            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(DarkBlue.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Outlined.Lock,
                    contentDescription = null,
                    tint = DarkBlue,
                    modifier = Modifier.size(36.dp),
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = stringResource(R.string.force_change_title),
                color = DarkBlue,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                textAlign = TextAlign.Center,
                lineHeight = 27.sp,
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = stringResource(R.string.force_change_subtitle),
                color = DarkGrey,
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                lineHeight = 21.sp,
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            PasswordField(
                label = stringResource(R.string.force_change_current),
                value = currentPassword,
                onValueChange = {
                    currentPassword = it
                    currentPasswordError = null
                },
                error = currentPasswordError,
            )

            PasswordField(
                label = stringResource(R.string.force_change_new),
                value = newPassword,
                onValueChange = {
                    newPassword = it
                    newPasswordError = null
                },
                error = newPasswordError,
            )

            PasswordField(
                label = stringResource(R.string.force_change_confirm),
                value = confirmPassword,
                onValueChange = {
                    confirmPassword = it
                    confirmPasswordError = null
                },
                error = confirmPasswordError,
            )

            PasswordRequirementsIndicator(password = newPassword)
        }

        Spacer(modifier = Modifier.height(24.dp))

        Column {
            Button(
                onClick = { validateAndSubmit() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = DarkBlue),
            ) {
                Text(
                    text = stringResource(R.string.force_change_button),
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp,
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Preview(showSystemUi = true)
@Composable
private fun ForceChangePasswordScreenPreview() {
    ForceChangePasswordScreen(navController = rememberNavController())
}
