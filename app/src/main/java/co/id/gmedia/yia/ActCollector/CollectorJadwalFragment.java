package co.id.gmedia.yia.ActCollector;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import co.id.gmedia.coremodul.ApiVolley;
import co.id.gmedia.coremodul.CustomModel;
import co.id.gmedia.coremodul.DialogBox;
import co.id.gmedia.coremodul.ItemValidation;
import co.id.gmedia.coremodul.SessionManager;
import co.id.gmedia.yia.ActCollector.Adapter.JadwalKunjunganCollectorAdapter;
import co.id.gmedia.yia.Model.DonaturModel;
import co.id.gmedia.yia.R;
import co.id.gmedia.yia.Utils.AppRequestCallback;
import co.id.gmedia.yia.Utils.GoogleLocationManager;
import co.id.gmedia.yia.Utils.IsLoading;
import co.id.gmedia.yia.Utils.JSONBuilder;
import co.id.gmedia.yia.Utils.ServerURL;

public class CollectorJadwalFragment extends Fragment {
    private static final String TAG = "MainActivity";

    private Activity activity;
    Context context;
    private JadwalKunjunganCollectorAdapter adapter;
    private List<DonaturModel> listDonatur = new ArrayList<>();
    private ItemValidation iv = new ItemValidation();

    //private TextView txt_tanggal;

    private DialogBox dialogBox;
    private Button btnRekapSetoran;
    private CheckBox cbk1, cbk2, cbk3;
    private ListView rv_jadwal;
    private LinearLayoutManager linearLayoutManager;
//    private TextView tvKunjungi, tvBelumKunjungi;
    private ImageView ivSort;
    private GoogleLocationManager locationManager;
    private boolean isLocationReloaded;
    private double lat = 0, lng = 0;

    private EditText edtSearch;
    private String search="";
    private String rk1="1";
    private String rk2="1";
    private String rk3="1";
    private Boolean location =false;
    private int start = 0, count = 10;
    private View footerList;
    private boolean isLoading = false;
    private int total_data=0;
    private boolean filterlocation=false;

    public CollectorJadwalFragment() {
        // Required empty public constructor
    }

    @SuppressLint({"ClickableViewAccessibility", "InflateParams"})
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        activity = getActivity();
        context = getContext();
        View v = inflater.inflate(R.layout.fragment_collector_jadwal, container, false);

        dialogBox = new DialogBox(activity);

        rv_jadwal = v.findViewById(R.id.rv_jadwal);
        cbk1 = (CheckBox) v.findViewById(R.id.cb_k1);
        cbk2 = (CheckBox) v.findViewById(R.id.cb_k2);
        cbk3 = (CheckBox) v.findViewById(R.id.cb_k3);
//        tvKunjungi = (TextView) v.findViewById(R.id.tv_dikunjungi);
//        tvBelumKunjungi = (TextView) v.findViewById(R.id.tv_belum_dikunjungi);
        ivSort = (ImageView) v.findViewById(R.id.iv_sort);
        edtSearch = (EditText) v.findViewById(R.id.edt_search);

        btnRekapSetoran = (Button) v.findViewById(R.id.btn_rekap_setoran);

