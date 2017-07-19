package com.falling.copysave.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by falling on 2017/7/18.
 */

public class ShareUtil {
    /**
     * return int[2] with x and y;
     * @param context
     * @return
     */
    public static int[] getPosition(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("position", Context.MODE_PRIVATE);
        int position[] = new int[2];
        position[0] = sharedPreferences.getInt("x", 0);
        position[1] = sharedPreferences.getInt("y", 0);
        return position;
    }

    public static void savePosition(Context context, int x, int y) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("position", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("x", x);
        editor.putInt("y", y);
        editor.apply();
    }

}
