package com.example.consumeradda.activities.support

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.consumeradda.R
import com.example.consumeradda.activities.Dashboard

import kotlinx.android.synthetic.main.activity_about.*

class About : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)

//        setSupportActionBar(toolbar)
//        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        btnBackAboutUs.setOnClickListener {
//            val intent = Intent(this, Dashboard::class.java)
//            startActivity(intent)
            finish()
        }
    }
}
