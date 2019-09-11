package com.sergeytalyzin.diary

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import kotlinx.android.synthetic.main.activity_password.*

class PasswordActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_password)

        MobileAds.initialize(this,
            "ca-app-pub-5571324881623034~2500601370")

        val mAdView = adViewPassActivity
        val adRequest = AdRequest.Builder().build()
        mAdView.loadAd(adRequest)

        descriptionOfPassInPA.text = resources.getText(R.string.textForDescriptionOfPasInPA)

        val password = Password(this@PasswordActivity).getPassFromSharedPreferences()

        if(password != "") {
            checkPassBtn.setOnClickListener {
                if(checkPassEditText.text.toString() == password)
                    startActivity(Intent(this, MainActivity::class.java))
                else {
                    errorsOfPasswordInPassActivity.visibility = View.VISIBLE
                    errorsOfPasswordInPassActivity.text = resources.getText(R.string.textForWrongPassword)
                }
            }
        }
        else if(password == "")
            startActivity(Intent(this, MainActivity::class.java))
    }
}
