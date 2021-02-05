package com.example.consumeradda.fragments.caseListFragment

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.transition.TransitionInflater
import com.example.consumeradda.R
import com.example.consumeradda.activities.CasesListActivity
import com.example.consumeradda.activities.Dashboard
import com.example.consumeradda.models.caseModels.Case
import com.example.consumeradda.service.CaseService
import com.example.consumeradda.service.ServiceBuilder
import com.example.shareapp.adapters.ApplicationListAdapter
import kotlinx.android.synthetic.main.case_type_filter.*
import kotlinx.android.synthetic.main.fragment_cases_list.*
import kotlinx.android.synthetic.main.location_alert_dialog.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.sql.ResultSetMetaData

class CasesListFragment : Fragment(), OnApplicationClicked {

    val APPLICATIONTAG="APPLICATIONTAG"
    lateinit var applicationListAdapter: ApplicationListAdapter
    private lateinit var casesList : MutableList<Case>

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
        return inflater.inflate(R.layout.fragment_cases_list, container, false)
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btnClearFilterSetOnClickListener()
        btnBackCaseListSetOnClickListener()
        tvFilterCaseSetOnClickListener()
        showLocationMessage()
    }



    private fun btnClearFilterSetOnClickListener() {
        btnClearFilter.setOnClickListener {
            CasesListActivity.caseType = ""
            CasesListActivity.isCaseTypeFiltered = false
            btnClearFilter.visibility = View.INVISIBLE
            CasesListActivity.applicationList = CasesListActivity.originalList
            applicationListAdapter.updateList(CasesListActivity.applicationList)
            tvFilterCase.visibility = View.VISIBLE
            loadCaseCards()
        }
    }

    private fun showLocationMessage() {
        val mDialog = Dialog(this.context!!)
        mDialog.setContentView(R.layout.location_alert_dialog)
        val window = mDialog.window
        window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        mDialog.setCanceledOnTouchOutside(false)
        mDialog.setCancelable(false)
        mDialog.show()
        mDialog.btnLocationAlert.setOnClickListener {
            pbCaseList.visibility = View.VISIBLE
            tvFilterCase.isEnabled = false
            mDialog.dismiss()
            getApplications()
        }
    }

    private fun loadCaseCards() {
        applicationListAdapter = ApplicationListAdapter(CasesListActivity.applicationList,this)
        rvCaseList.adapter = applicationListAdapter
        rvCaseList.layoutManager = LinearLayoutManager(this.context)
        rvCaseList.setHasFixedSize(true)
    }

    private fun getApplications() {


        val sharedPreferences = activity?.getSharedPreferences("ConsumerAdda",Context.MODE_PRIVATE)

        var stateNum = -1
        var districtNum = -1

        if (sharedPreferences != null) {
            stateNum = sharedPreferences.getInt("STATE_NUMBER",0)
            districtNum = sharedPreferences.getInt("DISTRICT_NUMBER",0)
        }
        if(districtNum==0)
        {
            getStatesApplications(stateNum)
        }
        else
        {
            getAllApplications(stateNum,districtNum)
        }

    }

    private fun getAllApplications(stateNum: Int, districtNum: Int) {

        val sharedPreferences = activity?.getSharedPreferences("ConsumerAdda",Context.MODE_PRIVATE)
        val token = sharedPreferences?.getString("ID_TOKEN","localToken").toString()

        val caseService = ServiceBuilder.buildService(CaseService::class.java)
        val requestCall = caseService.seeStateDistrictCases(stateNum,districtNum,token)

        requestCall.enqueue(object : Callback<MutableList<Case>>{
            override fun onResponse(call: Call<MutableList<Case>>, response: Response<MutableList<Case>>) {
                if(response.isSuccessful)
                {
                    casesList = response.body()!!
                    pbCaseList.visibility = View.INVISIBLE
                    tvFilterCase.isEnabled = true
                    Log.d("CasesList","${casesList.size}")
                    if(casesList.size == 0)
                    {
                        tvFilterCase.visibility = View.INVISIBLE
                        Toast.makeText(context,"No open cases in the selected location", Toast.LENGTH_SHORT).show()
                    }
                    else
                    {
//                        Log.d("CasesList","${casesList.toString()}")
//                        Toast.makeText(context,"${response.body().toString()}", Toast.LENGTH_SHORT).show()
                        CasesListActivity.originalList = casesList
                        CasesListActivity.applicationList = casesList

                        loadCaseCards()
                    }
                }
                else
                {
                    pbCaseList.visibility = View.INVISIBLE
//                    Toast.makeText(context,"Internal server error  ${response.errorBody().toString()}", Toast.LENGTH_LONG).show()
                    val jObjError = JSONObject(response.errorBody()!!.string())
                    Toast.makeText(context,"${jObjError.getString("msg")}",Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<MutableList<Case>>, t: Throwable) {
                pbCaseList.visibility = View.INVISIBLE
                Toast.makeText(context,"No Internet / Server Down", Toast.LENGTH_SHORT).show()
            }

        })

    }

    private fun getStatesApplications(stateNum: Int) {

        val sharedPreferences = activity?.getSharedPreferences("ConsumerAdda",Context.MODE_PRIVATE)
        val token = sharedPreferences?.getString("ID_TOKEN","localToken").toString()

        val caseService = ServiceBuilder.buildService(CaseService::class.java)
        val requestCall = caseService.seeStateCases(stateNum,token)
        requestCall.enqueue(object : Callback<MutableList<Case>>{
            override fun onResponse(call: Call<MutableList<Case>>, response: Response<MutableList<Case>>) {
                if(response.isSuccessful)
                {
                    casesList = response.body()!!
                    pbCaseList.visibility = View.INVISIBLE
                    tvFilterCase.isEnabled = true
                    Log.d("CasesList","${casesList.size}")
                    if(casesList.size == 0)
                    {
                        tvFilterCase.visibility = View.INVISIBLE
                        Toast.makeText(context,"No open cases in the selected location", Toast.LENGTH_SHORT).show()
                    }
                    else
                    {
//                        Log.d("CasesList","${casesList.toString()}")
//                        Toast.makeText(context,"${response.body().toString()}", Toast.LENGTH_SHORT).show()
                        CasesListActivity.originalList = casesList
                        CasesListActivity.applicationList = casesList

                        loadCaseCards()
                    }
                }
                else
                {
                    pbCaseList.visibility = View.INVISIBLE
//                    Toast.makeText(context,"Internal server error  ${response.errorBody().toString()}", Toast.LENGTH_LONG).show()
                    val jObjError = JSONObject(response.errorBody()!!.string())
                    Toast.makeText(context,"${jObjError.getString("msg")}",Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<MutableList<Case>>, t: Throwable) {
                pbCaseList.visibility = View.INVISIBLE
                Toast.makeText(context,"No Internet / Server Down", Toast.LENGTH_SHORT).show()
            }

        })
    }

    private fun btnBackCaseListSetOnClickListener() {
        btnBackCaseList.setOnClickListener {
            CasesListActivity.originalList.clear()
            CasesListActivity.isCaseTypeFiltered = false
            CasesListActivity.caseType = ""
            CasesListActivity.applicationList.clear()
            activity?.finish()
        }
    }

    private fun tvFilterCaseSetOnClickListener() {
        tvFilterCase.setOnClickListener {
            val mDialog = Dialog(this.context!!)
            mDialog.setContentView(R.layout.case_type_filter)
            val window = mDialog.window
            window?.setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT
            )
            mDialog.setCanceledOnTouchOutside(true)
            mDialog.setCancelable(true)
            mDialog.show()

            mDialog.rbCPLaw.setOnClickListener {
                CasesListActivity.caseType = "Consumer Protection Law"
                mDialog.dismiss()
                showFilteredCases()
            }
            mDialog.rbOther.setOnClickListener {
                CasesListActivity.caseType = "Other"
                mDialog.dismiss()
                showFilteredCases()
            }
            mDialog.rbRERA.setOnClickListener {
                CasesListActivity.caseType = "RERA"
                mDialog.dismiss()
                showFilteredCases()
            }
            mDialog.rbILaw.setOnClickListener {
                CasesListActivity.caseType = "Insurance Law"
                mDialog.dismiss()
                showFilteredCases()
            }
            mDialog.rbDRLaw.setOnClickListener {
                CasesListActivity.caseType = "Debt Recovery"
                mDialog.dismiss()
                showFilteredCases()
            }
            mDialog.rbBLaw.setOnClickListener {
                CasesListActivity.caseType = "Banking Regulation"
                mDialog.dismiss()
                showFilteredCases()
            }
        }
    }

    private fun showFilteredCases() {
        Log.d("Filter","${CasesListActivity.caseType}  ${CasesListActivity.originalList}")
        val temp = mutableListOf<Case>()
        CasesListActivity.isCaseTypeFiltered = true
        for(curr in CasesListActivity.originalList)
        {
            Log.d("CTFilter","${curr.caseType}")
            if(curr.caseType.equals(CasesListActivity.caseType))
            {
                temp.add(curr)
            }
        }

        Log.d("NewCases","$temp")
        CasesListActivity.applicationList = temp
        btnClearFilter.visibility = View.VISIBLE
        tvFilterCase.visibility = View.GONE
        if(CasesListActivity.applicationList.size == 0)
        {
            Toast.makeText(context,"No case of ${CasesListActivity.caseType} available.",Toast.LENGTH_SHORT).show()
        }
        else
        {
            applicationListAdapter.updateList(CasesListActivity.applicationList)
            loadCaseCards()
        }
    }

    override fun onApplicationItemClicked(position: Int) {
//        Toast.makeText(context,"${position}",Toast.LENGTH_SHORT).show()
        CasesListActivity.selectedApplicationNumber = position
        activity?.supportFragmentManager?.beginTransaction()?.replace(R.id.fl_wrapper_applications, CaseDetailsFragment())?.commit()
    }

}

interface OnApplicationClicked
{
    fun onApplicationItemClicked(position: Int)
}