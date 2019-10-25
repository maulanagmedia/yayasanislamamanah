package co.id.gmedia.yia.ActCollector;


import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

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

    public CollectorJadwalFragment() {
        // Required empty public constructor
    }

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

        rv_jadwal.setItemAnimator(new DefaultItemAnimator());
        rv_jadwal.setLayoutManager(new LinearLayoutManager(activity));
        btnRekapSetoran = (Button) v.findViewById(R.id.btn_rekap_setoran);
        adapter = new JadwalKunjunganCollectorAdapter(activity, listDonatur);
        rv_jadwal.setAdapter(adapter);

        isLocationReloaded = false;

        initEvent();

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadData(false);
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

                filterData();
            }
        });

        cbk2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                filterData();
            }
        });

        cbk3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                filterData();
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

    private void filterData(){

        final List<DonaturModel> newFilteredDonatur = new ArrayList<>();

        int terkunjungi = 0, tidakTerkunjungi = 0;

        for(DonaturModel item : listDonatur){

            if(item.getRk().equals("rk1") && cbk1.isChecked()){

                newFilteredDonatur.add(item);
                if(item.isDikunjungi()){
                    terkunjungi++;
                }else{
                    tidakTerkunjungi++;
                }
            }else if(item.getRk().equals("rk2") && cbk2.isChecked()){

                newFilteredDonatur.add(item);
                if(item.isDikunjungi()){
                    terkunjungi++;
                }else{
                    tidakTerkunjungi++;
                }
            }else if(item.getRk().equals("rk3") && cbk3.isChecked()){

                newFilteredDonatur.add(item);
                if(item.isDikunjungi()){
                    terkunjungi++;
                }else{
                    tidakTerkunjungi++;
                }
            }
        }

        tvKunjungi.setText(iv.ChangeToCurrencyFormat(terkunjungi));
        tvBelumKunjungi.setText(iv.ChangeToCurrencyFormat(tidakTerkunjungi));
        if(activity instanceof CollectorActivity){
            ((CollectorActivity)activity).updateJumlah(newFilteredDonatur.size());
        }

        adapter = new JadwalKunjunganCollectorAdapter(activity, newFilteredDonatur);
        rv_jadwal.setAdapter(adapter);
    }

    private void loadData(final boolean withLocation){
        //txt_tanggal.setText(Converter.getDateString(new Date()));
        dialogBox.showDialog(false);
        JSONBuilder body = new JSONBuilder();
        body.add("collector", new SessionManager(activity).getId());

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

                            int terkunjungi = 0, tidakTerkunjungi = 0;
                            listDonatur.clear();
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
