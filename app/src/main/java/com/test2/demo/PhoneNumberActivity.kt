package com.test2.demo

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.View
import com.huawei.agconnect.auth.PhoneAuthProvider
import com.huawei.agconnect.auth.VerifyCodeSettings
import kotlinx.android.synthetic.main.activity_phone_number.*
import java.lang.Exception
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.util.*

class PhoneNumberActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_phone_number)

        Log.d("GET APP HASH", getAppHash())
    }

    fun submitPhoneNumber(view: View) {

        val sp = getSharedPreferences("Demo_APP", Context.MODE_PRIVATE)
        with(sp.edit()) {
            putString("CC", country_code.editText?.text.toString())
            putString("PN", phone_number.editText?.text.toString())
            commit()
        }

        // start activity for SMS verification
        startActivity(Intent(this@PhoneNumberActivity, SMSVerifyActivity::class.java))
        finish()
    }


    fun getAppHash(): String {
        val packageName = applicationContext.packageName
        val msgDigest = MessageDigest.getInstance("SHA-256")

        val packageManager = applicationContext.packageManager
        val signatureArray =
            packageManager.getPackageInfo(packageName, PackageManager.GET_SIGNATURES).signatures

        val appInfo = packageName + " " + signatureArray[0].toCharsString()
        msgDigest.update(appInfo.toByteArray(StandardCharsets.UTF_8))
        var hashSignature = msgDigest.digest()
        hashSignature = Arrays.copyOfRange(hashSignature, 0, 9)

        var base64Hash = Base64.encodeToString(hashSignature, Base64.NO_PADDING or Base64.NO_WRAP)
        base64Hash = base64Hash.substring(0, 11)

        return base64Hash
    }
}
