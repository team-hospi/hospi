package com.gradproject.hospi.utils;

import android.app.ProgressDialog;
import android.content.Context;

public class Loading {
    String msg;
    Context context;
    ProgressDialog progressDialog;

    public Loading(Context context, String msg) {
        this.msg = msg;
        this.context = context;

        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage(msg);
        progressDialog.setCancelable(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
    }

    public void start() {
        progressDialog.show();
    }

    public void end() {
        progressDialog.dismiss();
    }
}
