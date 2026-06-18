package turmaA.grupoB.LinkStage.ui.admin.students

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.FilterList
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.outlined.KeyboardArrowUp
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateMap
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import androidx.lifecycle.viewmodel.compose.viewModel
import turmaA.grupoB.LinkStage.R
import turmaA.grupoB.LinkStage.ui.admin.AdminMentor
import turmaA.grupoB.LinkStage.ui.admin.AdminRoutes
import turmaA.grupoB.LinkStage.ui.admin.AdminStudent
import turmaA.grupoB.LinkStage.ui.admin.avatarColors
import turmaA.grupoB.LinkStage.ui.admin.sampleMentorsList
import turmaA.grupoB.LinkStage.ui.admin.sampleStudentsList
import turmaA.grupoB.LinkStage.viewmodel.admin.AdminAccountUiState
import turmaA.grupoB.LinkStage.viewmodel.admin.AdminAccountViewModel
import turmaA.grupoB.LinkStage.viewmodel.admin.AdminAccountViewModelFactory
import turmaA.grupoB.LinkStage.viewmodel.admin.AdminUsersUiState
import turmaA.grupoB.LinkStage.viewmodel.admin.AdminUsersViewModel
import turmaA.grupoB.LinkStage.viewmodel.admin.AdminUsersViewModelFactory
import turmaA.grupoB.LinkStage.ui.common.CommonTopBar
import turmaA.grupoB.LinkStage.ui.common.LinkStageDialog
import turmaA.grupoB.LinkStage.ui.common.LinkStageTabRow
import turmaA.grupoB.LinkStage.ui.common.TemporaryPasswordDialog
import turmaA.grupoB.LinkStage.ui.theme.BackgroundLight
import turmaA.grupoB.LinkStage.ui.theme.BorderGrey
import turmaA.grupoB.LinkStage.ui.theme.DarkBlue
import turmaA.grupoB.LinkStage.ui.theme.DarkGrey
import turmaA.grupoB.LinkStage.ui.theme.LightBlue
import turmaA.grupoB.LinkStage.ui.theme.MediumBlue
import turmaA.grupoB.LinkStage.ui.theme.Red

