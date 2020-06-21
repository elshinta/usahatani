package com.example.usahatanipadi;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class EdukasiFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_informasi, container, false);

        final Button btn_benih = view.findViewById(R.id.btn_benih);
        final TextView tv_benih = view.findViewById(R.id.tv_benih);
        final Button btn_hama = view.findViewById(R.id.btn_hama);
        final TextView tv_hama = view.findViewById(R.id.tv_hama);
        final Button btn_pengendalian_hama = view.findViewById(R.id.btn_pengendalian_hama);
        final TextView tv_pengendalian_hama = view.findViewById(R.id.tv_pengendalian_hama);

        btn_benih.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv_benih.setText(MenuUtama.BENIH);

                if(tv_benih.getVisibility() == View.GONE){

                    tv_benih.setVisibility(View.VISIBLE);
                    Drawable img = btn_benih.getContext().getResources().getDrawable( R.drawable.ic_back );
                    btn_benih.setCompoundDrawablesWithIntrinsicBounds(null,null,img,null);
                }
                else{
                    tv_benih.setVisibility(View.GONE);
                    Drawable img = btn_benih.getContext().getResources().getDrawable( R.drawable.ic_spinner );
                    btn_benih.setCompoundDrawablesWithIntrinsicBounds(null,null,img,null); }
            }
        });

        btn_hama.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv_hama.setText(MenuUtama.HAMA);

                if(tv_hama.getVisibility() == View.GONE){

                    tv_hama.setVisibility(View.VISIBLE);
                    Drawable img = btn_hama.getContext().getResources().getDrawable( R.drawable.ic_back );
                    btn_hama.setCompoundDrawablesWithIntrinsicBounds(null,null,img,null);
                }
                else{
                    tv_hama.setVisibility(View.GONE);
                    Drawable img = btn_hama.getContext().getResources().getDrawable( R.drawable.ic_spinner );
                    btn_hama.setCompoundDrawablesWithIntrinsicBounds(null,null,img,null); }
            }
        });

        btn_pengendalian_hama.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv_pengendalian_hama.setText(MenuUtama.PENGENDALIANHAMA);

                if(tv_pengendalian_hama.getVisibility() == View.GONE){

                    tv_pengendalian_hama.setVisibility(View.VISIBLE);
                    Drawable img = btn_pengendalian_hama.getContext().getResources().getDrawable( R.drawable.ic_back );
                    btn_pengendalian_hama.setCompoundDrawablesWithIntrinsicBounds(null,null,img,null);
                }
                else{
                    tv_pengendalian_hama.setVisibility(View.GONE);
                    Drawable img = btn_pengendalian_hama.getContext().getResources().getDrawable( R.drawable.ic_spinner );
                    btn_pengendalian_hama.setCompoundDrawablesWithIntrinsicBounds(null,null,img,null); }
            }
        });

        return view;
    }
}
