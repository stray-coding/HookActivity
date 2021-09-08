package com.coding.plugin

import android.app.Application
import android.content.Context
import com.coding.plugin.hook.HookHelper

/**
 * @author: Coding.He
 * @date: 2021/9/6
 * @emil: stray-coding@foxmail.com
 * @des:
 */
class PluginApplication : Application() {
    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        try {
            HookHelper.hookInstrumentation(base!!)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}