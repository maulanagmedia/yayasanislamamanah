package co.id.gmedia.yia.ActSalesChecking;


import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
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
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import co.id.gmedia.coremodul.ApiVolley;
import co.id.gmedia.coremodul.DialogBox;
import co.id.gmedia.coremodul.SessionManager;
import co.id.gmedia.yia.ActCollector.Adapter.JadwalKunjunganAdapter;
import co.id.gmedia.yia.Model.DonaturModel;
import co.id.gmedia.yia.R;
import co.id.gmedia.yia.Utils.AppRequestCallback;
import co.id.gmedia.yia.Utils.Converter;
import co.id.gmedia.yia.Utils.GoogleLocationManager;
import co.id.gmedia.yia.Utils.JSONBuilder;
import co.id.gmedia.yia.Utils.ServerURL;

/**
 * A simple {@link Fragment} subclass.
 */
public class CheckingJadwalFragment extends Fragment {

    private Activity activity;
    private JadwalKunjunganAdapter adapter;
    private List<DonaturModel> listDonatur = new ArrayList<>();

    private SessionManager session;
    private DialogBox dialogBox;

    private TextView txt_tanggal;
    private EditText edtSearch;
    private RecyclerView rv_jadwal;
    private View root;
    private ImageView ivSort;
    private GoogleLocationManager locationManager;
    private boolean isLocationReloaded;
    private double lat = 0, lng = 0;
    private boolean withLocation = false;
    private String keyword = "";

    public CheckingJadwalFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        activity = getActivity();
        root = inflater.inflate(R.layout.fragment_checking_jadwal, container, false);

        initUI();

        return root;
    }

    private void initUI() {

        edtSearch = (EditText) root.findViewById(R.id.edt_search);
        txt_tanggal = (TextView) root.findViewById(R.id.txt_tanggal);
        ivSort = (ImageView) root.findViewById(R.id.iv_sort);
        isLocationReloaded = false;
        keyword = "";

        rv_jadwal = (RecyclerView) root.findViewById(R.id.rv_jadwal);
        rv_jadwal.setItemAnimator(new DefaultItemAnimator());
        rv_jadwal.setLayoutManager(new LinearLayoutManager(activity));
        adapter = new JadwalKunjunganAdapter(activity, listDonatur);
        rv_jadwal.setAdapter(adapter);

        session = new SessionManager(activity);
        dialogBox = new DialogBox(activity);

        locationManager = new GoogleLocationManager((AppCompatActivity) activity, new GoogleLocationManager.LocationUpdateListener() {
            @Override
            public void onChange(Location location) {

                if(isLocationReloaded){

                    isLocationReloaded = false;
                    lat = location.getLatitude();
                    lng = location.getLongitude();
                    withLocation = true;
                    loadJadwal();
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

        edtSearch.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    keyword = edtSearch.getText().toString();
                    loadJadwal();
                    return true;
                }
                return false;
            }
        });

        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (edtSearch.getText().toString().isEmpty()) {
                    dialogBox.showDialog(false);
                    keyword = "";
                    loadJadwal();
                }
            }
        });
    }

    @Override
    public void onResume() {
        loadData();
        loadJadwal();

        super.onResume();

    }

    private void loadData(){
        txt_tanggal.setText(Converter.getDateString(new Date()));
    }

    private void loadJadwal(){
        dialogBox.showDialog(false);
        JSONBuilder body = new JSONBuilder();
        body.add("id_sales", session.getId());
        body.add("keyword", keyword);
        body.add("status", "1");
        if(withLocation){

            body.add("lat", lat);
            body.add("long", lng);
        }

        new ApiVolley(activity, body.create(), "POST", ServerURL.getJadwalKerjaSurvey,
                new AppRequestCallback(new AppRequestCallback.ResponseListener() {
                    @Override
                    public void onSuccess(String response, String message) {
                        dialogBox.dismissDialog();
                        Log.d(">>res",response);
                        try{
                            listDonatur.clear();
                            JSONArray object = new JSONArray(response);
                            for(int i = 0; i < object.length(); i++){
                                JSONObject donatur = object.getJSONObject(i);
                                JSONArray arrayFoto = donatur.getJSONArray("image");
                                List<String> listUrlFoto = new ArrayList<>();
                                for(int j = 0; j < arrayFoto.length(); j++){
                                    listUrlFoto.add(arrayFoto.getJSONObject(j).getString("image"));
                                }

                                DonaturModel donat = new DonaturModel(
                                        donatur.getString("id")
                                        ,donatur.getString("id_donatur")
                                        ,donatur.getString("nama")
                                        ,donatur.getString("alamat")
                                        ,donatur.getString("rt")
                                        ,donatur.getString("rw")
                                        ,donatur.getString("kontak")
                                        ,donatur.getString("wa")
                                        ,donatur.getString("total_kaleng")
                                        ,donatur.getString("lat")
                                        ,donatur.getString("long")
                                        ,donatur.getInt("status") == 0
                                        ,donatur.getString("note")
                                        , listUrlFoto);

                                donat.setKeterangan(donatur.getString("note"));
                                listDonatur.add(donat);
                            }

                            //Update teks jumlah di Activity
                            if(activity instanceof SalesCheckingActivity){
                                ((SalesCheckingActivity)activity).updateJumlahJadwal(object.length());
                            }

                            adapter.notifyDataSetChanged();
                        }
                        catch (JSONException e){
                            Log.e("json_log", e.getMessage());
                            View.OnClickListener clickListener = new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    dialogBox.dismissDialog();
                                    loadJadwal();
                                }
                            };

                            dialogBox.showDialog(clickListener, "Ulangi Proses",
                                    "Terjadi kesalahan saat mengambil data");
                        }
                    }

                    @Override
                    public void onEmpty(String message) {
                        listDonatur.clear();
                        adapter.notifyDataSetChanged();

                        //Update teks jumlah di Activity
                        if(activity instanceof SalesCheckingActivity){
                            ((SalesCheckingActivity)activity).updateJumlahJadwal(0);
                        }

                        dialogBox.dismissDialog();
                    }

                    @Override
                    public void onFail(String message) {

                        listDonatur.clear();
                        adapter.notifyDataSetChanged();

                        dialogBox.dismissDialog();
                        View.OnClickListener clickListener = new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialogBox.dismissDialog();
                                loadJadwal();
                            }
                        };

                        dialogBox.showDialog(clickListener, "Ulangi Proses", "Terjadi kesalahan saat mengambil data");
                    }
                }));
    }

    public void retryLocation(){
        locationManager.startLocationUpdates();
    }
}
