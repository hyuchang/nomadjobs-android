package com.valuebiz.nomadjobs.activities

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.valuebiz.nomadjobs.R

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        Handler().postDelayed(Runnable {
            startActivity(Intent(this, WebViewActivity::class.java))
            finish()
        }, 1500)
    }

    override fun onDestroy() {
        super.onDestroy()
        overridePendingTransition(R.anim.abc_fade_out, R.anim.abc_fade_in)
    }
}
