package com.coding.plugin.hook;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.lang.reflect.Array;
import java.lang.reflect.Field;

import dalvik.system.DexClassLoader;

/**
 * @author: Coding.He
 * @date: 2020/4/14
 * @emil: stray-coding@foxmail.com
 * @des:from https://blog.csdn.net/zly921112/article/details/83547750
 * 注意：/storage/emulated/0/Android/data/app包名/files/patch
 * <p>
 */
public class DexFixImpl {
    private static final String TAG = "DexFixImpl";
    private static final String FIELD_DEX_ELEMENTS = "dexElements";
    private static final String CLASS_NAME = "dalvik.system.BaseDexClassLoader";
    private static final String FIELD_PATH_LIST = "pathList";
    /**
     * 默认 dex优化存放目录
     * /data/data/app包名/app_odex
     */
    private static final String OPTIMIZE_DIR = "dex2opt";

    /**
     * 获取应用程序相关的缓存路径都不需要权限
     */
    public static void startFix(Context context, String apkPath) throws IllegalAccessException, NoSuchFieldException, ClassNotFoundException {
        if (context == null) {
            Log.i(TAG, "context is null");
            return;
        }
        Context appCtx = context.getApplicationContext();
        File optFile = context.getDir(OPTIMIZE_DIR, Context.MODE_PRIVATE);
        Log.d(TAG, "optFile:" + optFile.getAbsolutePath());
        optFile.mkdirs();
        //遍历查找文件中patch开头, .dex .jar .apk结尾的文件
        String optPath = optFile.getAbsolutePath();
        //拿到系统默认的PathClassLoader加载器
        ClassLoader pathClassLoader = appCtx.getClassLoader();
        //加载我们自己的补丁dex
        DexClassLoader dexClassLoader = new DexClassLoader(apkPath, optPath, null, appCtx.getClassLoader());
        //获取PathClassLoader Element[]
        Object pathElements = getElements(pathClassLoader);
        //获取DexClassLoader Element[]
        Object dexElements = getElements(dexClassLoader);
        //合并数组
        Object combineArray = combineArray(pathElements, dexElements);
        //将合并后Element[]数组设置回PathClassLoader pathList变量
        setDexElements(pathClassLoader, combineArray);
    }

    /**
     * 获取Element[]数组
     */
    private static Object getElements(ClassLoader classLoader) throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException {
        //拿到BaseDexClassLoader Class
        Class<?> BaseDexClassLoaderClazz = Class.forName(CLASS_NAME);
        //拿到pathList字段
        Field pathListField = BaseDexClassLoaderClazz.getDeclaredField(FIELD_PATH_LIST);
        Log.d(TAG, "pathListField:" + pathListField);
        pathListField.setAccessible(true);
        //拿到DexPathList对象
        Object DexPathList = pathListField.get(classLoader);
        //拿到dexElements字段
        Field dexElementsField = DexPathList.getClass().getDeclaredField(FIELD_DEX_ELEMENTS);
        dexElementsField.setAccessible(true);
        //拿到Element[]数组
        return dexElementsField.get(DexPathList);
    }

    /**
     * 合并Element[]数组 将补丁的放在前面
     */
    private static Object combineArray(Object pathElements, Object dexElements) {
        Class<?> componentType = pathElements.getClass().getComponentType();
        int i = Array.getLength(pathElements);
        int j = Array.getLength(dexElements);
        int k = i + j;
        // 创建一个类型为componentType，长度为k的新数组
        Object result = Array.newInstance(componentType, k);
        System.arraycopy(dexElements, 0, result, 0, j);
        System.arraycopy(pathElements, 0, result, j, i);
        return result;
    }

    /**
     * 将Element[]数组 设置回PathClassLoader
     */
    private static void setDexElements(ClassLoader classLoader, Object value) throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException {
        Class<?> BaseDexClassLoaderClazz = Class.forName(CLASS_NAME);
        Field pathListField = BaseDexClassLoaderClazz.getDeclaredField(FIELD_PATH_LIST);
        pathListField.setAccessible(true);
        Object dexPathList = pathListField.get(classLoader);
        Field dexElementsField = dexPathList.getClass().getDeclaredField(FIELD_DEX_ELEMENTS);
        dexElementsField.setAccessible(true);
        dexElementsField.set(dexPathList, value);
    }
}
