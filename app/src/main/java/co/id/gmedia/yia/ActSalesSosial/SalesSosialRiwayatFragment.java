package co.id.gmedia.yia.ActSalesSosial;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import co.id.gmedia.coremodul.ApiVolley;
import co.id.gmedia.coremodul.CustomModel;
import co.id.gmedia.coremodul.DialogBox;
import co.id.gmedia.coremodul.FormatItem;
import co.id.gmedia.coremodul.ItemValidation;
import co.id.gmedia.coremodul.SessionManager;
import co.id.gmedia.yia.ActSalesSosial.Adapter.ListRiwayatSSAdapter;
import co.id.gmedia.yia.HomeSocialSalesActivity;
import co.id.gmedia.yia.R;
import co.id.gmedia.yia.Utils.AppRequestCallback;
import co.id.gmedia.yia.Utils.Converter;
import co.id.gmedia.yia.Utils.DateTimeChooser;
import co.id.gmedia.yia.Utils.JSONBuilder;
import co.id.gmedia.yia.Utils.ServerURL;

public class SalesSosialRiwayatFragment extends Fragment {

    private View root;
    private Context context;
    private SessionManager session;
    private RelativeLayout rlDate1, rlDate2;
    private String dateFrom = Converter.DToString(new Date()),
            dateTo = Converter.DToString(new Date());
    private TextView tvDate1, tvDate2;
    private DialogBox dialogBox;
    private ListView lvRiwayat;
    private ListRiwayatSSAdapter adapter;
    private List<CustomModel> listData = new ArrayList<>();
    private EditText edtSearch;
    private TextView tvDonasiYa, tvDonasiTidak;
    private ItemValidation iv = new ItemValidation();

    public SalesSosialRiwayatFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        root = inflater.inflate(R.layout.fragment_list_riwayat_s, container, false);

        context = root.getContext();
        session = new SessionManager(context);
        initUI();
        initEvent();
        return root;
    }

    private void initUI() {

        rlDate1 = (RelativeLayout) root.findViewById(R.id.rl_date1);
        rlDate2 = (RelativeLayout) root.findViewById(R.id.rl_date2);
        tvDate1 = (TextView) root.findViewById(R.id.tv_date1);
        tvDate2 = (TextView) root.findViewById(R.id.tv_date2);
        edtSearch = (EditText) root.findViewById(R.id.edt_search);
        lvRiwayat = (ListView) root.findViewById(R.id.lv_riwayat);
        tvDonasiYa = (TextView) root.findViewById(R.id.tv_donasi_ya);
        tvDonasiTidak = (TextView) root.findViewById(R.id.tv_donasi_tidak);

        adapter = new ListRiwayatSSAdapter((Activity) context, listData);
        lvRiwayat.setAdapter(adapter);

        tvDate1.setText(Converter.getSlashedDateString(Converter.stringDToDate(dateFrom)));
        tvDate2.setText(Converter.getSlashedDateString(Converter.stringDToDate(dateTo)));

        tvDate1.setText(dateFrom);
        tvDate2.setText(dateTo);
        dialogBox = new DialogBox(context);

        initData();
    }

    private void initEvent() {
        rlDate1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DateTimeChooser.getInstance().selectDate(context, new DateTimeChooser.DateTimeListener() {
                    @Override
                    public void onFinished(String dateString) {
                        dateFrom = dateString;
                        tvDate1.setText(Converter.getSlashedDateString(Converter.stringDToDate(dateString)));
                    }
                });
            }
        });

        rlDate2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DateTimeChooser.getInstance().selectDate(context, new DateTimeChooser.DateTimeListener() {
                    @Override
                    public void onFinished(String dateString) {
                        dateTo = dateString;
                        tvDate2.setText(Converter.getSlashedDateString(Converter.stringDToDate(dateString)));
                    }
                });
            }
        });

        root.findViewById(R.id.btn_proses).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initData();
            }
        });
    }

    private void initData() {
        dialogBox.showDialog(false);
        JSONBuilder body = new JSONBuilder();
        body.add("id_sales", session.getId());
        body.add("tgl_awal", dateFrom);
        body.add("tgl_akhir", dateTo);
        body.add("keyword", edtSearch.getText().toString());
        body.add("status", "0");

        tvDonasiYa.setText("0");
        tvDonasiTidak.setText("0");

        new ApiVolley(context, body.create(), "POST", ServerURL.getRencanaKerjaSosial,
                new AppRequestCallback(new AppRequestCallback.ResponseListener() {
                    @Override
                    public void onSuccess(String response, String message) {
                        dialogBox.dismissDialog();
                        Log.d(">>res",response);
                        int totalYa = 0, totalTidak = 0;
                        try{
                            listData.clear();
                            JSONArray obj = new JSONArray(response);
                            for(int i = 0; i < obj.length(); i++){
                                JSONObject jadwal = obj.getJSONObject(i);
                                listData.add(new CustomModel(
                                        jadwal.getString("id")
                                        ,jadwal.getString("id_donatur")
                                        ,jadwal.getString("nama") // item3
                                        ,jadwal.getString("alamat") // item4
                                        ,jadwal.getString("kontak") // item5
                                        ,jadwal.getString("tgl") // item6
                                        ,jadwal.getString("status").equals("0")?"1":"0" // item7
                                        ,jadwal.getString("ket_status") // item8
                                        ,jadwal.getString("status_donasi") // item9
                                        ,jadwal.getString("note") // item10
                                        ,jadwal.getString("status_donasi_checking") // item11
                                        ,jadwal.getString("user_checking") // item12
                                        ,jadwal.getString("id_kota") // item13
                                        ,jadwal.getString("id_kec") // item14
                                        ,jadwal.getString("id_kel") // item15
                                        ,jadwal.getString("kota") // item16
                                        ,jadwal.getString("kecamatan") // item17
                                        ,jadwal.getString("kelurahan") // item18
                                        ,jadwal.getString("wa") // item19
                                        )
                                );

                                if (jadwal.getString("status_donasi").toUpperCase().equals("YA")){
                                    totalYa++;
                                } else {
                                    totalTidak++;
                                }
                            }

                            //Update teks jumlah di Activity
                            if(context instanceof HomeSocialSalesActivity){
                                ((HomeSocialSalesActivity)context).updateJumlahRiwayat(obj.length());
                            }

                            adapter.notifyDataSetChanged();

                            tvDonasiYa.setText(iv.ChangeToCurrencyFormat(totalYa));
                            tvDonasiTidak.setText(iv.ChangeToCurrencyFormat(totalTidak));
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

                        listData.clear();
                        adapter.notifyDataSetChanged();

                        //Update teks jumlah di Activity
                        if(context instanceof HomeSocialSalesActivity){
                            ((HomeSocialSalesActivity)context).updateJumlahRiwayat(0);
                        }

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
