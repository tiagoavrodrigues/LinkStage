package turmaA.grupoB.LinkStage.ui.auth.register

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
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
import turmaA.grupoB.LinkStage.data.remote.model.enums.UserRole
import turmaA.grupoB.LinkStage.ui.common.LinkStageTabRow
import turmaA.grupoB.LinkStage.ui.theme.*

@Composable
fun RegisterDataScreen(
    selectedProfile: UserRole,
    onBackClick: () -> Unit = {},
    onContinueClick: (Map<String, String>) -> Unit = {}
) {
    var name by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var institute by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var gdprConsent by rememberSaveable { mutableStateOf(false) }

    var institutionType by rememberSaveable { mutableIntStateOf(0) } // 0: Empresarial, 1: Escolar

    var passwordVisible by rememberSaveable { mutableStateOf(false) }

    var showSuccessMessage by remember { mutableStateOf(false) }

    val corporateLabel = stringResource(R.string.register_data_type_corporate)
    val schoolLabel = stringResource(R.string.register_data_type_school)

    // Validation
    val isEmailValid by remember {
        derivedStateOf {
            email.isEmpty() || android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
        }
    }
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

    val isFormValid by remember {
        derivedStateOf {
            name.isNotEmpty() &&
            android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() &&
            isPasswordValid &&
            (if (selectedProfile == UserRole.STUDENT) institute.isNotEmpty() else true) &&
            gdprConsent
        }
    }

    if (showSuccessMessage) {
        AlertDialog(
            onDismissRequest = { },
            confirmButton = {},
            title = { Text(stringResource(R.string.register_data_success_title), fontWeight = FontWeight.Bold, color = DarkBlue) },
            text = {
                Column {
                    Text(stringResource(R.string.register_data_success_message), color = Color.Gray)
                    Spacer(modifier = Modifier.height(24.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Fade2)
                    ) {
                        Button(
                            onClick = {
                                val data = mutableMapOf(
                                    "name" to name,
                                    "email" to email,
                                    "password" to password
                                )
                                if (selectedProfile == UserRole.STUDENT) data["institute"] = institute
                                if (selectedProfile == UserRole.INSTITUTION) {
                                    data["institutionType"] = if (institutionType == 0) corporateLabel else schoolLabel
                                }
                                onContinueClick(data)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Text(stringResource(R.string.register_data_success_ok), color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            },
            shape = RoundedCornerShape(16.dp),
            containerColor = Color(0xFFF5F5F5)
        )
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            Image(
                painter = painterResource(id = R.drawable.logo_link_stage2),
                contentDescription = "LinkStage Logo",
                modifier = Modifier
                    .height(30.dp)
                    .fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = stringResource(R.string.register_title),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Text(
                text = if (selectedProfile == UserRole.STUDENT) stringResource(R.string.register_data_step_student) else stringResource(R.string.register_data_step_institution),
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(24.dp))

            if (selectedProfile == UserRole.INSTITUTION) {
                LinkStageTabRow(
                    tabs = listOf(stringResource(R.string.register_data_tab_corporate), stringResource(R.string.register_data_tab_school)),
                    selectedIndex = institutionType,
                    onTabSelected = { institutionType = it }
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Dynamic Name Field
            val nameLabel = when {
                selectedProfile == UserRole.STUDENT -> stringResource(R.string.register_data_name_student)
                institutionType == 0 -> stringResource(R.string.register_data_name_corporate)
                else -> stringResource(R.string.register_data_name_school)
            }
            val namePlaceholder = when {
                selectedProfile == UserRole.STUDENT -> stringResource(R.string.register_data_name_student_placeholder)
                institutionType == 0 -> stringResource(R.string.register_data_name_corporate_placeholder)
                else -> stringResource(R.string.register_data_name_school_placeholder)
            }

            RegisterTextField(
                label = nameLabel,
                value = name,
                onValueChange = { name = it },
                placeholder = namePlaceholder
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Email Field
            RegisterTextField(
                label = stringResource(R.string.register_data_email),
                value = email,
                onValueChange = { email = it },
                placeholder = stringResource(R.string.register_data_email_placeholder),
                keyboardType = KeyboardType.Email,
                isError = !isEmailValid,
                errorText = if (!isEmailValid) stringResource(R.string.register_data_email_error) else null
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Dynamic Institute Field (Only for Estudante)
            if (selectedProfile == UserRole.STUDENT) {
                RegisterTextField(
                    label = stringResource(R.string.register_data_institution),
                    value = institute,
                    onValueChange = { institute = it },
                    placeholder = stringResource(R.string.register_data_institution_placeholder)
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Password Field
            RegisterTextField(
                label = stringResource(R.string.register_data_password),
                value = password,
                onValueChange = { password = it },
                placeholder = stringResource(R.string.register_data_password_placeholder),
                isPassword = true,
                passwordVisible = passwordVisible,
                onVisibilityChange = { passwordVisible = !passwordVisible },
                showValidation = password.isNotEmpty(),
                validations = listOf(
                    stringResource(R.string.validation_number) to hasNumber,
                    stringResource(R.string.validation_case) to hasUpperAndLower,
                    stringResource(R.string.validation_length) to hasMinLength
                )
            )

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = gdprConsent,
                    onCheckedChange = { gdprConsent = it },
                    colors = CheckboxDefaults.colors(checkedColor = DarkBlue)
                )
                Text(
                    text = stringResource(R.string.register_data_gdpr),
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // Buttons at the bottom
            Column(modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp)) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(
                            brush = if (isFormValid) Fade2 else SolidColor(Color.LightGray.copy(alpha = 0.5f))
                        )
                ) {
                    Button(
                        onClick = {
                            if (isFormValid) {
                                if (selectedProfile == UserRole.STUDENT) {
                                    val data = mutableMapOf(
                                        "name" to name,
                                        "email" to email,
                                        "password" to password,
                                        "institute" to institute
                                    )
                                    onContinueClick(data)
                                } else {
                                    val data = mutableMapOf(
                                        "name" to name,
                                        "email" to email,
                                        "password" to password,
                                        "institutionType" to if (institutionType == 0) corporateLabel else schoolLabel
                                    )
                                    onContinueClick(data)
                                    showSuccessMessage = true
                                }
                            }
                        },
                        enabled = isFormValid,
                        modifier = Modifier.fillMaxSize(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent,
                            disabledContainerColor = Color.Transparent
                        ),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues()
                    ) {
                        Text(
                            text = if (selectedProfile == UserRole.STUDENT) stringResource(R.string.register_continue) else stringResource(R.string.register_data_button_register),
                            color = if (isFormValid) Color.White else Color.Gray.copy(alpha = 0.8f),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedButton(
                    onClick = onBackClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, Fade2),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MediumBlue
                    )
                ) {
                    Text(
                        text = stringResource(R.string.register_back),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun RegisterTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    isPassword: Boolean = false,
    passwordVisible: Boolean = false,
    onVisibilityChange: () -> Unit = {},
    keyboardType: KeyboardType = KeyboardType.Text,
    isError: Boolean = false,
    errorText: String? = null,
    showValidation: Boolean = false,
    validations: List<Pair<String, Boolean>> = emptyList()
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            color = Color.DarkGray,
            fontWeight = FontWeight.Medium,
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder, color = Color.Gray) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color(0xFFF2F2F2),
                unfocusedContainerColor = Color(0xFFF2F2F2),
                focusedIndicatorColor = if (isError) Red else Color.Transparent,
                unfocusedIndicatorColor = if (isError) Red else Color.Transparent,
                disabledIndicatorColor = Color.Transparent
            ),
            visualTransformation = if (isPassword && !passwordVisible) PasswordVisualTransformation() else VisualTransformation.None,
            trailingIcon = {
                if (isPassword) {
                    val icon = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                    IconButton(onClick = onVisibilityChange) {
                        Icon(imageVector = icon, contentDescription = null, tint = Color.Gray)
                    }
                }
            },
            keyboardOptions = KeyboardOptions(keyboardType = if (isPassword) KeyboardType.Password else keyboardType),
            singleLine = true,
            isError = isError
        )
        if (isError && errorText != null) {
            Text(
                text = errorText,
                color = Red,
                fontSize = 12.sp,
                modifier = Modifier.padding(start = 4.dp, top = 4.dp)
            )
        }
        if (showValidation) {
            Spacer(modifier = Modifier.height(8.dp))
            validations.forEach { (text, isValid) ->
                ValidationItem(text = text, isValid = isValid)
            }
        }
    }
}

@Composable
private fun ValidationItem(text: String, isValid: Boolean) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 2.dp)
    ) {
        Icon(
            imageVector = if (isValid) Icons.Default.Check else Icons.Default.Close,
            contentDescription = null,
            tint = if (isValid) Color(0xFF27AE60) else Red,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text,
            color = if (isValid) Color(0xFF27AE60) else Red,
            fontSize = 12.sp
        )
    }
}

@Preview(showBackground = true)
@Composable
fun RegisterDataScreenPreview() {
    LinkStageTheme {
        Column {
            RegisterDataScreen(selectedProfile = UserRole.STUDENT)
            HorizontalDivider()
            RegisterDataScreen(selectedProfile = UserRole.INSTITUTION)
        }
    }
}
