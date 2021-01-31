package com.example.consumeradda.activities

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.consumeradda.R
import com.example.consumeradda.activities.support.About
import com.example.consumeradda.activities.support.ContactUs
import com.example.consumeradda.activities.support.Services
import com.example.consumeradda.models.authModels.Register
import com.example.shareapp.adapters.DashboardCardAdapter
import com.example.consumeradda.models.cardModels.DashboardCardModel
import com.example.consumeradda.models.caseModels.Case
import com.example.consumeradda.service.AuthService
import com.example.consumeradda.service.CaseService
import com.example.consumeradda.service.ServiceBuilder
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_about.view.*
import kotlinx.android.synthetic.main.activity_complaint_form.*
import kotlinx.android.synthetic.main.activity_dashboard.*
import kotlinx.android.synthetic.main.forgot_password_dialog.*
import kotlinx.android.synthetic.main.location_dialog.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Dashboard : AppCompatActivity(), OnCardClicked {

    private lateinit var auth: FirebaseAuth
    private var isCardInfoDisplayed : Boolean = false
    private var cardPositionClicked : Int = -1
    private lateinit var cardAdapter: DashboardCardAdapter
    var role : Int = 2
    lateinit var currentUser : Register
    private lateinit var CardModelList: MutableList<Case>


    companion object {
        var DASHBOARDTAG="DASHBOARDTAG"
        var state="State"
        var district="District"
        var stateNum=0
        var districtNum=0
        var fetchCases = false
        var cardPositionClicked=-1
//        lateinit var CardModelList: MutableList<Case>
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)


        val sharedPreferences = getSharedPreferences("ConsumerAdda",Context.MODE_PRIVATE)
        var fireId = sharedPreferences.getString("FIREBASE_ID",null)
        role = sharedPreferences.getInt("ROLE",-1)
        val verif = sharedPreferences.getBoolean("VERIFIED",false)
        Log.d("VERIFY",verif.toString())
        Log.d("FirebaseID",fireId)
        Log.d("ROLES ",role.toString())

//        doSignOut()

        if(role==-1 || verif == false)
        {
            vpCardView.visibility = View.INVISIBLE

            clSupporter.visibility = View.VISIBLE
            pbSupport.visibility = View.VISIBLE
            tvSupportMessage1.visibility = View.INVISIBLE
            tvSupportMessage2.visibility = View.INVISIBLE
            Log.d("AC99","Getting Data")
            btnMore.isEnabled = false
            complaint_btn.isEnabled = false
            complaint_btn.visibility = View.INVISIBLE
            cvCurrent.visibility = View.INVISIBLE
            Toast.makeText(this,"Fetching user's data!",Toast.LENGTH_SHORT).show()

            getDataFromDB(fireId.toString())
        }
        else
        {
            if(!fetchCases)
            {
                fetchCases = true
                vpCardView.visibility = View.INVISIBLE
                Log.d("HERE","Done")
                clSupporter.visibility = View.VISIBLE
                pbSupport.visibility = View.VISIBLE
                tvSupportMessage1.visibility = View.INVISIBLE
                tvSupportMessage2.visibility = View.INVISIBLE
                btnMore.isEnabled = false
                complaint_btn.visibility = View.INVISIBLE
                cvCurrent.visibility = View.INVISIBLE


                if(role==1)
                {
                    Toast.makeText(this,"Fetching User's Data",Toast.LENGTH_LONG).show()
                    getDataFromDB(fireId.toString())
                }else
                {
                    Toast.makeText(this,"Fetching Cases",Toast.LENGTH_LONG).show()
                    getUserCases(fireId.toString())
                }
            }
            else
            {
                Toast.makeText(this,"Activity CREATE",Toast.LENGTH_LONG).show()
            }
        }

        complaint_btnSetOnClickListener()
        btnMoreSetOnClickListener()
        tvCardHeaderSetOnClickListener()
    }

    override fun onResume() {
        super.onResume()
        val sharedPreferences = getSharedPreferences("ConsumerAdda",Context.MODE_PRIVATE)
        var fireId = sharedPreferences.getString("FIREBASE_ID",null)
        role = sharedPreferences.getInt("ROLE",-1)
        val verif = sharedPreferences.getBoolean("VERIFIED",false)
        Log.d("VERIFY",verif.toString())
//
//
        Log.d("FirebaseID",fireId)
        Log.d("ROLES ",role.toString())
        Log.d("PHASE","Resumed")

        if(role==-1 || verif == false)
        {
            vpCardView.visibility = View.INVISIBLE

            clSupporter.visibility = View.VISIBLE
            pbSupport.visibility = View.VISIBLE
            tvSupportMessage1.visibility = View.INVISIBLE
            tvSupportMessage2.visibility = View.INVISIBLE

            btnMore.isEnabled = false
            complaint_btn.isEnabled = false
            complaint_btn.visibility = View.INVISIBLE
            cvCurrent.visibility = View.INVISIBLE

            getDataFromDB(fireId.toString())
        }
        else
        {
            if(!fetchCases)
            {
                fetchCases = true
                vpCardView.visibility = View.INVISIBLE
                Log.d("HERE","Done")
                clSupporter.visibility = View.VISIBLE
                pbSupport.visibility = View.VISIBLE
                tvSupportMessage1.visibility = View.INVISIBLE
                tvSupportMessage2.visibility = View.INVISIBLE
                btnMore.isEnabled = false
                complaint_btn.visibility = View.INVISIBLE
                cvCurrent.visibility = View.INVISIBLE

                if(role==1)
                {
                    Toast.makeText(this,"Fetching User's Data",Toast.LENGTH_SHORT).show()
                    getDataFromDB(fireId.toString())
                }else
                {
                    Toast.makeText(this,"Fetching Cases",Toast.LENGTH_SHORT).show()
                    getUserCases(fireId.toString())
                }
            }
            else
            {
                cvCurrent.visibility = View.INVISIBLE
                complaint_btn.visibility = View.INVISIBLE
                clSupporter.visibility = View.VISIBLE
                pbSupport.visibility = View.VISIBLE
                tvSupportMessage1.visibility = View.INVISIBLE
                tvSupportMessage2.visibility = View.INVISIBLE
                Toast.makeText(this,"Fetching Cases",Toast.LENGTH_SHORT).show()
                getUserCases(fireId.toString())
            }
        }
    }

    override fun onRestart() {
        super.onRestart()

        val sharedPreferences = getSharedPreferences("ConsumerAdda",Context.MODE_PRIVATE)
        var fireId = sharedPreferences.getString("FIREBASE_ID",null)
        role = sharedPreferences.getInt("ROLE",-1)
        val verif = sharedPreferences.getBoolean("VERIFIED",false)
        Log.d("VERIFY",verif.toString())
//
//
        Log.d("FirebaseID",fireId)
        Log.d("ROLES ",role.toString())

        if(role==-1 || verif == false)
        {
            vpCardView.visibility = View.INVISIBLE

            clSupporter.visibility = View.VISIBLE
            pbSupport.visibility = View.VISIBLE
            tvSupportMessage1.visibility = View.INVISIBLE
            tvSupportMessage2.visibility = View.INVISIBLE

            btnMore.isEnabled = false
            complaint_btn.isEnabled = false
            complaint_btn.visibility = View.INVISIBLE
            cvCurrent.visibility = View.INVISIBLE

            getDataFromDB(fireId.toString())
        }
        else
        {
            if(!fetchCases)
            {
                fetchCases = true
                vpCardView.visibility = View.INVISIBLE
                Log.d("HERE","Done")
                clSupporter.visibility = View.VISIBLE
                pbSupport.visibility = View.VISIBLE
                tvSupportMessage1.visibility = View.INVISIBLE
                tvSupportMessage2.visibility = View.INVISIBLE
                btnMore.isEnabled = false
                complaint_btn.visibility = View.INVISIBLE
                cvCurrent.visibility = View.INVISIBLE

                if(role==1)
                {
                    Toast.makeText(this,"Fetching User's Data",Toast.LENGTH_SHORT).show()
                    getDataFromDB(fireId.toString())
                }else
                {
                    Toast.makeText(this,"Fetching Cases",Toast.LENGTH_SHORT).show()
                    getUserCases(fireId.toString())
                }
            }
            else
            {
                Toast.makeText(this,"Activity RESTART",Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun complaint_btnSetOnClickListener() {
        complaint_btn.setOnClickListener {
            if(complaint_btn.text.toString() == "Submit Complaint")
            {
                startActivity(Intent(this, ComplaintForm::class.java))
            }
            else if(complaint_btn.text.toString() == "Cases List")
            {
                if(stateNum==0)
                {
                    Toast.makeText(this,"You have to select either\na State or both State and District",Toast.LENGTH_LONG).show()
                }
                else
                {
                    startActivity(Intent(this,CasesListActivity::class.java))
                }
            }
            else
            {
                val sharedPreferences = getSharedPreferences("ConsumerAdda",Context.MODE_PRIVATE)
                val naam = sharedPreferences.getString("NAME","").toString()
                auth = FirebaseAuth.getInstance()
                val email = auth.currentUser?.email
                val intent = Intent(Intent.ACTION_SEND)
                val recipients = arrayOf("consumersadda@gmail.com")
                intent.putExtra(Intent.EXTRA_EMAIL, recipients)
                intent.putExtra(Intent.EXTRA_SUBJECT,"Identity Verification Request")
                intent.putExtra(Intent.EXTRA_TEXT, "Dear Admin,\n\nThis is an identity verification request mail from ${naam}.\nMy registered email address is\n\n${email}\n\n Looking for a positive response from your end.\n\nRegards,\n ${naam}")

                intent.type = "text/html"
                intent.setPackage("com.google.android.gm")
                startActivity(Intent.createChooser(intent, "Send mail"))
            }
        }
    }

    private fun getDataFromDB(fireId: String) {

//        pbSupport.visibility = View.INVISIBLE
//        btnMore.isEnabled = true
//        return;
//
//
        val authService = ServiceBuilder.buildService(AuthService::class.java)
        val requestCall = authService.getUserData(fireId)
        requestCall.enqueue(object: Callback<List<Register>> {
            override fun onResponse(call: Call<List<Register>>, response: Response<List<Register>>)
            {
                if(response.isSuccessful)
                {
                    if(response.body()!!.size!=0)
                    {
                        currentUser = response.body()!!.get(0)

                        role = if(currentUser.isLawyer) {
                            1
                        } else {
                            0
                        }
                        val sharedPreferences = getSharedPreferences("ConsumerAdda",Context.MODE_PRIVATE)
                        val editor = sharedPreferences.edit()
                        editor.apply {
                                putInt("ROLE",role)
                                putBoolean("VERIFIED",currentUser.isVerified)
                                putString("NAME",currentUser.name)
                                putString("FIREBASE_ID",currentUser.firebaseId)
                            }.apply()
                        var temp = sharedPreferences.getInt("ROLE",5)
                        Log.d("TEMP",temp.toString())
                        if(currentUser.isVerified)
                        {
                            fetchCases = true
                            Toast.makeText(this@Dashboard,"Fetching Cases",Toast.LENGTH_SHORT).show()
                            getUserCases(currentUser.firebaseId)
                        }
                        else
                        {
                            pbSupport.visibility = View.INVISIBLE
                            tvSupportMessage1.visibility = View.VISIBLE
                            btnMore.isEnabled = true
                            tvSupportMessage2.visibility = View.VISIBLE
                            complaint_btn.visibility = View.VISIBLE
                            val sharedPreferences = getSharedPreferences("ConsumerAdda",Context.MODE_PRIVATE)
                            val naam = sharedPreferences.getString("NAME","").toString()
                            tvSupportMessage1.text = "Hello $naam !"
                            tvSupportMessage2.text = "Kindly verify your identity to\nget started with Consumer Adda!"
                            complaint_btn.text = "Request Verification!"
                            complaint_btn.isEnabled = true
//                            updateDashboard()
                        }
                    }
                    else
                    {
                        pbSupport.visibility = View.INVISIBLE
                        btnMore.isEnabled = true
                        tvSupportMessage1.text = "Some error Occurred!\nPlease restart the app."
                        Toast.makeText(this@Dashboard,"An error occured! ${response.body().toString()}",Toast.LENGTH_LONG).show()
                    }
                }
                else
                {
                    val jObjError = JSONObject(response.errorBody()!!.string())
                    Toast.makeText(this@Dashboard,"${jObjError.getString("msg")}",Toast.LENGTH_LONG).show()
                }
            }
            override fun onFailure(call: Call<List<Register>>, t: Throwable) {
                Toast.makeText(this@Dashboard,"No Internet / Server Down",Toast.LENGTH_SHORT).show()
            }
        })

    }

    private fun getUserCases(fireId:String)
    {
        val caseService = ServiceBuilder.buildService(CaseService::class.java)
        val requestCall = caseService.fetchCases(fireId)
        requestCall.enqueue(object: Callback<MutableList<Case>>{
            override fun onResponse(call: Call<MutableList<Case>>, response: Response<MutableList<Case>>) {
                if(response.isSuccessful)
                {
                    CardModelList = response.body()!!
                    updateDashboard()
                }
                else
                {
                    pbSupport.visibility = View.INVISIBLE
                    tvSupportMessage1.visibility = View.VISIBLE
                    tvSupportMessage1.text = "Please restart the app!"
                    val jObjError = JSONObject(response.errorBody()!!.string())
                    Toast.makeText(this@Dashboard,"${jObjError.getString("msg")}",Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<MutableList<Case>>, t: Throwable) {
                Toast.makeText(this@Dashboard,"No Internet / Server Error",Toast.LENGTH_LONG).show()
            }

        })
    }

    private fun tvCardHeaderSetOnClickListener() {


        tvCardHeader.setOnClickListener {
            if(tvCardHeader.text.equals("Location Selected") || tvCardHeader.text.equals("Select Location"))
            {


                val mDialog = BottomSheetDialog(this)
                mDialog.setContentView(R.layout.location_dialog)
                val window = mDialog.window
                window?.setLayout(
                    WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.WRAP_CONTENT
                )
                mDialog.setCanceledOnTouchOutside(true)
                mDialog.setCancelable(true)
                mDialog.show()
                val StatesList = arrayOf("Select One:","Andhra Pradesh","Arunachal Pradesh","Assam","Bihar","Chandigarh (UT)","Chhattisgarh","Dadra and Nagar Haveli (UT)","Daman and Diu (UT)","Delhi (NCT)","Goa","Gujarat","Haryana","Himachal Pradesh","Jammu and Kashmir","Jharkhand","Karnataka","Kerala","Lakshadweep (UT)","Madhya Pradesh","Maharashtra","Manipur","Meghalaya","Mizoram","Nagaland","Odisha","Puducherry (UT)","Punjab","Rajasthan","Sikkim","Tamil Nadu","Telangana","Tripura","Uttarakhand","Uttar Pradesh","West Bengal")
                mDialog.spState.adapter = ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,StatesList)

                mDialog.spState.onItemSelectedListener = object : AdapterView.OnItemClickListener, AdapterView.OnItemSelectedListener{
                    override fun onItemClick(parent: AdapterView<*>?,view: View?,position: Int,id: Long)
                    {
                        TODO("Not yet implemented")
                    }

                    override fun onItemSelected(parent: AdapterView<*>?, view: View?,position: Int, id: Long)
                    {
                        state = StatesList[position]
                        stateNum = position
                        when(position)
                        {
                            0-> {
                                val districts = arrayOf("Select One:")
                                mDialog.spDistrict.adapter = ArrayAdapter<String>(this@Dashboard,android.R.layout.simple_list_item_1,districts)
                                mDialog.spDistrict.onItemSelectedListener = object : AdapterView.OnItemClickListener, AdapterView.OnItemSelectedListener {
                                    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                                        TODO("Not yet implemented")
                                    }

                                    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                                        district = "Select One:"
                                        districtNum = 0
                                    }

                                    override fun onNothingSelected(parent: AdapterView<*>?) {
                                        TODO("Not yet implemented")
                                    }

                                }
                            }
                            1-> {
                                val districts = arrayOf("Select One:","Anantapur","Chittoor","East Godavari","Guntur","Krishna","Kurnool","Nellore","Prakasam","Srikakulam","Visakhapatnam","Vizianagaram","West Godavari","YSR Kadapa")
                                mDialog.spDistrict.adapter = ArrayAdapter<String>(this@Dashboard,android.R.layout.simple_list_item_1,districts)
                                mDialog.spDistrict.onItemSelectedListener = object : AdapterView.OnItemClickListener, AdapterView.OnItemSelectedListener{
                                    override fun onItemClick(parent: AdapterView<*>?,view: View?,position: Int,id: Long) {
                                        TODO("Not yet implemented")
                                    }

                                    override fun onItemSelected(parent: AdapterView<*>?, view: View?,position: Int,id: Long) {
                                        districtNum = position
                                        if(position!=0)
                                        {
                                            district = districts[position]
                                        }
                                        else{
                                            district = "Select One:"
                                        }
                                    }

                                    override fun onNothingSelected(parent: AdapterView<*>?) {
                                        TODO("Not yet implemented")
                                    }

                                }
                            }
                            2->{
                                val districts = arrayOf("Select One:","Tawang","West Kameng","East Kameng","Papum Pare","Kurung Kumey","Kra Daadi","Lower Subansiri","Upper Subansiri","West Siang","East Siang","Siang","Upper Siang","Lower Siang","Lower Dibang Valley","Dibang Valley","Anjaw","Lohit","Namsai","Changlang","Tirap","Longding")
                                mDialog.spDistrict.adapter = ArrayAdapter<String>(this@Dashboard,android.R.layout.simple_list_item_1,districts)
                                mDialog.spDistrict.onItemSelectedListener = object : AdapterView.OnItemClickListener, AdapterView.OnItemSelectedListener{
                                    override fun onItemClick(parent: AdapterView<*>?,view: View?,position: Int,id: Long) {
                                        TODO("Not yet implemented")
                                    }

                                    override fun onItemSelected(parent: AdapterView<*>?, view: View?,position: Int,id: Long) {
                                        districtNum = position
                                        if(position!=0)
                                        {
                                            district = districts[position]
                                        }
                                        else{
                                            district = "Select One:"
                                        }
                                    }

                                    override fun onNothingSelected(parent: AdapterView<*>?) {
                                        TODO("Not yet implemented")
                                    }

                                }
                            }
                            3->{
                                val districts = arrayOf("Select One:","Baksa","Barpeta","Biswanath","Bongaigaon","Cachar","Charaideo","Chirang","Darrang","Dhemaji","Dhubri","Dibrugarh","Goalpara","Golaghat","Hailakandi","Hojai","Jorhat","Kamrup Metropolitan","Kamrup","Karbi Anglong","Karimganj","Kokrajhar","Lakhimpur","Majuli","Morigaon","Nagaon","Nalbari","Dima Hasao","Sivasagar","Sonitpur","South Salmara-Mankachar","Tinsukia","Udalguri","West Karbi Anglong")
                                mDialog.spDistrict.adapter = ArrayAdapter<String>(this@Dashboard,android.R.layout.simple_list_item_1,districts)
                                mDialog.spDistrict.onItemSelectedListener = object : AdapterView.OnItemClickListener, AdapterView.OnItemSelectedListener{
                                    override fun onItemClick(parent: AdapterView<*>?,view: View?,position: Int,id: Long) {
                                        TODO("Not yet implemented")
                                    }

                                    override fun onItemSelected(parent: AdapterView<*>?, view: View?,position: Int,id: Long) {
                                        districtNum = position
                                        if(position!=0)
                                        {
                                            district = districts[position]
                                        }
                                        else{
                                            district = "Select One:"
                                        }
                                    }

                                    override fun onNothingSelected(parent: AdapterView<*>?) {
                                        TODO("Not yet implemented")
                                    }

                                }
                            }
                            4->{
                                val districts = arrayOf("Select One:","Araria","Arwal","Aurangabad","Banka","Begusarai","Bhagalpur","Bhojpur","Buxar","Darbhanga","East Champaran (Motihari)","Gaya","Gopalganj","Jamui","Jehanabad","Kaimur (Bhabua)","Katihar","Khagaria","Kishanganj","Lakhisarai","Madhepura","Madhubani","Munger (Monghyr)","Muzaffarpur","Nalanda","Nawada","Patna","Purnia (Purnea)","Rohtas","Saharsa","Samastipur","Saran","Sheikhpura","Sheohar","Sitamarhi","Siwan","Supaul","Vaishali","West Champaran")
                                mDialog.spDistrict.adapter = ArrayAdapter<String>(this@Dashboard,android.R.layout.simple_list_item_1,districts)
                                mDialog.spDistrict.onItemSelectedListener = object : AdapterView.OnItemClickListener, AdapterView.OnItemSelectedListener{
                                    override fun onItemClick(parent: AdapterView<*>?,view: View?,position: Int,id: Long) {
                                        TODO("Not yet implemented")
                                    }

                                    override fun onItemSelected(parent: AdapterView<*>?, view: View?,position: Int,id: Long) {
                                        districtNum = position
                                        if(position!=0)
                                        {
                                            district = districts[position]
                                        }
                                        else{
                                            district = "Select One:"
                                        }
                                    }

                                    override fun onNothingSelected(parent: AdapterView<*>?) {
                                        TODO("Not yet implemented")
                                    }

                                }
                            }
                            5->{
                                val districts = arrayOf("Select One:","Chandigarh")
                                mDialog.spDistrict.adapter = ArrayAdapter<String>(this@Dashboard,android.R.layout.simple_list_item_1,districts)
                                mDialog.spDistrict.onItemSelectedListener = object : AdapterView.OnItemClickListener, AdapterView.OnItemSelectedListener{
                                    override fun onItemClick(parent: AdapterView<*>?,view: View?,position: Int,id: Long) {
                                        TODO("Not yet implemented")
                                    }

                                    override fun onItemSelected(parent: AdapterView<*>?, view: View?,position: Int,id: Long) {
                                        districtNum = position
                                        if(position!=0)
                                        {
                                            district = districts[position]
                                        }
                                        else{
                                            district = "Select One:"
                                        }
                                    }

                                    override fun onNothingSelected(parent: AdapterView<*>?) {
                                        TODO("Not yet implemented")
                                    }

                                }
                            }
                            6->{
                                val districts = arrayOf("Select One:","Balod","Baloda Bazar","Balrampur","Bastar","Bemetara","Bijapur","Bilaspur","Dantewada (South Bastar)","Dhamtari","Durg","Gariyaband","Janjgir-Champa","Jashpur","Kabirdham (Kawardha)","Kanker (North Bastar)","Kondagaon","Korba","Korea (Koriya)","Mahasamund","Mungeli","Narayanpur","Raigarh","Raipur","Rajnandgaon","Sukma","Surajpur","Surguja")
                                mDialog.spDistrict.adapter = ArrayAdapter<String>(this@Dashboard,android.R.layout.simple_list_item_1,districts)
                                mDialog.spDistrict.onItemSelectedListener = object : AdapterView.OnItemClickListener, AdapterView.OnItemSelectedListener{
                                    override fun onItemClick(parent: AdapterView<*>?,view: View?,position: Int,id: Long) {
                                        TODO("Not yet implemented")
                                    }

                                    override fun onItemSelected(parent: AdapterView<*>?, view: View?,position: Int,id: Long) {
                                        districtNum = position
                                        if(position!=0)
                                        {
                                            district = districts[position]
                                        }
                                        else{
                                            district = "Select One:"
                                        }
                                    }

                                    override fun onNothingSelected(parent: AdapterView<*>?) {
                                        TODO("Not yet implemented")
                                    }

                                }
                            }
                            7->{
                                val districts = arrayOf("Select One:","Dadra & Nagar Haveli")
                                mDialog.spDistrict.adapter = ArrayAdapter<String>(this@Dashboard,android.R.layout.simple_list_item_1,districts)
                                mDialog.spDistrict.onItemSelectedListener = object : AdapterView.OnItemClickListener, AdapterView.OnItemSelectedListener{
                                    override fun onItemClick(parent: AdapterView<*>?,view: View?,position: Int,id: Long) {
                                        TODO("Not yet implemented")
                                    }

                                    override fun onItemSelected(parent: AdapterView<*>?, view: View?,position: Int,id: Long) {
                                        districtNum = position
                                        if(position!=0)
                                        {
                                            district = districts[position]
                                        }
                                        else{
                                            district = "Select One:"
                                        }
                                    }

                                    override fun onNothingSelected(parent: AdapterView<*>?) {
                                        TODO("Not yet implemented")
                                    }

                                }
                            }
                            8->{
                                val districts = arrayOf("Select One:","Daman","Diu")
                                mDialog.spDistrict.adapter = ArrayAdapter<String>(this@Dashboard,android.R.layout.simple_list_item_1,districts)
                                mDialog.spDistrict.onItemSelectedListener = object : AdapterView.OnItemClickListener, AdapterView.OnItemSelectedListener{
                                    override fun onItemClick(parent: AdapterView<*>?,view: View?,position: Int,id: Long) {
                                        TODO("Not yet implemented")
                                    }

                                    override fun onItemSelected(parent: AdapterView<*>?, view: View?,position: Int,id: Long) {
                                        districtNum = position
                                        if(position!=0)
                                        {
                                            district = districts[position]
                                        }
                                        else{
                                            district = "Select One:"
                                        }
                                    }

                                    override fun onNothingSelected(parent: AdapterView<*>?) {
                                        TODO("Not yet implemented")
                                    }

                                }
                            }
                            9->{
                                val districts = arrayOf("Select One:","Central Delhi","East Delhi","New Delhi","North Delhi","North East  Delhi","North West  Delhi","Shahdara","South Delhi","South East Delhi","South West  Delhi","West Delhi")
                                mDialog.spDistrict.adapter = ArrayAdapter<String>(this@Dashboard,android.R.layout.simple_list_item_1,districts)
                                mDialog.spDistrict.onItemSelectedListener = object : AdapterView.OnItemClickListener, AdapterView.OnItemSelectedListener{
                                    override fun onItemClick(parent: AdapterView<*>?,view: View?,position: Int,id: Long) {
                                        TODO("Not yet implemented")
                                    }

                                    override fun onItemSelected(parent: AdapterView<*>?, view: View?,position: Int,id: Long) {
                                        districtNum = position
                                        if(position!=0)
                                        {
                                            district = districts[position]
                                        }
                                        else{
                                            district = "Select One:"
                                        }
                                    }

                                    override fun onNothingSelected(parent: AdapterView<*>?) {
                                        TODO("Not yet implemented")
                                    }

                                }
                            }
                            10->{
                                val districts = arrayOf("Select One:","North Goa","South Goa")
                                mDialog.spDistrict.adapter = ArrayAdapter<String>(this@Dashboard,android.R.layout.simple_list_item_1,districts)
                                mDialog.spDistrict.onItemSelectedListener = object : AdapterView.OnItemClickListener, AdapterView.OnItemSelectedListener{
                                    override fun onItemClick(parent: AdapterView<*>?,view: View?,position: Int,id: Long) {
                                        TODO("Not yet implemented")
                                    }

                                    override fun onItemSelected(parent: AdapterView<*>?, view: View?,position: Int,id: Long) {
                                        districtNum = position
                                        if(position!=0)
                                        {
                                            district = districts[position]
                                        }
                                        else{
                                            district = "Select One:"
                                        }
                                    }

                                    override fun onNothingSelected(parent: AdapterView<*>?) {
                                        TODO("Not yet implemented")
                                    }

                                }
                            }
                            11->{
                                val districts = arrayOf("Select One:","Ahmedabad","Amreli","Anand","Aravalli","Banaskantha (Palanpur)","Bharuch","Bhavnagar","Botad","Chhota Udepur","Dahod","Dangs (Ahwa)","Devbhoomi Dwarka","Gandhinagar","Gir Somnath","Jamnagar","Junagadh","Kachchh","Kheda (Nadiad)","Mahisagar","Mehsana","Morbi","Narmada (Rajpipla)","Navsari","Panchmahal (Godhra)","Patan","Porbandar","Rajkot","Sabarkantha (Himmatnagar)","Surat","Surendranagar","Tapi (Vyara)","Vadodara","Valsad")
                                mDialog.spDistrict.adapter = ArrayAdapter<String>(this@Dashboard,android.R.layout.simple_list_item_1,districts)
                                mDialog.spDistrict.onItemSelectedListener = object : AdapterView.OnItemClickListener, AdapterView.OnItemSelectedListener{
                                    override fun onItemClick(parent: AdapterView<*>?,view: View?,position: Int,id: Long) {
                                        TODO("Not yet implemented")
                                    }

                                    override fun onItemSelected(parent: AdapterView<*>?, view: View?,position: Int,id: Long) {
                                        districtNum = position
                                        if(position!=0)
                                        {
                                            district = districts[position]
                                        }
                                        else{
                                            district = "Select One:"
                                        }
                                    }

                                    override fun onNothingSelected(parent: AdapterView<*>?) {
                                        TODO("Not yet implemented")
                                    }

                                }
                            }
                            12->{
                                val districts = arrayOf("Select One:","Ambala","Bhiwani","Charkhi Dadri","Faridabad","Fatehabad","Gurgaon","Hisar","Jhajjar","Jind","Kaithal","Karnal","Kurukshetra","Mahendragarh","Mewat","Palwal","Panchkula","Panipat","Rewari","Rohtak","Sirsa","Sonipat","Yamunanagar")
                                mDialog.spDistrict.adapter = ArrayAdapter<String>(this@Dashboard,android.R.layout.simple_list_item_1,districts)
                                mDialog.spDistrict.onItemSelectedListener = object : AdapterView.OnItemClickListener, AdapterView.OnItemSelectedListener{
                                    override fun onItemClick(parent: AdapterView<*>?,view: View?,position: Int,id: Long) {
                                        TODO("Not yet implemented")
                                    }

                                    override fun onItemSelected(parent: AdapterView<*>?, view: View?,position: Int,id: Long) {
                                        districtNum = position
                                        if(position!=0)
                                        {
                                            district = districts[position]
                                        }
                                        else{
                                            district = "Select One:"
                                        }
                                    }

                                    override fun onNothingSelected(parent: AdapterView<*>?) {
                                        TODO("Not yet implemented")
                                    }

                                }
                            }
                            13->{
                                val districts = arrayOf("Select One:","Bilaspur","Chamba","Hamirpur","Kangra","Kinnaur","Kullu","Lahaul & Spiti","Mandi","Shimla","Sirmaur (Sirmour)","Solan","Una")
                                mDialog.spDistrict.adapter = ArrayAdapter<String>(this@Dashboard,android.R.layout.simple_list_item_1,districts)
                                mDialog.spDistrict.onItemSelectedListener = object : AdapterView.OnItemClickListener, AdapterView.OnItemSelectedListener{
                                    override fun onItemClick(parent: AdapterView<*>?,view: View?,position: Int,id: Long) {
                                        TODO("Not yet implemented")
                                    }

                                    override fun onItemSelected(parent: AdapterView<*>?, view: View?,position: Int,id: Long) {
                                        districtNum = position
                                        if(position!=0)
                                        {
                                            district = districts[position]
                                        }
                                        else{
                                            district = "Select One:"
                                        }
                                    }

                                    override fun onNothingSelected(parent: AdapterView<*>?) {
                                        TODO("Not yet implemented")
                                    }

                                }
                            }
                            14->{
                                val districts = arrayOf("Select One:","Anantnag","Bandipore","Baramulla","Budgam","Doda","Ganderbal","Jammu","Kargil","Kathua","Kishtwar","Kulgam","Kupwara","Leh","Poonch","Pulwama","Rajouri","Ramban","Reasi","Samba","Shopian","Srinagar","Udhampur")
                                mDialog.spDistrict.adapter = ArrayAdapter<String>(this@Dashboard,android.R.layout.simple_list_item_1,districts)
                                mDialog.spDistrict.onItemSelectedListener = object : AdapterView.OnItemClickListener, AdapterView.OnItemSelectedListener{
                                    override fun onItemClick(parent: AdapterView<*>?,view: View?,position: Int,id: Long) {
                                        TODO("Not yet implemented")
                                    }

                                    override fun onItemSelected(parent: AdapterView<*>?, view: View?,position: Int,id: Long) {
                                        districtNum = position
                                        if(position!=0)
                                        {
                                            district = districts[position]
                                        }
                                        else{
                                            district = "Select One:"
                                        }
                                    }

                                    override fun onNothingSelected(parent: AdapterView<*>?) {
                                        TODO("Not yet implemented")
                                    }

                                }
                            }
                            15->{
                                val districts = arrayOf("Select One:","Bokaro","Chatra","Deoghar","Dhanbad","Dumka","East Singhbhum","Garhwa","Giridih","Godda","Gumla","Hazaribag","Jamtara","Khunti","Koderma","Latehar","Lohardaga","Pakur","Palamu","Ramgarh","Ranchi","Sahibganj","Seraikela-Kharsawan","Simdega","West Singhbhum")
                                mDialog.spDistrict.adapter = ArrayAdapter<String>(this@Dashboard,android.R.layout.simple_list_item_1,districts)
                                mDialog.spDistrict.onItemSelectedListener = object : AdapterView.OnItemClickListener, AdapterView.OnItemSelectedListener{
                                    override fun onItemClick(parent: AdapterView<*>?,view: View?,position: Int,id: Long) {
                                        TODO("Not yet implemented")
                                    }

                                    override fun onItemSelected(parent: AdapterView<*>?, view: View?,position: Int,id: Long) {
                                        districtNum = position
                                        if(position!=0)
                                        {
                                            district = districts[position]
                                        }
                                        else{
                                            district = "Select One:"
                                        }
                                    }

                                    override fun onNothingSelected(parent: AdapterView<*>?) {
                                        TODO("Not yet implemented")
                                    }

                                }
                            }
                            16->{
                                val districts = arrayOf("Select One:","Bagalkot","Ballari (Bellary)","Belagavi (Belgaum)","Bengaluru (Bangalore) Rural","Bengaluru (Bangalore) Urban","Bidar","Chamarajanagar","Chikballapur","Chikkamagaluru (Chikmagalur)","Chitradurga","Dakshina Kannada","Davangere","Dharwad","Gadag","Hassan","Haveri","Kalaburagi (Gulbarga)","Kodagu","Kolar","Koppal","Mandya","Mysuru (Mysore)","Raichur","Ramanagara","Shivamogga (Shimoga)","Tumakuru (Tumkur)","Udupi","Uttara Kannada (Karwar)","Vijayapura (Bijapur)","Yadgir")
                                mDialog.spDistrict.adapter = ArrayAdapter<String>(this@Dashboard,android.R.layout.simple_list_item_1,districts)
                                mDialog.spDistrict.onItemSelectedListener = object : AdapterView.OnItemClickListener, AdapterView.OnItemSelectedListener{
                                    override fun onItemClick(parent: AdapterView<*>?,view: View?,position: Int,id: Long) {
                                        TODO("Not yet implemented")
                                    }

                                    override fun onItemSelected(parent: AdapterView<*>?, view: View?,position: Int,id: Long) {
                                        districtNum = position
                                        if(position!=0)
                                        {
                                            district = districts[position]
                                        }
                                        else{
                                            district = "Select One:"
                                        }
                                    }

                                    override fun onNothingSelected(parent: AdapterView<*>?) {
                                        TODO("Not yet implemented")
                                    }

                                }
                            }
                            17->{
                                val districts = arrayOf("Select One:","Alappuzha","Ernakulam","Idukki","Kannur","Kasaragod","Kollam","Kottayam","Kozhikode","Malappuram","Palakkad","Pathanamthitta","Thiruvananthapuram","Thrissur","Wayanad")
                                mDialog.spDistrict.adapter = ArrayAdapter<String>(this@Dashboard,android.R.layout.simple_list_item_1,districts)
                                mDialog.spDistrict.onItemSelectedListener = object : AdapterView.OnItemClickListener, AdapterView.OnItemSelectedListener{
                                    override fun onItemClick(parent: AdapterView<*>?,view: View?,position: Int,id: Long) {
                                        TODO("Not yet implemented")
                                    }

                                    override fun onItemSelected(parent: AdapterView<*>?, view: View?,position: Int,id: Long) {
                                        districtNum = position
                                        if(position!=0)
                                        {
                                            district = districts[position]
                                        }
                                        else{
                                            district = "Select One:"
                                        }
                                    }

                                    override fun onNothingSelected(parent: AdapterView<*>?) {
                                        TODO("Not yet implemented")
                                    }

                                }
                            }
                            18->{
                                val districts = arrayOf("Select One:","Agatti","Amini","Androth","Bithra","Chethlath","Kavaratti","Kadmath","Kalpeni","Kilthan","Minicoy")
                                mDialog.spDistrict.adapter = ArrayAdapter<String>(this@Dashboard,android.R.layout.simple_list_item_1,districts)
                                mDialog.spDistrict.onItemSelectedListener = object : AdapterView.OnItemClickListener, AdapterView.OnItemSelectedListener{
                                    override fun onItemClick(parent: AdapterView<*>?,view: View?,position: Int,id: Long) {
                                        TODO("Not yet implemented")
                                    }

                                    override fun onItemSelected(parent: AdapterView<*>?, view: View?,position: Int,id: Long) {
                                        districtNum = position
                                        if(position!=0)
                                        {
                                            district = districts[position]
                                        }
                                        else{
                                            district = "Select One:"
                                        }
                                    }

                                    override fun onNothingSelected(parent: AdapterView<*>?) {
                                        TODO("Not yet implemented")
                                    }

                                }
                            }
                            19->{
                                val districts = arrayOf("Select One:","Agar Malwa","Alirajpur","Anuppur","Ashoknagar","Balaghat","Barwani","Betul","Bhind","Bhopal","Burhanpur","Chhatarpur","Chhindwara","Damoh","Datia","Dewas","Dhar","Dindori","Guna","Gwalior","Harda","Hoshangabad","Indore","Jabalpur","Jhabua","Katni","Khandwa","Khargone","Mandla","Mandsaur","Morena","Narsinghpur","Neemuch","Panna","Raisen","Rajgarh","Ratlam","Rewa","Sagar","Satna","Sehore","Seoni","Shahdol","Shajapur","Sheopur","Shivpuri","Sidhi","Singrauli","Tikamgarh","Ujjain","Umaria","Vidisha")
                                mDialog.spDistrict.adapter = ArrayAdapter<String>(this@Dashboard,android.R.layout.simple_list_item_1,districts)
                                mDialog.spDistrict.onItemSelectedListener = object : AdapterView.OnItemClickListener, AdapterView.OnItemSelectedListener{
                                    override fun onItemClick(parent: AdapterView<*>?,view: View?,position: Int,id: Long) {
                                        TODO("Not yet implemented")
                                    }

                                    override fun onItemSelected(parent: AdapterView<*>?, view: View?,position: Int,id: Long) {
                                        districtNum = position
                                        if(position!=0)
                                        {
                                            district = districts[position]
                                        }
                                        else{
                                            district = "Select One:"
                                        }
                                    }

                                    override fun onNothingSelected(parent: AdapterView<*>?) {
                                        TODO("Not yet implemented")
                                    }

                                }
                            }
                            20->{
                                val districts = arrayOf("Select One:","Ahmednagar","Akola","Amravati","Aurangabad","Beed","Bhandara","Buldhana","Chandrapur","Dhule","Gadchiroli","Gondia","Hingoli","Jalgaon","Jalna","Kolhapur","Latur","Mumbai district","Mumbai Suburban","Nagpur","Nanded","Nandurbar","Nashik","Osmanabad","Palghar","Parbhani","Pune","Raigad","Ratnagiri","Sangli","Satara","Sindhudurg","Solapur","Thane","Wardha","Washim","Yavatmal")
                                mDialog.spDistrict.adapter = ArrayAdapter<String>(this@Dashboard,android.R.layout.simple_list_item_1,districts)
                                mDialog.spDistrict.onItemSelectedListener = object : AdapterView.OnItemClickListener, AdapterView.OnItemSelectedListener{
                                    override fun onItemClick(parent: AdapterView<*>?,view: View?,position: Int,id: Long) {
                                        TODO("Not yet implemented")
                                    }

                                    override fun onItemSelected(parent: AdapterView<*>?, view: View?,position: Int,id: Long) {
                                        districtNum = position
                                        if(position!=0)
                                        {
                                            district = districts[position]
                                        }
                                        else{
                                            district = "Select One:"
                                        }
                                    }

                                    override fun onNothingSelected(parent: AdapterView<*>?) {
                                        TODO("Not yet implemented")
                                    }

                                }
                            }
                            21->{
                                val districts = arrayOf("Select One:","Bishnupur","Chandel","Churachandpur","Imphal East","Imphal West","Jiribam","Kakching","Kamjong","Kangpokpi","Noney","Pherzawl","Senapati","Tamenglong","Tengnoupal","Thoubal","Ukhrul")
                                mDialog.spDistrict.adapter = ArrayAdapter<String>(this@Dashboard,android.R.layout.simple_list_item_1,districts)
                                mDialog.spDistrict.onItemSelectedListener = object : AdapterView.OnItemClickListener, AdapterView.OnItemSelectedListener{
                                    override fun onItemClick(parent: AdapterView<*>?,view: View?,position: Int,id: Long) {
                                        TODO("Not yet implemented")
                                    }

                                    override fun onItemSelected(parent: AdapterView<*>?, view: View?,position: Int,id: Long) {
                                        districtNum = position
                                        if(position!=0)
                                        {
                                            district = districts[position]
                                        }
                                        else{
                                            district = "Select One:"
                                        }
                                    }

                                    override fun onNothingSelected(parent: AdapterView<*>?) {
                                        TODO("Not yet implemented")
                                    }

                                }
                            }
                            22->{
                                val districts = arrayOf("Select One:","East Garo Hills","East Jaintia Hills","East Khasi Hills","North Garo Hills","Ri Bhoi","South Garo Hills","South West Garo Hills ","South West Khasi Hills","West Garo Hills","West Jaintia Hills","West Khasi Hills")
                                mDialog.spDistrict.adapter = ArrayAdapter<String>(this@Dashboard,android.R.layout.simple_list_item_1,districts)
                                mDialog.spDistrict.onItemSelectedListener = object : AdapterView.OnItemClickListener, AdapterView.OnItemSelectedListener{
                                    override fun onItemClick(parent: AdapterView<*>?,view: View?,position: Int,id: Long) {
                                        TODO("Not yet implemented")
                                    }

                                    override fun onItemSelected(parent: AdapterView<*>?, view: View?,position: Int,id: Long) {
                                        districtNum = position
                                        if(position!=0)
                                        {
                                            district = districts[position]
                                        }
                                        else{
                                            district = "Select One:"
                                        }
                                    }

                                    override fun onNothingSelected(parent: AdapterView<*>?) {
                                        TODO("Not yet implemented")
                                    }

                                }
                            }
                            23->{
                                val districts = arrayOf("Select One:","Aizawl","Champhai","Kolasib","Lawngtlai","Lunglei","Mamit","Saiha","Serchhip")
                                mDialog.spDistrict.adapter = ArrayAdapter<String>(this@Dashboard,android.R.layout.simple_list_item_1,districts)
                                mDialog.spDistrict.onItemSelectedListener = object : AdapterView.OnItemClickListener, AdapterView.OnItemSelectedListener{
                                    override fun onItemClick(parent: AdapterView<*>?,view: View?,position: Int,id: Long) {
                                        TODO("Not yet implemented")
                                    }

                                    override fun onItemSelected(parent: AdapterView<*>?, view: View?,position: Int,id: Long) {
                                        districtNum = position
                                        if(position!=0)
                                        {
                                            district = districts[position]
                                        }
                                        else{
                                            district = "Select One:"
                                        }
                                    }

                                    override fun onNothingSelected(parent: AdapterView<*>?) {
                                        TODO("Not yet implemented")
                                    }

                                }
                            }
                            24->{
                                val districts = arrayOf("Select One:","Dimapur","Kiphire","Kohima","Longleng","Mokokchung","Mon","Peren","Phek","Tuensang","Wokha","Zunheboto")
                                mDialog.spDistrict.adapter = ArrayAdapter<String>(this@Dashboard,android.R.layout.simple_list_item_1,districts)
                                mDialog.spDistrict.onItemSelectedListener = object : AdapterView.OnItemClickListener, AdapterView.OnItemSelectedListener{
                                    override fun onItemClick(parent: AdapterView<*>?,view: View?,position: Int,id: Long) {
                                        TODO("Not yet implemented")
                                    }

                                    override fun onItemSelected(parent: AdapterView<*>?, view: View?,position: Int,id: Long) {
                                        districtNum = position
                                        if(position!=0)
                                        {
                                            district = districts[position]
                                        }
                                        else{
                                            district = "Select One:"
                                        }
                                    }

                                    override fun onNothingSelected(parent: AdapterView<*>?) {
                                        TODO("Not yet implemented")
                                    }

                                }
                            }
                            25->{
                                val districts = arrayOf("Select One:","Angul","Balangir","Balasore","Bargarh","Bhadrak","Boudh","Cuttack","Deogarh","Dhenkanal","Gajapati","Ganjam","Jagatsinghapur","Jajpur","Jharsuguda","Kalahandi","Kandhamal","Kendrapara","Kendujhar (Keonjhar)","Khordha","Koraput","Malkangiri","Mayurbhanj","Nabarangpur","Nayagarh","Nuapada","Puri","Rayagada","Sambalpur","Sonepur","Sundargarh")
                                mDialog.spDistrict.adapter = ArrayAdapter<String>(this@Dashboard,android.R.layout.simple_list_item_1,districts)
                                mDialog.spDistrict.onItemSelectedListener = object : AdapterView.OnItemClickListener, AdapterView.OnItemSelectedListener{
                                    override fun onItemClick(parent: AdapterView<*>?,view: View?,position: Int,id: Long) {
                                        TODO("Not yet implemented")
                                    }

                                    override fun onItemSelected(parent: AdapterView<*>?, view: View?,position: Int,id: Long) {
                                        districtNum = position
                                        if(position!=0)
                                        {
                                            district = districts[position]
                                        }
                                        else{
                                            district = "Select One:"
                                        }
                                    }

                                    override fun onNothingSelected(parent: AdapterView<*>?) {
                                        TODO("Not yet implemented")
                                    }

                                }
                            }
                            26->{
                                val districts = arrayOf("Select One:","Karaikal","Mahe","Pondicherry","Yanam")
                                mDialog.spDistrict.adapter = ArrayAdapter<String>(this@Dashboard,android.R.layout.simple_list_item_1,districts)
                                mDialog.spDistrict.onItemSelectedListener = object : AdapterView.OnItemClickListener, AdapterView.OnItemSelectedListener{
                                    override fun onItemClick(parent: AdapterView<*>?,view: View?,position: Int,id: Long) {
                                        TODO("Not yet implemented")
                                    }

                                    override fun onItemSelected(parent: AdapterView<*>?, view: View?,position: Int,id: Long) {
                                        districtNum = position
                                        if(position!=0)
                                        {
                                            district = districts[position]
                                        }
                                        else{
                                            district = "Select One:"
                                        }
                                    }

                                    override fun onNothingSelected(parent: AdapterView<*>?) {
                                        TODO("Not yet implemented")
                                    }

                                }
                            }
                            27->{
                                val districts = arrayOf("Select One:","Amritsar","Barnala","Bathinda","Faridkot","Fatehgarh Sahib","Fazilka","Ferozepur","Gurdaspur","Hoshiarpur","Jalandhar","Kapurthala","Ludhiana","Mansa","Moga","Muktsar","Nawanshahr (Shahid Bhagat Singh Nagar)","Pathankot","Patiala","Rupnagar","Sahibzada Ajit Singh Nagar (Mohali)","Sangrur","Tarn Taran")
                                mDialog.spDistrict.adapter = ArrayAdapter<String>(this@Dashboard,android.R.layout.simple_list_item_1,districts)
                                mDialog.spDistrict.onItemSelectedListener = object : AdapterView.OnItemClickListener, AdapterView.OnItemSelectedListener{
                                    override fun onItemClick(parent: AdapterView<*>?,view: View?,position: Int,id: Long) {
                                        TODO("Not yet implemented")
                                    }

                                    override fun onItemSelected(parent: AdapterView<*>?, view: View?,position: Int,id: Long) {
                                        districtNum = position
                                        if(position!=0)
                                        {
                                            district = districts[position]
                                        }
                                        else{
                                            district = "Select One:"
                                        }
                                    }

                                    override fun onNothingSelected(parent: AdapterView<*>?) {
                                        TODO("Not yet implemented")
                                    }

                                }
                            }
                            28->{
                                val districts = arrayOf("Select One:","Ajmer","Alwar","Banswara","Baran","Barmer","Bharatpur","Bhilwara","Bikaner","Bundi","Chittorgarh","Churu","Dausa","Dholpur","Dungarpur","Hanumangarh","Jaipur","Jaisalmer","Jalore","Jhalawar","Jhunjhunu","Jodhpur","Karauli","Kota","Nagaur","Pali","Pratapgarh","Rajsamand","Sawai Madhopur","Sikar","Sirohi","Sri Ganganagar","Tonk","Udaipur")
                                mDialog.spDistrict.adapter = ArrayAdapter<String>(this@Dashboard,android.R.layout.simple_list_item_1,districts)
                                mDialog.spDistrict.onItemSelectedListener = object : AdapterView.OnItemClickListener, AdapterView.OnItemSelectedListener{
                                    override fun onItemClick(parent: AdapterView<*>?,view: View?,position: Int,id: Long) {
                                        TODO("Not yet implemented")
                                    }

                                    override fun onItemSelected(parent: AdapterView<*>?, view: View?,position: Int,id: Long) {
                                        districtNum = position
                                        if(position!=0)
                                        {
                                            district = districts[position]
                                        }
                                        else{
                                            district = "Select One:"
                                        }
                                    }

                                    override fun onNothingSelected(parent: AdapterView<*>?) {
                                        TODO("Not yet implemented")
                                    }

                                }
                            }
                            29->{
                                val districts = arrayOf("Select One:","East Sikkim","North Sikkim","South Sikkim","West Sikkim")
                                mDialog.spDistrict.adapter = ArrayAdapter<String>(this@Dashboard,android.R.layout.simple_list_item_1,districts)
                                mDialog.spDistrict.onItemSelectedListener = object : AdapterView.OnItemClickListener, AdapterView.OnItemSelectedListener{
                                    override fun onItemClick(parent: AdapterView<*>?,view: View?,position: Int,id: Long) {
                                        TODO("Not yet implemented")
                                    }

                                    override fun onItemSelected(parent: AdapterView<*>?, view: View?,position: Int,id: Long) {
                                        districtNum = position
                                        if(position!=0)
                                        {
                                            district = districts[position]
                                        }
                                        else{
                                            district = "Select One:"
                                        }
                                    }

                                    override fun onNothingSelected(parent: AdapterView<*>?) {
                                        TODO("Not yet implemented")
                                    }

                                }
                            }
                            30->{
                                val districts = arrayOf("Select One:","Ariyalur","Chennai","Coimbatore","Cuddalore","Dharmapuri","Dindigul","Erode","Kanchipuram","Kanyakumari","Karur","Krishnagiri","Madurai","Nagapattinam","Namakkal","Nilgiris","Perambalur","Pudukkottai","Ramanathapuram","Salem","Sivaganga","Thanjavur","Theni","Thoothukudi (Tuticorin)","Tiruchirappalli","Tirunelveli","Tiruppur","Tiruvallur","Tiruvannamalai","Tiruvarur","Vellore","Viluppuram","Virudhunagar")
                                mDialog.spDistrict.adapter = ArrayAdapter<String>(this@Dashboard,android.R.layout.simple_list_item_1,districts)
                                mDialog.spDistrict.onItemSelectedListener = object : AdapterView.OnItemClickListener, AdapterView.OnItemSelectedListener{
                                    override fun onItemClick(parent: AdapterView<*>?,view: View?,position: Int,id: Long) {
                                        TODO("Not yet implemented")
                                    }

                                    override fun onItemSelected(parent: AdapterView<*>?, view: View?,position: Int,id: Long) {
                                        districtNum = position
                                        if(position!=0)
                                        {
                                            district = districts[position]
                                        }
                                        else{
                                            district = "Select One:"
                                        }
                                    }

                                    override fun onNothingSelected(parent: AdapterView<*>?) {
                                        TODO("Not yet implemented")
                                    }

                                }
                            }
                            31->{
                                val districts = arrayOf("Select One:","Adilabad","Bhadradri Kothagudem","Hyderabad","Jagtial","Jangaon","Jayashankar Bhoopalpally","Jogulamba Gadwal","Kamareddy","Karimnagar","Khammam","Komaram Bheem Asifabad","Mahabubabad","Mahabubnagar","Mancherial","Medak","Medchal","Nagarkurnool","Nalgonda","Nirmal","Nizamabad","Peddapalli","Rajanna Sircilla","Rangareddy","Sangareddy","Siddipet","Suryapet","Vikarabad","Wanaparthy","Warangal (Rural)","Warangal (Urban)","Yadadri Bhuvanagiri")
                                mDialog.spDistrict.adapter = ArrayAdapter<String>(this@Dashboard,android.R.layout.simple_list_item_1,districts)
                                mDialog.spDistrict.onItemSelectedListener = object : AdapterView.OnItemClickListener, AdapterView.OnItemSelectedListener{
                                    override fun onItemClick(parent: AdapterView<*>?,view: View?,position: Int,id: Long) {
                                        TODO("Not yet implemented")
                                    }

                                    override fun onItemSelected(parent: AdapterView<*>?, view: View?,position: Int,id: Long) {
                                        districtNum = position
                                        if(position!=0)
                                        {
                                            district = districts[position]
                                        }
                                        else{
                                            district = "Select One:"
                                        }
                                    }

                                    override fun onNothingSelected(parent: AdapterView<*>?) {
                                        TODO("Not yet implemented")
                                    }
                                }
                            }
                            32->{
                                val districts = arrayOf("Select One:","Dhalai","Gomati","Khowai","North Tripura","Sepahijala","South Tripura","Unakoti","West Tripura")
                                mDialog.spDistrict.adapter = ArrayAdapter<String>(this@Dashboard,android.R.layout.simple_list_item_1,districts)
                                mDialog.spDistrict.onItemSelectedListener = object : AdapterView.OnItemClickListener, AdapterView.OnItemSelectedListener{
                                    override fun onItemClick(parent: AdapterView<*>?,view: View?,position: Int,id: Long) {
                                        TODO("Not yet implemented")
                                    }

                                    override fun onItemSelected(parent: AdapterView<*>?, view: View?,position: Int,id: Long) {
                                        districtNum = position
                                        if(position!=0)
                                        {
                                            district = districts[position]
                                        }
                                        else{
                                            district = "Select One:"
                                        }
                                    }

                                    override fun onNothingSelected(parent: AdapterView<*>?) {
                                        TODO("Not yet implemented")
                                    }
                                }
                            }
                            33->{
                                val districts = arrayOf("Select One:","Almora","Bageshwar","Chamoli","Champawat","Dehradun","Haridwar","Nainital","Pauri Garhwal","Pithoragarh","Rudraprayag","Tehri Garhwal","Udham Singh Nagar","Uttarkashi")
                                mDialog.spDistrict.adapter = ArrayAdapter<String>(this@Dashboard,android.R.layout.simple_list_item_1,districts)
                                mDialog.spDistrict.onItemSelectedListener = object : AdapterView.OnItemClickListener, AdapterView.OnItemSelectedListener{
                                    override fun onItemClick(parent: AdapterView<*>?,view: View?,position: Int,id: Long) {
                                        TODO("Not yet implemented")
                                    }

                                    override fun onItemSelected(parent: AdapterView<*>?, view: View?,position: Int,id: Long) {
                                        districtNum = position
                                        if(position!=0)
                                        {
                                            district = districts[position]
                                        }
                                        else{
                                            district = "Select One:"
                                        }
                                    }

                                    override fun onNothingSelected(parent: AdapterView<*>?) {
                                        TODO("Not yet implemented")
                                    }
                                }
                            }
                            34->{
                                val districts = arrayOf("Select One:","Agra","Aligarh","Allahabad","Ambedkar Nagar","Amethi (Chatrapati Sahuji Mahraj Nagar)","Amroha (J.P. Nagar)","Auraiya","Azamgarh","Baghpat","Bahraich","Ballia","Balrampur","Banda","Barabanki","Bareilly","Basti","Bhadohi","Bijnor","Budaun","Bulandshahr","Chandauli","Chitrakoot","Deoria","Etah","Etawah","Faizabad","Farrukhabad","Fatehpur","Firozabad","Gautam Buddha Nagar","Ghaziabad","Ghazipur","Gonda","Gorakhpur","Hamirpur","Hapur (Panchsheel Nagar)","Hardoi","Hathras","Jalaun","Jaunpur","Jhansi","Kannauj","Kanpur Dehat","Kanpur Nagar","Kanshiram Nagar (Kasganj)","Kaushambi","Kushinagar (Padrauna)","Lakhimpur - Kheri","Lalitpur","Lucknow","Maharajganj","Mahoba","Mainpuri","Mathura","Mau","Meerut","Mirzapur","Moradabad","Muzaffarnagar","Pilibhit","Pratapgarh","RaeBareli","Rampur","Saharanpur","Sambhal (Bhim Nagar)","Sant Kabir Nagar","Shahjahanpur","Shamali (Prabuddh Nagar)","Shravasti","Siddharth Nagar","Sitapur","Sonbhadra","Sultanpur","Unnao","Varanasi")
                                mDialog.spDistrict.adapter = ArrayAdapter<String>(this@Dashboard,android.R.layout.simple_list_item_1,districts)
                                mDialog.spDistrict.onItemSelectedListener = object : AdapterView.OnItemClickListener, AdapterView.OnItemSelectedListener{
                                    override fun onItemClick(parent: AdapterView<*>?,view: View?,position: Int,id: Long) {
                                        TODO("Not yet implemented")
                                    }

                                    override fun onItemSelected(parent: AdapterView<*>?, view: View?,position: Int,id: Long) {
                                        districtNum = position
                                        if(position!=0)
                                        {
                                            district = districts[position]
                                        }
                                        else{
                                            district = "Select One:"
                                        }
                                    }

                                    override fun onNothingSelected(parent: AdapterView<*>?) {
                                        TODO("Not yet implemented")
                                    }
                                }
                            }
                            35->{
                                val districts = arrayOf("Select One:","Alipurduar","Bankura","Birbhum","Burdwan (Bardhaman)","Cooch Behar","Dakshin Dinajpur (South Dinajpur)","Darjeeling","Hooghly","Howrah","Jalpaiguri","Kalimpong","Kolkata","Malda","Murshidabad","Nadia","North 24 Parganas","Paschim Medinipur (West Medinipur)","Purba Medinipur (East Medinipur)","Purulia","South 24 Parganas","Uttar Dinajpur (North Dinajpur)")
                                mDialog.spDistrict.adapter = ArrayAdapter<String>(this@Dashboard,android.R.layout.simple_list_item_1,districts)
                                mDialog.spDistrict.onItemSelectedListener = object : AdapterView.OnItemClickListener, AdapterView.OnItemSelectedListener{
                                    override fun onItemClick(parent: AdapterView<*>?,view: View?,position: Int,id: Long) {
                                        TODO("Not yet implemented")
                                    }

                                    override fun onItemSelected(parent: AdapterView<*>?, view: View?,position: Int,id: Long) {
                                        districtNum = position
                                        if(position!=0)
                                        {
                                            district = districts[position]
                                        }
                                        else{
                                            district = "Select One:"
                                        }
                                    }

                                    override fun onNothingSelected(parent: AdapterView<*>?) {
                                        TODO("Not yet implemented")
                                    }
                                }
                            }
                        }

                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {
                        TODO("Not yet implemented")
                    }

                }

                mDialog.btnUpdateLocation.setOnClickListener {

                    val sharedPreferences = getSharedPreferences("ConsumerAdda",Context.MODE_PRIVATE)
                    val editor = sharedPreferences.edit()
                    editor.apply{
                        putString("DISTRICT", district)
                        putString("STATE", state)
                        putInt("DISTRICT_NUMBER",districtNum)
                        putInt("STATE_NUMBER",stateNum)
                    }.apply()

                    tvCardData.text = "${district}, ${state}"
                    mDialog.dismiss()
                }
            }
        }
    }

    private fun updateDashboard()
    {
        btnMore.isEnabled = true
        if(role==0)
        {
            if(CardModelList.size != 0)
            {
                clSupporter.visibility = View.INVISIBLE
                vpCardView.visibility = View.VISIBLE
                loadcards()
                complaint_btn.visibility = View.INVISIBLE
                cvCurrent.visibility = View.VISIBLE
                tvCardHeader.text = "Case Status"

                when(CardModelList[0].caseStatus)
                {
                    0->{
                        tvCardData.text = "Case Logged"
                    }
                    1->{
                        tvCardData.text = "Mediation Initiated"
                    }
                    2->{
                        tvCardData.text = "Legal Notice Sent"
                    }
                    3->{
                        tvCardData.text = "Case Filed in Court"
                    }
                }
            }
            else
            {
                clSupporter.visibility = View.VISIBLE
                tvSupportMessage1.visibility = View.VISIBLE
                tvSupportMessage2.visibility = View.VISIBLE
                tvSupportMessage1.text = "Welcome!"
                tvSupportMessage2.text = "Got any legal issue?\nSubmit your case below."
                pbSupport.visibility = View.INVISIBLE
                cvCurrent.visibility = View.INVISIBLE
                complaint_btn.isEnabled = true
                complaint_btn.visibility = View.VISIBLE
                complaint_btn.text = "Submit Complaint"
            }
        }
        else
        {
            complaint_btn.text = "Cases List"
            complaint_btn.isEnabled = true
            complaint_btn.visibility = View.VISIBLE

            if(CardModelList.size!=0)
            {
                clSupporter.visibility = View.INVISIBLE
                vpCardView.visibility = View.VISIBLE
                loadcards()
                cvCurrent.visibility = View.VISIBLE
                tvCardHeader.text = "Location Selected"
                val sharedPreferences = getSharedPreferences("ConsumerAdda",Context.MODE_PRIVATE)
                state = sharedPreferences.getString("STATE","Select One:").toString()
                district = sharedPreferences.getString("DISTRICT","Select One:").toString()
                stateNum = sharedPreferences.getInt("STATE_NUMBER",0)
                districtNum = sharedPreferences.getInt("DISTRICT_NUMBER",0)
                tvCardData.text = "${district}, ${state}"
            }
            else
            {
                clSupporter.visibility = View.VISIBLE
                pbSupport.visibility = View.INVISIBLE
                tvSupportMessage1.visibility = View.VISIBLE
                tvSupportMessage2.visibility = View.VISIBLE
                val sharedPreferences = getSharedPreferences("ConsumerAdda",Context.MODE_PRIVATE)
                val verif = sharedPreferences.getBoolean("VERIFIED",false)
                val naam = sharedPreferences.getString("NAME","").toString()
                if(verif)
                {
                    tvSupportMessage1.text = "Hello $naam !"
                    tvSupportMessage2.text = "Start your start journey with\nus by accepting case from our\ncases list."
                    tvCardHeader.text = "Location Selected"
                    val sharedPreferences = getSharedPreferences("ConsumerAdda",Context.MODE_PRIVATE)
                    state = sharedPreferences.getString("STATE","Select One:").toString()
                    district = sharedPreferences.getString("DISTRICT","Select One:").toString()
                    stateNum = sharedPreferences.getInt("STATE_NUMBER",0)
                    districtNum = sharedPreferences.getInt("DISTRICT_NUMBER",0)
                    tvCardData.text = "${district}, ${state}"
                    cvCurrent.visibility = View.VISIBLE
                }
                else
                {
                    tvSupportMessage1.text = "Hello $naam !"
                    tvSupportMessage2.text = "Kindly verify your identity to\nget started with Consumer Adda!"
                    complaint_btn.text = "Request Verification!"
                }
            }

        }
    }

    private fun loadcards()
    {

        vpCardView.adapter = DashboardCardAdapter(this,CardModelList,this)

        vpCardView.setPadding(20,10,20,10)
        isCardInfoDisplayed = true
    }

    private fun btnMoreSetOnClickListener() {
        btnMore.setOnClickListener{
            val popupMenu: PopupMenu = PopupMenu(this, btnMore)
            popupMenu.menuInflater.inflate( R.menu.toolbar_menu, popupMenu.menu )
            popupMenu.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.about -> {
                        startActivity(Intent(this, About::class.java))
                    }
                    R.id.service -> {
                        startActivity(Intent(this, Services::class.java))
                    }
                    R.id.sign_out -> doSignOut()
                    R.id.contact -> {
                        startActivity(Intent(this, ContactUs::class.java))
                    }
                }
                true
            })
            popupMenu.show()
        }
    }
    
    private fun doSignOut()
    {
        val sharedPreferences = getSharedPreferences("ConsumerAdda",Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.apply {
            putInt("ROLE",-1)
            putString("FIREBASE_ID",null)
            putBoolean("LOGGED_IN",false)
            putString("NAME",null)
            putString("DISTRICT",null)
            putString("STATE",null)
            putInt("STRING_NUMBER",-1)
            putInt("DISTRICT_NUMBER",-1)
        }.apply()
        FirebaseAuth.getInstance().signOut()
        val intent = Intent(this, LoginActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
    }

    override fun onCardClicked(position: Int) {
        if(CardModelList[position].lawyerName != "N/A")
        {
            val intent = Intent(this,ChatActivity::class.java)
            intent.putExtra("Client",CardModelList[position].applicantFirstName+" "+CardModelList[position].applicantLastName)
            startActivity(intent)
        }
    }
}

interface OnCardClicked
{
    fun onCardClicked(position: Int)
}