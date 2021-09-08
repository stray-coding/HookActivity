package com.coding.demo

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.coding.plugin.PluginManager
import java.io.File

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.d("MainActivity", "R.id.decor_content_parent:" + R.id.decor_content_parent)
        findViewById<Button>(R.id.btn_load_apk).setOnClickListener {
            try {
                val patchDir: File = this.getDir("patch", Context.MODE_PRIVATE)
                val filename = "otherapk-debug.apk"
                val hotFixFile = File(patchDir, filename)
                hotFixFile.writeBytes(assets.open(filename).readBytes())
                PluginManager.loadApk(this, hotFixFile.absolutePath)
                Toast.makeText(this, "load apk success", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        findViewById<Button>(R.id.btn_to_plugin).setOnClickListener {
            try {
                startApk()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun startApk() {
        val intent = Intent()
        intent.setClassName(this, "com.coding.other.Other1Activity")
        startActivity(intent)
    }
}