package com.example.consumeradda.activities

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.widget.Toast
import com.example.consumeradda.R
import kotlinx.android.synthetic.main.activity_chat_detail.*
import kotlinx.android.synthetic.main.activity_complaint_form.*

class ChatDetailActivity : AppCompatActivity() {

    private var isAtt1 = false
    private var isAtt2 = false
    private var isAtt3 = false
    private var isAtt4 = false
    private var isAtt5 = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_detail)

        btnBackAppDetailSetOnClickListener()
        showData()


        val sharedPreferences = getSharedPreferences("ConsumerAdda", Context.MODE_PRIVATE)
        val role = sharedPreferences.getInt("ROLE",-1)

        if(role==0)
        {
            btnDownloadAttachment1.visibility = View.INVISIBLE
            btnDownloadAttachment2.visibility = View.INVISIBLE
            btnDownloadAttachment3.visibility = View.INVISIBLE
            btnDownloadAttachment4.visibility = View.INVISIBLE
            btnDownloadAttachment5.visibility = View.INVISIBLE
        }
        else
        {
            btnDownloadAttachment1.visibility = View.VISIBLE
            btnDownloadAttachment2.visibility = View.VISIBLE
            btnDownloadAttachment3.visibility = View.VISIBLE
            btnDownloadAttachment4.visibility = View.VISIBLE
            btnDownloadAttachment5.visibility = View.VISIBLE
        }

        val case = Dashboard.CardModelList[Dashboard.cardPositionClicked]

        if(case.attachment2.isNullOrBlank() || case.attachment2.isEmpty() ||case.attachment2.equals(""))
        {
            btnDownloadAttachment2.visibility = View.GONE
        }
        if(case.attachment3.isNullOrBlank() || case.attachment3.isEmpty() ||case.attachment3.equals(""))
        {
            btnDownloadAttachment3.visibility = View.GONE
        }
        if(case.attachment4.isNullOrBlank() || case.attachment4.isEmpty() ||case.attachment4.equals(""))
        {
            btnDownloadAttachment4.visibility = View.GONE
        }
        if(case.attachment5.isNullOrBlank() || case.attachment5.isEmpty() ||case.attachment5.equals(""))
        {
            btnDownloadAttachment5.visibility = View.GONE
        }


        checkIfDownloadedOrNot()
        btnDownloadAttachment1SetOnClickListener()
        btnDownloadAttachment2SetOnClickListener()
        btnDownloadAttachment3SetOnClickListener()
        btnDownloadAttachment4SetOnClickListener()
        btnDownloadAttachment5SetOnClickListener()
    }

    private fun checkIfDownloadedOrNot() {
        val att1 = "Attachment1"
        val att2 = "Attachment2"
        val att3 = "Attachment3"
        val att4 = "Attachment4"
        val att5 = "Attachment5"

        val fileAtt1Pdf = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS + "/ConsumerAdda/CaseAttachments/CASE-${Dashboard.CardModelList[Dashboard.cardPositionClicked].caseId}/$att1.pdf"
        )
        isAtt1 = fileAtt1Pdf.exists()

        val fileAtt2Pdf = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS + "/ConsumerAdda/CaseAttachments/CASE-${Dashboard.CardModelList[Dashboard.cardPositionClicked].caseId}/$att2.pdf"
        )
        isAtt2 = fileAtt2Pdf.exists()

        val fileAtt3Pdf = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS + "/ConsumerAdda/CaseAttachments/CASE-${Dashboard.CardModelList[Dashboard.cardPositionClicked].caseId}/$att3.pdf"
        )
        isAtt3 = fileAtt3Pdf.exists()

        val fileAtt4Pdf = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS + "/ConsumerAdda/CaseAttachments/CASE-${Dashboard.CardModelList[Dashboard.cardPositionClicked].caseId}/$att4.pdf"
        )

        isAtt4 = fileAtt4Pdf.exists()

        val fileAtt5Pdf = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS + "/ConsumerAdda/CaseAttachments/CASE-${Dashboard.CardModelList[Dashboard.cardPositionClicked].caseId}/$att5.pdf"
        )
        isAtt5 = fileAtt5Pdf.exists()

        if (isAtt1)
        {
            btnDownloadAttachment1.setCompoundDrawablesWithIntrinsicBounds(0, 0,R.drawable.ic_baseline_green_check_24 , 0)
        }
        if(isAtt2)
        {
            btnDownloadAttachment2.setCompoundDrawablesWithIntrinsicBounds(0, 0,R.drawable.ic_baseline_green_check_24 , 0)
        }
        if(isAtt3)
        {
            btnDownloadAttachment3.setCompoundDrawablesWithIntrinsicBounds(0, 0,R.drawable.ic_baseline_green_check_24 , 0)
        }
        if(isAtt4)
        {
            btnDownloadAttachment4.setCompoundDrawablesWithIntrinsicBounds(0, 0,R.drawable.ic_baseline_green_check_24 , 0)
        }
        if(isAtt5)
        {
            btnDownloadAttachment5.setCompoundDrawablesWithIntrinsicBounds(0, 0,R.drawable.ic_baseline_green_check_24 , 0)
        }
    }

    private fun btnDownloadAttachment1SetOnClickListener() {
        btnDownloadAttachment1.setOnClickListener {
            if(!isAtt1)
            {
                downloadPDF(Dashboard.CardModelList[Dashboard.cardPositionClicked].attachment1, 1)
                downloadImage(Dashboard.CardModelList[Dashboard.cardPositionClicked].attachment1, 1)
                btnDownloadAttachment1.setCompoundDrawablesWithIntrinsicBounds(0, 0,R.drawable.ic_baseline_green_check_24 , 0)
            }
            else
            {
                Toast.makeText(this,"Attachment present in Downloads/ConsumerAdda/CaseAttachments/CASE-${Dashboard.CardModelList[Dashboard.cardPositionClicked].caseId}",Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun downloadImage(url: String, num:Int) {
        val request = DownloadManager.Request(Uri.parse(url))
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
        request.setTitle("Attachment-$num")
        request.setDescription("File is downloading")
        Toast.makeText(this,"Download Started",Toast.LENGTH_SHORT).show()
        request.allowScanningByMediaScanner()
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "ConsumerAdda/CaseAttachments/CASE-${Dashboard.CardModelList[Dashboard.cardPositionClicked].caseId}/Attachment$num.jpeg")

        val manager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        manager.enqueue(request)
    }

    private fun downloadPDF(url: String, num:Int) {
        val request = DownloadManager.Request(Uri.parse(url))
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
        request.setTitle("Attachment-$num")
        request.setDescription("File is downloading")
        Toast.makeText(this,"Download Started",Toast.LENGTH_SHORT).show()
        request.allowScanningByMediaScanner()
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "ConsumerAdda/CaseAttachments/CASE-${Dashboard.CardModelList[Dashboard.cardPositionClicked].caseId}/Attachment$num.pdf")

        val manager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        manager.enqueue(request)
    }

    private fun btnDownloadAttachment2SetOnClickListener() {
        btnDownloadAttachment2.setOnClickListener {
            if(!isAtt2)
            {
                downloadPDF(Dashboard.CardModelList[Dashboard.cardPositionClicked].attachment2, 2)
                downloadImage(Dashboard.CardModelList[Dashboard.cardPositionClicked].attachment2, 2)
                btnDownloadAttachment2.setCompoundDrawablesWithIntrinsicBounds(0, 0,R.drawable.ic_baseline_green_check_24 , 0)
            }
            else
            {
                Toast.makeText(this,"Attachment present in Downloads/ConsumerAdda/CaseAttachments/CASE-${Dashboard.CardModelList[Dashboard.cardPositionClicked].caseId}",Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun btnDownloadAttachment3SetOnClickListener() {
        btnDownloadAttachment3.setOnClickListener {
            if(!isAtt3)
            {
                downloadPDF(Dashboard.CardModelList[Dashboard.cardPositionClicked].attachment3, 3)
                downloadImage(Dashboard.CardModelList[Dashboard.cardPositionClicked].attachment3, 3)
                btnDownloadAttachment3.setCompoundDrawablesWithIntrinsicBounds(0, 0,R.drawable.ic_baseline_green_check_24 , 0)
            }
            else
            {
                Toast.makeText(this,"Attachment present in Downloads/ConsumerAdda/CaseAttachments/CASE-${Dashboard.CardModelList[Dashboard.cardPositionClicked].caseId}",Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun btnDownloadAttachment4SetOnClickListener() {
        btnDownloadAttachment4.setOnClickListener {
            if(!isAtt4)
            {
                downloadPDF(Dashboard.CardModelList[Dashboard.cardPositionClicked].attachment4, 4)
                downloadImage(Dashboard.CardModelList[Dashboard.cardPositionClicked].attachment4, 4)
                btnDownloadAttachment4.setCompoundDrawablesWithIntrinsicBounds(0, 0,R.drawable.ic_baseline_green_check_24 , 0)
            }
            else
            {
                Toast.makeText(this,"Attachment present in Downloads/ConsumerAdda/CaseAttachments/CASE-${Dashboard.CardModelList[Dashboard.cardPositionClicked].caseId}",Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun btnDownloadAttachment5SetOnClickListener() {
        btnDownloadAttachment5.setOnClickListener {
            if(!isAtt5)
            {
                downloadPDF(Dashboard.CardModelList[Dashboard.cardPositionClicked].attachment5, 5)
                downloadImage(Dashboard.CardModelList[Dashboard.cardPositionClicked].attachment5, 5)
                btnDownloadAttachment5.setCompoundDrawablesWithIntrinsicBounds(0, 0,R.drawable.ic_baseline_green_check_24 , 0)
            }
            else
            {
                Toast.makeText(this,"Attachment present in Downloads/ConsumerAdda/CaseAttachments/CASE-${Dashboard.CardModelList[Dashboard.cardPositionClicked].caseId}",Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun showData() {
        tvClientHometown.text = Dashboard.CardModelList[Dashboard.cardPositionClicked].applicantDistrict + ", "+Dashboard.CardModelList[Dashboard.cardPositionClicked].applicantState
        tvCDCaseAgainst.text = Dashboard.CardModelList[Dashboard.cardPositionClicked].caseAgainst
        tvCDCaseDescription.text = Dashboard.CardModelList[Dashboard.cardPositionClicked].caseDescription
        tvCDCaseType.text = Dashboard.CardModelList[Dashboard.cardPositionClicked].caseType
        tvCDClientName.text = Dashboard.CardModelList[Dashboard.cardPositionClicked].applicantFirstName +" "+ Dashboard.CardModelList[Dashboard.cardPositionClicked].applicantLastName
    }

    private fun btnBackAppDetailSetOnClickListener() {
        btnBackAppDetail.setOnClickListener {
            finish()
        }
    }
}