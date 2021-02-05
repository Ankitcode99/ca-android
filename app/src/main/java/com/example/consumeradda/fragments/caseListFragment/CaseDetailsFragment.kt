package com.example.consumeradda.fragments.caseListFragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.transition.TransitionInflater
import com.example.consumeradda.R
import com.example.consumeradda.activities.CasesListActivity
import com.example.consumeradda.activities.Dashboard
import com.example.consumeradda.models.caseModels.AcceptCase
import com.example.consumeradda.models.caseModels.AcceptCaseResponse
import com.example.consumeradda.service.CaseService
import com.example.consumeradda.service.ServiceBuilder
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_complaint_form.*
import kotlinx.android.synthetic.main.fragment_case_details.*
import kotlinx.android.synthetic.main.fragment_cases_list.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class CaseDetailsFragment : Fragment() {

    private lateinit var mAuth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val inflater = TransitionInflater.from(requireContext())
        enterTransition = inflater.inflateTransition(R.transition.explode)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_case_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mAuth = FirebaseAuth.getInstance()

        btnBackCaseDetailSetOnClickListener()
        btnAcceptCaseSetOnClickListener()
        displayData()
    }

    private fun btnBackCaseDetailSetOnClickListener() {
        btnBackCaseDetail.setOnClickListener {
            activity?.supportFragmentManager?.beginTransaction()
                    ?.replace(R.id.fl_wrapper_applications, CasesListFragment())?.commit()
        }
    }

    private fun displayData() {
        tvCaseDetailRecipient.text = CasesListActivity.applicationList[CasesListActivity.selectedApplicationNumber].applicantFirstName+" "+CasesListActivity.applicationList[CasesListActivity.selectedApplicationNumber].applicantLastName
        tvCaseDetailLocation.text = CasesListActivity.applicationList[CasesListActivity.selectedApplicationNumber].applicantDistrict+", "+CasesListActivity.applicationList[CasesListActivity.selectedApplicationNumber].applicantState
        tvCaseDetailCaseAgainst.text = CasesListActivity.applicationList[CasesListActivity.selectedApplicationNumber].caseAgainst
        tvCaseDetailCaseType.text = CasesListActivity.applicationList[CasesListActivity.selectedApplicationNumber].caseType
        tvCaseDetailMoneyInvolved.text = CasesListActivity.applicationList[CasesListActivity.selectedApplicationNumber].caseMoney
        tvCaseDetailPurpose.text = CasesListActivity.applicationList[CasesListActivity.selectedApplicationNumber].caseDescription
    }

    private fun btnAcceptCaseSetOnClickListener() {
        btnAcceptCase.setOnClickListener {
            pbAcceptCase.visibility = View.VISIBLE
            btnAcceptCase.text = ""
            btnAcceptCase.isClickable = false
            val sharedPreferences = activity?.getSharedPreferences("ConsumerAdda",Context.MODE_PRIVATE)
            val token = sharedPreferences?.getString("ID_TOKEN","localToken").toString()
            val lawyername = sharedPreferences?.getString("NAME","").toString()
            val fireId = sharedPreferences?.getString("FIREBASE_ID","").toString()
            val curruser= mAuth.currentUser
            btnBackCaseDetail.isEnabled = false
            val lawyeremail=curruser?.email.toString()

            if(fireId.equals(""))
            {
                pbAcceptCase.visibility = View.INVISIBLE
                btnBackCaseDetail.isEnabled = true
                btnAcceptCase.text = "ACCEPT CASE"
                Toast.makeText(context,"Your session has expired\nplease login again!",Toast.LENGTH_SHORT).show()
            }
            else
            {
                val caseid = CasesListActivity.applicationList[CasesListActivity.selectedApplicationNumber].caseId
                val obj = AcceptCase(lawyerFirebaseId = fireId, lawyerName = lawyername, lawyerEmail = lawyeremail)
                val caseService = ServiceBuilder.buildService(CaseService::class.java)
                val requestCall = caseService.acceptCase(caseid,token,obj)
                requestCall.enqueue(object: Callback<AcceptCaseResponse>{
                    override fun onResponse(call: Call<AcceptCaseResponse>, response: Response<AcceptCaseResponse>) {
                        if(response.isSuccessful)
                        {
                            val mess = response.body()!!
                            pbAcceptCase.visibility = View.INVISIBLE
                            btnAcceptCase.text = "Case Accepted!"
                            btnAcceptCase.isClickable = false
                            Toast.makeText(context,"${mess.msg}",Toast.LENGTH_SHORT).show()
                            CasesListActivity.applicationList.clear()
                            CasesListActivity.originalList.clear()
                            Handler().postDelayed({
                                val intent = Intent(context,Dashboard::class.java)
                                startActivity(intent)
                                activity?.finish()
                            },2000)
                        }
                        else
                        {
                            pbAcceptCase.visibility = View.INVISIBLE
                            btnAcceptCase.text = "ACCEPT CASE"
                            btnAcceptCase.isClickable = true
                            Toast.makeText(context,"Some error occurred!", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<AcceptCaseResponse>, t: Throwable) {
                        pbAcceptCase.visibility = View.INVISIBLE
                        btnAcceptCase.text = "ACCEPT CASE"
                        btnAcceptCase.isClickable = true
                        Toast.makeText(context,"No Internet / Server Error",Toast.LENGTH_SHORT).show()
                    }

                })
            }
        }
    }
}