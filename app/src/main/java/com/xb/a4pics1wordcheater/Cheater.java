package com.xb.a4pics1wordcheater;

import android.app.AndroidAppHelper;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;

public class Cheater implements IXposedHookLoadPackage {
    public void handleLoadPackage(final LoadPackageParam lpparam) throws Throwable {
        if (!lpparam.packageName.equals("de.lotum.whatsinthefoto.us")) {
            // XposedBridge.log("Irrelevent APP: " + lpparam.packageName);
            return;
        }
        XposedBridge.log("App loaded: "+lpparam.packageName);
        findAndHookMethod("de.lotum.whatsinthefoto.ui.controller.AutoJokerController", lpparam.classLoader,
                "setSolution", "de.lotum.whatsinthefoto.entity.Duel", String.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                XSharedPreferences pref = new XSharedPreferences("de.lotum.whatsinthefoto.us", "user_settings");
                String status = pref.getString("cheater_switch", "OFF");
                if (status.equals("ON")) {
                    XposedBridge.log("Solution: " + param.args[1]);
                    Context context = (Context) AndroidAppHelper.currentApplication();
                    CharSequence text = "" + param.args[1];
                    int duration = Toast.LENGTH_SHORT;
                    Toast toast = Toast.makeText(context, text, duration);
                    toast.setGravity(Gravity.TOP, 0, 0);
                    toast.show();
                }
            }
        });
        findAndHookMethod("de.lotum.whatsinthefoto.ui.widget.CountView", lpparam.classLoader, "onAttachedToWindow", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                TextView tv = (TextView) param.thisObject;
                tv.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        Context context = AndroidAppHelper.currentApplication();
                        XSharedPreferences pref = new XSharedPreferences("de.lotum.whatsinthefoto.us", "user_settings");
                        String text = pref.getString("cheater_switch", "OFF");
                        XposedBridge.log("Cheater Switch: "+text);
                        SharedPreferences wpref = context.getSharedPreferences("user_settings", Context.MODE_WORLD_READABLE);
                        Editor editor = wpref.edit();
                        String newtext = "";
                        if (text.equals("ON"))
                            newtext = "OFF";
                        if (text.equals("OFF"))
                            newtext = "ON";
                        editor.putString("cheater_switch", newtext);
                        editor.commit();
                        // Toasts
                        CharSequence msg = "Switch "+newtext;
                        int duration = Toast.LENGTH_SHORT;
                        Toast toast = Toast.makeText(context, msg, duration);
                        toast.setGravity(Gravity.TOP, 0, 0);
                        toast.show();
                        return true;
                    }
                });
            }
        });

    }
}