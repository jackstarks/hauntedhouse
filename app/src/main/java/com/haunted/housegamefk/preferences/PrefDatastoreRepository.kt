package com.haunted.housegamefk.preferences

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.haunted.housegamefk.AppConstants
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PrefDatastoreRepository(val context: Context) {

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = AppConstants.PREF_KEY)

    companion object {
        private val ANSWER_KEY = stringPreferencesKey(AppConstants.ANSWER_KEY)
        private val DEEP_LINK_KEY = stringPreferencesKey(AppConstants.DEEP_LINK_KEY)
        private val STATUS_KEY = stringPreferencesKey(AppConstants.STATUS_KEY)
        private val CAMPAIGN_KEY = stringPreferencesKey(AppConstants.CAMPAIGN_KEY)
        private val ADSET_KEY = stringPreferencesKey(AppConstants.ADSET_KEY)
        private val AD_ID_KEY = stringPreferencesKey(AppConstants.AD_ID_KEY)
        private val CHANNEL_KEY = stringPreferencesKey(AppConstants.CHANNEL_KEY)
        private val PARAM_KEY = stringPreferencesKey(AppConstants.PARAM_KEY)
        private val USER_ID_KEY = stringPreferencesKey(AppConstants.USER_ID_KEY)
    }

    suspend fun saveAnswer(value: String) {
        context.dataStore.edit {
            it[ANSWER_KEY] = value
        }
    }

    val answer: Flow<String> = context.dataStore.data
        .map {
            it[ANSWER_KEY] ?: ""
        }

    suspend fun saveDeeplink(value: String) {

        context.dataStore.edit {
            it[DEEP_LINK_KEY] = value
        }
    }

    val deeplink: Flow<String> = context.dataStore.data
        .map {
            it[DEEP_LINK_KEY] ?: ""
        }

    suspend fun saveStatus(answer: String) {
        context.dataStore.edit {
            it[STATUS_KEY] = answer
        }
    }

    val status: Flow<String> = context.dataStore.data
        .map {
            it[STATUS_KEY] ?: ""
        }

    suspend fun saveCampaign(value: String) {
        context.dataStore.edit {
            it[CAMPAIGN_KEY] = value
        }
    }

    val campaign: Flow<String> = context.dataStore.data
        .map {
            it[CAMPAIGN_KEY] ?: ""
        }

    suspend fun saveAdset(value: String) {
        context.dataStore.edit {
            it[ADSET_KEY] = value
        }
    }

    val adset: Flow<String> = context.dataStore.data
        .map {
            it[ADSET_KEY] ?: ""
        }

    suspend fun saveAdid(value: String) {
        context.dataStore.edit {
            it[AD_ID_KEY] = value
        }
    }

    val adid: Flow<String> = context.dataStore.data
        .map {
            it[AD_ID_KEY] ?: ""
        }

    suspend fun saveChannel(value: String) {
        context.dataStore.edit {
            it[CHANNEL_KEY] = value
        }
    }

    val channel: Flow<String> = context.dataStore.data
        .map {
            it[CHANNEL_KEY] ?: ""
        }

    suspend fun saveParam(map: HashMap<String, String>) {
        val jsonString: String = Gson().toJson(map)
        Log.e("saved", jsonString)
        context.dataStore.edit {
            it[PARAM_KEY] = jsonString
        }
    }


    val param: Flow<HashMap<String, String>> = context.dataStore.data
        .map {
            val jsonString = it[PARAM_KEY] ?: ""
            if (jsonString.isNotEmpty()) {
                val listType = object : TypeToken<HashMap<String, String>>() {}.type
                return@map Gson().fromJson(jsonString, listType)
            } else {
                return@map HashMap<String, String>()
            }

        }

    suspend fun saveUserId(value: String) {
        context.dataStore.edit {
            it[USER_ID_KEY] = value
        }
    }

    val userId: Flow<String> = context.dataStore.data
        .map {
            it[USER_ID_KEY] ?: ""
        }
}