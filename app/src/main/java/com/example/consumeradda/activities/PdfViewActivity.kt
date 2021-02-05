package com.example.consumeradda.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.consumeradda.R
import kotlinx.android.synthetic.main.activity_pdf_view.*
import java.io.File

class PdfViewActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pdf_view)

        supportActionBar?.apply {
            // Set toolbar title/app title
            title = "Bridge"
            setDisplayShowHomeEnabled(true)
            setDisplayUseLogoEnabled(true)
        }

//        pdfView.fromFile().load()
        val intent = getIntent()
        val filepath = intent.getStringExtra("File")
        val file = File(filepath)

        pdfView.fromFile(file).load()

        tvPDFHeaderSetOnClickListener()
    }

    private fun tvPDFHeaderSetOnClickListener() {
        tvPDFHeader.setOnClickListener {
            finish()
        }
    }
}