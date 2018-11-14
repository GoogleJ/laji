package com.zhishen.aixuexue.util;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

/**
 * Created by Jerome on 15:43
 */
public class AppUtils {

    public static String getActivityLabel(Context context) {
        Intent intent = new Intent(context, context.getClass());
        return getPackageManager(context).resolveActivity(intent, 0)
                .loadLabel(getPackageManager(context)).toString();
    }

    public static PackageManager getPackageManager(Context context) {
        return context.getPackageManager();
    }

}
