package turmaA.grupoB.LinkStage.data.repository.storage

import io.github.jan.supabase.storage.storage
import turmaA.grupoB.LinkStage.data.remote.supabase.SupabaseClientProvider

class StorageRepository : StorageRepositoryInterface {
    private val supabase = SupabaseClientProvider.client

    override suspend fun uploadFile(
        bucket: String,
        path: String,
        bytes: ByteArray,
        upsert: Boolean,
    ): String {
        supabase.storage.from(bucket).upload(path, bytes) {
            this.upsert = upsert
        }
        return path
    }
}
