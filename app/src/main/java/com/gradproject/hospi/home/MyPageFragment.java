package com.gradproject.hospi.home;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.TestLooperManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.gradproject.hospi.R;

public class MyPageFragment extends Fragment {
    TextView version;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_my_page, container,false);

        version = rootView.findViewById(R.id.version);

        version.setText(getVersionInfo(getContext()));

        return rootView;
    }

    public String getVersionInfo(Context context){
        String version = null;
        try {
            PackageInfo i = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            version = i.versionName;
        } catch(PackageManager.NameNotFoundException e) { }
        return version;
    }
}