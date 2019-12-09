package co.id.gmedia.yia.ActCollector;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
import co.id.gmedia.yia.Utils.JSONBuilder;
import co.id.gmedia.yia.Utils.ServerURL;

public class CollectorJadwalFragment extends Fragment {

    private Activity activity;
    private JadwalKunjunganCollectorAdapter adapter;
    private List<DonaturModel> listDonatur = new ArrayList<>();
    private ItemValidation iv = new ItemValidation();

    //private TextView txt_tanggal;

    private DialogBox dialogBox;
    private Button btnRekapSetoran;
    private CheckBox cbk1, cbk2, cbk3;
    private RecyclerView rv_jadwal;
    private TextView tvKunjungi, tvBelumKunjungi;
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
    public CollectorJadwalFragment() {
        // Required empty public constructor
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        activity = getActivity();
        View v = inflater.inflate(R.layout.fragment_collector_jadwal, container, false);

        dialogBox = new DialogBox(activity);
        //txt_tanggal = v.findViewById(R.id.txt_tanggal);

        rv_jadwal = v.findViewById(R.id.rv_jadwal);
        cbk1 = (CheckBox) v.findViewById(R.id.cb_k1);
        cbk2 = (CheckBox) v.findViewById(R.id.cb_k2);
        cbk3 = (CheckBox) v.findViewById(R.id.cb_k3);
        tvKunjungi = (TextView) v.findViewById(R.id.tv_dikunjungi);
        tvBelumKunjungi = (TextView) v.findViewById(R.id.tv_belum_dikunjungi);
        ivSort = (ImageView) v.findViewById(R.id.iv_sort);
        edtSearch = (EditText) v.findViewById(R.id.edt_search);

        rv_jadwal.setItemAnimator(new DefaultItemAnimator());
        rv_jadwal.setLayoutManager(new LinearLayoutManager(activity));
        btnRekapSetoran = (Button) v.findViewById(R.id.btn_rekap_setoran);
        adapter = new JadwalKunjunganCollectorAdapter(activity, listDonatur);
        rv_jadwal.setAdapter(adapter);

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
//                    Toast.makeText(getContext(),edtSearch.getText().toString(),Toast.LENGTH_SHORT).show();
                    InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    search = edtSearch.getText().toString();
                    loadData(false);
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
        loadData(false);
    }
    private void filter(String text) {
        ArrayList<DonaturModel> filteredList = new ArrayList<>();

        for (DonaturModel item : listDonatur) {
            if(item.getNama().toLowerCase().contains(text.toLowerCase()))
                filteredList.add(item);
        }
        adapter.filterList(filteredList);
    }

    private void initEvent() {

        btnRekapSetoran.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(activity, ListRekapSetoranActivity.class);
                startActivity(intent);
            }
        });

        cbk1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                rk1 = String.valueOf(isChecked ? 1 : 0);
//                Toast.makeText(getContext(),"rk1 "+rk1,Toast.LENGTH_SHORT).show();
                loadData(false);
//                filterData(search);
            }
        });

        cbk2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                filterData(search);
                rk2 = String.valueOf(isChecked ? 1 : 0);
                loadData(false);

            }
        });

        cbk3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                rk3 = String.valueOf(isChecked ? 1 : 0);
                loadData(false);
