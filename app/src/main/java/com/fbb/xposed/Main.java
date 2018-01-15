package com.fbb.xposed;


import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import de.robv.android.xposed.IXposedHookInitPackageResources;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_InitPackageResources;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

import static de.robv.android.xposed.XposedHelpers.callMethod;
import static de.robv.android.xposed.XposedHelpers.callStaticMethod;
import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import static de.robv.android.xposed.XposedHelpers.findClass;

/**
 * Created by fengbb on 2017/12/24.
 */

public class Main implements IXposedHookLoadPackage, IXposedHookZygoteInit, IXposedHookInitPackageResources {
    public static final String BJJJ_PACNAME = "com.zcbl.bjjj_driving";
    private final String TAG = "Xposed";

    public void log(String s) {
        Log.d(TAG, s);
        XposedBridge.log(s);
    }

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {

        if (!lpparam.packageName.equals(BJJJ_PACNAME))
            return;
        XposedBridge.log("---handleLoadPackage: " + lpparam.packageName);
        findAndHookMethod("com.stub.StubApp", lpparam.classLoader, "getNewAppInstance", Context.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                XposedBridge.log("beforeHookedMethod");
                Context context = (Context) param.args[0];
                //获取360的classloader，之后hook加固后的就使用这个classloader
                final ClassLoader classLoader = context.getClassLoader();
                AppInfo.classLoader = classLoader;
                findAndHookMethod("com.zcbl.driving_simple.activity.FirstPagerAcitivty", classLoader, "changeFragemnt",String.class, new XC_MethodHook() {
                            @Override
                            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                                super.afterHookedMethod(param);
                                XposedBridge.log("FirstPagerAcitivty  changeFragemnt after");
                                XposedBridge.log("param:"+param.args[0]);

//                                Class<?> cipherUtil = findClass("com.zcbl.driving_simple.util.CipherUtil", classLoader);
//                                Method[] allMethods = cipherUtil.getDeclaredMethods();
//                                XposedBridge.log("allMethods Size: " + allMethods.length);
//                                Class<?>[] a = {String.class};
//                                Object aliSign = callStaticMethod(cipherUtil, "aliSign",a,"i'm fbb");
//                                XposedBridge.log("aliSign: " + aliSign);

                                Class<?> myApplication = findClass("com.zcbl.driving_simple.activity.MyApplication",classLoader);
                                AppInfo.application = callStaticMethod(myApplication,"getApplication");
                                AppInfo.applicationContext= (Context) callMethod(AppInfo.application,"getApplicationContext");
                                Toast.makeText(AppInfo.applicationContext,""+param.args[0],Toast.LENGTH_SHORT).show();

                                MainConnection.openConnect();
                            }
                        }
                );
            }

            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
//                XposedBridge.log("i'm 360");
                //获取到360的Context对象，通过这个对象来获取classloader
                Context context = (Context) param.args[0];
                //获取360的classloader，之后hook加固后的就使用这个classloader
                final ClassLoader classLoader = context.getClassLoader();
                //下面就是强classloader修改成360的classloader就可以成功的hook了
//                XposedHelpers.findAndHookMethod("com.zcbl.driving_simple.activity.SplashActivity", classLoader, "onWindowFocusChanged", boolean.class, new XC_MethodHook() {
//                    @Override
//                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
//                        super.beforeHookedMethod(param);
//                        XposedBridge.log("i'm fbb before");
////                        XposedBridge.log("afterHookedMethod : " + param.thisObject);
//                    }
//
//                    @Override
//                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
//                        super.beforeHookedMethod(param);
//                        XposedBridge.log("i'm fbb after");
//                    }
//                });
//                Class<?> pictureUtil = findClass("com.zcbl.driving_simple.activity.bjjj.PictureUtil", classLoader);
//                Object albumName = callStaticMethod(pictureUtil, "getAlbumName");
//                XposedBridge.log("albumName: " + albumName);

//                Class<?> cipherUtil = findClass("com.zcbl.driving_simple.util.CipherUtil", classLoader);
//                Method[] allMethods = cipherUtil.getDeclaredMethods();
//                XposedBridge.log("allMethods Size: " + allMethods.length);
//                Class<?>[] a = {String.class};
//                Object aliSign = callStaticMethod(cipherUtil, "aliSign",a,"i'm fbb");
//                XposedBridge.log("aliSign: " + aliSign);

            }
        });
    }

    @Override
    public void initZygote(StartupParam startupParam) throws Throwable {
        XposedBridge.log("initZygote");
    }

    @Override
    public void handleInitPackageResources(XC_InitPackageResources.InitPackageResourcesParam resparam) throws Throwable {
    }
}
