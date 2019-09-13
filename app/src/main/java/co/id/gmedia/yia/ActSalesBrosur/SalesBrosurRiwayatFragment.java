package co.id.gmedia.yia.ActSalesBrosur;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import co.id.gmedia.coremodul.CustomModel;
import co.id.gmedia.coremodul.DialogBox;
import co.id.gmedia.yia.ActSalesBrosur.Adapter.ListSalesBrosurAdapter;
import co.id.gmedia.yia.R;


public class SalesBrosurRiwayatFragment extends Fragment {

    private View root;
    private Context context;
    private DialogBox dialogBox;
    private ListView lvCalon;
    private List<CustomModel> listData = new ArrayList<>();
    private ListSalesBrosurAdapter adapter;

    public SalesBrosurRiwayatFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_sales_brosur_riwayat, container, false);
        context = root.getContext();
        initUI();
        return root;
    }

    private void initUI() {

        dialogBox = new DialogBox(context);
        lvCalon = (ListView) root.findViewById(R.id.lv_calon);

        adapter = new ListSalesBrosurAdapter((Activity) context, listData);
        lvCalon.setAdapter(adapter);

        listData.add(new CustomModel("1", "Rokhim", "Jl. Mataram", "080989999", "2019-09-20 10:10:10"));
        listData.add(new CustomModel("2", "Victor", "Jl. Pandanaran", "080989999", "2019-09-20 10:10:10"));
        listData.add(new CustomModel("3", "Maul", "Jl. Pamularsih", "", "2019-09-20 10:10:10"));
        listData.add(new CustomModel("4", "Bayu", "Jl. Singosari", "", "2019-09-20 10:10:10"));
        listData.add(new CustomModel("5", "Bayu", "Jl. Singosari", "", "2019-09-20 10:10:10"));
        listData.add(new CustomModel("6", "Bayu", "Jl. Singosari", "", "2019-09-20 10:10:10"));
        listData.add(new CustomModel("7", "Bayu", "Jl. Singosari", "", "2019-09-20 10:10:10"));
        listData.add(new CustomModel("8", "Bayu", "Jl. Singosari", "", "2019-09-20 10:10:10"));

        adapter.notifyDataSetChanged();
    }
}
