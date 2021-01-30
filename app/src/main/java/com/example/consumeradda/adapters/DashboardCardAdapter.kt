package com.example.shareapp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.viewpager.widget.PagerAdapter
import com.example.consumeradda.R

import com.example.consumeradda.activities.OnCardClicked
import com.example.consumeradda.models.cardModels.DashboardCardModel
import com.example.consumeradda.models.caseModels.Case
import kotlinx.android.synthetic.main.case_card.view.*

class DashboardCardAdapter (private val context: Context, private val cardlist: MutableList<Case>, private var onCardClicked: OnCardClicked): PagerAdapter()
{
    override fun getCount(): Int {
        return cardlist.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view==`object`
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {

        val view = LayoutInflater.from(context).inflate(R.layout.case_card,container,false)

        val currentModel=cardlist[position]

        val appNumber = currentModel.caseId.toString()
        val clientName = currentModel.applicantFirstName+" "+currentModel.applicantLastName
        val lawyerName = currentModel.lawyerName
        val caseType=currentModel.caseType

        var type :String = ""

        when(caseType)
        {
            "Consumer Protection Law"->{
                type = "CPLAW"
            }
            "Banking Regulation"->{
                type = "BLAW"
            }
            "Insurance Law"->{
                type = "INSL"
            }
            "Debt Recovery"->{
                type = "DEBT"
            }
            "RERA"->{
                type = "RERA"
            }
            "Other"->{
                type = "OTHER"
            }
        }

        view.tvCardCaseIdHeader.text = "CA_${type}_${appNumber}"
        view.tvCardClientName.text = clientName
        view.tvCardLawyerName.text = lawyerName
        view.tvCardCaseType.text = caseType

        if(lawyerName != "N/A")
        {
            view.btnChat.visibility = View.VISIBLE
        }

        when(currentModel.caseStatus)
        {
            0 ->
            {
                view.pbComplaintSubmitted.progress = 100
            }
            1->
            {
                view.pbComplaintSubmitted.progress = 100
                view.pbMediation.progress = 100
            }
            2->
            {
                view.pbComplaintSubmitted.progress = 100
                view.pbMediation.progress = 100
                view.pbLegalNotice.progress = 100
            }
            3->
            {
                view.pbComplaintSubmitted.progress = 100
                view.pbMediation.progress = 100
                view.pbLegalNotice.progress = 100
                view.pbCaseFiled.progress = 100
            }

        }

        view.btnChat.setOnClickListener {
            onCardClicked.onCardClicked(position)
        }

        view.setOnClickListener{

            Toast.makeText(context,"${view.tvCardCaseIdHeader.text}", Toast.LENGTH_SHORT).show()
        }

        container.addView(view, position)

        return view
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }
}