@Composable
fun StudentsAdminScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
) {
    val usersViewModel: AdminUsersViewModel = viewModel(factory = AdminUsersViewModelFactory())
    val usersUiState by usersViewModel.uiState.collectAsState()
    var selectedTab by rememberSaveable { mutableIntStateOf(0) }
    var searchQuery by rememberSaveable { mutableStateOf("") }
    var showFilterDialog by remember { mutableStateOf(false) }

    // State for filters
    var studentFilterStatus by rememberSaveable { mutableStateOf("") }
    var studentFilterInstitution by rememberSaveable { mutableStateOf("") }
    var studentFilterCourse by rememberSaveable { mutableStateOf("") }

    var mentorFilterInstitution by rememberSaveable { mutableStateOf("") }
    var mentorFilterDepartment by rememberSaveable { mutableStateOf("") }

    LaunchedEffect(Unit) {
        usersViewModel.loadUsers()
    }

    LaunchedEffect(searchQuery, studentFilterStatus, studentFilterInstitution, studentFilterCourse) {
        usersViewModel.filterStudents(
            query = searchQuery,
            institution = studentFilterInstitution,
            course = studentFilterCourse,
            internshipStatus = studentFilterStatus,
        )
    }

    LaunchedEffect(searchQuery, mentorFilterInstitution, mentorFilterDepartment) {
        usersViewModel.filterMentors(
            query = searchQuery,
            institution = mentorFilterInstitution,
            department = mentorFilterDepartment,
        )
    }

    if (showFilterDialog) {
        if (selectedTab == 0) {
                StudentFilterDialog(
                    currentStatus = studentFilterStatus,
                    currentInstitution = studentFilterInstitution,
                    currentCourse = studentFilterCourse,
                    institutionOptions = filteredStudentInstitutions(usersUiState),
                    courseOptions = filteredStudentCourses(usersUiState),
                    onApply = { status, institution, course ->
                    studentFilterStatus = status
                    studentFilterInstitution = institution
                    studentFilterCourse = course
                    showFilterDialog = false
                },
                onDismiss = { showFilterDialog = false },
            )
        } else {
            MentorFilterDialog(
                currentInstitution = mentorFilterInstitution,
                currentDepartment = mentorFilterDepartment,
                institutionOptions = filteredMentorInstitutions(usersUiState),
                onApply = { institution, department ->
                    mentorFilterInstitution = institution
                    mentorFilterDepartment = department
                    showFilterDialog = false
                },
                onDismiss = { showFilterDialog = false },
            )
        }
    }

    Scaffold(
        modifier = modifier,
        containerColor = BackgroundLight,
        topBar = {
            Column(modifier = Modifier.background(BackgroundLight)) {
                CommonTopBar()

                Text(
                    text = stringResource(R.string.admin_users_title),
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = DarkBlue,
                    ),
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
                )

                Spacer(modifier = Modifier.height(8.dp))
                SearchBarWithFilter(
                    query = searchQuery,
                    onQueryChange = { searchQuery = it },
                    placeholder = if (selectedTab == 0) stringResource(R.string.admin_users_search_students) else stringResource(R.string.admin_users_search_advisors),
                    onFilterClick = { showFilterDialog = true },
                )
                Spacer(modifier = Modifier.height(8.dp))
                LinkStageTabRow(
                    tabs = listOf(stringResource(R.string.admin_tab_students), stringResource(R.string.admin_tab_mentors)),
                    selectedIndex = selectedTab,
                    onTabSelected = { selectedTab = it },
                )
            }
        },
    ) { innerPadding ->
        when (selectedTab) {
            0 -> StudentsTabContent(
                navController = navController,
                searchQuery = searchQuery,
                filterStatus = studentFilterStatus,
                filterInstitution = studentFilterInstitution,
                filterCourse = studentFilterCourse,
                modifier = Modifier.padding(top = innerPadding.calculateTopPadding()),
            )
            1 -> MentorsTabContent(
                navController = navController,
                searchQuery = searchQuery,
                filterInstitution = mentorFilterInstitution,
                filterDepartment = mentorFilterDepartment,
                modifier = Modifier.padding(top = innerPadding.calculateTopPadding()),
            )
        }
    }
}

private fun filteredStudentInstitutions(uiState: AdminUsersUiState): List<String> = when (uiState) {
    is AdminUsersUiState.Success -> uiState.students.map { it.institution }.distinct()
    is AdminUsersUiState.StudentsSuccess -> uiState.students.map { it.institution }.distinct()
    else -> sampleStudentsList.map { it.institution }.distinct()
}

private fun filteredStudentCourses(uiState: AdminUsersUiState): List<String> = when (uiState) {
    is AdminUsersUiState.Success -> uiState.students.map { it.course }.distinct()
    is AdminUsersUiState.StudentsSuccess -> uiState.students.map { it.course }.distinct()
    else -> sampleStudentsList.map { it.course }.distinct()
}

private fun filteredMentorInstitutions(uiState: AdminUsersUiState): List<String> = when (uiState) {
    is AdminUsersUiState.Success -> uiState.mentors.map { it.institution }.distinct()
    is AdminUsersUiState.MentorsSuccess -> uiState.mentors.map { it.institution }.distinct()
    else -> sampleMentorsList.map { it.institution }.distinct()
}

// region Students Tab

