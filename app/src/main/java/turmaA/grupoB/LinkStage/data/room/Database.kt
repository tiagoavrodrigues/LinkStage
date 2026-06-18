package turmaA.grupoB.LinkStage.data.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase


@Database(
    entities = [ActivityLogEntity::class],
    version = 3,
    exportSchema = false
)
abstract class AtDatabase: RoomDatabase(){
    abstract fun atividadeDAO(): AtividadeDAO
    
    companion object{
        @Volatile
        private var INSTANCE: AtDatabase? = null

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "ALTER TABLE atividades_aluno ADD COLUMN pendingSync INTEGER NOT NULL DEFAULT 0"
                )
            }
        }

        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "ALTER TABLE atividades_aluno ADD COLUMN attachmentUrl TEXT"
                )
            }
        }
        
        fun getDatabase(context: Context): AtDatabase{
            return INSTANCE ?: synchronized(this){
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AtDatabase::class.java,
                    "atividades_aluno"
                )
                    .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
                    .build()
                
                INSTANCE = instance
                instance
            }
        }
    }
}
