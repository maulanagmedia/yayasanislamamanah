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

import java.util.ArrayList;
import java.util.List;

import co.id.gmedia.yia.ActCollector.Adapter.JadwalKunjunganAdapter;
import co.id.gmedia.yia.Model.DonaturModel;
import co.id.gmedia.yia.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class JadwalKunjunganFragment extends Fragment {

    private Activity activity;
    private JadwalKunjunganAdapter adapter;
    private List<DonaturModel> listDonatur = new ArrayList<>();

    public JadwalKunjunganFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        activity = getActivity();
        View v = inflater.inflate(R.layout.fragment_jadwal_kunjungan, container, false);

        RecyclerView rv_jadwal = v.findViewById(R.id.rv_jadwal);
        rv_jadwal.setItemAnimator(new DefaultItemAnimator());
        rv_jadwal.setLayoutManager(new LinearLayoutManager(activity));
        adapter = new JadwalKunjunganAdapter(activity, listDonatur);
        rv_jadwal.setAdapter(adapter);

        loadJadwal();

        return v;
    }

    private void loadJadwal(){
        listDonatur.add(new DonaturModel("Bapak Jaya Selalu", "Jl. Mangga Dalam Selatan - Srondol Wetan", "081234938940"));
        listDonatur.add(new DonaturModel("Leonardus Irfan", "Jl. Kasipah 19", "081234938940"));
        adapter.notifyDataSetChanged();
    }
}
