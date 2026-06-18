package turmaA.grupoB.LinkStage.data.repository.student

import io.github.jan.supabase.postgrest.from
import turmaA.grupoB.LinkStage.data.remote.model.user.CreateStudentInput
import turmaA.grupoB.LinkStage.data.remote.model.user.StudentModel
import turmaA.grupoB.LinkStage.data.remote.supabase.SupabaseClientProvider


class StudentRepository : StudentRepositoryInterface {

    private val supabase = SupabaseClientProvider.client

    override suspend fun getStudents(): List<StudentModel> {
        return supabase
            .from("students")
            .select()
            .decodeList<StudentModel>()
    }

    override suspend fun getStudentById(studentId: String): StudentModel? {
        return supabase
            .from("students")
            .select {
                filter {
                    eq("id", studentId)
                }
            }
            .decodeList<StudentModel>()
            .firstOrNull()
    }

    override suspend fun getStudentByUserId(userId: String): StudentModel? {
        return supabase
            .from("students")
            .select {
                filter {
                    eq("user_id", userId)
                }
            }
            .decodeList<StudentModel>()
            .firstOrNull()
    }

    override suspend fun getStudentByNumber(studentNumber: String): StudentModel? {
        return supabase
            .from("students")
            .select {
                filter {
                    eq("student_number", studentNumber)
                }
            }
            .decodeList<StudentModel>()
            .firstOrNull()
    }

    override suspend fun getStudentsByCourse(course: String): List<StudentModel> {
        return supabase
            .from("students")
            .select {
                filter {
                    eq("course", course)
                }
            }
            .decodeList<StudentModel>()
    }

    override suspend fun createStudent(input: CreateStudentInput): StudentModel {
        return supabase
            .from("students")
            .insert(input) {
                select()
            }
            .decodeSingle<StudentModel>()
    }
}