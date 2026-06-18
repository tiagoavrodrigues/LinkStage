package turmaA.grupoB.LinkStage.data.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface AtividadeDAO{
    @Query("SELECT * FROM atividades_aluno ORDER BY createdAt ASC")
    fun getAtividades(): Flow<List<ActivityLogEntity>>

    @Query("SELECT * FROM atividades_aluno WHERE internshipId = :internshipId ORDER BY activityDate ASC")
    suspend fun getAtividadesByInternship(internshipId: String): List<ActivityLogEntity>

    @Query("SELECT * FROM atividades_aluno WHERE id = :activityLogId LIMIT 1")
    suspend fun getAtividadeById(activityLogId: String): ActivityLogEntity?

    @Query("SELECT * FROM atividades_aluno WHERE pendingSync = 1 ORDER BY createdAt ASC")
    suspend fun getPendingAtividades(): List<ActivityLogEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(atividade: ActivityLogEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(atividades: List<ActivityLogEntity>)

    @Delete
    suspend fun delete(atividade: ActivityLogEntity)

    @Update
    suspend fun update(atividade: ActivityLogEntity)
}
