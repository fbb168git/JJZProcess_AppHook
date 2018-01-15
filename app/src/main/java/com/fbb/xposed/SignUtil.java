package com.fbb.xposed;

import de.robv.android.xposed.XposedBridge;

import static de.robv.android.xposed.XposedHelpers.callStaticMethod;
import static de.robv.android.xposed.XposedHelpers.findClass;

/**
 * Created by fengbb on 2017/12/26.
 */

public class SignUtil {

    public static String sign(String value) {
        XposedBridge.log("sign value:"+value);
        Class<?> cipherUtil = findClass("com.zcbl.driving_simple.util.CipherUtil", AppInfo.classLoader);
        Class<?>[] a = {String.class};
        String aliSign = (String) callStaticMethod(cipherUtil, "aliSign",a,value);
        XposedBridge.log("aliSign result:"+aliSign);
        return aliSign;
    }
}
