package com.example.users

import com.github.benmanes.caffeine.cache.AsyncLoadingCache
import com.github.benmanes.caffeine.cache.Caffeine
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.future.await
import kotlinx.coroutines.future.future
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.*
import java.util.concurrent.TimeUnit

object UserCache : KoinComponent {

    private val repository: Repository by inject()

    private val cacheContext = CoroutineScope(Dispatchers.Default + SupervisorJob())

    private val store: AsyncLoadingCache<UUID, User?> =
        Caffeine.newBuilder().expireAfterWrite(10L, TimeUnit.MINUTES).buildAsync { key, _ ->
            cacheContext.future {
                repository.findUserById(key)
            }
        }

    suspend fun get(key: UUID) = withContext(cacheContext.coroutineContext) {
        store[key].await()
    }
}