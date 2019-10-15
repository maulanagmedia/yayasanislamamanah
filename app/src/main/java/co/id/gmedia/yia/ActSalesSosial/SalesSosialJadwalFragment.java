package co.id.gmedia.yia.ActSalesSosial;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import co.id.gmedia.coremodul.ApiVolley;
import co.id.gmedia.coremodul.CustomModel;
import co.id.gmedia.coremodul.DialogBox;
import co.id.gmedia.coremodul.FormatItem;
import co.id.gmedia.coremodul.ItemValidation;
import co.id.gmedia.coremodul.SessionManager;
import co.id.gmedia.yia.ActSalesSosial.Adapter.ListJadwalSSAdapter;
import co.id.gmedia.yia.R;
import co.id.gmedia.yia.Utils.AppRequestCallback;
import co.id.gmedia.yia.Utils.Converter;
import co.id.gmedia.yia.Utils.JSONBuilder;
import co.id.gmedia.yia.Utils.ServerURL;

public class SalesSosialJadwalFragment extends Fragment {

    private View root;
    private Context context;
    private DialogBox dialogBox;
    private List<CustomModel> listData = new ArrayList<>();
    private ListJadwalSSAdapter adapter;
    private TextView tvTanggal;
    private ListView lvJadwal;
    private ItemValidation iv = new ItemValidation();
    private RelativeLayout rlDate1, rlDate2;
    private TextView tvDate1, tvDate2;
    private EditText edtSearch;
    private SessionManager session;
    private String dateFrom = "", dateTo = "";

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
        session = new SessionManager(context);
        initUI();

        return root;
    }

    private void initUI() {

        tvTanggal = (TextView) root.findViewById(R.id.tv_tanggal);
        lvJadwal = (ListView) root.findViewById(R.id.lv_jadwal);
        rlDate1 = (RelativeLayout) root.findViewById(R.id.rl_date1);
        rlDate2 = (RelativeLayout) root.findViewById(R.id.rl_date2);
        tvDate1 = (TextView) root.findViewById(R.id.tv_date1);
        tvDate2 = (TextView) root.findViewById(R.id.tv_date2);
        edtSearch = (EditText) root.findViewById(R.id.edt_search);

        dateFrom = iv.sumDate(iv.getCurrentDate(FormatItem.formatDateDisplay), -1, FormatItem.formatDateDisplay) ;
        dateTo = iv.getCurrentDate(FormatItem.formatDateDisplay);

        tvDate1.setText(dateFrom);
        tvDate2.setText(dateTo);
        dialogBox = new DialogBox(context);

        String curdate = iv.getCurrentDate(FormatItem.formatDate1);
        tvTanggal.setText(curdate);

        adapter = new ListJadwalSSAdapter((Activity) context, listData);
        lvJadwal.setAdapter(adapter);

        /*listData.add(new CustomModel("1", "Rokhim", "Jl. Mataram", "080989999", "2019-09-20 10:10:10", "1"));
        listData.add(new CustomModel("2", "Victor", "Jl. Pandanaran", "080989999", "2019-09-20 10:10:10", "1"));
        listData.add(new CustomModel("3", "Maul", "Jl. Pamularsih", "", "2019-09-20 10:10:10", "1"));
        listData.add(new CustomModel("4", "Bayu", "Jl. Singosari", "", "2019-09-20 10:10:10", "1"));
        listData.add(new CustomModel("5", "Bayu", "Jl. Singosari", "", "2019-09-20 10:10:10", "0"));
        listData.add(new CustomModel("6", "Bayu", "Jl. Singosari", "", "2019-09-20 10:10:10", "0"));
        listData.add(new CustomModel("7", "Bayu", "Jl. Singosari", "", "2019-09-20 10:10:10", "0"));
        listData.add(new CustomModel("8", "Bayu", "Jl. Singosari", "", "2019-09-20 10:10:10", "0"));*/

        adapter.notifyDataSetChanged();
    }

    @Override
    public void onResume() {
        initData();
        super.onResume();
    }

    private void initData() {
        dialogBox.showDialog(false);
        JSONBuilder body = new JSONBuilder();
        body.add("id_sales", session.getId());
        body.add("tgl_awal", Converter.DToString(new Date()));
        body.add("tgl_akhir", Converter.DToString(new Date()));
        body.add("keyword", edtSearch.getText().toString());
        body.add("status", "");

        new ApiVolley(context, body.create(), "POST", ServerURL.getRencanaKerjaSosial,
                new AppRequestCallback(new AppRequestCallback.ResponseListener() {
                    @Override
                    public void onSuccess(String response, String message) {
                        dialogBox.dismissDialog();
                        try{
                            listData.clear();
                            JSONArray obj = new JSONArray(response);
                            for(int i = 0; i < obj.length(); i++){
                                JSONObject jadwal = obj.getJSONObject(i);
                                listData.add(new CustomModel(jadwal.getString("id"),
                                        jadwal.getString("nama"), jadwal.getString("alamat"),
                                        jadwal.getString("kontak"), "tanggal",
                                        jadwal.getString("status").equals("0")?"1":"0"));
                            }

                            adapter.notifyDataSetChanged();
                        }
                        catch (JSONException e){
                            dialogBox.dismissDialog();
                            View.OnClickListener clickListener = new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                    dialogBox.dismissDialog();
                                    initData();

                                }
                            };

                            dialogBox.showDialog(clickListener, "Ulangi Proses", "Terjadi kesalahan saat mengambil data");
                        }
                    }

                    @Override
                    public void onEmpty(String message) {
                        dialogBox.dismissDialog();
                    }

                    @Override
                    public void onFail(String message) {
                        dialogBox.dismissDialog();
                        View.OnClickListener clickListener = new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                dialogBox.dismissDialog();
                                initData();

                            }
                        };

                        dialogBox.showDialog(clickListener, "Ulangi Proses", "Terjadi kesalahan saat mengambil data");
                    }
                }));
    }
}
