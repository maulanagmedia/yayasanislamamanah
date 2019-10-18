package co.id.gmedia.yia.ActCollector;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import co.id.gmedia.coremodul.ApiVolley;
import co.id.gmedia.coremodul.DialogBox;
import co.id.gmedia.coremodul.ItemValidation;
import co.id.gmedia.coremodul.OptionItem;
import co.id.gmedia.coremodul.SessionManager;
import co.id.gmedia.yia.ActSalesBrosur.Adapter.SearchableSpinnerDialogOptionAdapter;
import co.id.gmedia.yia.ActSalesBrosur.DetailCurrentPosActivity;
import co.id.gmedia.yia.Model.DonaturModel;
import co.id.gmedia.yia.R;
import co.id.gmedia.yia.Utils.AppRequestCallback;
import co.id.gmedia.yia.Utils.Converter;
import co.id.gmedia.yia.Utils.DialogFactory;
import co.id.gmedia.yia.Utils.GoogleLocationManager;
import co.id.gmedia.yia.Utils.JSONBuilder;
import co.id.gmedia.yia.Utils.ServerURL;

public class CollectorDonaturDetailActivity extends AppCompatActivity {

    private DonaturModel donatur;

    private TextView tv_latitude, tv_longitude;
    private EditText edt_nama, edt_alamat, edt_kontak, txt_jumlah_kaleng, txt_nominal;

    private Context context;
    private SessionManager session;
    private DialogBox dialogBox;

    private GoogleLocationManager locationManager;
    private double lat = 0, lng = 0;

