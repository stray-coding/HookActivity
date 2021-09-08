package com.coding.plugin.hook

import android.app.Instrumentation
import android.content.Context

object HookHelper {
    const val TARGET_INTENT = "target_intent"

    @Throws(Exception::class)
    fun hookInstrumentation(context: Context) {
        val contextImplClass = Class.forName("android.app.ContextImpl")
        val mMainThreadField = contextImplClass.getDeclaredField("mMainThread")
        mMainThreadField.isAccessible = true
        val activityThread = mMainThreadField[context]
        val activityThreadClass = Class.forName("android.app.ActivityThread")
        val mInstrumentation = activityThreadClass.getDeclaredField("mInstrumentation")
        mInstrumentation.isAccessible = true
        mInstrumentation[activityThread] = InstrumentationProxy(
            mInstrumentation[activityThread] as Instrumentation,
            context.packageManager
        )
    }

}