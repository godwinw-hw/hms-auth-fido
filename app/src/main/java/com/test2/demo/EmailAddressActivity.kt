package com.test2.demo

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.huawei.agconnect.auth.EmailAuthProvider
import com.huawei.agconnect.auth.VerifyCodeSettings
import kotlinx.android.synthetic.main.activity_email_address.*

class EmailAddressActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_email_address)

    }

    fun submitEmailAddress(view: View) {
//        // email verification settings
        val settings = VerifyCodeSettings.newBuilder().action(VerifyCodeSettings.ACTION_REGISTER_LOGIN)
            .sendInterval(30) //interval or code timeout
            .build()
//
//        // request Auth Service to send the verification code
        val task = EmailAuthProvider.requestVerifyCode(email_address.editText?.text.toString(), settings)
        task.addOnSuccessListener {
            // verification code successfully sent
            // save email address to Shared Pref
            val sp = getSharedPreferences("Demo_APP", Context.MODE_PRIVATE)
            with(sp.edit()) {
                putString("Email_Address", email_address.editText?.text.toString())
                commit()
            }

            // start activity for SMS verification
            startActivity(Intent(this@EmailAddressActivity, VerifyCodeActivity::class.java))
            finish()

        }.addOnFailureListener {exception ->
            // exception during sending of verification code
            exception?.printStackTrace()
        }
    }
}