    private String current = "";
    private LinearLayout llBukaMap;
    private ImageView ivKota, ivKecamatan, ivKelurahan;
    private TextView tvKota, tvKecamatan, tvKelurahan;
    private List<OptionItem> listKota = new ArrayList<>(), listKecamatan = new ArrayList<>(), listKeluarahan = new ArrayList<>();
    private String selectedKota = "", selectedKecamatan = "", selectedKelurahan = "";
    private ItemValidation iv = new ItemValidation();
    private RelativeLayout rlLokasi;
    private LinearLayout llNominal;
    private boolean isEdit = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collector_donatur_detail);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );

        setTitle("Detail Donasi");
        context = this;
        session = new SessionManager(context);
        locationManager = new GoogleLocationManager(this, new GoogleLocationManager.LocationUpdateListener() {
            @Override
            public void onChange(Location location) {
                lat = location.getLatitude();
                lng = location.getLongitude();

                tv_latitude.setText(String.valueOf(lat));
                tv_longitude.setText(String.valueOf(lng));
            }
        });
        locationManager.startLocationUpdates();

        initUI();
        initEvent();
        getDataKota();

        if(getIntent().hasExtra("donatur")){
            Gson gson = new Gson();
            donatur = gson.fromJson(getIntent().getStringExtra("donatur"), DonaturModel.class);
            initDonatur();
        }

        Bundle bundle = getIntent().getExtras();
        if(bundle != null){

            isEdit = bundle.getBoolean("edit", false);
            if(isEdit){

                rlLokasi.setVisibility(View.VISIBLE);
                llNominal.setVisibility(View.GONE);

                setTitle("Detail Donatur");
                selectedKota = donatur.getIdKota();
                selectedKecamatan = donatur.getIdKecamatan();
                selectedKelurahan = donatur.getIdKelurahan();

                tvKota.setText(donatur.getKota());
                tvKecamatan.setText(donatur.getKecamatan());
                tvKelurahan.setText(donatur.getKelurahan());
            }else{

                rlLokasi.setVisibility(View.GONE);
                llNominal.setVisibility(View.VISIBLE);
            }
        }

    }

    private void initDonatur(){
        edt_nama.setText(donatur.getNama());
        edt_alamat.setText(donatur.getAlamat());
        edt_kontak.setText(donatur.getKontak());

    }

    private void initUI() {

        edt_nama = findViewById(R.id.edt_nama);
        edt_alamat = findViewById(R.id.edt_alamat);
        edt_kontak = findViewById(R.id.edt_kontak);
        tv_latitude = findViewById(R.id.tv_latitude);
        tv_longitude = findViewById(R.id.tv_longitude);
        txt_jumlah_kaleng = findViewById(R.id.txt_jumlah_kaleng);
        txt_nominal = findViewById(R.id.txt_nominal);
        llBukaMap = (LinearLayout) findViewById(R.id.ll_buka_map);

        rlLokasi = (RelativeLayout) findViewById(R.id.rl_lokasi);
        llNominal = (LinearLayout) findViewById(R.id.ll_nominal);

        ivKota = (ImageView) findViewById(R.id.iv_kota);
        tvKota = (TextView) findViewById(R.id.tv_kota);
        ivKecamatan = (ImageView) findViewById(R.id.iv_kecamatan);
        tvKecamatan = (TextView) findViewById(R.id.tv_kecamatan);
        ivKelurahan = (ImageView) findViewById(R.id.iv_kelurahan);
        tvKelurahan = (TextView) findViewById(R.id.tv_kelurahan);

        llBukaMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context, DetailCurrentPosActivity.class);
                startActivity(intent);
            }
        });

        txt_nominal.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(!s.toString().equals(current)){
                    txt_nominal.removeTextChangedListener(this);

                    String cleanString = s.toString().replaceAll("[Rp,.\\s]", "");
                    String formatted;
                    if(!cleanString.isEmpty()){
                        double parsed = Double.parseDouble(cleanString);
                        formatted = Converter.doubleToRupiah(parsed);
                    }
                    else{
                        formatted = "";
                    }

                    current = formatted;
                    txt_nominal.setText(formatted);
                    txt_nominal.setSelection(formatted.length());
                    txt_nominal.addTextChangedListener(this);
                }
            }
        });

        findViewById(R.id.btn_simpan).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(donatur != null){
                    if(lat == 0 || lng == 0){
                        Toast.makeText(CollectorDonaturDetailActivity.this, "Lokasi tidak terdekteksi, " +
                                "tidak bisa melanjutkan survey", Toast.LENGTH_SHORT).show();
                    }
                    else{

                        AlertDialog dialog = new AlertDialog.Builder(context)
                                .setTitle("Konfirmasi")
                                .setMessage("Apakah anda yakin ingin menyimpan data?")
                                .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        jemputInfaq();
                                    }
                                })
                                .setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                })
                                .show();

                    }
                }
            }
        });

        dialogBox = new DialogBox(this);
    }

    private void initEvent() {


        ivKota.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(listKota.size() > 0){

                    showDialog(listKota, 1);
                }
            }
        });

        ivKecamatan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(listKota.size() > 0){

                    showDialog(listKecamatan, 2);
                }
            }
        });

        ivKelurahan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(listKota.size() > 0){

                    showDialog(listKeluarahan, 3);
                }
            }
        });
    }

    private void showDialog(final List<OptionItem> listData, final int type){

        final Dialog dialogChooser = DialogFactory.getInstance().createDialog((Activity) context,
                R.layout.dialog_searchable_spinner, 90, 70);

        final EditText txt_search = dialogChooser.findViewById(R.id.txt_search);

        final SearchableSpinnerDialogOptionAdapter.ChooserListener listener = new SearchableSpinnerDialogOptionAdapter.ChooserListener() {
            @Override
            public void onSelected(String value, String text) {

                if(type == 1){

                    if(dialogChooser != null) {

                        try {
                            dialogChooser.dismiss();
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }

                    tvKota.setText(text);
                    selectedKota = value;
                    getDataKecamatan(value);

                }else if(type == 2){

                    if(dialogChooser != null) {

                        try {
                            dialogChooser.dismiss();
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }

                    tvKecamatan.setText(text);
                    selectedKecamatan = value;
                    getDataKelurahan(value);

                }else if (type == 3){

                    if(dialogChooser != null) {

                        try {
                            dialogChooser.dismiss();
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }

                    tvKelurahan.setText(text);
                    selectedKelurahan= value;
                }
            }
        };

        final RecyclerView rv_items = dialogChooser.findViewById(R.id.rv_items);
        final SearchableSpinnerDialogOptionAdapter[] dialogAdapter = {new SearchableSpinnerDialogOptionAdapter((Activity) context,
                listData, listener)};

        txt_search.setHint("Keyword");
        txt_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String search = s.toString();

                String keyword = txt_search.getText().toString().toLowerCase();

                List<OptionItem> newList = new ArrayList<>();

                for(OptionItem item : listData){

                    if(item.getText().toLowerCase().contains(keyword)){

                        newList.add(item);
                    }
                }

                dialogAdapter[0] = new SearchableSpinnerDialogOptionAdapter((Activity)context, newList, listener);
                rv_items.setItemAnimator(new DefaultItemAnimator());
                rv_items.setLayoutManager(new LinearLayoutManager((Activity) context));
                rv_items.setAdapter(dialogAdapter[0]);
                dialogAdapter[0].notifyDataSetChanged();
            }
        });

        rv_items.setItemAnimator(new DefaultItemAnimator());
        rv_items.setLayoutManager(new LinearLayoutManager((Activity) context));
        rv_items.setAdapter(dialogAdapter[0]);

        dialogChooser.show();
    }

    private void getDataKota() {

        dialogBox.showDialog(false);
        JSONObject jBody = new JSONObject();

        new ApiVolley(context, jBody, "GET", ServerURL.getKota, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {

                dialogBox.dismissDialog();
                try {

                    JSONObject response = new JSONObject(result);
                    String status = response.getJSONObject("metadata").getString("status");
                    String message = response.getJSONObject("metadata").getString("message");
                    listKota.clear();

                    if(iv.parseNullInteger(status) == 200){

                        JSONArray ja = response.getJSONArray("response");
                        for(int i = 0; i < ja.length(); i ++){

                            JSONObject jo = ja.getJSONObject(i);
                            listKota.add(
                                    new OptionItem(
                                            jo.getString("id")
                                            ,jo.getString("kota")
                                    )
                            );
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    View.OnClickListener clickListener = new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            dialogBox.dismissDialog();
                            getDataKota();

                        }
                    };

                    dialogBox.showDialog(clickListener, "Ulangi Proses", "Terjadi kesalahan saat mengambil data");
                }

                /*adapterKota.notifyDataSetChanged();

                if(listKota.size() > 0){

                    spKota.setSelection(0);
                }*/
            }

            @Override
            public void onError(String result) {
                dialogBox.dismissDialog();
                View.OnClickListener clickListener = new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        dialogBox.dismissDialog();
                        getDataKota();

                    }
                };

                dialogBox.showDialog(clickListener, "Ulangi Proses", "Terjadi kesalahan saat mengambil data");
            }
        });
    }

    private void getDataKecamatan(final String idKota) {

        dialogBox.showDialog(false);

        selectedKecamatan = "";
        tvKecamatan.setText("");
        selectedKelurahan = "";
        tvKelurahan.setText("");

        JSONObject jBody = new JSONObject();
        try {
            jBody.put("id_kota", idKota);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        new ApiVolley(context, jBody, "POST", ServerURL.getKecamatan, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {

                dialogBox.dismissDialog();
                try {

                    JSONObject response = new JSONObject(result);
                    String status = response.getJSONObject("metadata").getString("status");
                    String message = response.getJSONObject("metadata").getString("message");
                    listKecamatan.clear();

                    if(iv.parseNullInteger(status) == 200){

                        JSONArray ja = response.getJSONArray("response");
                        for(int i = 0; i < ja.length(); i ++){

                            JSONObject jo = ja.getJSONObject(i);
                            listKecamatan.add(
                                    new OptionItem(
                                            jo.getString("id")
                                            ,jo.getString("kecamatan")
                                    )
                            );
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    View.OnClickListener clickListener = new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            dialogBox.dismissDialog();
                            getDataKecamatan(idKota);

                        }
                    };

                    dialogBox.showDialog(clickListener, "Ulangi Proses", "Terjadi kesalahan saat mengambil data");
                }

                /*adapterKecamatan.notifyDataSetChanged();

                if(listKecamatan.size() > 0){

                    spKecamatan.setSelection(0);
                }*/
            }

            @Override
            public void onError(String result) {
                dialogBox.dismissDialog();
                View.OnClickListener clickListener = new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        dialogBox.dismissDialog();
                        getDataKecamatan(idKota);

                    }
                };

                dialogBox.showDialog(clickListener, "Ulangi Proses", "Terjadi kesalahan saat mengambil data");
            }
        });
    }

    private void getDataKelurahan(final String idKecamatan) {

        dialogBox.showDialog(false);

        selectedKelurahan = "";
        tvKelurahan.setText("");

        JSONObject jBody = new JSONObject();
        try {
            jBody.put("id_kecamatan", idKecamatan);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        new ApiVolley(context, jBody, "POST", ServerURL.getKelurahan, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {

                dialogBox.dismissDialog();
                try {

                    JSONObject response = new JSONObject(result);
                    String status = response.getJSONObject("metadata").getString("status");
                    String message = response.getJSONObject("metadata").getString("message");
                    listKeluarahan.clear();

                    if(iv.parseNullInteger(status) == 200){

                        JSONArray ja = response.getJSONArray("response");
                        for(int i = 0; i < ja.length(); i ++){

                            JSONObject jo = ja.getJSONObject(i);
                            listKeluarahan.add(
                                    new OptionItem(
                                            jo.getString("id")
                                            ,jo.getString("kelurahan")
                                    )
                            );
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    View.OnClickListener clickListener = new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            dialogBox.dismissDialog();
                            getDataKelurahan(idKecamatan);

                        }
                    };

                    dialogBox.showDialog(clickListener, "Ulangi Proses", "Terjadi kesalahan saat mengambil data");
                }

                /*adapterKelurahan.notifyDataSetChanged();

                if(listKeluarahan.size() > 0){

                    spKelurahan.setSelection(0);
                }*/
            }

            @Override
            public void onError(String result) {

                dialogBox.dismissDialog();
                View.OnClickListener clickListener = new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        dialogBox.dismissDialog();
                        getDataKelurahan(idKecamatan);

                    }
                };

                dialogBox.showDialog(clickListener, "Ulangi Proses", "Terjadi kesalahan saat mengambil data");
            }
        });
    }

    private void jemputInfaq(){

        dialogBox.showDialog(false);
        JSONBuilder body = new JSONBuilder();

        String url = ServerURL.saveCollector;

        if(isEdit){

            url = ServerURL.updateDonatur;
            body.add("id", donatur.getId_donatur());
            body.add("nama", edt_nama.getText().toString());
            body.add("alamat", edt_alamat.getText().toString());
            body.add("kontak", edt_kontak.getText().toString());
            body.add("kota", selectedKota);
            body.add("kecamatan", selectedKecamatan);
            body.add("kelurahan", selectedKelurahan);
        }else{

            body.add("id_sales", session.getId());
            body.add("id_template", donatur.getId());
            body.add("id_donatur", donatur.getId_donatur());
            body.add("nominal", txt_nominal.getText().toString().replaceAll("[Rp,.\\s]", ""));
            body.add("kaleng_kembali", txt_jumlah_kaleng.getText().toString());
            body.add("latitude", tv_latitude.getText().toString());
            body.add("longitude", tv_longitude.getText().toString());
        }

        new ApiVolley(context, body.create(), "POST", url,
                new AppRequestCallback(new AppRequestCallback.ResponseListener() {
                    @Override
                    public void onSuccess(String response, String message) {

                        dialogBox.dismissDialog();
                        Toast.makeText(CollectorDonaturDetailActivity.this, message, Toast.LENGTH_SHORT).show();
                        finish();
                    }

                    @Override
                    public void onEmpty(String message) {
                        dialogBox.dismissDialog();
                        Toast.makeText(CollectorDonaturDetailActivity.this, message, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFail(String message) {
                        dialogBox.dismissDialog();
                        Toast.makeText(CollectorDonaturDetailActivity.this, message, Toast.LENGTH_SHORT).show();
                    }
                }));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == GoogleLocationManager.ACTIVATE_LOCATION){
            if(resultCode == RESULT_OK){
                locationManager.startLocationUpdates();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == GoogleLocationManager.PERMISSION_LOCATION){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                locationManager.startLocationUpdates();
            }
            else{
                Toast.makeText(this, "Ijin ditolak, gagal mendeteksi lokasi", Toast.LENGTH_SHORT).show();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {

        super.onBackPressed();
        overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right);
    }
}