//                filterData(search);
            }
        });

        locationManager = new GoogleLocationManager((AppCompatActivity) activity, new GoogleLocationManager.LocationUpdateListener() {
            @Override
            public void onChange(Location location) {

                if(isLocationReloaded){

                    isLocationReloaded = false;
                    lat = location.getLatitude();
                    lng = location.getLongitude();
                    loadData(true);
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

//    private void filterData(String s){
//
//        final List<DonaturModel> newFilteredDonatur = new ArrayList<>();
//
//        int terkunjungi = 0, tidakTerkunjungi = 0;
//
//        for(DonaturModel item : listDonatur){
//
//            if(item.getRk().equals("rk1") && cbk1.isChecked()){
//                if(!s.equalsIgnoreCase("")){
//                    if(item.getNama().toLowerCase().contains(s.toLowerCase()))
//                        newFilteredDonatur.add(item);
//                }
//                newFilteredDonatur.add(item);
//                if(item.isDikunjungi()){
//                    terkunjungi++;
//                }else{
//                    tidakTerkunjungi++;
//                }
//            }else if(item.getRk().equals("rk2") && cbk2.isChecked()){
//
//                newFilteredDonatur.add(item);
//                if(item.isDikunjungi()){
//                    terkunjungi++;
//                }else{
//                    tidakTerkunjungi++;
//                }
//            }else if(item.getRk().equals("rk3") && cbk3.isChecked()){
//
//                newFilteredDonatur.add(item);
//                if(item.isDikunjungi()){
//                    terkunjungi++;
//                }else{
//                    tidakTerkunjungi++;
//                }
//            }
//        }
//
//        tvKunjungi.setText(iv.ChangeToCurrencyFormat(terkunjungi));
//        tvBelumKunjungi.setText(iv.ChangeToCurrencyFormat(tidakTerkunjungi));
//        if(activity instanceof CollectorActivity){
//            ((CollectorActivity)activity).updateJumlah(newFilteredDonatur.size());
//        }
//
//        adapter = new JadwalKunjunganCollectorAdapter(activity, newFilteredDonatur);
//        rv_jadwal.setAdapter(adapter);
//    }

    private void loadData(final boolean withLocation){
        adapter.clearAdapter();
        //txt_tanggal.setText(Converter.getDateString(new Date()));
        dialogBox.showDialog(false);
        JSONBuilder body = new JSONBuilder();
//        Toast.makeText(getContext(),"rk1 "+rk1,Toast.LENGTH_SHORT).show();
//        Toast.makeText(getContext(),"rk2 "+rk2,Toast.LENGTH_SHORT).show();
//        Toast.makeText(getContext(),"rk3 "+rk3,Toast.LENGTH_SHORT).show();
        body.add("collector", new SessionManager(activity).getId());
        body.add("keyword",search);
        body.add("rk1",rk1);
        body.add("rk2",rk2);
        body.add("rk3",rk3);

        tvKunjungi.setText("0");
        tvBelumKunjungi.setText("0");

        if(withLocation){

            body.add("latitude", lat);
            body.add("longitude", lng);
        }

        new ApiVolley(activity, body.create(), "POST", ServerURL.getJadwalCollector,
                new AppRequestCallback(new AppRequestCallback.ResponseListener() {
                    @Override
                    public void onSuccess(String response, String message) {
                        dialogBox.dismissDialog();
                        try{
                            listDonatur.clear();
                            int terkunjungi = 0, tidakTerkunjungi = 0;
                            JSONArray object = new JSONArray(response);
                            for(int i = 0; i < object.length(); i++){
                                JSONObject donatur = object.getJSONObject(i);
                                listDonatur.add(new DonaturModel(
                                        donatur.getString("id")
                                        ,donatur.getString("id_donatur")
                                        ,donatur.getString("nama")
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

                                if(donatur.getString("status").equals("0")){

                                    terkunjungi++;
                                }else{

                                    tidakTerkunjungi++;
                                }
                            }

                            adapter.notifyDataSetChanged();

                            if(activity instanceof CollectorActivity){
                                ((CollectorActivity)activity).updateJumlah(object.length());
                            }

                            tvKunjungi.setText(iv.ChangeToCurrencyFormat(terkunjungi));
                            tvBelumKunjungi.setText(iv.ChangeToCurrencyFormat(tidakTerkunjungi));
                        }
                        catch (JSONException e){
                            Log.e("json_log", e.getMessage());
                            View.OnClickListener clickListener = new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    dialogBox.dismissDialog();
                                    loadData(withLocation);
                                }
                            };

                            dialogBox.showDialog(clickListener, "Ulangi Proses",
                                    "Terjadi kesalahan saat mengambil data");
                        }
                    }

                    @Override
                    public void onEmpty(String message) {
                        dialogBox.dismissDialog();
                        if(activity instanceof CollectorActivity){
                            ((CollectorActivity)activity).updateJumlah(0);
                        }
                    }

                    @Override
                    public void onFail(String message) {
                        dialogBox.dismissDialog();
                        View.OnClickListener clickListener = new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialogBox.dismissDialog();
                                loadData(withLocation);

                            }
                        };

                        dialogBox.showDialog(clickListener, "Ulangi Proses",
                                "Terjadi kesalahan saat mengambil data");
                    }
                }));
    }

    public void retryLocation(){
        locationManager.startLocationUpdates();
    }
}
