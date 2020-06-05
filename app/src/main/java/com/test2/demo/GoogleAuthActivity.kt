package com.test2.demo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.huawei.agconnect.AGConnectInstance
import com.huawei.agconnect.auth.AGConnectAuth
import com.huawei.agconnect.auth.GoogleAuthProvider
import kotlinx.android.synthetic.main.activity_google_auth.*

class GoogleAuthActivity : AppCompatActivity() {
    val user = AGConnectAuth.getInstance().currentUser
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_google_auth)

        user?.let {
            g_sign_in.visibility = View.GONE
            user_status.setText("User signed in: ${it.email}")
        } ?: run {

        }
    }

    fun googleSignIn(view: View) {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken("1015242068454-mh7lrjtgene2tph6spv18jpf4unlit0b.apps.googleusercontent.com")
            .requestProfile()
            .build()
    }
}
