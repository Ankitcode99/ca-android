package com.example.consumeradda.activities.support

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.consumeradda.R
import com.example.consumeradda.activities.Dashboard
import kotlinx.android.synthetic.main.activity_contact_us.*

class ContactUs : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contact_us)

        btnBackContactUs.setOnClickListener {
            finish()
        }

        twitter_btn.setOnClickListener {
            val intent= Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/Consumeradda"))
            startActivity(intent)
        }

        fb_btn.setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/ConsumerAdda/")))
        }

        gmail_btn.setOnClickListener {
            val intent = Intent(Intent.ACTION_SEND)
            val recipients = arrayOf("consumersadda@gmail.com")
            intent.putExtra(Intent.EXTRA_EMAIL, recipients)

            intent.type = "text/html"
            intent.setPackage("com.google.android.gm")
            startActivity(Intent.createChooser(intent, "Send mail"))
        }

        call_btn.setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL)
            intent.setData(Uri.parse("tel:8888067063"))
            startActivity(intent)
        }
    }
}