@Composable
private fun StudentsTabContent(
    navController: NavController,
    searchQuery: String,
    filterStatus: String,
    filterInstitution: String,
    filterCourse: String,
    modifier: Modifier = Modifier,
) {
    var showAddDialog by remember { mutableStateOf(false) }
    var newAccountEmail by remember { mutableStateOf("") }

    val usersViewModel = viewModel<AdminUsersViewModel>(factory = AdminUsersViewModelFactory())
    val usersUiState by usersViewModel.uiState.collectAsState()
    val accountViewModel = viewModel<AdminAccountViewModel>(factory = AdminAccountViewModelFactory())
    val accountUiState by accountViewModel.uiState.collectAsState()
    val filtered = when (val state = usersUiState) {
        is AdminUsersUiState.StudentsSuccess -> state.students
        is AdminUsersUiState.Success -> state.students
        else -> emptyList()
    }

    val grouped = filtered
        .groupBy { it.institution }
        .mapValues { (_, students) -> students.groupBy { it.course } }

    val expandedInstitutions = remember(filtered) {
        filtered.map { it.institution }.distinct().map { it to true }.toMutableStateMap()
    }

    val accountSuccess = accountUiState as? AdminAccountUiState.Success
    if (showAddDialog && accountSuccess == null) {
        AddStudentDialog(
            onDismiss = {
                showAddDialog = false
                accountViewModel.resetState()
            },
            onConfirm = { name, email, course, studentNumber ->
                newAccountEmail = email
                accountViewModel.createStudentAccount(name, email, course, studentNumber)
            },
            isSubmitting = accountUiState is AdminAccountUiState.Loading,
            errorMessage = (accountUiState as? AdminAccountUiState.Error)?.message,
        )
    }
    if (accountSuccess != null) {
        TemporaryPasswordDialog(
            email = newAccountEmail,
            password = accountSuccess.temporaryPassword,
            onDismiss = {
                showAddDialog = false
                accountViewModel.resetState()
                usersViewModel.loadUsers()
            },
        )
    }

    Scaffold(
        modifier = modifier,
        containerColor = BackgroundLight,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = LightBlue,
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp),
            ) {
                Icon(Icons.Default.Add, contentDescription = stringResource(R.string.admin_users_add_student))
            }
        },
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            grouped.forEach { (institution, courseMap) ->
                val totalStudents = courseMap.values.sumOf { it.size }
                val isExpanded = expandedInstitutions[institution] == true

                item(key = "inst_$institution") {
                    InstitutionHeader(
                        name = institution,
                        count = totalStudents,
                        label = stringResource(R.string.admin_users_student_label),
                        isExpanded = isExpanded,
                        onClick = {
                            expandedInstitutions[institution] = !isExpanded
                        },
                    )
                }

                if (isExpanded) {
                    courseMap.forEach { (course, students) ->
                        item(key = "course_${institution}_$course") {
                            CourseHeader(name = course, count = students.size)
                        }
                        items(students, key = { "student_${it.id}" }) { student ->
                            StudentListItem(
                                student = student,
                                onClick = {
                                    navController.navigate(AdminRoutes.studentDetail(student.id))
                                },
                            )
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
    }
}

@Composable
private fun CourseHeader(name: String, count: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White.copy(alpha = 0.7f))
            .padding(start = 32.dp, end = 16.dp, top = 10.dp, bottom = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = name,
            color = MediumBlue,
            fontWeight = FontWeight.SemiBold,
            fontSize = 13.sp,
            modifier = Modifier.weight(1f),
        )
        Text(
            text = "$count",
            color = DarkGrey,
            fontSize = 12.sp,
        )
    }
}

@Composable
fun StudentListItem(
    student: AdminStudent,
    onClick: () -> Unit,
    showStatus: Boolean = true,
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .clickable { onClick() }
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(avatarColors[student.avatarColorIndex % avatarColors.size]),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = student.avatarInitials,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = student.name,
                    color = DarkBlue,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                )
                Text(
                    text = student.email,
                    color = DarkGrey,
                    fontSize = 12.sp,
                )
            }
            if (showStatus) {
                Column(horizontalAlignment = Alignment.End) {
                    if (student.hasActiveInternship) {
                        Text(
                            text = stringResource(R.string.student_status_internship),
                            color = LightBlue,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                        )
                    } else {
                        Text(
                            text = stringResource(R.string.admin_users_applications_count, student.applicationCount),
                            color = DarkGrey,
                            fontSize = 11.sp,
                        )
                    }
                }
            }
        }
        HorizontalDivider(color = BorderGrey)
    }
}

