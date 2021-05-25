package com.haunted.housegamefk.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.appsflyer.AppsFlyerLib
import com.haunted.housegamefk.AppConstants.Companion.ADSET_KEY
import com.haunted.housegamefk.AppConstants.Companion.AD_ID_KEY
import com.haunted.housegamefk.AppConstants.Companion.BUYER_NAME_KEY
import com.haunted.housegamefk.AppConstants.Companion.CHANNEL_KEY
import com.haunted.housegamefk.AppConstants.Companion.CHECK_KEY
import com.haunted.housegamefk.AppConstants.Companion.STATUS_KEY
import com.haunted.housegamefk.AppConstants.Companion.USER_COUNTRY_KEY
import com.haunted.housegamefk.AppConstants.Companion.USER_ID_KEY
import com.haunted.housegamefk.GameActivity
import com.haunted.housegamefk.R
import com.haunted.housegamefk.databinding.SplashFragmentBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class SplashFragment : Fragment() {
    private val className = SplashFragment::class.simpleName

    private lateinit var viewModel: SplashViewModel
    private lateinit var binding: SplashFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.splash_fragment, container, false
        )
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this.requireActivity()).get(SplashViewModel::class.java)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        viewModel.eventGameFinish.observe(viewLifecycleOwner, { hasFinished ->
            if (hasFinished) gameStart()
        })

        viewModel.dataString.observe(viewLifecycleOwner, { dataString ->
            val bundle = Bundle()
            Log.e(className, dataString)
            if (dataString.equals(CHECK_KEY, ignoreCase = true)) {
                gameStart()
            } else {
                var res = dataString
                val appsFlyerId = AppsFlyerLib.getInstance()
                    .getAppsFlyerUID(context)
                res += appsFlyerId + viewModel.getDeeplink(activity?.intent!!)
                val status: String
                runBlocking(Dispatchers.IO) {
                    status = viewModel.datastoreRepository.status.first()
                }
                if (status.isNotEmpty()) {
                    res += "&$STATUS_KEY=$status"
                }
                val campaign: String
                runBlocking(Dispatchers.IO) {
                    campaign = viewModel.datastoreRepository.campaign.first()
                }
                if (campaign.isNotEmpty()) {
                    val campaignStr = campaign.replace(" ", "_")
                    res += "&af_campaign=$campaignStr"
                    val parts = campaignStr.split("_")
                    if (parts.size > 1) {
                        res += "&" + BUYER_NAME_KEY + "=" + parts[1]
                    }
                }
                val adset: String
                runBlocking(Dispatchers.IO) {
                    adset = viewModel.datastoreRepository.adset.first()
                }
                if (adset.isNotEmpty()) {
                    res += "&$ADSET_KEY=$adset"
                }
                val adid: String
                runBlocking(Dispatchers.IO) {
                    adid = viewModel.datastoreRepository.adid.first()
                }
                if (adid.isNotEmpty()) {
                    res += "&$AD_ID_KEY=$adid"
                }
                val channel: String
                runBlocking(Dispatchers.IO) {
                    channel = viewModel.datastoreRepository.channel.first()
                }
                if (channel.isNotEmpty()) {
                    res += "&$CHANNEL_KEY=$channel"
                }
                val userId: String
                runBlocking(Dispatchers.IO) {
                    userId = viewModel.datastoreRepository.userId.first()
                }
                if (userId.isNotEmpty()) {
                    res += "&$USER_ID_KEY=$userId"
                }
                val userCountry = viewModel.getUserCountry(activity?.application!!)
                if (userCountry.isNotEmpty()) {
                    res += "&$USER_COUNTRY_KEY=$userCountry"
                }

                val hashMap: HashMap<String, String>
                runBlocking(Dispatchers.IO) {
                    hashMap = viewModel.datastoreRepository.param.first()
                }
                Log.e("result", hashMap.toString())
                hashMap.let { cvData ->
                    cvData.map {
                        res += "&" + it.key.replace(" ", "_") + "=" + it.value.replace(" ", "_")
                    }
                }
                bundle.putString(ARG_PARAM1, res)
                Log.e(className, res)
                findNavController().navigate(R.id.action_nav_splash_to_webFragment, bundle)
            }
        })

    }


    private fun gameStart() {
        //findNavController().navigate(R.id.action_Splash_to_Main)
        val intent = Intent(activity, GameActivity::class.java)
        startActivity(intent)
        requireActivity().finish()

    }

}







