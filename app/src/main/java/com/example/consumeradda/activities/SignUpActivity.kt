@file:Suppress("DEPRECATION")

package com.example.consumeradda.activities

import android.app.Dialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import com.example.consumeradda.R
import com.example.consumeradda.models.authModels.Register
import com.example.consumeradda.models.authModels.RegisterDefaultResponse
import com.example.consumeradda.service.AuthService
import com.example.consumeradda.service.ServiceBuilder
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_sign_up.*
import kotlinx.android.synthetic.main.lawyer_phone_dialog.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignUpActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private var role = -1
    private var lawyer:Boolean = false
    private var PhoneNumber: String = ""
    private var SIGNUPFRAGTAG="SIGNUP-TAG"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        auth = FirebaseAuth.getInstance()

        rbClientSetOnClickListener()
        rbLawyerSetOnClickListener()
        btnSignupSetOnClickListener()
        Login_pgSetOnClickListener()
    }

    private fun Login_pgSetOnClickListener() {
        Login_pg.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }

    private fun btnSignupSetOnClickListener() {
        btnSignup.setOnClickListener {
            if(validData())
            {
                if(role == 1)
                {
                    getPhoneNumber()
                }
                else
                {
                    doSignUp()
                }
            }
        }
    }

    private fun validData(): Boolean {

        val userName = etSignUpName.text.toString()
        val userEmail = etSignUpEmail.text.toString()
        val userPassword = etSignUpPassword.text.toString()

        if(userName.isNullOrBlank() || userName.isNullOrEmpty())
        {
            etSignUpName.error="Field should not be empty"
            etSignUpName.requestFocus()
            return false
        }

        if(!isEmailValid(userEmail) || userEmail.isNullOrEmpty() || userEmail.isNullOrBlank())
        {
            etSignUpEmail.error="Please enter a valid email"
            etSignUpEmail.requestFocus()
            return false
        }

        if(userPassword.length < 6 || userPassword.isNullOrBlank() || userPassword.isNullOrEmpty())
        {
            etSignUpPassword.error="Password must contain at-least 6 letters"
            etSignUpPassword.requestFocus()
            return false
        }

        if( role==-1 )
        {
            Toast.makeText(this,"Select some role to Sign Up",Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }

    private fun doSignUp() {
        val userEmail = etSignUpEmail.text.toString().trim()
        val userPassword = etSignUpPassword.text.toString().trim()

        setState(false)

        pbSignUp.visibility = View.VISIBLE
        auth.createUserWithEmailAndPassword(userEmail, userPassword)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful)
                {
                    val user = auth.currentUser
                    user?.sendEmailVerification()
                        ?.addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val fid = user.uid
                                saveToDB(fid)
                            }
                            else
                            {
                                setState(b = true)
                                Toast.makeText(baseContext, "Sign-Up failed.Try again after sometime",
                                    Toast.LENGTH_SHORT).show()
                            }
                        }
                }
                else
                {
                    setState(b = true)
                    pbSignUp.visibility = View.INVISIBLE
                    Toast.makeText(baseContext, "This email is already in use",
                        Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun setState(b: Boolean) {
        etSignUpName.isEnabled = b
        etSignUpEmail.isEnabled = b
        etSignUpPassword.isEnabled = b
        rbClient.isEnabled = b
        rbLawyer.isEnabled = b
        btnSignup.isEnabled = b
        if(b)
        {
            btnSignup.text = "SIGNUP"
        }
        else{
            btnSignup.text=""
        }
    }

    private fun saveToDB(fid: String) {
        val userName = etSignUpName.text.toString().trim()
        val userEmail = etSignUpEmail.text.toString().trim()

        val verified = !lawyer

        val newUser = Register(userName,userEmail,fid,PhoneNumber,lawyer,verified)

        val authService = ServiceBuilder.buildService(AuthService::class.java)

        val requestCall = authService.registerUser(newUser)

        requestCall.enqueue(object : Callback<RegisterDefaultResponse>{
            override fun onResponse(
                call: Call<RegisterDefaultResponse>,
                response: Response<RegisterDefaultResponse>
            ) {
                if(response.isSuccessful)
                {

                    val sharedPreferences = getSharedPreferences("ConsumerAdda",Context.MODE_PRIVATE)
                    val editor = sharedPreferences.edit()
                    editor.apply {
                        putBoolean("VERIFIED",verified)
                    }

                    val msg = response.body()!!.msg
                    Toast.makeText(this@SignUpActivity,"${msg}",Toast.LENGTH_SHORT).show()
                    Handler().postDelayed({
                        val intent = Intent(this@SignUpActivity, LoginActivity::class.java)
                        startActivity(intent)
                    },2000)
                }
                else
                {
                    Toast.makeText(this@SignUpActivity,"${response.body()!!.msg}",Toast.LENGTH_SHORT).show()
                }
                pbSignUp.visibility = View.INVISIBLE
            }

            override fun onFailure(call: Call<RegisterDefaultResponse>, t: Throwable) {
                pbSignUp.visibility = View.INVISIBLE
                Toast.makeText(this@SignUpActivity,"No Internet / Server Down",Toast.LENGTH_SHORT).show()
            }

        })

    }


    private fun getPhoneNumber() {
        val mDialog = Dialog(this)
        mDialog.setContentView(R.layout.lawyer_phone_dialog)
        val window = mDialog.window
        window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        mDialog.setCanceledOnTouchOutside(false)
        mDialog.setCancelable(false)
        mDialog.show()
        mDialog.btnPhoneSubmit.setOnClickListener {
            if(mDialog.etLawyerPhoneNumber.text.toString().length == 10) {
                PhoneNumber = mDialog.etLawyerPhoneNumber.text.toString()
                mDialog.dismiss()
                doSignUp()
            }
            else
                Toast.makeText(this,"Enter a valid phone number to register as a new Lawyer",Toast.LENGTH_SHORT).show()
        }
    }

    private fun rbLawyerSetOnClickListener() {
        rbLawyer.setOnClickListener {
            role = 1
            lawyer = true
            rbLawyer.isChecked = true
            rbClient.isChecked = false
        }
    }

    private fun rbClientSetOnClickListener() {
        rbClient.setOnClickListener {
            role = 0
            lawyer = false
            rbClient.isChecked = true
            rbLawyer.isChecked = false
        }
    }

    private fun isEmailValid(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}