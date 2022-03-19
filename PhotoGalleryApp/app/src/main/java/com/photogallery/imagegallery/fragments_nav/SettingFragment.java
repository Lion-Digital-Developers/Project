package com.photogallery.imagegallery.fragments_nav;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.photogallery.imagegallery.LanguagesDialog;
import com.photogallery.imagegallery.MainNavActivity;
import com.photogallery.imagegallery.R;
import com.photogallery.imagegallery.theam.Constant;
import com.turkialkhateeb.materialcolorpicker.ColorChooserDialog;
import com.turkialkhateeb.materialcolorpicker.ColorListener;

public class SettingFragment extends Fragment {


    Button button;

    TextView tvLang;

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {

        View rootview = inflater.inflate(R.layout.fragment_setting,container,false);
        button = rootview.findViewById(R.id.button_color);
        tvLang = rootview.findViewById(R.id.tv_lang);

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MyGAllery", Context.MODE_PRIVATE);
        int color = sharedPreferences.getInt("KEY_COLOR", 0);
        if (sharedPreferences.contains("KEY_COLOR")){
            // toolbar.setBackgroundColor(Constant.color);
            button.setBackgroundColor(color);

        }else {

            button.setBackgroundColor(Constant.color);

        }

        tvLang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LanguagesDialog languagesDialog = new LanguagesDialog();
                languagesDialog.show(getActivity().getFragmentManager(), "LanguagesDialogFragment");
            }
        });

        button.setBackgroundColor(Constant.color);
        button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                ColorChooserDialog dialog = new ColorChooserDialog(getActivity());
                dialog.setTitle("Select");
                dialog.setColorListener(new ColorListener() {
                    @Override
                    public void OnColorClick(View v, int color) {
                        Constant.color = color;
                        button.setBackgroundColor(Constant.color);
                        Log.e("TAG", "OnColorClick: "+Constant.color);

                        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MyGAllery",Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putInt("KEY_COLOR",Constant.color);
                        editor.commit();
                        Intent intent = new Intent(getActivity(), MainNavActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }
                });

                dialog.show();
            }
        });

        return rootview;
    }
}