        LayoutInflater li = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        footerList = li.inflate(R.layout.footer_list, null);
        adapter = new JadwalKunjunganCollectorAdapter(context, listDonatur);
        rv_jadwal.addFooterView(footerList);
        rv_jadwal.setAdapter(adapter);
        rv_jadwal.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {
                int threshold = 1;
                int countMerchant = rv_jadwal.getCount();
                int visiblePosition =rv_jadwal.getLastVisiblePosition();

                if (i == SCROLL_STATE_IDLE) {
                    if ( visiblePosition >= countMerchant - threshold && !isLoading) {
                        isLoading = true;
                        rv_jadwal.addFooterView(footerList);
                        start += count;
                        loadData();
                    }
                }
            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {

            }
        });

        isLocationReloaded = false;

        initEvent();
        edtSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edtSearch.setFocusable(true);
                edtSearch.setFocusableInTouchMode(true);
            }
        });

        edtSearch.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    search = edtSearch.getText().toString();
                    loadData();
                    return true;
                }
                return false;
            }
        });

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadData();
    }

    private void initEvent() {

        btnRekapSetoran.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context, ListRekapSetoranActivity.class);
                startActivity(intent);
            }
        });

        cbk1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                rk1 = String.valueOf(isChecked ? 1 : 0);
                loadData();
                totalData();
            }
        });

        cbk2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                rk2 = String.valueOf(isChecked ? 1 : 0);
                loadData();
                totalData();
            }
        });

        cbk3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                rk3 = String.valueOf(isChecked ? 1 : 0);
                loadData();
                totalData();
            }
        });

        locationManager = new GoogleLocationManager((AppCompatActivity) context, new GoogleLocationManager.LocationUpdateListener() {
            @Override
            public void onChange(Location location) {

                if(isLocationReloaded){

                    isLocationReloaded = false;
                    lat = location.getLatitude();
                    lng = location.getLongitude();
                    filterlocation = true;
                    loadData();
                    totalData();
                }

            }
        });

        locationManager.startLocationUpdates();

        ivSort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                isLocationReloaded = true;
                retryLocation();
            }
        });
    }

    private void loadData(){
        Toast.makeText(activity, "Start "+start, Toast.LENGTH_SHORT).show();
        totalData();
        isLoading = true;
//        start = 0;

        if(start == 0){
            dialogBox.showDialog(false);
        }

        dialogBox.showDialog(false);
        JSONBuilder body = new JSONBuilder();
        body.add("collector", new SessionManager(activity).getId());
        body.add("keyword",search);
        body.add("rk1",rk1);
        body.add("rk2",rk2);
        body.add("rk3",rk3);
        body.add("start", String.valueOf(start));
        body.add("count", String.valueOf(count));

//        tvKunjungi.setText("0");
//        tvBelumKunjungi.setText("0");

        if(filterlocation){
            body.add("latitude", lat);
            body.add("longitude", lng);
        }
        new ApiVolley(activity, body.create(), "POST", ServerURL.getJadwalCollector2,
                new AppRequestCallback(new AppRequestCallback.ResponseListener() {
                    @Override
                    public void onSuccess(String response, String message) {
                        dialogBox.dismissDialog();
                        rv_jadwal.removeFooterView(footerList);
                        isLoading=false;
                        if(start == 0){
                            listDonatur.clear();
                        }
                        try{

//                            int terkunjungi = 0, tidakTerkunjungi = 0;
                            JSONArray object = new JSONArray(response);

                            for(int i = 0; i < object.length(); i++){
                                JSONObject donatur = object.getJSONObject(i);
                                listDonatur.add(new DonaturModel(
                                        object.getJSONObject(i).getString("id")
                                        ,object.getJSONObject(i).getString("id_donatur")
                                        ,object.getJSONObject(i).getString("nama")
                                        ,donatur.getString("alamat")
                                        ,donatur.getString("kontak")
                                        ,donatur.getString("lat")
                                        ,donatur.getString("long")
                                        ,donatur.getString("total_kaleng")
                                        ,donatur.getString("rk")
                                        ,donatur.getString("id_kota")
                                        ,donatur.getString("id_kecamatan")
                                        ,donatur.getString("id_kelurahan")
                                        ,donatur.getString("kota")
                                        ,donatur.getString("kecamatan")
                                        ,donatur.getString("kelurahan")
                                        ,donatur.getString("status").equals("0")
                                ));

//                                if(donatur.getString("status").equals("0")){
//
//                                    terkunjungi++;
//                                }else{
//
//                                    tidakTerkunjungi++;
//                                }
                            }

                            if(activity instanceof CollectorActivity){
                                ((CollectorActivity)activity).updateJumlah(total_data);
                            }

//                            tvKunjungi.setText(iv.ChangeToCurrencyFormat(terkunjungi));
//                            tvBelumKunjungi.setText(iv.ChangeToCurrencyFormat(tidakTerkunjungi));
//                            adapter.notifyDataSetChanged();

                        }
                        catch (JSONException e){
                            Log.e("json_log", e.getMessage());
                            View.OnClickListener clickListener = new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    dialogBox.dismissDialog();
                                    loadData();
                                }
                            };

                            dialogBox.showDialog(clickListener, "Ulangi Proses",
                                    "Terjadi kesalahan saat mengambil data");
                        }
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onEmpty(String message) {
                        dialogBox.dismissDialog();
                        isLoading=false;
//                        adapter.notifyDataSetChanged();
//                        if (rk1.equalsIgnoreCase("0") && rk2.equalsIgnoreCase("0")  && rk3.equalsIgnoreCase("0") ) {
//                            adapter.clearAdapter();
//                        }
                        rv_jadwal.removeFooterView(footerList);
                        if(activity instanceof CollectorActivity){
                            ((CollectorActivity)activity).updateJumlah(total_data);
                        }
                    }

                    @Override
                    public void onFail(String message) {
                        isLoading=false;
                        dialogBox.dismissDialog();
                        rv_jadwal.removeFooterView(footerList);
                        View.OnClickListener clickListener = new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialogBox.dismissDialog();
                                loadData();

                            }
                        };

                        dialogBox.showDialog(clickListener, "Ulangi Proses",
                                "Terjadi kesalahan saat mengambil data");
                    }
                }));
    }

    private void totalData(){
        dialogBox.showDialog(false);
        JSONBuilder body = new JSONBuilder();
        body.add("collector", new SessionManager(activity).getId());
        body.add("keyword",search);
        body.add("rk1",rk1);
        body.add("rk2",rk2);
        body.add("rk3",rk3);

        if(filterlocation){
            body.add("latitude", lat);
            body.add("longitude", lng);
        }

        new ApiVolley(activity, body.create(), "POST", ServerURL.getTotalKunjungan,
                new AppRequestCallback(new AppRequestCallback.ResponseListener() {
                    @Override
                    public void onSuccess(String response, String message) {
                        dialogBox.dismissDialog();
                        try{
                            Log.d("response>>",String.valueOf(response));
                            JSONObject obj = new JSONObject(response);
                            total_data = obj.getInt("total");

                        }
                        catch (JSONException e){
                            Log.e("json_log", e.getMessage());
                        }
                    }

                    @Override
                    public void onEmpty(String message) {
                    }

                    @Override
                    public void onFail(String message) {
                    }
                }));
    }

    public void retryLocation(){
        locationManager.startLocationUpdates();
    }
}
