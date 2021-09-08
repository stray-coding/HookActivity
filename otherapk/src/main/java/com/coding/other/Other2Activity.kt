package com.coding.other

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.widget.Button
import android.widget.PopupWindow
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.coding.plugin.PluginActivity

class Other2Activity : PluginActivity() {

    override fun layoutId(): Int {
        return R.layout.activity_other2
    }

    override fun onContinueCreate(savedInstanceState: Bundle?) {
        val button = findViewById<Button>(R.id.button2)
        button.setOnClickListener {
            Log.d("OtherActivity", "you click: other2Activity button")
            Toast.makeText(this, "you click ", Toast.LENGTH_SHORT).show()
            val tv = TextView(this)
            tv.layoutParams = ViewGroup.LayoutParams(200, 200)
            tv.text = "我来了"
            tv.setTextColor(Color.BLACK)
            val popupWindow = PopupWindow(tv)
            popupWindow.width = 200
            popupWindow.height = 200
            popupWindow.showAsDropDown(button)
            AlertDialog.Builder(this)
                .setTitle("this is title")
                .setMessage("message")
                .setPositiveButton("confirm", null)
                .setNegativeButton("cancel", null)
                .show()
        }
    }
}