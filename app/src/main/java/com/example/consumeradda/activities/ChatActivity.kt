package com.example.consumeradda.activities

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.PopupMenu
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.consumeradda.R
import com.example.consumeradda.adapters.ChatAdapter
import com.example.consumeradda.models.caseModels.CaseResponse
import com.example.consumeradda.models.chatModels.ChatModel
import com.example.consumeradda.models.chatModels.Message
import com.example.consumeradda.service.CaseService
import com.example.consumeradda.service.ServiceBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_chat.*
import kotlinx.android.synthetic.main.confirm_update.*
import kotlinx.android.synthetic.main.forgot_password_dialog.*
import kotlinx.android.synthetic.main.update_case_status.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChatActivity : AppCompatActivity(),OnClicked {

    private var btnAttachmentClicked : Boolean = false

    private var flagForTypeDownload: Int=0 //IMAGE=0  PDF == 1
    private val STORAGE_PERMISSION_CODE: Int=1000
    private lateinit var selectedPDFUri: Uri
    private lateinit var imageDownloadableUrl: Uri
    private lateinit var pdfDownloadableUrl: Uri
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mStorageRef: StorageReference
    private lateinit var selectedImageUri:Uri
    private var currentRole:Int = -1
    private var currentUid:String = ""
    private val IMAGE_CODE=0
    private var PDF_CODE=1
    var imageNumberId=0
    var pdfNumberId=0
    private lateinit var currentDownloadUrl:String
    private var CaseId:Int = 0

    private var CaseStatus:Int = 0

    private lateinit var currentId:String
    private val db: FirebaseDatabase by lazy {
        FirebaseDatabase.getInstance()
    }
    private lateinit var chatAdapter: ChatAdapter
    private val messages = mutableListOf<ChatModel>()


    private val rotateOpen: Animation by lazy { AnimationUtils.loadAnimation(
            this,
            R.anim.rotate_open_anim
    ) }
    private val rotateClose: Animation by lazy { AnimationUtils.loadAnimation(
            this,
            R.anim.rotate_close_anim
    ) }
    private val fromBottom: Animation by lazy { AnimationUtils.loadAnimation(
            this,
            R.anim.from_bottom_anim
    ) }
    private val toBottom: Animation by lazy { AnimationUtils.loadAnimation(
            this,
            R.anim.to_bottom_anim
    ) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        mAuth= FirebaseAuth.getInstance()
        mStorageRef = FirebaseStorage.getInstance().reference

        val applicantName = Dashboard.CardModelList[Dashboard.cardPositionClicked].applicantFirstName + " " + Dashboard.CardModelList[Dashboard.cardPositionClicked].applicantLastName
        val lawyerName = Dashboard.CardModelList[Dashboard.cardPositionClicked].lawyerName

        val sharedPreferences = getSharedPreferences("ConsumerAdda", Context.MODE_PRIVATE)
        currentRole = sharedPreferences.getInt("ROLE",-1)
        currentUid = mAuth.currentUser!!.uid//sharedPreferences.getString("FIREBASE_ID","").toString()

        CaseId = Dashboard.CardModelList[Dashboard.cardPositionClicked].caseId
        CaseStatus = Dashboard.CardModelList[Dashboard.cardPositionClicked].caseStatus

        chatAdapter = ChatAdapter(list = messages,currentUid,this)
        rvChatActivity.apply {
            layoutManager = LinearLayoutManager(this@ChatActivity)
            adapter = chatAdapter
        }
        sendMessageToFirebase("default","DEFAULT")


        if(currentRole == 0)
        {
            btnChatDetails.text = lawyerName
            btnChatMore.visibility = View.INVISIBLE
        }
        else
        {
            btnChatDetails.text = applicantName
        }

        listenToMessages()
        btnCASendMessageSetOnClickListener()
        btnBackChatSetOnClickListener()
        btnAddAttachmentSetOnClickListener()
        btnChatDetailsSetOnClickListener()
        rvChatActivityAddOnLayoutChangeListener()
        btnChatMoreSetOnClickListener()
    }

    private fun rvChatActivityAddOnLayoutChangeListener() {
        rvChatActivity.addOnLayoutChangeListener { _, _, _, _, bottom, _, _, _, oldBottom ->
            if (bottom < oldBottom) {
                rvChatActivity.postDelayed({
                    if (rvChatActivity.adapter!!.itemCount>0) {
                        rvChatActivity.smoothScrollToPosition(
                                rvChatActivity.adapter!!.itemCount - 1
                        )
                    }
                }, 100)
            }
        }
    }

    private fun btnCASendMessageSetOnClickListener() {
        btnCASendMessage.setOnClickListener{
            etCAMessage.text?.let {  //Message written by current user.
                if (it.isNotEmpty()) {
                    sendMessageToFirebase(it.toString(), "TEXT")
                    it.clear()
                }
            }
        }
    }

    private fun sendMessageToFirebase(msg: String, type: String) {
        val id = getRoomFirebaseRef().push().key //Push will generate auto new key for messages.
        checkNotNull(id) { "Cannot be null" }
        val msgMap = Message(msg, currentUid, id, type, imageNumberId, pdfNumberId)
        getRoomFirebaseRef().child(id).setValue(msgMap).addOnSuccessListener {
            Log.i("TAG", "sendMessage: Successful")
        }.addOnFailureListener {
            Log.i("TAG", "sendMessage: Failure")
        }
    }

    private fun btnChatMoreSetOnClickListener() {
        btnChatMore.setOnClickListener {
            val popupMenu: PopupMenu = PopupMenu(this, btnChatMore)
            popupMenu.menuInflater.inflate(
                R.menu.lawyer_chat_menu,
                popupMenu.menu
            )
            popupMenu.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.btnUpdateCaseStatus -> {
                        openSetStatusDialog()
                    }
                }
                true
            })
            popupMenu.show()
        }
    }

    private fun openSetStatusDialog() {
        val mDialog = Dialog(this)
        mDialog.setContentView(R.layout.update_case_status)
        val window = mDialog.window
        window?.setLayout(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT
        )
        mDialog.setCanceledOnTouchOutside(true)
        mDialog.setCancelable(true)
        mDialog.show()

        mDialog.rbUSCaseClosed.isEnabled=true
        mDialog.rbUSMediation.isEnabled = true
        mDialog.rbUSNotice.isEnabled = true
        mDialog.rbUSCaseFiled.isEnabled = true

        if(CaseStatus==1)
        {
            mDialog.rbUSMediation.isChecked = true
            mDialog.rbUSMediation.isEnabled = false
        }
        else if(CaseStatus==2)
        {
            mDialog.rbUSMediation.isChecked = true
            mDialog.rbUSMediation.isEnabled = false
            mDialog.rbUSNotice.isChecked = true
            mDialog.rbUSNotice.isEnabled = false
        }
        else if(CaseStatus==3)
        {
            mDialog.rbUSMediation.isChecked = true
            mDialog.rbUSMediation.isEnabled = false
            mDialog.rbUSNotice.isChecked = true
            mDialog.rbUSNotice.isEnabled = false
            mDialog.rbUSCaseFiled.isChecked = true
            mDialog.rbUSCaseFiled.isEnabled = false
        }

        var newStatusDesc = ""

        mDialog.rbUSMediation.setOnClickListener {
            newStatusDesc = "Mediation Initiated"
            mDialog.dismiss()
            openConfirm(1,newStatusDesc)
        }
        mDialog.rbUSNotice.setOnClickListener {
            newStatusDesc = "Legal Notice Sent"
            mDialog.dismiss()
            openConfirm(2,newStatusDesc)
        }
        mDialog.rbUSCaseFiled.setOnClickListener {
            newStatusDesc = "Case Filed In Court"
            mDialog.dismiss()
            openConfirm(3,newStatusDesc)
        }
        mDialog.rbUSCaseClosed.setOnClickListener {
            newStatusDesc = "Case Closed"
            mDialog.dismiss()
            openConfirm(4,newStatusDesc)
        }
    }

    private fun openConfirm(newStatus: Int, newStatusDesc: String) {
        val mDialog = Dialog(this)
        mDialog.setContentView(R.layout.confirm_update)
        val window = mDialog.window
        window?.setLayout(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT
        )
        mDialog.setCanceledOnTouchOutside(false)
        mDialog.setCancelable(false)
        mDialog.show()
        mDialog.tvUpdNewStatus.text = newStatusDesc
        mDialog.btnUpdOk.setOnClickListener {
            updateCaseStatus(newStatus)
            mDialog.dismiss()
        }
        mDialog.btnUpdNo.setOnClickListener {
            mDialog.dismiss()
        }
    }

    private fun updateCaseStatus(newStatus: Int) {

        val sharedPreferences = getSharedPreferences("ConsumerAdda",Context.MODE_PRIVATE)
        val token = sharedPreferences.getString("ID_TOKEN","localToken").toString()

        val caseService = ServiceBuilder.buildService(CaseService::class.java)
        val requestCall = caseService.updateCaseStatus(CaseId,newStatus,token)
        requestCall.enqueue(object: Callback<CaseResponse> {
            override fun onResponse(call: Call<CaseResponse>, response: Response<CaseResponse>) {
                val mess = response.body()!!.msg
                Toast.makeText(this@ChatActivity,"${mess}",Toast.LENGTH_LONG).show()
            }

            override fun onFailure(call: Call<CaseResponse>, t: Throwable) {
                Toast.makeText(this@ChatActivity,"No Internet / Server Down",Toast.LENGTH_SHORT).show()
            }

        })
    }

    private fun btnAddAttachmentSetOnClickListener() {
        btnAddAttachment.setOnClickListener {
            onAddButtonClicked()
        }

        btnAddPdf.setOnClickListener {
            val intent = Intent()
            intent.type = "application/pdf"
            intent.action = Intent.ACTION_GET_CONTENT
            onAddButtonClicked()
            startActivityForResult(Intent.createChooser(intent, "Select PDF"), PDF_CODE)
        }

        btnAddImage.setOnClickListener {
            val intent = Intent()
            intent.type = "image/*"
            onAddButtonClicked()
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(Intent.createChooser(intent, "Select PDF"), IMAGE_CODE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(resultCode == Activity.RESULT_OK) {
            if(requestCode == IMAGE_CODE) {
                if(data!=null) {
                    selectedImageUri = data.data!!
                    uploadImage()
                }
            }
            else if(requestCode==PDF_CODE){
                if(data!=null) {
                    selectedPDFUri = data.data!!
                    uploadPDF()
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun uploadPDF() {
        val mDocsRef = mStorageRef.child(getRoomId()).child("uploads/$pdfNumberId.pdf")
        pdfNumberId++
        mDocsRef.putFile(selectedPDFUri).continueWithTask{ task->
            if (!task.isSuccessful) {
                task.exception?.let {
                    throw it
                }
            }
            mDocsRef.downloadUrl
        }.addOnCompleteListener {
            if (it.isSuccessful){
                Log.i("test", it.result.toString())
                pdfDownloadableUrl=it.result!!
                sendMessageToFirebase(pdfDownloadableUrl.toString(), "PDF")
            }
            else{
                Log.i("test", "upload failed")
            }
        }
    }

    private fun uploadImage() {
        val mDocsRef = mStorageRef.child(getRoomId()).child("uploads/$imageNumberId.png")
        imageNumberId++
        mDocsRef.putFile(selectedImageUri).continueWithTask{ task->
            if (!task.isSuccessful) {
                task.exception?.let {
                    throw it
                }
            }
            mDocsRef.downloadUrl
        }.addOnCompleteListener {
            if (it.isSuccessful){
                Log.i("test", it.result.toString())
                imageDownloadableUrl=it.result!!
                sendMessageToFirebase(imageDownloadableUrl.toString(), "IMAGE")
            }
            else{
                Log.i("test", "upload failed")
            }
        }
    }

    private fun listenToMessages() {
        getRoomFirebaseRef()
                .orderByKey()
                .addChildEventListener(object : ChildEventListener {
                    override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                        val message = snapshot.getValue(Message::class.java)!!
                        if (message.type!="DEFAULT") {
                            addMessageToRecyclerView(message)
                            pdfNumberId = message.pdfNumberId
                            imageNumberId = message.imageNumberId
                        }
//                        pbCAMessageLoading?.visibility = View.INVISIBLE
                    }

                    override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}

                    override fun onChildRemoved(snapshot: DataSnapshot) {}

                    override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}

                    override fun onCancelled(error: DatabaseError) {
                        Log.d("Chat-Error","$error")
                    }
                })
    }

    private fun addMessageToRecyclerView(msg: Message) {
        messages.add(msg)
        chatAdapter.notifyItemInserted(messages.size - 1)
        rvChatActivity.smoothScrollToPosition(chatAdapter.itemCount - 1)
    }

    private fun getRoomFirebaseRef() = db.reference.child("messages/${getRoomId()}")

    private fun getRoomId(): String {
        return CaseId.toString() + Dashboard.CardModelList[Dashboard.cardPositionClicked].lawyerFirebaseId
    }

    override fun onImageClicked(url: String, msgID: String) {
        val file = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS + "/ConsumerAdda/$msgID.jpeg"
        )
        val path = file.absolutePath
        if (file.exists()) {
            val intent = Intent(this, ImageViewActivity::class.java)
            intent.putExtra("ImagePath", path)
            startActivity(intent)
        } else {
            Toast.makeText(this, "Download file to view", Toast.LENGTH_SHORT).show()
            Log.i("test", "not Exist")
        }
    }

    override fun onPdfClicked(url: String, msgID: String) {
        val file = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS + "/ConsumerAdda/$msgID.pdf"
        )
        val path = file.absolutePath
        Log.i("test", path.toString())
        if (file.exists()) {
            val intent = Intent(this, PdfViewActivity::class.java)
            intent.putExtra("File", path)
            startActivity(intent)
        } else {
            Toast.makeText(this, "Download file to view", Toast.LENGTH_SHORT).show()
            Log.i("test", "not Exist")
        }
    }

    override fun onImageDownloadClicked(url: String, msgID: String) {
        currentDownloadUrl=url
        currentId=msgID
        flagForTypeDownload=0
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)==PackageManager.PERMISSION_DENIED){
                requestPermissions(
                        arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                        STORAGE_PERMISSION_CODE
                )
            }
            else{
                startDownload()
            }
        }
        else{
            startDownload()
        }
    }

    override fun onPdfDownloadClicked(url: String, msgID: String) {
        currentDownloadUrl=url
        currentId=msgID
        flagForTypeDownload=1
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)== PackageManager.PERMISSION_DENIED){
                requestPermissions(
                        arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                        STORAGE_PERMISSION_CODE
                )
            }
            else{
                startDownload()
            }
        }
        else{
            startDownload()
        }
    }

    private fun startDownload() {
        val url=currentDownloadUrl
        val id=currentId
        if (flagForTypeDownload==0) {
            val request = DownloadManager.Request(Uri.parse(url))
            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
            request.setTitle("Download Image - CA")
            request.setDescription("File is downloading")
            request.allowScanningByMediaScanner()
            Toast.makeText(this,"Download started",Toast.LENGTH_SHORT).show()
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "ConsumerAdda/$id.jpeg")

            val manager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            manager.enqueue(request)
        }
        else{
            val request = DownloadManager.Request(Uri.parse(url))
            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
            request.setTitle("Download pdf - CA")
            request.setDescription("File is downloading")
            Toast.makeText(this,"Download started",Toast.LENGTH_SHORT).show()
            request.allowScanningByMediaScanner()
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "ConsumerAdda/$id.pdf")

            val manager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            manager.enqueue(request)
        }
    }

    override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<out String>,
            grantResults: IntArray
    ) {
        when(requestCode){
            STORAGE_PERMISSION_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startDownload()
                } else {
                    Toast.makeText(this, "PERMISSION DENIED!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun onAddButtonClicked() {
        fromBottom.duration=500
        toBottom.duration=300
        rotateOpen.duration=300
        rotateClose.duration=300

        if(!btnAttachmentClicked)
        {
            btnAddPdf.visibility= View.GONE
            btnAddImage.visibility= View.VISIBLE
            btnAddImage.isClickable=true
            btnAddPdf.isClickable=false
            btnAddImage.isFocusable=true
            btnAddPdf.isFocusable=false
            btnAddImage.isEnabled=true
            btnAddPdf.isEnabled=false
            btnAddPdf.startAnimation(fromBottom)
            btnAddImage.startAnimation(fromBottom)
            btnAddAttachment.startAnimation(rotateOpen)
        }
        else
        {
            btnAddImage.visibility=View.INVISIBLE
            btnAddImage.isClickable=false
            btnAddPdf.isClickable=false
            btnAddImage.isFocusable=false
            btnAddPdf.isFocusable=false
            btnAddImage.isEnabled=false
            btnAddPdf.isEnabled=false
            btnAddPdf.startAnimation(toBottom)
            btnAddImage.startAnimation(toBottom)
            btnAddAttachment.startAnimation(rotateClose)
        }

        btnAttachmentClicked = !btnAttachmentClicked
    }

    private fun btnChatDetailsSetOnClickListener() {
        btnChatDetails.setOnClickListener{
            startActivity(Intent(this,ChatDetailActivity::class.java))
        }
    }

    private fun btnBackChatSetOnClickListener() {
        btnBackChat.setOnClickListener {
            val intent = Intent(this,Dashboard::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
        }
    }

}

interface OnClicked
{
    fun onImageClicked(url: String, msgID: String)

    fun onPdfClicked(url: String, msgID: String)

    fun onImageDownloadClicked(url: String, msgID: String)

    fun onPdfDownloadClicked(url: String, msgID: String)
}
