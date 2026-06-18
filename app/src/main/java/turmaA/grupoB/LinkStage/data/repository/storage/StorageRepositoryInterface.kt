package turmaA.grupoB.LinkStage.data.repository.storage

interface StorageRepositoryInterface {
    suspend fun uploadFile(
        bucket: String,
        path: String,
        bytes: ByteArray,
        upsert: Boolean = false,
    ): String
}
