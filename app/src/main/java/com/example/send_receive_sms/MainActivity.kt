package com.example.send_receive_sms

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.telephony.SmsManager
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.send_receive_sms.databinding.ActivityMainBinding
import kotlin.Exception

class MainActivity : AppCompatActivity(){

    private lateinit var binding: ActivityMainBinding
    private val TAG = "MainActivity"
    private val READ_SMS_PERMISSION = "android.permission.RECEIVE_SMS"
    private val SEND_SMS_PERMISSION = "android.permission.SEND_SMS"
    private val PERMISSION_REQUEST_CODE = 100
    private val messageBroadcastReceiver = MessageBroadcastReceiver()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, IntentFilter(CONSTANT.PASS_DATA_ACTION))


        if(!(checkReadSMSPerm() && checkSendSMSPerm())){
            requestPerms()
        }

        binding.sendBtn.setOnClickListener {
            if (binding.phoneNumberEdt.text?.trim().isNullOrEmpty()) {
                Toast.makeText(this, getString(R.string.enter_destination_phone_number), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if(!checkSendSMSPerm()){
                Toast.makeText(this, getString(R.string.send_message_is_not_granted), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            sendSMS(
                binding.phoneNumberEdt.text?.trim().toString(),
                binding.smsBodyEdt.text?.trim().toString()
            )
            Toast.makeText(this, getString(R.string.smessage_sent),Toast.LENGTH_SHORT).show()

        }

    }

    private fun checkReadSMSPerm() = ActivityCompat.checkSelfPermission(this, READ_SMS_PERMISSION) == PackageManager.PERMISSION_GRANTED
    private fun checkSendSMSPerm() = ActivityCompat.checkSelfPermission(this, SEND_SMS_PERMISSION) == PackageManager.PERMISSION_GRANTED

    private fun requestPerms() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(READ_SMS_PERMISSION, SEND_SMS_PERMISSION),
            PERMISSION_REQUEST_CODE
        )
    }

    private fun sendSMS(phoneNumber: String, message: String) {

        try {
            getSystemService(SmsManager::class.java).sendTextMessage(
                phoneNumber,
                null,
                message,
                null,
                null
            )
        } catch (exeption: Exception) {
            Toast.makeText(this, getString(R.string.failed),Toast.LENGTH_SHORT).show()
            exeption.printStackTrace()

        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "permission is granted")
                registerSMSBroadcast()
            }
        }
    }

    private fun registerSMSBroadcast() {
        val intentFilter = IntentFilter("android.provider.Telephony.SMS_RECEIVED")
        registerReceiver(messageBroadcastReceiver, intentFilter)
    }

    override fun onDestroy() {
        super.onDestroy()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver)
        unregisterReceiver(messageBroadcastReceiver)
    }


    private val receiver = object : BroadcastReceiver(){
        override fun onReceive(context:Context?, intent: Intent?) {
            if (intent?.action == CONSTANT.PASS_DATA_ACTION){
                binding.receivedSmsTxt.text = String.format("%s \nfrom: %s", intent.getStringExtra(CONSTANT.SMS_BODY), intent.getStringExtra(CONSTANT.PHONE_NUMBER))

            }
        }

    }




}
