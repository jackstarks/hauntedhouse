package com.haunted.housegamefk.ui.fragments

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.haunted.housegamefk.R
import com.haunted.housegamefk.databinding.FragmentWebBinding


const val ARG_PARAM1 = "param1"

@Suppress("DEPRECATION")
class WebFragment : Fragment() {
    private var param1: String? = null
    private var aswFilePath: ValueCallback<Array<Uri>>? = null
    private val aswFileReq = 1

    private lateinit var binding: FragmentWebBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
        }
    }


    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_web, container, false
        )

        val webSettings = binding.webView.settings
        webSettings.apply {
            javaScriptEnabled = true
            setSupportZoom(true)
            setSupportMultipleWindows(true)
            javaScriptCanOpenWindowsAutomatically = true
            domStorageEnabled = true
            setAppCacheEnabled(true)
            allowFileAccess = true
            allowContentAccess = true
            allowFileAccessFromFileURLs = true
            allowUniversalAccessFromFileURLs = true
            loadsImagesAutomatically = true
        }
        val cookieManager: CookieManager = CookieManager.getInstance()
        cookieManager.apply {
            setAcceptCookie(true)
            setAcceptThirdPartyCookies(binding.webView, true)
        }
        binding.webView.webViewClient = MyWebViewClient()
        //binding.webView.setWebChromeClient(WebChromeClient())
        binding.webView.webChromeClient = object : WebChromeClient() {
            //Handling input[type="file"] requests for android API 21+
            override fun onShowFileChooser(
                webView: WebView,
                filePathCallback: ValueCallback<Array<Uri>>,
                fileChooserParams: FileChooserParams
            ): Boolean {


                if (aswFilePath != null) {
                    aswFilePath!!.onReceiveValue(null)
                }
                aswFilePath = filePathCallback
                val takePictureIntent: Intent? = null
                val contentSelectionIntent = Intent(Intent.ACTION_GET_CONTENT)
                contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE)
                contentSelectionIntent.type = "image/*"
                val intentArray: Array<Intent?> = if (takePictureIntent != null) {
                    arrayOf(takePictureIntent)
                } else {
                    arrayOfNulls(0)
                }

                val chooserIntent = Intent(Intent.ACTION_CHOOSER)
                chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent)
                chooserIntent.putExtra(Intent.EXTRA_TITLE, "file")
                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray)
                startActivityForResult(chooserIntent, aswFileReq)

                return true

            }

        }
        param1?.let { binding.webView.loadUrl(it) }
        binding.webView.setOnKeyListener(View.OnKeyListener { _, keyCode, event -> //This is the filter
            if (event.action != KeyEvent.ACTION_DOWN) return@OnKeyListener true
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                if (binding.webView.canGoBack()) {
                    binding.webView.goBack()


                } else {
                    activity?.onBackPressed()
                }
                true
            } else false
        })

        return binding.root
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)

        var results: Array<Uri>? = null
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == aswFileReq) {
                if (null == aswFilePath) {
                    return
                }

                val dataString = intent?.dataString
                if (dataString != null) {
                    results = arrayOf(Uri.parse(dataString))
                }

            }
        }
        aswFilePath!!.onReceiveValue(results)
        aswFilePath = null

    }

    class MyWebViewClient : WebViewClient() {
        override fun shouldOverrideUrlLoading(
            view: WebView?, request: WebResourceRequest?
        ): Boolean {
            view?.loadUrl(request?.url.toString())
            return true
        }
    }


}
