package com.tappli.state

interface DataSourceMapper<DataSourceType, CacheType> {
    fun toDataSourceType(cache: CacheType): DataSourceType
}

interface DataSourceAccessor<DataType, KeyType> {
    suspend fun get(key: KeyType): DataType
}

interface CacheAccessor<DataSourceType, CacheType, KeyType> { // キャッシュは判断しない
    suspend fun get(key: KeyType): CacheType? // キャッシュを返せるか、返せないかなので null で良い？
    suspend fun put(key: KeyType, value: DataSourceType)
    suspend fun delete(key: KeyType)
}

interface SimpleCacheAccessor<DataType, KeyType> : CacheAccessor<DataType, DataType, KeyType>

abstract class Judge<CacheType, DataSourceType>(
    protected val value: CacheType
) {
    abstract fun hasEnabledValue(): Boolean
    abstract fun get(): DataSourceType
}


class Selector<DataSourceType, CacheType, KeyType>(
    private val dataSourceAccessor: DataSourceAccessor<DataSourceType, KeyType>,
    private val cacheAccessor: CacheAccessor<DataSourceType, CacheType, KeyType>,
    private val getJudge: (CacheType) -> Judge<CacheType, DataSourceType>,
) : DataSourceAccessor<DataSourceType, KeyType> {

    override suspend fun get(key: KeyType): DataSourceType {
        val cacheData = cacheAccessor.get(key)
        if (cacheData != null) {
            val judge = getJudge(cacheData)
            if (judge.hasEnabledValue()) {
                return judge.get()
            }
        }

        val value = dataSourceAccessor.get(key)
        cacheAccessor.put(key, value)
        return value
    }
}
