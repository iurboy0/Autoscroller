package com.vanta.instascroll;

import android.content.Context;
import android.content.SharedPreferences;

public class Prefs {
    private static final String NAME = "vanta_scroll";

    public static SharedPreferences get(Context ctx) {
        return ctx.getSharedPreferences(NAME, Context.MODE_PRIVATE);
    }

    public static int getDelay(Context ctx) {
        return get(ctx).getInt("delay", 150);
    }

    public static void setDelay(Context ctx, int v) {
        get(ctx).edit().putInt("delay", v).apply();
    }

    public static int getLengthPct(Context ctx) {
        return get(ctx).getInt("length", 45);
    }

    public static void setLengthPct(Context ctx, int v) {
        get(ctx).edit().putInt("length", v).apply();
    }

    public static int getDirection(Context ctx) {
        return get(ctx).getInt("direction", 0);
    }

    public static void setDirection(Context ctx, int v) {
        get(ctx).edit().putInt("direction", v).apply();
    }
}
