package gavinhua.contacts.utils;

import android.content.res.Resources;
import android.graphics.Color;

import gavinhua.contacts.R;

/**
 * Created by GavinHua on 2016/4/1.
 * color
 */
public class ColorUtils {
    static int[] colors = {R.color.google_blue,
            R.color.google_blue_highlight,
            R.color.google_green,
            R.color.google_red,
            R.color.google_gray,
            R.color.google_blue_dark,
            R.color.google_purple,
            R.color.google_orange,
            R.color.google_yellow};

    public static int getColor(Resources mResources, String str) {
        return mResources.getColor(colors[Math.abs(str.hashCode()) % colors.length]);
    }

    public static int getDarkerColor(int color) {
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv); // convert to hsv
        // make darker
        hsv[1] = hsv[1] + 0.1f; // more saturation
        hsv[2] = hsv[2] - 0.1f; // less brightness
        return Color.HSVToColor(hsv);
    }

}