// endregion

// region Mentors Tab

@Composable
private fun MentorsTabContent(
    navController: NavController,
    searchQuery: String,
    filterInstitution: String,
    filterDepartment: String,
    modifier: Modifier = Modifier,
) {
    var showAddDialog by remember { mutableStateOf(false) }
    var newAccountEmail by remember { mutableStateOf("") }

    val usersViewModel = viewModel<AdminUsersViewModel>(factory = AdminUsersViewModelFactory())
    val usersUiState by usersViewModel.uiState.collectAsState()
    val accountViewModel = viewModel<AdminAccountViewModel>(factory = AdminAccountViewModelFactory())
    val accountUiState by accountViewModel.uiState.collectAsState()
    val filtered = when (val state = usersUiState) {
        is AdminUsersUiState.MentorsSuccess -> state.mentors
        is AdminUsersUiState.Success -> state.mentors
        else -> emptyList()
    }

    val grouped = filtered.groupBy { it.institution }

    val expandedInstitutions = remember(filtered) {
        filtered.map { it.institution }.distinct().map { it to true }.toMutableStateMap()
    }

    val accountSuccess = accountUiState as? AdminAccountUiState.Success
    if (showAddDialog && accountSuccess == null) {
        AddMentorDialog(
            onDismiss = {
                showAddDialog = false
                accountViewModel.resetState()
            },
            onConfirm = { name, email, department ->
                newAccountEmail = email
                accountViewModel.createMentorAccount(name, email, department)
            },
            isSubmitting = accountUiState is AdminAccountUiState.Loading,
            errorMessage = (accountUiState as? AdminAccountUiState.Error)?.message,
        )
    }
    if (accountSuccess != null) {
        TemporaryPasswordDialog(
            email = newAccountEmail,
            password = accountSuccess.temporaryPassword,
            onDismiss = {
                showAddDialog = false
                accountViewModel.resetState()
                usersViewModel.loadUsers()
            },
        )
    }

    Scaffold(
        modifier = modifier,
        containerColor = BackgroundLight,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = LightBlue,
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp),
            ) {
                Icon(Icons.Default.Add, contentDescription = stringResource(R.string.admin_users_add_mentor))
            }
        },
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            grouped.forEach { (institution, mentors) ->
                val isExpanded = expandedInstitutions[institution] == true

                item(key = "mentor_inst_$institution") {
                    InstitutionHeader(
                        name = institution,
                        count = mentors.size,
                        label = stringResource(R.string.admin_users_mentor_label),
                        isExpanded = isExpanded,
                        onClick = {
                            expandedInstitutions[institution] = !isExpanded
                        },
                    )
                }

                if (isExpanded) {
                    items(mentors, key = { "mentor_${it.id}" }) { mentor ->
                        MentorListItem(
                            mentor = mentor,
                            onClick = {
                                navController.navigate(AdminRoutes.mentorDetail(mentor.id))
                            },
                        )
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
    }
}

@Composable
fun MentorListItem(
    mentor: AdminMentor,
    onClick: () -> Unit,
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .clickable { onClick() }
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(avatarColors[mentor.avatarColorIndex % avatarColors.size]),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = mentor.avatarInitials,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = mentor.name,
                    color = DarkBlue,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                )
                Text(
                    text = mentor.department,
                    color = DarkGrey,
                    fontSize = 12.sp,
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = stringResource(R.string.admin_users_active_students_count, mentor.activeStudentsCount),
                    color = LightBlue,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 12.sp,
                )
                Text(
                    text = stringResource(R.string.admin_users_active),
                    color = DarkGrey,
                    fontSize = 11.sp,
                )
            }
        }
        HorizontalDivider(color = BorderGrey)
    }
}

