package com.example.allofpower

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.allofpower.floatingview.FloatingManage
import com.example.allofpower.services.FloatingService
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btn.setOnClickListener {  startService(Intent(this,FloatingService::class.java)) }
    }

    override fun onDestroy() {
        super.onDestroy()
        stopService(Intent(this,FloatingService::class.java))
    }
}
