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

import co.id.gmedia.yia.ActCollector.Adapter.TambahanDonaturFotoAdapter;
import co.id.gmedia.yia.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class TambahanDonaturFragment extends Fragment {

    private Activity activity;
    private TambahanDonaturFotoAdapter adapter;
    private List<String> listGambar = new ArrayList<>();

    public TambahanDonaturFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        activity = getActivity();
        View v =inflater.inflate(R.layout.fragment_tambahan_donatur, container, false);

        RecyclerView rv_foto = v.findViewById(R.id.rv_foto);
        rv_foto.setItemAnimator(new DefaultItemAnimator());
        rv_foto.setLayoutManager(new LinearLayoutManager(activity, RecyclerView.HORIZONTAL, false));
        adapter = new TambahanDonaturFotoAdapter(activity, listGambar);
        rv_foto.setAdapter(adapter);

        loadGambar();

        return v;
    }

    private void loadGambar(){

    }
}