// endregion

// region Shared Components

@Composable
private fun SearchBarWithFilter(
    query: String,
    onQueryChange: (String) -> Unit,
    placeholder: String,
    onFilterClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        OutlinedTextField(
            value = query,
            onValueChange = onQueryChange,
            modifier = Modifier.weight(1f),
            placeholder = { Text(placeholder, color = DarkGrey) },
            leadingIcon = {
                Icon(Icons.Outlined.Search, contentDescription = null, tint = DarkGrey)
            },
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = BorderGrey,
                focusedBorderColor = DarkBlue,
                unfocusedContainerColor = Color.White,
                focusedContainerColor = Color.White,
            ),
            singleLine = true,
        )

        Box(
            modifier = Modifier
                .size(52.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(DarkBlue)
                .clickable { onFilterClick() },
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                Icons.Outlined.FilterList,
                contentDescription = stringResource(R.string.discover_filters),
                tint = Color.White,
                modifier = Modifier.size(22.dp),
            )
        }
    }
}

@Composable
private fun InstitutionHeader(
    name: String,
    count: Int,
    label: String,
    isExpanded: Boolean,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(BackgroundLight)
            .clickable { onClick() }
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = name,
            color = DarkBlue,
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp,
            modifier = Modifier.weight(1f),
        )
        Text(
            text = "$count $label",
            color = DarkGrey,
            fontSize = 12.sp,
        )
        Spacer(modifier = Modifier.width(4.dp))
        Icon(
            imageVector = if (isExpanded) Icons.Outlined.KeyboardArrowUp
            else Icons.Outlined.KeyboardArrowDown,
            contentDescription = null,
            tint = DarkGrey,
            modifier = Modifier.size(20.dp),
        )
    }
}

// endregion

