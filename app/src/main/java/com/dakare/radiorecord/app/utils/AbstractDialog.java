package com.dakare.radiorecord.app.utils;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;

import android.view.Window;

import androidx.appcompat.app.AppCompatDialog;

public class AbstractDialog extends AppCompatDialog {

    public AbstractDialog(final Context context) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }
}
