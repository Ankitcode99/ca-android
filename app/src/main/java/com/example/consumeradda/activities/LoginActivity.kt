package com.example.consumeradda.activities

import android.app.Dialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import com.example.consumeradda.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.forgot_password_dialog.*

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var idToken : String
    val LOGINFRAGTAG = "Login-Tag"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()

        signup_pageSetOnClickListener()
        btnLoginSetOnClickListener()
        btnForgotPasswordSetOnClickListener()
    }

    public override fun onStart() {
        super.onStart()

        val currentUser = auth.currentUser
        updateUI(currentUser)
    }

    private fun btnForgotPasswordSetOnClickListener() {
        btnForgotPassword.setOnClickListener {
            val mDialog = Dialog(this)
            mDialog.setContentView(R.layout.forgot_password_dialog)
            val window = mDialog.window
            window?.setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT
            )
            mDialog.setCanceledOnTouchOutside(true)
            mDialog.setCancelable(true)
            mDialog.show()
            mDialog.btnFPSubmit.setOnClickListener {
                forgotpwd(mDialog.etUserEmailFP.text.toString())
            }
            mDialog.btnFPCancel.setOnClickListener{
                mDialog.dismiss()
            }
        }
    }

    private fun forgotpwd(email: String)
    {
        if(!isEmailValid(email) || email.isEmpty())
        {
            return
        }

        val progress=ProgressDialog(this, R.style.AlertDialogTheme)
        progress.setMessage("Sending reset link...")
        progress.setCancelable(false)
        progress.show()
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    progress.dismiss()
                    Toast.makeText(this,"Password reset e-mail sent.",Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun btnLoginSetOnClickListener()
    {
        btnLogin.setOnClickListener {
            if(validData())
            {
                doLogin()
            }
        }
    }

    private fun doLogin() {
        val email=etLoginEmail.text.toString().trim()
        val pwd = etLoginPassword.text.toString().trim()

        startLogin()

        auth.signInWithEmailAndPassword(email, pwd)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    updateUI(user)
                } else {
                    pbLogin.visibility = View.INVISIBLE
                    etLoginEmail.isActivated = true
                    etLoginEmail.isClickable = true
                    etLoginPassword.isClickable = true
                    etLoginPassword.isActivated = true
                    btnLogin.isEnabled = true
                    btnLogin.text = "LOGIN"
                    btnForgotPassword.isEnabled = true
                    toastMaker("Failed To Login - " + task.exception?.message)
                }

            }
    }

    private fun startLogin() {
        pbLogin.visibility = View.VISIBLE
        etLoginEmail.isActivated = false
        etLoginEmail.isClickable = false
        etLoginPassword.isClickable = false
        etLoginPassword.isActivated = false
        btnLogin.isEnabled = false
        btnLogin.text = ""
        btnForgotPassword.isEnabled = false
    }

    private fun toastMaker(message: String?) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun updateUI(currentUser: FirebaseUser?) {
        if(currentUser!=null) {
            if (currentUser.isEmailVerified) {

                val sharedPreferences = getSharedPreferences("ConsumerAdda",Context.MODE_PRIVATE)
                val editor = sharedPreferences.edit()
                val fid = currentUser.uid
                editor.apply {
                    putString("FIREBASE_ID",fid)
                    putInt("ROLE",-1)
                    putBoolean("LOGGED_IN",true)
                }.apply()
                val intent = Intent(this, Dashboard::class.java)
                intent.putExtra("FirebaseId",fid)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(intent)
            } else {
                pbLogin.visibility = View.INVISIBLE
                btnLogin.text = "LOGIN"
                btnLogin.isEnabled = true
                btnForgotPassword.isEnabled = true
                Toast.makeText(this, "Verify your e-mail to get started", Toast.LENGTH_SHORT).show()
            }
        }
    }




    private fun validData(): Boolean {
        val email=etLoginEmail.text.toString().trim()
        val pwd = etLoginPassword.text.toString().trim()

        if(!isEmailValid(email) ||email.isEmpty())
        {
            etLoginEmail.error="Please enter a valid email"
            etLoginEmail.requestFocus()
            return false
        }


        if(pwd.isEmpty() || pwd.length < 6)
        {
            etLoginPassword.error="Password must contain at-least 6 letters"
            etLoginPassword.requestFocus()
            return false
        }
        return true
    }

    private fun isEmailValid(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun signup_pageSetOnClickListener()
    {
        signup_page.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }
    }
}