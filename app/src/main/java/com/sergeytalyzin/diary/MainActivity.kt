package com.sergeytalyzin.diary

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.InterstitialAd
import kotlinx.android.synthetic.main.toolbar.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        val mInterstitialAd = InterstitialAd(this)
        mInterstitialAd.adUnitId = "ca-app-pub-5571324881623034/2182172641"
        mInterstitialAd.loadAd(AdRequest.Builder().build())
        mInterstitialAd.adListener = object: AdListener() {
            override fun onAdLoaded() {
                mInterstitialAd.show()
            }
        }
    }

    override fun onBackPressed() {
        val fm = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val fragment = fm.childFragmentManager.fragments[0]

        var backPressedListener: OnBackPressedListener? = null

        if (fragment is OnBackPressedListener) {
            backPressedListener = fragment
        }

        if (backPressedListener != null) {
            backPressedListener.onBackPressed()
        } else {
            super.onBackPressed()
        }
    }

    fun myBackFun() {
        super.onBackPressed()
    }
}

