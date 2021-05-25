package com.haunted.housegamefk.ui.fragments

import android.app.Application
import android.content.Context
import android.content.Intent
import android.telephony.TelephonyManager
import android.util.Log
import android.view.View
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.haunted.housegamefk.data.APIClient
import com.haunted.housegamefk.preferences.PrefDatastoreRepository
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first
import java.util.*

class SplashViewModel(application: Application) : AndroidViewModel(application) {
    private val tag = SplashViewModel::class.simpleName
    private var score: String = ""
    private val _eventGameFinish = MutableLiveData<Boolean>()
    val eventGameFinish: LiveData<Boolean>
        get() = _eventGameFinish
    private val _dataString = MutableLiveData<String>()
    val dataString: LiveData<String>
        get() = _dataString
    var datastoreRepository: PrefDatastoreRepository = PrefDatastoreRepository(getApplication())

    private fun getDataInfoModel(): LiveData<String> {
        runBlocking(Dispatchers.IO) {

            val status: String = datastoreRepository.status.first()
            val country = getUserCountry(getApplication())
            try {
                val response = APIClient.apiClient.getProjectList(status, country)
                // Check if response was successful.
                if (response.result.isNotEmpty()) {
                    score = response.result
                    datastoreRepository.saveAnswer(score)
                    Log.e(tag, "$score from server")
                    _dataString.postValue(score)
                } else {
                    _eventGameFinish.postValue(true)
                }
            } catch (e: Exception) {
                _eventGameFinish.postValue(true)
            }
        }

        return dataString


    }

    init {

        runBlocking(Dispatchers.IO) {
            score = datastoreRepository.answer.first()
        }
        if (score.isNotEmpty()) {
            Log.e(tag, score + "from pref")
            _dataString.postValue(score)
        } else {
            viewModelScope.launch {
                delay(10000)
                checkDataExist()
            }
        }
    }

    fun checkDataExist() {
        runBlocking(Dispatchers.IO) {
            score = datastoreRepository.answer.first()
        }
        if (score.isEmpty()) {
            getDataInfoModel()
        }

    }


    fun getUserCountry(context: Context): String {
        try {
            val tm = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            val simCountry = tm.simCountryIso
            if (simCountry != null) { // SIM country code is available
                return simCountry.uppercase(Locale.US)
            } else if (tm.phoneType != TelephonyManager.PHONE_TYPE_CDMA) { // device is not 3G (would be unreliable)
                val networkCountry = tm.networkCountryIso
                if (networkCountry != null) { // network country code is available
                    return networkCountry.uppercase(Locale.US)
                }
            }
        } catch (e: Exception) {
            Log.e(tag, Log.getStackTraceString(e))
        }
        return ""
    }

    fun getDeeplink(intent: Intent): String {
        if (intent.dataString != null) {
            val value = intent.dataString!!
            if (value.isNotEmpty()) {
                var deeplink = value
                if (deeplink.contains("?")) {
                    deeplink = deeplink.substring(deeplink.indexOf("?"))
                    deeplink = deeplink.replaceFirst("?", "&")
                }
                if (deeplink.contains("&target_url", true)) {
                    deeplink = deeplink.substring(0, deeplink.indexOf("&target_url"))
                }
                //SharedPref(getApplication()).saveDeeplink(deeplink)
                GlobalScope.launch {
                    datastoreRepository.saveDeeplink(deeplink)
                }
            }
        }
        val deeplink: String
        runBlocking(Dispatchers.IO) {
            deeplink = datastoreRepository.deeplink.first()
        }
        if (deeplink.isNotEmpty()) {
            return "&deep=yes$deeplink"
        }
        return "&deep=no"
    }


    fun onClick(v: View?) {

    }
}







