package com.gradproject.hospi.utils;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.Window;

import com.gradproject.hospi.databinding.LoadingBinding;

public class Loading extends Dialog {

    public Loading(Context context) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        LoadingBinding binding = LoadingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        setCancelable(false);
    }
}
