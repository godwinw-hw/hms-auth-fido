package com.test2.demo

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.huawei.agconnect.auth.AGConnectAuth
import com.huawei.agconnect.auth.EmailAuthProvider
import com.huawei.agconnect.auth.PhoneAuthProvider
import com.huawei.agconnect.auth.VerifyCodeSettings
import kotlinx.android.synthetic.main.activity_verifycode.*
import java.lang.Exception

class VerifyCodeActivity : AppCompatActivity() {
    lateinit var emailAddress:String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verifycode)

        val sp = getSharedPreferences("Demo_APP", Context.MODE_PRIVATE)
        emailAddress = sp.getString("Email_Address", "").toString()
    }

    fun submitVerificationCode(view: View) {
        // create credential object with the phone number and verification code
        val credential = EmailAuthProvider.credentialWithVerifyCode(emailAddress, // country code
            "", // password (optional)
            verification_code.editText?.text.toString().trim()) // verification code entered

        // Sign in the user with the specified credential above
        AGConnectAuth.getInstance().signIn(credential).addOnSuccessListener {
            // user successfully signed-in using phone number
            val builder = AlertDialog.Builder(this)
            builder.setMessage("User ${it.user.email} Logged in!")
            //builder.setPositiveButton("OK", DialogInterface.OnClickListener(function = x))

            builder.setPositiveButton(android.R.string.yes) { dialog, which ->
                finish()
            }
            builder.show()

        }.addOnFailureListener {
            // something went wrong during sign in
            it.printStackTrace()
        }

    }

    fun resendCode(view: View) {
        val settings = VerifyCodeSettings.newBuilder().action(VerifyCodeSettings.ACTION_REGISTER_LOGIN)
            .sendInterval(30) //interval or code timeout
            .build()
//
//        // request Auth Service to send the verification code
        val task = EmailAuthProvider.requestVerifyCode(emailAddress, settings)
        task.addOnSuccessListener {
            // verification code successfully sent
            Toast.makeText(this@VerifyCodeActivity, "Verification code sent!", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener {exception ->
            // exception during sending of verification code
            exception?.printStackTrace()
        }
    }
}
