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
import java.util.Date;
import java.util.List;

import co.id.gmedia.yia.ActCollector.Adapter.HistoryCollectorAdapter;
import co.id.gmedia.yia.Model.DonaturModel;
import co.id.gmedia.yia.Model.HistoryDonaturModel;
import co.id.gmedia.yia.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class HistoryCollectorFragment extends Fragment {

    private Activity activity;
    private HistoryCollectorAdapter adapter;
    private List<HistoryDonaturModel> listDonatur = new ArrayList<>();

    public HistoryCollectorFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        activity = getActivity();
        View v = inflater.inflate(R.layout.fragment_history_collector, container, false);

        RecyclerView rv_history = v.findViewById(R.id.rv_history);
        rv_history.setItemAnimator(new DefaultItemAnimator());
        rv_history.setLayoutManager(new LinearLayoutManager(activity));
        adapter = new HistoryCollectorAdapter(activity, listDonatur);
        rv_history.setAdapter(adapter);

        loadHistory();

        return v;
    }

    private void loadHistory(){
        listDonatur.add(new HistoryDonaturModel(new DonaturModel("Leonardus Irfan", "Jl. Menur Raya 16", "085742089087"), "continuous", "biasa", 200000, new Date(), true));
        listDonatur.add(new HistoryDonaturModel(new DonaturModel("Bayu Mahendra", "Jl. Kasipah 19", "085742089087"), "single", "luar biasa", 1300000, new Date(), false));
        adapter.notifyDataSetChanged();
    }
}
