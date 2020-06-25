package com.test2.demo

import android.emcom.IOneHopAppCallback
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.huawei.onehop.appsdk.HwOneHopSdk
import kotlinx.android.synthetic.main.activity_one_hop.*
import org.json.JSONObject

class OneHopActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_one_hop)

        // register for one hop
        HwOneHopSdk.getInstance().registerOneHop(BuildConfig.APPLICATION_ID,
            HwOneHopSdk.ONEHOP_DATA_TYPE_BUSINESS_CONTINUITY,
            OneHopCallback())
    }

    // onehop callback to determine whether you are the sending or receiving end
    // phone: sender, tablet: receiver
    inner class OneHopCallback : IOneHopAppCallback.Stub() {
        override fun onOneHopReceived(parameter: String?) {
            parameter?.let {
                val jo = JSONObject(parameter)
                // check if receive type is event
                if (jo.has(HwOneHopSdk.ONEHOP_RECEIVE_TYPE)
                    && jo.getInt(HwOneHopSdk.ONEHOP_RECEIVE_TYPE) == HwOneHopSdk.ONEHOP_RECEIVE_TYPE_EVENT) {
                    // receive the data sent from the sender
                    doReceive(it)
                } else {
                    // prepare the data and send to the receiver
                    doSend()
                }

            }
        }
    }

    private fun doSend() {
        // construct the json object and call sdk to send the data over
        val data = JSONObject()
        data.put("data", text_to_send.editText?.text.toString())
        HwOneHopSdk.getInstance().oneHopSend(BuildConfig.APPLICATION_ID, data)
    }

    private fun doReceive(param: String) {
        // receive the data and show a toast on the activity
        val data = JSONObject(param)
        Toast.makeText(this, data.getString("data"), Toast.LENGTH_SHORT).show()
    }

}
