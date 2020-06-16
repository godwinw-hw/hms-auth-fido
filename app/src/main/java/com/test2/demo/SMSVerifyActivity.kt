package com.test2.demo

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.huawei.agconnect.auth.AGConnectAuth
import com.huawei.agconnect.auth.PhoneAuthProvider
import com.huawei.agconnect.auth.VerifyCodeSettings
import com.huawei.hms.common.api.CommonStatusCodes
import com.huawei.hms.support.api.client.Status
import com.huawei.hms.support.sms.ReadSmsManager
import com.huawei.hms.support.sms.common.ReadSmsConstant
import kotlinx.android.synthetic.main.activity_phone_number.*
import kotlinx.android.synthetic.main.activity_smsverify.*
import java.lang.Exception

class SMSVerifyActivity : AppCompatActivity() {
    lateinit var countryCode:String
    lateinit var phoneNumber:String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_smsverify)

        val sp = getSharedPreferences("Demo_APP", Context.MODE_PRIVATE)
        countryCode = sp.getString("CC", "").toString()
        phoneNumber = sp.getString("PN", "").toString()

        // start the service to listen for sms message
        ReadSmsManager.start(this).addOnCompleteListener {
            if (it.isSuccessful) {
                Log.d ("GET", "read sms success")

                // create filter and register the broadcast receiver
                val filter = IntentFilter().apply {
                    addAction(ReadSmsConstant.READ_SMS_BROADCAST_ACTION)
                }
                registerReceiver(SMSBroadcastReceiver(), filter)
            }
        }

    }

    // broadcast receiver that will receive SMS Broadcast from HMS Core
    inner class SMSBroadcastReceiver: BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val bundle = intent?.extras
            bundle?.let {
                val status = it.getParcelable<Status>(ReadSmsConstant.EXTRA_STATUS)

                // check status
                status?.let { stats ->
                    if (stats.statusCode == CommonStatusCodes.TIMEOUT) {
                        // timeout
                    } else if (stats.statusCode == CommonStatusCodes.SUCCESS) {
                        // success, get the sms from the broadcast
                        val verificationCode = it.getString(ReadSmsConstant.EXTRA_SMS_MESSAGE)
                        verificationCode?.let { code ->
                            // read the code
                            readSmsCode(code)
                        }
                    }

                }

            }

        }

    }

    fun readSmsCode(code: String ) {
        // substring to get just the code
        sms_code.editText?.setText(code.substring(39, 46))
    }

    fun submitSMSCode(view: View) {
        // create credential object with the phone number and verification code
        val credential = PhoneAuthProvider.credentialWithVerifyCode(countryCode, // country code
            phoneNumber, // phone number
            "", // password (optional)
            sms_code.editText?.text.toString().trim()) // sms code entered

        // Sign in the user with the specified credential above
        AGConnectAuth.getInstance().signIn(credential).addOnSuccessListener {
            // user successfully signed-in using phone number
            Log.d("User Logged in", it.user.phone)
        }.addOnFailureListener {
            // something went wrong during sign in
            it.printStackTrace()
        }

    }

    fun resendSMSCode(view: View) {
        val settings = VerifyCodeSettings.newBuilder().action(VerifyCodeSettings.ACTION_REGISTER_LOGIN)
            .sendInterval(30) //interval or code timeout
            .build()
        PhoneAuthProvider.verifyPhoneCode(countryCode, phoneNumber, settings, object: VerifyCodeSettings.OnVerifyCodeCallBack {
            override fun onVerifyFailure(p0: Exception?) {
                p0?.printStackTrace()
            }

            override fun onVerifySuccess(p0: String?, p1: String?) {
                Toast.makeText(this@SMSVerifyActivity, "SMS Code resend", Toast.LENGTH_SHORT).show()
            }

        })
    }
}
