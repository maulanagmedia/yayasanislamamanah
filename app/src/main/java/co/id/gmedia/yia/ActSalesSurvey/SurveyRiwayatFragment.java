package co.id.gmedia.yia.ActSalesSurvey;

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
import java.util.Date;
import java.util.List;

import co.id.gmedia.yia.ActCollector.Adapter.HistoryCollectorAdapter;
import co.id.gmedia.yia.ActCollector.Adapter.JadwalKunjunganAdapter;
import co.id.gmedia.yia.ActSalesSurvey.Adapter.SurveyRiwayatAdapter;
import co.id.gmedia.yia.Model.DonaturModel;
import co.id.gmedia.yia.Model.HistoryDonaturModel;
import co.id.gmedia.yia.Model.SurveyRiwayatModel;
import co.id.gmedia.yia.R;

public class SurveyRiwayatFragment extends Fragment {

    private Activity activity;
    private SurveyRiwayatAdapter adapter;
    private List<SurveyRiwayatModel> listDonatur = new ArrayList<>();

    public SurveyRiwayatFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        activity = getActivity();
        View v = inflater.inflate(R.layout.fragment_survey_riwayat, container, false);

        RecyclerView rv_history = v.findViewById(R.id.rv_history);
        rv_history.setItemAnimator(new DefaultItemAnimator());
        rv_history.setLayoutManager(new LinearLayoutManager(activity));
        adapter = new SurveyRiwayatAdapter(activity, listDonatur);
        rv_history.setAdapter(adapter);

        loadHistory();

        return v;
    }

    private void loadHistory(){
        listDonatur.add(new SurveyRiwayatModel(new DonaturModel("Leonardus Irfan", "Jl. Menur Raya 16", "085742089087"), "continuous", "biasa", new Date(), "Ya"));
        listDonatur.add(new SurveyRiwayatModel(new DonaturModel("Bayu Mahendra", "Jl. Kasipah 19", "085742089087"), "single", "luar biasa", new Date(), "Tidak"));
        adapter.notifyDataSetChanged();
    }
}
