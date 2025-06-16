package com.suffhillrabbitfarm.rabbitinfosystem.helpers;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;

import com.suffhillrabbitfarm.rabbitinfosystem.R;

public class MiscHelper {

    public static Dialog openNetLoaderDialog(Context context) {
       Dialog dialogP=new Dialog(context);
        dialogP.setContentView(R.layout.dialog_loading);
        dialogP.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogP.setCancelable(false);
        dialogP.show();
        return dialogP;
    }

}
