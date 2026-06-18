package turmaA.grupoB.LinkStage.viewmodel.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import turmaA.grupoB.LinkStage.data.repository.application.ApplicationRepositoryInterface
import turmaA.grupoB.LinkStage.data.repository.auth.AuthRepositoryInterface
import turmaA.grupoB.LinkStage.data.repository.institution.InstitutionRepositoryInterface
import turmaA.grupoB.LinkStage.data.repository.internship.InternshipRepositoryInterface
import turmaA.grupoB.LinkStage.data.repository.offer.OfferRepositoryInterface
import turmaA.grupoB.LinkStage.data.repository.profile.ProfileRepositoryInterface
import turmaA.grupoB.LinkStage.data.repository.student.StudentRepositoryInterface
import turmaA.grupoB.LinkStage.data.repository.supervisor.SupervisorRepositoryInterface

class AdminDashboardViewModelFactory(
    private val studentRepository: StudentRepositoryInterface,
    private val supervisorRepository: SupervisorRepositoryInterface,
    private val institutionRepository: InstitutionRepositoryInterface,
    private val profileRepository: ProfileRepositoryInterface,
    private val applicationRepository: ApplicationRepositoryInterface,
    private val internshipRepository: InternshipRepositoryInterface,
) : ViewModelProvider.Factory {
    constructor() : this(
        turmaA.grupoB.LinkStage.data.repository.student.StudentRepository(),
        turmaA.grupoB.LinkStage.data.repository.supervisor.SupervisorRepository(),
        turmaA.grupoB.LinkStage.data.repository.institution.InstitutionRepository(),
        turmaA.grupoB.LinkStage.data.repository.profile.ProfileRepository(),
        turmaA.grupoB.LinkStage.data.repository.application.ApplicationRepository(),
        turmaA.grupoB.LinkStage.data.repository.internship.InternshipRepository(),
    )

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T = AdminDashboardViewModel(
        studentRepository,
        supervisorRepository,
        institutionRepository,
        profileRepository,
        applicationRepository,
        internshipRepository,
    ) as T
}

class AdminUsersViewModelFactory(
    private val studentRepository: StudentRepositoryInterface,
    private val supervisorRepository: SupervisorRepositoryInterface,
    private val institutionRepository: InstitutionRepositoryInterface,
    private val profileRepository: ProfileRepositoryInterface,
    private val internshipRepository: InternshipRepositoryInterface,
) : ViewModelProvider.Factory {
    constructor() : this(
        turmaA.grupoB.LinkStage.data.repository.student.StudentRepository(),
        turmaA.grupoB.LinkStage.data.repository.supervisor.SupervisorRepository(),
        turmaA.grupoB.LinkStage.data.repository.institution.InstitutionRepository(),
        turmaA.grupoB.LinkStage.data.repository.profile.ProfileRepository(),
        turmaA.grupoB.LinkStage.data.repository.internship.InternshipRepository(),
    )

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T = AdminUsersViewModel(
        studentRepository,
        supervisorRepository,
        institutionRepository,
        profileRepository,
        internshipRepository,
    ) as T
}

class AdminStudentDetailViewModelFactory(
    private val studentRepository: StudentRepositoryInterface,
    private val profileRepository: ProfileRepositoryInterface,
    private val internshipRepository: InternshipRepositoryInterface,
    private val applicationRepository: ApplicationRepositoryInterface,
    private val institutionRepository: InstitutionRepositoryInterface,
    private val offerRepository: OfferRepositoryInterface,
    private val authRepository: AuthRepositoryInterface,
) : ViewModelProvider.Factory {
    constructor() : this(
        turmaA.grupoB.LinkStage.data.repository.student.StudentRepository(),
        turmaA.grupoB.LinkStage.data.repository.profile.ProfileRepository(),
        turmaA.grupoB.LinkStage.data.repository.internship.InternshipRepository(),
        turmaA.grupoB.LinkStage.data.repository.application.ApplicationRepository(),
        turmaA.grupoB.LinkStage.data.repository.institution.InstitutionRepository(),
        turmaA.grupoB.LinkStage.data.repository.offer.OfferRepository(),
        turmaA.grupoB.LinkStage.data.repository.auth.AuthRepository(),
    )

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T = AdminStudentDetailViewModel(
        studentRepository,
        profileRepository,
        internshipRepository,
        applicationRepository,
        institutionRepository,
        offerRepository,
        authRepository,
    ) as T
}

class AdminMentorDetailViewModelFactory(
    private val supervisorRepository: SupervisorRepositoryInterface,
    private val profileRepository: ProfileRepositoryInterface,
    private val internshipRepository: InternshipRepositoryInterface,
    private val studentRepository: StudentRepositoryInterface,
    private val authRepository: AuthRepositoryInterface,
) : ViewModelProvider.Factory {
    constructor() : this(
        turmaA.grupoB.LinkStage.data.repository.supervisor.SupervisorRepository(),
        turmaA.grupoB.LinkStage.data.repository.profile.ProfileRepository(),
        turmaA.grupoB.LinkStage.data.repository.internship.InternshipRepository(),
        turmaA.grupoB.LinkStage.data.repository.student.StudentRepository(),
        turmaA.grupoB.LinkStage.data.repository.auth.AuthRepository(),
    )

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T = AdminMentorDetailViewModel(
        supervisorRepository,
        profileRepository,
        internshipRepository,
        studentRepository,
        authRepository,
    ) as T
}

