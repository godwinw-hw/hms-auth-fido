package com.test2.demo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.huawei.agconnect.auth.AGConnectAuth
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // get currently signed in user
        val user = AGConnectAuth.getInstance().currentUser

        user?.let {
            // User already signed-in
            sign_in.text = "Hello World, ${it.email}!"
        } ?: run {
            // User is not signed-in, proceed to verify Phone Number
            startActivity(Intent(this@MainActivity, EmailAddressActivity::class.java))
        }

    }

    override fun onResume() {
        super.onResume()
        val user = AGConnectAuth.getInstance().currentUser

        user?.let {
            // User already signed-in
            sign_in.text = "Hello World, ${it.email}!"
        }
    }
}
