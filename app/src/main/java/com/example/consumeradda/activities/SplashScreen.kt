package com.example.consumeradda.activities

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import com.example.consumeradda.R
import com.google.firebase.auth.FirebaseAuth

class SplashScreen : AppCompatActivity() {

    lateinit var prefs: SharedPreferences
    lateinit var countdownTimer: CountDownTimer
    lateinit var mAuth: FirebaseAuth
    var isSplashScreenDone=false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)


        mAuth= FirebaseAuth.getInstance()


        countdownTimer = object : CountDownTimer(2000, 1000) {
            override fun onTick(millisUntilFinished: Long) {}
            override fun onFinish() {

                val sharedPreferences = getSharedPreferences("ConsumerAdda",Context.MODE_PRIVATE)
                val isLoggedIn = sharedPreferences.getBoolean("LOGGED_IN",false)
                Log.d("Status",isLoggedIn.toString())
                callIntent(isLoggedIn)
            }
        }
        countdownTimer.start()
    }

    private fun callIntent(loggedIn: Boolean) {
        if (!isSplashScreenDone) {
            if (loggedIn && mAuth.currentUser!=null) {
                goToDashboard()
            } else {
                goToAuth()
            }
        }
    }

    private fun goToAuth() {
        isSplashScreenDone=true
        val intent=Intent(this, LoginActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
    }

    private fun goToDashboard() {
        isSplashScreenDone=true
        val intent=Intent(this, Dashboard::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
    }

    override fun onResume() {
        super.onResume()
        if (!isSplashScreenDone) {
            countdownTimer.start()
        }
    }
}