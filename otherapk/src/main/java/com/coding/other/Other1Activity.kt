package com.coding.other


import android.content.Intent
import android.os.Bundle
import android.widget.Button
import com.coding.plugin.PluginActivity


class Other1Activity : PluginActivity() {
    override fun layoutId(): Int {
        return R.layout.activity_other1
    }

    override fun onContinueCreate(savedInstanceState: Bundle?) {
        val btn = findViewById<Button>(R.id.button)
        btn.setOnClickListener {
            startActivity(Intent(this, Other2Activity::class.java))
        }
    }
}