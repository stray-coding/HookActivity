package com.coding.plugin.hook

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Instrumentation
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.text.TextUtils
import androidx.annotation.RequiresApi
import com.coding.plugin.PluginManager

class InstrumentationProxy(
    private val mInstrumentation: Instrumentation,
    private val mPackageManager: PackageManager
) :
    Instrumentation() {
    @RequiresApi(Build.VERSION_CODES.M)
    @SuppressLint("DiscouragedPrivateApi", "QueryPermissionsNeeded")
    fun execStartActivity(
        who: Context?, contextThread: IBinder?, token: IBinder?, target: Activity?,
        intent: Intent, requestCode: Int, options: Bundle?
    ): ActivityResult? {
        val list = mPackageManager.queryIntentActivities(intent, PackageManager.MATCH_ALL)
        if (list.isEmpty()) {
            intent.putExtra(HookHelper.TARGET_INTENT, intent.component!!.className) //1
            intent.setClassName(who!!, PluginManager.PLACEHOLDER_ACTIVITY_NAME) //2
        }
        try {
            val execMethod = Instrumentation::class.java.getDeclaredMethod(
                "execStartActivity",
                Context::class.java,
                IBinder::class.java,
                IBinder::class.java,
                Activity::class.java,
                Intent::class.java,
                Int::class.javaPrimitiveType,
                Bundle::class.java
            )
            return execMethod.invoke(
                mInstrumentation, who,
                contextThread, token, target, intent, requestCode, options
            ) as? ActivityResult
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    @Throws(
        IllegalAccessException::class,
        InstantiationException::class,
        ClassNotFoundException::class
    )

    override fun newActivity(cl: ClassLoader, className: String, intent: Intent): Activity {
        val intentName = intent.getStringExtra(HookHelper.TARGET_INTENT)
        return if (!TextUtils.isEmpty(intentName)) {
            super.newActivity(cl, intentName, intent)
        } else {
            super.newActivity(cl, className, intent)
        }
    }
}