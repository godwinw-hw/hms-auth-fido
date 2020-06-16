package com.test2.demo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CancellationSignal
import android.util.Log
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.huawei.agconnect.auth.AGConnectAuth
import com.huawei.hms.support.api.fido.bioauthn.BioAuthnCallback
import com.huawei.hms.support.api.fido.bioauthn.BioAuthnPrompt
import com.huawei.hms.support.api.fido.bioauthn.BioAuthnResult
import com.huawei.hms.support.api.fido.bioauthn.FaceManager
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    val bioAuthCallback = object : BioAuthnCallback() {
        override fun onAuthError(p0: Int, p1: CharSequence) {
            super.onAuthError(p0, p1)
            Log.e("Error", p1.toString())
        }

        override fun onAuthSucceeded(p0: BioAuthnResult) {
            super.onAuthSucceeded(p0)
            val builder = AlertDialog.Builder(this@MainActivity)
            builder.setMessage("User Biometric authenticated!")
            //builder.setPositiveButton("OK", DialogInterface.OnClickListener(function = x))

            builder.setPositiveButton(android.R.string.yes) { dialog, which ->
            }
            builder.show()
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // get currently signed in user
        val user = AGConnectAuth.getInstance().currentUser

        user?.let {
            // User already signed-in
            sign_in.text = "Hello World, ${it.email}!"
            auth_button.visibility = View.VISIBLE
        } ?: run {
            // User is not signed-in, proceed to verify Email Number
            startActivity(Intent(this@MainActivity, PhoneNumberActivity::class.java))
            auth_button.visibility = View.GONE
        }

    }

    override fun onResume() {
        super.onResume()
        val user = AGConnectAuth.getInstance().currentUser

        user?.let {
            // User already signed-in
            sign_in.text = "Hello World, ${it.email}!"
            auth_button.visibility = View.VISIBLE
        } ?: run {
            auth_button.visibility = View.GONE
        }
    }

    fun biometricAuth(view: View) {
        val cancellationSignal = CancellationSignal()
        val faceManager = FaceManager(this)

        val bioAuthnPrompt = BioAuthnPrompt(this, ContextCompat.getMainExecutor(this), bioAuthCallback)

        faceManager.canAuth().let {
            if (it != 0) {
                // face auth is not supported, try fingerprint instead
                val builder = BioAuthnPrompt.PromptInfo.Builder().setTitle("FIDO Auth Demo")
                        .setSubtitle("Fingerprint Auth")
                        .setDescription("For Demo only")
                        .setDeviceCredentialAllowed(true)

                bioAuthnPrompt.auth(builder.build())
            } else {
                // face auth is supported, start the auth
                faceManager.auth(null, cancellationSignal, 0, bioAuthCallback, null)
            }
        }

    }
}