// region Dialogs

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun StudentFilterDialog(
    currentStatus: String,
    currentInstitution: String,
    currentCourse: String,
    institutionOptions: List<String>,
    courseOptions: List<String>,
    onApply: (status: String, institution: String, course: String) -> Unit,
    onDismiss: () -> Unit,
) {
    var status by remember { mutableStateOf(currentStatus) }
    var institution by remember { mutableStateOf(currentInstitution) }
    var course by remember { mutableStateOf(currentCourse) }
    var statusExpanded by remember { mutableStateOf(false) }
    var institutionExpanded by remember { mutableStateOf(false) }

    val allLabel = stringResource(R.string.admin_filter_all)
    val internshipLabel = stringResource(R.string.student_status_internship)
    val noInternshipLabel = stringResource(R.string.student_status_no_internship)
    val statusOptions = listOf(
        "" to allLabel,
        "Em estágio" to internshipLabel,
        "Sem estágio" to noInternshipLabel,
    )
    val institutionOptions = institutionOptions.ifEmpty { sampleStudentsList.map { it.institution }.distinct() }
    val courseOptions = courseOptions.ifEmpty { sampleStudentsList.map { it.course }.distinct() }

    LinkStageDialog(
        title = stringResource(R.string.filter_title),
        onConfirm = {
            onApply(status, institution, course)
        },
        onDismiss = onDismiss,
        confirmText = stringResource(R.string.filter_apply),
        dismissText = stringResource(R.string.common_cancel),
        content = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    turmaA.grupoB.LinkStage.ui.common.SectionLabel(stringResource(R.string.admin_filter_status))
                    ExposedDropdownMenuBox(
                        expanded = statusExpanded,
                        onExpandedChange = { statusExpanded = it },
                    ) {
                        OutlinedTextField(
                            value = statusOptions.firstOrNull { it.first == status }?.second ?: allLabel,
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = statusExpanded) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(MenuAnchorType.PrimaryNotEditable),
                            shape = RoundedCornerShape(10.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = LightBlue,
                                unfocusedBorderColor = BorderGrey,
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White,
                            ),
                            singleLine = true,
                        )
                        ExposedDropdownMenu(
                            expanded = statusExpanded,
                            onDismissRequest = { statusExpanded = false },
                        ) {
                            statusOptions.forEach { (key, label) ->
                                DropdownMenuItem(
                                    text = { Text(label) },
                                    onClick = {
                                        status = key
                                        statusExpanded = false
                                    },
                                )
                            }
                        }
                    }
                }

                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    turmaA.grupoB.LinkStage.ui.common.SectionLabel(stringResource(R.string.admin_inst_filter_institution))
                    ExposedDropdownMenuBox(
                        expanded = institutionExpanded,
                        onExpandedChange = { institutionExpanded = it },
                    ) {
                        OutlinedTextField(
                            value = institution.ifEmpty { allLabel },
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = institutionExpanded) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(MenuAnchorType.PrimaryNotEditable),
                            shape = RoundedCornerShape(10.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = LightBlue,
                                unfocusedBorderColor = BorderGrey,
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White,
                            ),
                            singleLine = true,
                        )
                        ExposedDropdownMenu(
                            expanded = institutionExpanded,
                            onDismissRequest = { institutionExpanded = false },
                        ) {
                            DropdownMenuItem(
                                text = { Text(allLabel) },
                                onClick = {
                                    institution = ""
                                    institutionExpanded = false
                                },
                            )
                            institutionOptions.forEach { option ->
                                DropdownMenuItem(
                                    text = { Text(option) },
                                    onClick = {
                                        institution = option
                                        institutionExpanded = false
                                    },
                                )
                            }
                        }
                    }
                }

                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    turmaA.grupoB.LinkStage.ui.common.SectionLabel("Curso")
                    OutlinedTextField(
                        value = course,
                        onValueChange = { course = it },
                        placeholder = { Text(stringResource(R.string.common_write_here), color = DarkGrey, fontSize = 14.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = LightBlue,
                            unfocusedBorderColor = BorderGrey,
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                        ),
                        singleLine = true,
                    )
                }
            }
        },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MentorFilterDialog(
    currentInstitution: String,
    currentDepartment: String,
    institutionOptions: List<String>,
    onApply: (institution: String, department: String) -> Unit,
    onDismiss: () -> Unit,
) {
    var institution by remember { mutableStateOf(currentInstitution) }
    var department by remember { mutableStateOf(currentDepartment) }
    var institutionExpanded by remember { mutableStateOf(false) }

    val allLabel = stringResource(R.string.admin_filter_all)
    val institutionOptions = institutionOptions.ifEmpty { sampleMentorsList.map { it.institution }.distinct() }

    LinkStageDialog(
        title = stringResource(R.string.filter_title),
        onConfirm = { onApply(institution, department) },
        onDismiss = onDismiss,
        confirmText = stringResource(R.string.filter_apply),
        dismissText = stringResource(R.string.common_cancel),
        content = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    turmaA.grupoB.LinkStage.ui.common.SectionLabel(stringResource(R.string.admin_inst_filter_institution))
                    ExposedDropdownMenuBox(
                        expanded = institutionExpanded,
                        onExpandedChange = { institutionExpanded = it },
                    ) {
                        OutlinedTextField(
                            value = institution.ifEmpty { allLabel },
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = institutionExpanded) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(MenuAnchorType.PrimaryNotEditable),
                            shape = RoundedCornerShape(10.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = LightBlue,
                                unfocusedBorderColor = BorderGrey,
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White,
                            ),
                            singleLine = true,
                        )
                        ExposedDropdownMenu(
                            expanded = institutionExpanded,
                            onDismissRequest = { institutionExpanded = false },
                        ) {
                            DropdownMenuItem(
                                text = { Text(allLabel) },
                                onClick = {
                                    institution = ""
                                    institutionExpanded = false
                                },
                            )
                            institutionOptions.forEach { option ->
                                DropdownMenuItem(
                                    text = { Text(option) },
                                    onClick = {
                                        institution = option
                                        institutionExpanded = false
                                    },
                                )
                            }
                        }
                    }
                }

                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    turmaA.grupoB.LinkStage.ui.common.SectionLabel(stringResource(R.string.admin_detail_department))
                    OutlinedTextField(
                        value = department,
                        onValueChange = { department = it },
                        placeholder = { Text(stringResource(R.string.common_write_here), color = DarkGrey, fontSize = 14.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = LightBlue,
                            unfocusedBorderColor = BorderGrey,
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                        ),
                        singleLine = true,
                    )
                }
            }
        },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddStudentDialog(
    onDismiss: () -> Unit,
    onConfirm: (name: String, email: String, course: String, studentNumber: String) -> Unit,
    isSubmitting: Boolean,
    errorMessage: String?,
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var course by remember { mutableStateOf("") }
    var studentNumber by remember { mutableStateOf("") }

    LinkStageDialog(
        onDismiss = onDismiss,
        title = stringResource(R.string.admin_users_add_student_title),
        onConfirm = { onConfirm(name, email, course, studentNumber) },
        confirmText = stringResource(R.string.admin_inst_add_button),
        dismissText = stringResource(R.string.common_cancel),
        confirmEnabled = !isSubmitting &&
            name.isNotBlank() && email.isNotBlank() && course.isNotBlank() && studentNumber.isNotBlank(),
        content = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text(stringResource(R.string.admin_users_name)) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    singleLine = true,
                )
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text(stringResource(R.string.admin_inst_add_email)) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    singleLine = true,
                )
                OutlinedTextField(
                    value = course,
                    onValueChange = { course = it },
                    label = { Text(stringResource(R.string.admin_detail_course)) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    singleLine = true,
                )
                OutlinedTextField(
                    value = studentNumber,
                    onValueChange = { studentNumber = it },
                    label = { Text(stringResource(R.string.admin_users_student_number)) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    singleLine = true,
                )
                if (errorMessage != null) {
                    Text(
                        text = errorMessage,
                        color = Red,
                        fontSize = 12.sp,
                    )
                }
            }
        }
    )
}

