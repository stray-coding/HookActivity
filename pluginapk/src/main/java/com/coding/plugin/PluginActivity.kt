package com.coding.plugin

import android.content.Context
import android.content.res.AssetManager
import android.content.res.Resources
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.ContextThemeWrapper
import java.lang.reflect.Field


/**
 * @author: Coding.He
 * @date: 2021/09/06
 * @emil: stray-coding@foxmail.com
 * @des:占位activity，主要用于启动插件activity时，跳过AMF检测
 * PluginActivity可继承Activity/AppCompatActivity
 */
abstract class PluginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (superAppCompatActivity() && PluginManager.isPlugin) {
            val resource: Resources = PluginManager.pluginRes!!
            val ctx = ContextThemeWrapper(baseContext, 0)
            val clazz: Class<out Context?> = ctx.javaClass
            try {
                val mResourcesField: Field = clazz.getDeclaredField("mResources")
                mResourcesField.isAccessible = true
                mResourcesField.set(ctx, resource)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            val view: View = LayoutInflater.from(ctx).inflate(layoutId(), null)
            setContentView(view)
        } else {

            setContentView(layoutId())
        }
        onContinueCreate(savedInstanceState)
    }

    //布局填充
    abstract fun layoutId(): Int

    //onCreate方法后续
    abstract fun onContinueCreate(savedInstanceState: Bundle?)

    override fun getResources(): Resources? {
        return if (!PluginManager.isPlugin)
            super.getResources()
        else {
            PluginManager.pluginRes
        }
    }

    override fun getAssets(): AssetManager? {
        return if (!PluginManager.isPlugin)
            super.getAssets()
        else
            PluginManager.pluginAssets
    }

    private fun superAppCompatActivity(): Boolean {
        return this::class.java.superclass == AppCompatActivity::class.java
    }
}