package io.github.textrecognisionsample.util;

import android.app.Activity;
import android.graphics.Point;
import android.os.Build;
import android.util.DisplayMetrics;

public abstract class ScreenUtil {

    public static Point getSize(Activity activity) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            activity.getDisplay().getRealMetrics(displayMetrics);
        } else {
            activity.getWindowManager().getDefaultDisplay().getRealMetrics(displayMetrics);
        }
        return new Point(displayMetrics.widthPixels, displayMetrics.heightPixels);
    }

}