class AdminInstitutionsViewModelFactory(
    private val institutionRepository: InstitutionRepositoryInterface,
    private val profileRepository: ProfileRepositoryInterface,
    private val studentRepository: StudentRepositoryInterface,
    private val supervisorRepository: SupervisorRepositoryInterface,
    private val internshipRepository: InternshipRepositoryInterface,
) : ViewModelProvider.Factory {
    constructor() : this(
        turmaA.grupoB.LinkStage.data.repository.institution.InstitutionRepository(),
        turmaA.grupoB.LinkStage.data.repository.profile.ProfileRepository(),
        turmaA.grupoB.LinkStage.data.repository.student.StudentRepository(),
        turmaA.grupoB.LinkStage.data.repository.supervisor.SupervisorRepository(),
        turmaA.grupoB.LinkStage.data.repository.internship.InternshipRepository(),
    )

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T = AdminInstitutionsViewModel(
        institutionRepository,
        profileRepository,
        studentRepository,
        supervisorRepository,
        internshipRepository,
    ) as T
}

class AdminInstitutionDetailViewModelFactory(
    private val institutionRepository: InstitutionRepositoryInterface,
    private val profileRepository: ProfileRepositoryInterface,
    private val studentRepository: StudentRepositoryInterface,
    private val supervisorRepository: SupervisorRepositoryInterface,
    private val internshipRepository: InternshipRepositoryInterface,
    private val authRepository: AuthRepositoryInterface,
) : ViewModelProvider.Factory {
    constructor() : this(
        turmaA.grupoB.LinkStage.data.repository.institution.InstitutionRepository(),
        turmaA.grupoB.LinkStage.data.repository.profile.ProfileRepository(),
        turmaA.grupoB.LinkStage.data.repository.student.StudentRepository(),
        turmaA.grupoB.LinkStage.data.repository.supervisor.SupervisorRepository(),
        turmaA.grupoB.LinkStage.data.repository.internship.InternshipRepository(),
        turmaA.grupoB.LinkStage.data.repository.auth.AuthRepository(),
    )

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T = AdminInstitutionDetailViewModel(
        institutionRepository,
        profileRepository,
        studentRepository,
        supervisorRepository,
        internshipRepository,
        authRepository,
    ) as T
}

class AdminInternshipDetailViewModelFactory(
    private val internshipRepository: InternshipRepositoryInterface,
    private val offerRepository: OfferRepositoryInterface,
    private val institutionRepository: InstitutionRepositoryInterface,
    private val studentRepository: StudentRepositoryInterface,
    private val supervisorRepository: SupervisorRepositoryInterface,
    private val profileRepository: ProfileRepositoryInterface,
    private val applicationRepository: ApplicationRepositoryInterface,
) : ViewModelProvider.Factory {
    constructor() : this(
        turmaA.grupoB.LinkStage.data.repository.internship.InternshipRepository(),
        turmaA.grupoB.LinkStage.data.repository.offer.OfferRepository(),
        turmaA.grupoB.LinkStage.data.repository.institution.InstitutionRepository(),
        turmaA.grupoB.LinkStage.data.repository.student.StudentRepository(),
        turmaA.grupoB.LinkStage.data.repository.supervisor.SupervisorRepository(),
        turmaA.grupoB.LinkStage.data.repository.profile.ProfileRepository(),
        turmaA.grupoB.LinkStage.data.repository.application.ApplicationRepository(),
    )

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T = AdminInternshipDetailViewModel(
        internshipRepository,
        offerRepository,
        institutionRepository,
        studentRepository,
        supervisorRepository,
        profileRepository,
        applicationRepository,
    ) as T
}

class AdminAccountViewModelFactory(
    private val authRepository: AuthRepositoryInterface,
    private val studentRepository: StudentRepositoryInterface,
    private val supervisorRepository: SupervisorRepositoryInterface,
    private val institutionRepository: InstitutionRepositoryInterface,
) : ViewModelProvider.Factory {
    constructor() : this(
        turmaA.grupoB.LinkStage.data.repository.auth.AuthRepository(),
        turmaA.grupoB.LinkStage.data.repository.student.StudentRepository(),
        turmaA.grupoB.LinkStage.data.repository.supervisor.SupervisorRepository(),
        turmaA.grupoB.LinkStage.data.repository.institution.InstitutionRepository(),
    )

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T = AdminAccountViewModel(
        authRepository,
        studentRepository,
        supervisorRepository,
        institutionRepository,
    ) as T
}
