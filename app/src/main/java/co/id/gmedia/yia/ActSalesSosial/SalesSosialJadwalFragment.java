package co.id.gmedia.yia.ActSalesSosial;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import co.id.gmedia.coremodul.CustomModel;
import co.id.gmedia.coremodul.DialogBox;
import co.id.gmedia.coremodul.FormatItem;
import co.id.gmedia.coremodul.ItemValidation;
import co.id.gmedia.yia.ActSalesSosial.Adapter.ListJadwalSSAdapter;
import co.id.gmedia.yia.R;

public class SalesSosialJadwalFragment extends Fragment {

    private View root;
    private Context context;
    private DialogBox dialogBox;
    private List<CustomModel> listData = new ArrayList<>();
    private ListJadwalSSAdapter adapter;
    private TextView tvTanggal;
    private ListView lvJadwal;
    private ItemValidation iv = new ItemValidation();

    public SalesSosialJadwalFragment() {
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
        root = inflater.inflate(R.layout.fragment_sales_sosial_jadwal, container, false);
        context = root.getContext();
        initUI();
        return root;
    }

    private void initUI() {

        tvTanggal = (TextView) root.findViewById(R.id.tv_tanggal);
        lvJadwal = (ListView) root.findViewById(R.id.lv_jadwal);

        String curdate = iv.getCurrentDate(FormatItem.formatDate1);
        tvTanggal.setText(curdate);

        adapter = new ListJadwalSSAdapter((Activity) context, listData);
        lvJadwal.setAdapter(adapter);

        listData.add(new CustomModel("1", "Rokhim", "Jl. Mataram", "080989999", "2019-09-20 10:10:10", "1"));
        listData.add(new CustomModel("2", "Victor", "Jl. Pandanaran", "080989999", "2019-09-20 10:10:10", "1"));
        listData.add(new CustomModel("3", "Maul", "Jl. Pamularsih", "", "2019-09-20 10:10:10", "1"));
        listData.add(new CustomModel("4", "Bayu", "Jl. Singosari", "", "2019-09-20 10:10:10", "1"));
        listData.add(new CustomModel("5", "Bayu", "Jl. Singosari", "", "2019-09-20 10:10:10", "0"));
        listData.add(new CustomModel("6", "Bayu", "Jl. Singosari", "", "2019-09-20 10:10:10", "0"));
        listData.add(new CustomModel("7", "Bayu", "Jl. Singosari", "", "2019-09-20 10:10:10", "0"));
        listData.add(new CustomModel("8", "Bayu", "Jl. Singosari", "", "2019-09-20 10:10:10", "0"));

        adapter.notifyDataSetChanged();
    }
}
