package com.haunted.housegamefk

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.appsflyer.AppsFlyerConversionListener
import com.appsflyer.AppsFlyerLib
import com.facebook.FacebookSdk
import com.facebook.applinks.AppLinkData
import com.haunted.housegamefk.ui.fragments.SplashViewModel
import com.onesignal.OSSubscriptionObserver
import com.onesignal.OSSubscriptionStateChanges
import com.onesignal.OneSignal
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity(), OSSubscriptionObserver {
    private lateinit var viewModel: SplashViewModel
    private val tag = MainActivity::class.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        val devKey = getString(R.string.appsflyer_id)
        //printHashKey(this)

        FacebookSdk.setAutoInitEnabled(true)
        FacebookSdk.fullyInitialize()
        AppLinkData.fetchDeferredAppLinkData(
            this
        ) {
            getDeeplink(it)
        }
        viewModel =


            ViewModelProvider(this).get(SplashViewModel::class.java)
        OneSignal.addSubscriptionObserver(this)

        val conversionDataListener = object : AppsFlyerConversionListener {
            override fun onConversionDataSuccess(data: MutableMap<String, Any>?) {
                var hashMap: HashMap<String, String>
                runBlocking(Dispatchers.IO) {
                    hashMap = viewModel.datastoreRepository.param.first()
                }
                data?.let { cvData ->
                    cvData.map {
                        Log.e(tag, "conversion_attribute2:  ${it.key} = ${it.value}")
                        if (!"${it.value}".equals("null", true))
                            hashMap[it.key] = "${it.value}"
                        if (it.key.equals(AppConstants.STATUS_KEY, true)) {
                            GlobalScope.launch {
                                viewModel.datastoreRepository.saveStatus("${it.value}")
                            }
                        }
                        if (it.key.equals(AppConstants.CAMPAIGN_KEY, true)) {
                            GlobalScope.launch {
                                viewModel.datastoreRepository.saveCampaign("${it.value}")
                            }
                        }
                        if (it.key.equals(AppConstants.ADSET_KEY, true)) {
                            GlobalScope.launch {
                                viewModel.datastoreRepository.saveAdset("${it.value}")
                            }
                        }
                        if (it.key.equals(AppConstants.AD_ID_KEY, true)) {
                            GlobalScope.launch {
                                viewModel.datastoreRepository.saveAdid("${it.value}")
                            }
                        }
                        if (it.key.equals(AppConstants.CHANNEL_KEY, true)) {
                            GlobalScope.launch {
                                viewModel.datastoreRepository.saveChannel("${it.value}")
                            }
                        }

                    }

                }
                GlobalScope.launch {
                    viewModel.datastoreRepository.saveParam(hashMap)
                }
                viewModel.checkDataExist()
            }

            override fun onConversionDataFail(error: String?) {
                Log.e(tag, "error onAttributionFailure :  $error")
                viewModel.checkDataExist()

            }

            override fun onAppOpenAttribution(data: MutableMap<String, String>?) {
                data?.map {
                    Log.e(tag, "onAppOpen_attribute: ${it.key} = ${it.value}")
                }
            }

            override fun onAttributionFailure(error: String?) {
                Log.e(tag, "error onAttributionFailure :  $error")
            }
        }
        AppsFlyerLib.getInstance().init(devKey, conversionDataListener, this)
        AppsFlyerLib.getInstance().start(this)

    }


    @SuppressLint("PackageManagerGetSignatures")
    fun printHashKey(pContext: Context) {
        try {
            val info = pContext.packageManager.getPackageInfo(
                pContext.packageName,
                PackageManager.GET_SIGNATURES
            )
            for (signature in info.signatures) {
                val md = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                val hashKey = String(Base64.encode(md.digest(), 0))
                Toast.makeText(pContext, hashKey, Toast.LENGTH_LONG).show()
                val share = Intent(Intent.ACTION_SEND)
                share.type = "text/plain"
                share.putExtra(Intent.EXTRA_TEXT, hashKey)
                startActivity(Intent.createChooser(share, "Share flower information"))
            }
        } catch (e: NoSuchAlgorithmException) {
        } catch (e: java.lang.Exception) {
        }
    }

    private fun getDeeplink(data: AppLinkData?): String {
        if (data != null && data.targetUri != null) {
            val value = data.targetUri.toString()
            if (value.isNotEmpty()) {
                var deeplink = value
                if (deeplink.contains("?")) {
                    deeplink = deeplink.substring(deeplink.indexOf("?"))
                    deeplink = deeplink.replaceFirst("?", "&")
                }
                GlobalScope.launch {
                    viewModel.datastoreRepository.saveDeeplink(deeplink)
                }
            }
        }
        val deeplink: String
        runBlocking(Dispatchers.IO) {
            deeplink = viewModel.datastoreRepository.deeplink.first()
        }
        if (deeplink.isNotEmpty()) {
            return "&deep=yes$deeplink"
        }
        return "&deep=no"
    }


    override fun onBackPressed() {
        //super.onBackPressed()
    }

    fun onFragmentBackPressed() {
        super.onBackPressed()
    }

    override fun onOSSubscriptionChanged(stateChanges: OSSubscriptionStateChanges?) {
        if (!(stateChanges?.from?.isSubscribed!! || !stateChanges.to.isSubscribed)
        ) {
            // The user is subscribed
            // Either the user subscribed for the first time
            // Or the user was subscribed -> unsubscribed -> subscribed
            val userId = stateChanges.to?.userId.toString()
            Log.e(tag, userId)
            GlobalScope.launch {
                viewModel.datastoreRepository.saveUserId(userId)
            }
        }
    }


}



