package com.example.usahatanipadi;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class MenuUtamaFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_menu_utama, container, false);

        CardView crd = (CardView)view.findViewById(R.id.pengeluaran_biaya);
        crd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), PeriodeTransaksiPengeluaran.class));
            }
        });

        CardView crd_penerimaan = (CardView)view.findViewById(R.id.penerimaan_dana);
        crd_penerimaan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), PeriodeTransaksiPenerimaan.class));
            }
        });

        CardView crd_laporan = (CardView)view.findViewById(R.id.laporan);
        crd_laporan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), MenuLaporan.class));
            }
        });
        return view;
    }
}
