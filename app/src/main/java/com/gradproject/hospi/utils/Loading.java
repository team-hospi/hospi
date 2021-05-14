package com.gradproject.hospi.utils;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.Window;

import com.gradproject.hospi.databinding.ActivityCoronaCheckBinding;
import com.gradproject.hospi.databinding.LoadingBinding;

public class Loading extends Dialog {
    private LoadingBinding binding;

    public Loading(Context context) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        binding = LoadingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        setCancelable(false);
    }
}
