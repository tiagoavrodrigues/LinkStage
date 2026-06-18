package turmaA.grupoB.LinkStage.ui.auth.updatepassword

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import turmaA.grupoB.LinkStage.R
import turmaA.grupoB.LinkStage.ui.common.LinkStageButton
import turmaA.grupoB.LinkStage.ui.common.LinkStageOutlinedButton
import turmaA.grupoB.LinkStage.ui.common.SuccessDialog
import turmaA.grupoB.LinkStage.ui.common.ValidationItem
import turmaA.grupoB.LinkStage.ui.theme.DarkBlue
import turmaA.grupoB.LinkStage.ui.theme.Fade2
import turmaA.grupoB.LinkStage.ui.theme.LinkStageTheme
import turmaA.grupoB.LinkStage.ui.theme.MediumBlue
import turmaA.grupoB.LinkStage.ui.theme.Red

@Composable
fun UpdatePasswordScreen(
    onUpdatePasswordClick: (String) -> Unit = {},
    onBackToLogin: () -> Unit = {}
) {
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    var showSuccessDialog by remember { mutableStateOf(false) }

    SuccessDialog(
        message = stringResource(R.string.update_password_success),
        show = showSuccessDialog,
        onConfirm = {
            onUpdatePasswordClick(password)
            onBackToLogin()
        }
    )

    // Validation logic
    val hasNumber by remember { derivedStateOf { password.any { it.isDigit() } } }
    val hasUpperAndLower by remember {
        derivedStateOf {
            password.any { it.isUpperCase() } && password.any { it.isLowerCase() }
        }
    }
    val hasMinLength by remember { derivedStateOf { password.length >= 8 } }

    val isPasswordValid by remember {
        derivedStateOf { hasNumber && hasUpperAndLower && hasMinLength }
    }

    val isConfirmValid by remember {
        derivedStateOf { confirmPassword == password && confirmPassword.isNotEmpty() }
    }

    val isEnabled by remember {
        derivedStateOf { isPasswordValid && isConfirmValid }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 32.dp, top = 48.dp, end = 32.dp, bottom = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.logocompleto),
                contentDescription = "LinkStage Logo",
                modifier = Modifier
                    .height(240.dp)
                    .fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = stringResource(R.string.update_password_title),
                color = DarkBlue,
                style = MaterialTheme.typography.bodyLarge,
                fontSize = 18.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = stringResource(R.string.update_password_subtitle),
                color = Color.Gray,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 8.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = stringResource(R.string.update_password_new),
                    color = Color.DarkGray,
                    fontWeight = FontWeight.Medium,
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = password,
                    onValueChange = { password = it },
                    placeholder = { Text(stringResource(R.string.update_password_new_placeholder), color = Color.Gray) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFFF2F2F2),
                        unfocusedContainerColor = Color(0xFFF2F2F2),
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent
                    ),
                    trailingIcon = {
                        val image = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(imageVector = image, contentDescription = null, tint = Color.Gray)
                        }
                    },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    singleLine = true
                )

                if (password.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    ValidationItem(text = stringResource(R.string.validation_number), isValid = hasNumber)
                    ValidationItem(text = stringResource(R.string.validation_case), isValid = hasUpperAndLower)
                    ValidationItem(text = stringResource(R.string.validation_length), isValid = hasMinLength)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = stringResource(R.string.update_password_confirm),
                    color = Color.DarkGray,
                    fontWeight = FontWeight.Medium,
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    placeholder = { Text(stringResource(R.string.update_password_confirm_placeholder), color = Color.Gray) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFFF2F2F2),
                        unfocusedContainerColor = Color(0xFFF2F2F2),
                        focusedIndicatorColor = if (confirmPassword.isEmpty() || isConfirmValid) Color.Transparent else Red,
                        unfocusedIndicatorColor = if (confirmPassword.isEmpty() || isConfirmValid) Color.Transparent else Red,
                        disabledIndicatorColor = Color.Transparent
                    ),
                    isError = confirmPassword.isNotEmpty() && !isConfirmValid,
                    trailingIcon = {
                        val image = if (confirmPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                        IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                            Icon(imageVector = image, contentDescription = null, tint = Color.Gray)
                        }
                    },
                    visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    singleLine = true
                )
                if (confirmPassword.isNotEmpty() && !isConfirmValid) {
                    Text(
                        text = stringResource(R.string.update_password_mismatch),
                        color = Red,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(start = 4.dp, top = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Column(modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp)) {
                LinkStageButton(
                    text = stringResource(R.string.update_password_button),
                    onClick = { if (isEnabled) showSuccessDialog = true },
                    enabled = isEnabled
                )

                Spacer(modifier = Modifier.height(12.dp))

                LinkStageOutlinedButton(
                    text = stringResource(R.string.common_back),
                    onClick = onBackToLogin
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun UpdatePasswordScreenPreview() {
    LinkStageTheme {
        UpdatePasswordScreen()
    }
}
