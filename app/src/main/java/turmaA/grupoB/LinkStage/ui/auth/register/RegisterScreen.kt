package turmaA.grupoB.LinkStage.ui.auth.register

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AutoStories
import androidx.compose.material.icons.outlined.BusinessCenter
import androidx.compose.material.icons.outlined.Person
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import turmaA.grupoB.LinkStage.R
import turmaA.grupoB.LinkStage.data.remote.model.enums.UserRole
import turmaA.grupoB.LinkStage.ui.theme.CompanyGreen
import turmaA.grupoB.LinkStage.ui.theme.Fade2
import turmaA.grupoB.LinkStage.ui.theme.LinkStageTheme
import turmaA.grupoB.LinkStage.ui.theme.MediumBlue
import turmaA.grupoB.LinkStage.ui.theme.Red

@Composable
fun RegisterScreen(
    modifier: Modifier = Modifier,
    onContinueClick: (UserRole) -> Unit = {},
    onBackToLogin: () -> Unit = {}
) {
    var selectedProfile by remember { mutableStateOf<UserRole?>(null) }

    Surface(
        modifier = modifier.fillMaxSize(),
        color = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 32.dp, end = 32.dp, top = 8.dp, bottom = 8.dp),
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
                text = stringResource(R.string.register_subtitle),
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(24.dp))

            ProfileCard(
                title = stringResource(R.string.register_profile_student),
                description = listOf(
                    stringResource(R.string.register_profile_student_f1),
                    stringResource(R.string.register_profile_student_f2),
                    stringResource(R.string.register_profile_student_f3),
                    stringResource(R.string.register_profile_student_f4)
                ),
                icon = Icons.Outlined.AutoStories,
                color = MediumBlue,
                isSelected = selectedProfile == UserRole.STUDENT,
                onClick = { selectedProfile = UserRole.STUDENT }
            )

            Spacer(modifier = Modifier.height(12.dp))

            ProfileCard(
                title = stringResource(R.string.register_profile_institution),
                description = listOf(
                    stringResource(R.string.register_profile_institution_f1),
                    stringResource(R.string.register_profile_institution_f2),
                    stringResource(R.string.register_profile_institution_f3),
                    stringResource(R.string.register_profile_institution_f4)
                ),
                icon = Icons.Outlined.BusinessCenter,
                color = CompanyGreen,
                isSelected = selectedProfile == UserRole.INSTITUTION,
                onClick = { selectedProfile = UserRole.INSTITUTION }
            )

            Spacer(modifier = Modifier.weight(1f))
            Spacer(modifier = Modifier.height(32.dp))

            Column(modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp)) {
                val isContinueEnabled = selectedProfile != null
                
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .then(
                            if (isContinueEnabled) Modifier.background(Fade2) 
                            else Modifier.background(Color.LightGray.copy(alpha = 0.5f))
                        )
                ) {
                    Button(
                        onClick = { selectedProfile?.let { onContinueClick(it) } },
                        enabled = isContinueEnabled,
                        modifier = Modifier.fillMaxSize(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent,
                            disabledContainerColor = Color.Transparent
                        ),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues()
                    ) {
                        Text(
                            text = stringResource(R.string.register_continue),
                            color = if (isContinueEnabled) Color.White else Color.Gray.copy(alpha = 0.8f),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(12.dp))

                OutlinedButton(
                    onClick = onBackToLogin,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, Fade2),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MediumBlue
                    ),
                    contentPadding = PaddingValues()
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
fun ProfileCard(
    title: String,
    description: List<String>,
    icon: ImageVector,
    color: Color,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = if (isSelected) color.copy(alpha = 0.05f) else Color.White

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(155.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor)
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) color else color.copy(alpha = 0.5f),
                shape = RoundedCornerShape(12.dp)
            )
            .clickable { onClick() }
    ) {
        Column(modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = color,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            description.forEach { item ->
                Text(
                    text = "-$item",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.DarkGray,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(start = 4.dp, bottom = 2.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RegisterScreenPreview() {
    LinkStageTheme {
        RegisterScreen()
    }
}
