package com.example.consumeradda.fragments.caseListFragment

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.transition.TransitionInflater
import com.example.consumeradda.R
import com.example.consumeradda.activities.CasesListActivity
import com.example.consumeradda.activities.Dashboard
import com.example.shareapp.adapters.ApplicationListAdapter
import kotlinx.android.synthetic.main.case_type_filter.*
import kotlinx.android.synthetic.main.fragment_cases_list.*
import kotlinx.android.synthetic.main.location_alert_dialog.*

class CasesListFragment : Fragment(), OnApplicationClicked {

    val APPLICATIONTAG="APPLICATIONTAG"
    lateinit var applicationListAdapter: ApplicationListAdapter


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
        showLocationMessage()
        btnBackCaseListSetOnClickListener()
        tvFilterCaseSetOnClickListener()
        getApplications()
        loadCaseCards()
    }

    private fun btnClearFilterSetOnClickListener() {
        btnClearFilter.setOnClickListener {
            CasesListActivity.caseType = ""
            btnClearFilter.visibility = View.INVISIBLE
            CasesListActivity.applicationList = CasesListActivity.originalList
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
            mDialog.dismiss()
        }
    }

    private fun loadCaseCards() {
        applicationListAdapter = ApplicationListAdapter(CasesListActivity.applicationList,this)
        rvCaseList.adapter = applicationListAdapter
        rvCaseList.layoutManager = LinearLayoutManager(this.context)
        rvCaseList.setHasFixedSize(true)
    }

    private fun getApplications() {

    }

    private fun btnBackCaseListSetOnClickListener() {
        btnBackCaseList.setOnClickListener {
//            val intent=Intent(context, Dashboard::class.java)
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
//            startActivity(intent)
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
                CasesListActivity.caseType = "Banking Law"
                mDialog.dismiss()
                showFilteredCases()
            }
        }
    }

    private fun showFilteredCases() {

    }

    override fun onApplicationItemClicked(position: Int) {
        CasesListActivity.selectedApplicationNumber = position
        activity?.supportFragmentManager?.beginTransaction()?.replace(R.id.fl_wrapper_applications, CaseDetailsFragment())?.commit()
    }

}

interface OnApplicationClicked
{
    fun onApplicationItemClicked(position: Int)
}