package com.coding.plugin

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.AssetManager
import android.content.res.Resources
import android.util.Log
import com.coding.plugin.hook.DexFixImpl
import java.lang.reflect.Method


/**
 * @author: Coding.He
 * @date: 2020/10/22
 * @emil: stray-coding@foxmail.com
 * @des:插件管理类，主要进行classloader和resource的生成,从而调用apk中的资源和类。
 */
@SuppressLint("StaticFieldLeak")
object PluginManager {
    private const val TAG = "PluginManager"

    /**
     * 占位activity名称，主要用于启动插件activity时，跳过AMF检测
     * */
    const val PLACEHOLDER_ACTIVITY_NAME = "com.coding.plugin.PlaceholderActivity"

    /**
     * true 作为插件使用，供其他app动态加载该apk,
     * @see loadApk(ctx,apkPath)后，
     * false 作为独立的app使用
     * 在调用 {@link #loadApk(ctx, apkPath)} 方法后，标志位true
     * */
    var isPlugin = false
        private set
    var pluginAssets: AssetManager? = null
        private set
    var pluginRes: Resources? = null
        private set

    @SuppressLint("DiscouragedPrivateApi")
    fun loadApk(ctx: Context?, apkPath: String) {
        if (ctx == null) {
            Log.d(TAG, "ctx is null, apk cannot be loaded dynamically")
            throw RuntimeException("ctx is null, apk cannot be loaded dynamically")
        }
        isPlugin = true
        try {
            DexFixImpl.startFix(ctx, apkPath)
            pluginAssets = AssetManager::class.java.newInstance()
            val addAssetPath: Method =
                AssetManager::class.java.getDeclaredMethod("addAssetPath", String::class.java)
            addAssetPath.invoke(pluginAssets, apkPath)
            val superResources: Resources? = ctx.resources
            pluginRes = Resources(
                pluginAssets,
                superResources?.displayMetrics,
                superResources?.configuration
            )
            Log.d(TAG, "dynamic loading of apk success")
        } catch (e: Exception) {
            Log.d(TAG, "dynamic loading of apk failed")
            isPlugin = false
            e.printStackTrace()
        }
    }
}