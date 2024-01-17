package com.example.send_receive_sms

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.telephony.SmsMessage
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager

class MessageBroadcastReceiver  : BroadcastReceiver(){
    private  val TAG = "MessageBroadcastReceive"


    override fun onReceive(context: Context, intent: Intent) {
        var currentSms : SmsMessage

        val dataBundle = intent.extras
        val puds = dataBundle?.get("pdus") as Array<*>

        puds.forEach {
            currentSms = getIncomingMessage(it as ByteArray, dataBundle)

            val localIntent = Intent(CONSTANT.PASS_DATA_ACTION)
            localIntent.putExtra(CONSTANT.SMS_BODY, currentSms.displayMessageBody)
            localIntent.putExtra(CONSTANT.PHONE_NUMBER, currentSms.displayOriginatingAddress)
            LocalBroadcastManager.getInstance(context).sendBroadcast(localIntent)

        }

    }


    fun getIncomingMessage(bytes: ByteArray, bundle: Bundle) : SmsMessage{
        val format = bundle.getString("format")
        return SmsMessage.createFromPdu(bytes, format)

    }

}
