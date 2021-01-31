package com.example.consumeradda.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.consumeradda.R
import com.example.consumeradda.fragments.caseListFragment.CasesListFragment
import com.example.consumeradda.models.caseModels.Case
import com.example.shareapp.adapters.ApplicationListAdapter

class CasesListActivity : AppCompatActivity() {

    val APPLICATIONTAG="APPLICATIONTAG"
    lateinit var applicationListAdapter: ApplicationListAdapter

    companion object {
        var applicationList= mutableListOf<Case>()
        var originalList = mutableListOf<Case>()
        var selectedApplicationNumber=-1
        var caseType=""
        var isCaseTypeFiltered = false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cases_list)


        supportFragmentManager.beginTransaction().replace(R.id.fl_wrapper_applications, CasesListFragment()).commit()
    }
}