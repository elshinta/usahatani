package com.example.usahatanipadi;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.cardview.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class MasterDataFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_master_data, container, false);

        CardView crd = (CardView)view.findViewById(R.id.data_alat);
        crd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), MasterDataAlat.class));
            }
        });

        CardView crd_bibit = (CardView)view.findViewById(R.id.data_bibit);
        crd_bibit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), MasterDataBibit.class));
            }
        });

        CardView crd_panen = (CardView)view.findViewById(R.id.data_hasil_panen);
        crd_panen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), MasterDataHasilPanen.class));
            }
        });

        CardView crd_hama = (CardView)view.findViewById(R.id.data_obat_hama);
        crd_hama.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), MasterDataObatHama.class));
            }
        });

        CardView crd_pupuk = (CardView)view.findViewById(R.id.data_pupuk);
        crd_pupuk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), MasterDataPupuk.class));
            }
        });

        CardView crd_satuan = (CardView)view.findViewById(R.id.data_satuan);
        crd_satuan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), MasterDataSatuan.class));
            }
        });

        CardView crd_sawah = (CardView)view.findViewById(R.id.data_sawah);
        crd_sawah.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), MasterDataSawah.class));
            }
        });

        return view;
    }
}
