package co.id.gmedia.yia.ActCollector;


import android.app.Activity;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import co.id.gmedia.yia.ActCollector.Adapter.JadwalKunjunganAdapter;
import co.id.gmedia.yia.ActCollector.Adapter.JadwalKunjunganCollectorAdapter;
import co.id.gmedia.yia.Model.DonaturModel;
import co.id.gmedia.yia.R;
import co.id.gmedia.yia.Utils.Converter;

/**
 * A simple {@link Fragment} subclass.
 */
public class CollectorJadwalFragment extends Fragment {

    private Activity activity;
    private JadwalKunjunganCollectorAdapter adapter;
    private List<DonaturModel> listDonatur = new ArrayList<>();

    private TextView txt_tanggal;

    public CollectorJadwalFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        activity = getActivity();
        View v = inflater.inflate(R.layout.fragment_collector_jadwal, container, false);

        txt_tanggal = v.findViewById(R.id.txt_tanggal);

        RecyclerView rv_jadwal = v.findViewById(R.id.rv_jadwal);
        rv_jadwal.setItemAnimator(new DefaultItemAnimator());
        rv_jadwal.setLayoutManager(new LinearLayoutManager(activity));
        adapter = new JadwalKunjunganCollectorAdapter(activity, listDonatur);
        rv_jadwal.setAdapter(adapter);

        loadData();
        loadJadwal();

        return v;
    }

    private void loadData(){
        txt_tanggal.setText(Converter.getDateString(new Date()));
    }

    private void loadJadwal(){

        listDonatur.add(new DonaturModel("Bapak Jaya Selalu", "Jl. Mangga Dalam Selatan - Srondol Wetan", "081234938940", false));
        listDonatur.add(new DonaturModel("Leonardus Irfan", "Jl. Kasipah 19", "081234938940", true));
        adapter.notifyDataSetChanged();
    }
}
