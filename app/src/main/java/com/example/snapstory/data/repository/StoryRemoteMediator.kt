package com.example.snapstory.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.example.snapstory.data.local.db.SnapStoryDb
import com.example.snapstory.data.local.db.entity.RemoteKey
import com.example.snapstory.data.local.preferences.UserSessionPreference
import com.example.snapstory.data.remote.api.ApiService
import com.example.snapstory.data.remote.model.StoryItem
import kotlinx.coroutines.flow.first

@OptIn(ExperimentalPagingApi::class)
class StoryRemoteMediator(
    private val snapStoryDb: SnapStoryDb,
    private val userSessionPreference: UserSessionPreference,
    private val apiService: ApiService
) : RemoteMediator<Int, StoryItem>() {

    override suspend fun initialize(): InitializeAction {
        return InitializeAction.LAUNCH_INITIAL_REFRESH
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, StoryItem>
    ): MediatorResult {
        val page = when (loadType) {
            LoadType.REFRESH -> {
                val remoteKey = getRemoteKeyClosestToCurrentPosition(state)
                remoteKey?.nextKey?.minus(1) ?: INITIAL_PAGE_INDEX
            }

            LoadType.PREPEND -> {
                val remoteKey = getRemoteKeyForFirstItem(state)
                remoteKey?.prevKey ?: return MediatorResult.Success(endOfPaginationReached = remoteKey != null)
            }

            LoadType.APPEND -> {
                val remoteKeys = getRemoteKeyForLastItem(state)
                remoteKeys?.nextKey ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
            }
        }

        try {
            val token = userSessionPreference.getUserToken().first()
            val responseData = apiService.getAllStory("Bearer $token",page, state.config.pageSize)
            val endOfPaginationReached = responseData.listStory.isEmpty()
            val storyItem = responseData.listStory.map {
                StoryItem(it.id, it.name, it.photoUrl, it.createdAt, it.description, it.lat, it.lon)
            }

            snapStoryDb.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    snapStoryDb.remoteKeyDao().deleteRemoteKey()
                    snapStoryDb.storyDao().deleteAll()
                }
                val prevKey = if (page == 1) null else page - 1
                val nextKey = if (endOfPaginationReached) null else page + 1
                val keys = responseData.listStory.map {
                    RemoteKey(id = it.id, prevKey = prevKey, nextKey = nextKey)
                }
                snapStoryDb.remoteKeyDao().insertAll(keys)
                snapStoryDb.storyDao().insertStories(storyItem)
            }
            return MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        } catch (exception: Exception) {
            return MediatorResult.Error(exception)
        }
    }


    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, StoryItem>): RemoteKey? {
        return state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()?.let { data ->
            snapStoryDb.remoteKeyDao().getRemoteKeyId(data.id)
        }
    }

    private suspend fun getRemoteKeyForFirstItem(state: PagingState<Int, StoryItem>): RemoteKey? {
        return state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()?.let { data ->
            snapStoryDb.remoteKeyDao().getRemoteKeyId(data.id)
        }
    }

    private suspend fun getRemoteKeyClosestToCurrentPosition(state: PagingState<Int, StoryItem>): RemoteKey? {
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.id?.let { id ->
                snapStoryDb.remoteKeyDao().getRemoteKeyId(id)
            }
        }
    }

    private companion object {
        const val INITIAL_PAGE_INDEX = 1
    }
}