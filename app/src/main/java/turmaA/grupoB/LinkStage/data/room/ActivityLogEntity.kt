package turmaA.grupoB.LinkStage.data.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "atividades_aluno")
data class ActivityLogEntity(
    @PrimaryKey val id: String,
    val internshipId: String,
    val studentId: String,
    val description: String,
    val activityDate: String,
    val hours: Double? = null,
    val type: String? = null,
    val location: String? = null,
    val attachmentUrl: String? = null,
    val createdAt: String,
    val pendingSync: Boolean = false,
)