@Composable
private fun AddMentorDialog(
    onDismiss: () -> Unit,
    onConfirm: (name: String, email: String, department: String) -> Unit,
    isSubmitting: Boolean,
    errorMessage: String?,
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var department by remember { mutableStateOf("") }

    LinkStageDialog(
        onDismiss = onDismiss,
        title = stringResource(R.string.admin_users_add_mentor_title),
        onConfirm = { onConfirm(name, email, department) },
        confirmText = stringResource(R.string.admin_inst_add_button),
        dismissText = stringResource(R.string.common_cancel),
        confirmEnabled = !isSubmitting && name.isNotBlank() && email.isNotBlank() && department.isNotBlank(),
        content = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text(stringResource(R.string.admin_users_name)) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    singleLine = true,
                )
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text(stringResource(R.string.admin_inst_add_email)) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    singleLine = true,
                )
                OutlinedTextField(
                    value = department,
                    onValueChange = { department = it },
                    label = { Text(stringResource(R.string.admin_detail_department)) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    singleLine = true,
                )
                if (errorMessage != null) {
                    Text(
                        text = errorMessage,
                        color = Red,
                        fontSize = 12.sp,
                    )
                }
            }
        }
    )
}

// endregion

@Preview(showSystemUi = true)
@Composable
private fun StudentsAdminScreenPreview() {
    MaterialTheme {
        StudentsAdminScreen(navController = rememberNavController())
    }
}
