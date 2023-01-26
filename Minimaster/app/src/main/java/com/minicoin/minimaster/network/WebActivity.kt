package com.minicoin.minimaster.network

import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.webkit.*
import com.minicoin.minimaster.R
import com.minicoin.minimaster.data.Constants.mainAFT
import com.minicoin.minimaster.utills.SaveUtil

class WebActivity : ChromeClient.MainWebClient() {

    private var isOfferVisible = false
    private lateinit var myWebView: WebView

    private val myWebChromeClient by lazy {
        ChromeClient(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web)

        initViews()
        if (mainAFT.isNotEmpty()) showWebContainer(mainAFT)
    }

    private fun initViews() {
        myWebView = findViewById(R.id.WebView)
        initWebParts()
    }

    private fun initWebParts() {
        myWebView.apply {
            webChromeClient = myWebChromeClient
            webViewClient = object : WebViewClient() {
                override fun onPageFinished(view: WebView?, url: String?) {
                    CookieSyncManager.getInstance().sync()
                    requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR
                    isOfferVisible = true
                }

                override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                    Log.d("testWEB", "onPageStarted: $url")
                    super.onPageStarted(view, url, favicon)
                }

                override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                    Log.d("testWEB", "shouldOverrideUrlLoading: $url")
                    SaveUtil.write(SaveUtil.SHARED_DATA, url)
                    if (url.startsWith("tel:")) {
                        val intent = Intent(
                            Intent.ACTION_DIAL,
                            Uri.parse(url)
                        )
                        context.startActivity(intent)
                    } else if (url.startsWith("whatsapp://send?phone=")) {
                        val url2 = "https://api.whatsapp.com/send?phone=" + url.replace(
                            "whatsapp://send?phone=",
                            ""
                        )
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url2))
                        intent.addFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT or Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET or Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP).setPackage("com.whatsapp")
                        try {
                            context.startActivity(intent)
                        } catch (e: ActivityNotFoundException) {
                            context.startActivity(
                                Intent(
                                    Intent.ACTION_VIEW,
                                    Uri.parse("https://play.google.com/store/apps/details?id=com.whatsapp")
                                )
                            )
                            this@apply.goBack()
                        }
                    } else if (url.startsWith("https://api.whatsapp.com/send?phone=")) {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                        intent.addFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT or Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET or Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP)
                            .setPackage("com.whatsapp")
                        try {
                            context.startActivity(intent)
                        } catch (e: ActivityNotFoundException) {
                            context.startActivity(
                                Intent(
                                    Intent.ACTION_VIEW,
                                    Uri.parse("https://play.google.com/store/apps/details?id=com.whatsapp")
                                )
                            )
                            this@apply.goBack()
                        }
                    } else if (url.startsWith("whatsapp://send?text=")) {
                        val uri = Uri.parse(url)
                        val msg = uri.getQueryParameter("text")
                        val sendIntent = Intent()
                        sendIntent.action = Intent.ACTION_SEND
                        sendIntent.putExtra(Intent.EXTRA_TEXT, msg)
                        sendIntent.type = "text/plain"
                        sendIntent.setPackage("com.whatsapp")
                        try {
                            context.startActivity(sendIntent)
                        } catch (e: ActivityNotFoundException) {
                            context.startActivity(Intent(
                                    Intent.ACTION_VIEW,
                                    Uri.parse("https://play.google.com/store/apps/details?id=com.whatsapp")
                                )
                            )
                            this@apply.goBack()
                        }
                    } else if (url.startsWith("viber:")) {
                        val intent = Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse(url)
                        )
                        try {
                            context.startActivity(intent)
                        } catch (e: ActivityNotFoundException) {
                            context.startActivity(
                                Intent(
                                    Intent.ACTION_VIEW,
                                    Uri.parse("https://play.google.com/store/apps/details?id=com.viber.voip")
                                )
                            )
                            this@apply.goBack()
                        }
                    } else if (url.startsWith("https://t.me")) {
                        val intent = Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse(url)
                        )
                        try {
                            context.startActivity(intent)
                        } catch (e: ActivityNotFoundException) {
                            context.startActivity(
                                Intent(
                                    Intent.ACTION_VIEW,
                                    Uri.parse("https://play.google.com/store/apps/details?id=org.telegram.messenger")
                                )
                            )
                            this@apply.goBack()
                        }
                    } else if (url.startsWith("tg://")) {
                        val intent = Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse(url)
                        )
                        try {
                            context.startActivity(intent)
                        } catch (e: ActivityNotFoundException) {
                            context.startActivity(Intent(
                                    Intent.ACTION_VIEW,
                                    Uri.parse("https://play.google.com/store/apps/details?id=org.telegram.messenger")
                                )
                            )
                            this@apply.goBack()
                        }
                    } else if (url.startsWith("http:") || url.startsWith("https:")) {
                        view.loadUrl(url)
                    }
                    return true
                }

                override fun onReceivedError(
                    view: WebView?,
                    request: WebResourceRequest?,
                    error: WebResourceError?) {
                }
            }

            settings.apply {
                pluginState = WebSettings.PluginState.ON
                allowFileAccess = true
                allowContentAccess = true
                allowFileAccessFromFileURLs = true
                allowUniversalAccessFromFileURLs = true
                javaScriptEnabled = true
                mediaPlaybackRequiresUserGesture = false
                javaScriptCanOpenWindowsAutomatically = true
                domStorageEnabled = true
                databaseEnabled = true
                useWideViewPort = true
                loadWithOverviewMode = true
            }
        }
    }

    private fun showWebContainer(url: String) {
        runOnUiThread {
            if (!isOfferVisible) {
                myWebView.apply {
                    Log.d("testWEB", "TOTAL WEB: $url")
                    loadUrl(url)
                }
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (myWebView.canGoBack()) myWebView.goBack()
    }
}