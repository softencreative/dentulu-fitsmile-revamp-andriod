package com.app.fitsmile.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.app.fitsmile.R;
import com.app.fitsmile.myaccount.MyAccount;
import com.app.fitsmile.splash.BiometricScreen;
import com.app.fitsmile.splash.LoginActivity;
import com.app.fitsmile.splash.RegisterActivity;
import com.app.fitsmile.splash.SliderActivity;

public class CommanClass {

    public static void openChangeLanguagePopup(Activity context, String isFrom) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.language_selection_view);

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
       // dialog.getWindow().setLayout(500, 500);
        TextView btnEnglish = dialog.findViewById(R.id.btn_english);
        TextView btnSpanish = dialog.findViewById(R.id.btn_spanish);
        btnEnglish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              LocaleManager.setNewLocale(context, LocaleManager.ENGLISH);
//                LocaleManager.setNewLocale(context, LocaleManager.SPANISH);
                dialog.dismiss();
                Intent intent = null;
                if (isFrom.equals("biometric")) {
                    intent = new Intent(context, BiometricScreen.class);
                } else if (isFrom.equals("slider")) {
                    intent = new Intent(context, SliderActivity.class);
                } else if (isFrom.equals("register")) {
                    intent = new Intent(context, RegisterActivity.class);
                }
                else if (isFrom.equals("login")) {
                    intent = new Intent(context, LoginActivity.class);
                }
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
                context.finish();
            }
        });


        btnSpanish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LocaleManager.setNewLocale(context, LocaleManager.SPANISH);
                dialog.dismiss();
                Intent intent = null;
                if (isFrom.equals("biometric")) {
                    intent = new Intent(context, BiometricScreen.class);
                } else if (isFrom.equals("slider")) {
                    intent = new Intent(context, SliderActivity.class);
                } else if (isFrom.equals("register")) {
                    intent = new Intent(context, RegisterActivity.class);
                }
                else if (isFrom.equals("login")) {
                    intent = new Intent(context, LoginActivity.class);
                }
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
                context.finish();

            }
        });

        dialog.show();


    }
}
