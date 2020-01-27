package co.id.gmedia.yia.ActSalesSosial;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.fxn.pix.Options;
import com.fxn.pix.Pix;
import com.fxn.utility.ImageQuality;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import co.id.gmedia.coremodul.ApiVolley;
import co.id.gmedia.coremodul.CustomModel;
import co.id.gmedia.coremodul.DialogBox;
import co.id.gmedia.coremodul.SessionManager;
import co.id.gmedia.yia.ActCollector.Adapter.FotoAdapter;
import co.id.gmedia.yia.ActSalesBrosur.DetailCurrentPosActivity;
import co.id.gmedia.yia.R;
import co.id.gmedia.yia.Utils.AppRequestCallback;
import co.id.gmedia.yia.Utils.Converter;
import co.id.gmedia.yia.Utils.GoogleLocationManager;
import co.id.gmedia.yia.Utils.JSONBuilder;
import co.id.gmedia.yia.Utils.ServerURL;

public class SalesSosialJadwalDetailActivity extends AppCompatActivity {

    private CustomModel donatur;
    private List<String> listGambarDetail = new ArrayList<>();
    private List<String> listGambarUpload = new ArrayList<>();
    private FotoAdapter adapter;

    private TextView tv_latitude, tv_longitude;
    private EditText edt_nama, edt_alamat, edt_kontak, txt_jumlah_kaleng, edtRt, edtRw;
    private RadioButton rb_donasi_ya, rb_kaleng_ya;

    private SessionManager sessionManager;
    private DialogBox dialogBox;

    private GoogleLocationManager locationManager;
    private double lat = 0, lng = 0;
    private LinearLayout llBukaMap;
    private Context context;
    private EditText edtKeterangan;
    private RadioGroup rgDonasi;
    private LinearLayout llKaleng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales_sosial_jadwal_detail);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );

        setTitle("Calon Donatur");
        context = this;
        sessionManager = new SessionManager(this);
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

        if(getIntent().hasExtra("donatur")){
            Gson gson = new Gson();
            donatur = gson.fromJson(getIntent().getStringExtra("donatur"), CustomModel.class);
            initDonatur();
        }
    }

    private void initDonatur(){

        edt_nama.setText(donatur.getItem2());
        edt_alamat.setText(donatur.getItem3());
        edt_kontak.setText(donatur.getItem4());
        edtKeterangan.setText(donatur.getItem10());
        edtRt.setText(donatur.getItem11());
        edtRw.setText(donatur.getItem12());

        try{
            JSONArray array_gambar = new JSONArray(donatur.getItem9());
            for(int i = 0; i < array_gambar.length(); i++){
                Log.d("gambar_log", array_gambar.getJSONObject(i).getString("image"));
                listGambarDetail.add(array_gambar.getJSONObject(i).getString("image"));
            }
        }
        catch (JSONException e){
            Log.e("parse_error_log", e.getMessage());
        }
    }

    private void initUI() {

        edt_nama = findViewById(R.id.edt_nama);
        edt_alamat = findViewById(R.id.edt_alamat);
        edtRt = findViewById(R.id.edt_rt);
        edtRw = findViewById(R.id.edt_rw);
        edt_kontak = findViewById(R.id.edt_kontak);
        rb_kaleng_ya = findViewById(R.id.rb_kaleng_ya);
        rb_donasi_ya = findViewById(R.id.rb_donasi_ya);
        rgDonasi = (RadioGroup) findViewById(R.id.rg_donasi);
        llKaleng = (LinearLayout) findViewById(R.id.ll_kaleng);
        tv_latitude = findViewById(R.id.tv_latitude);
        tv_longitude = findViewById(R.id.tv_longitude);
        txt_jumlah_kaleng = findViewById(R.id.txt_jumlah_kaleng);
        llBukaMap = (LinearLayout) findViewById(R.id.ll_buka_map);
        edtKeterangan = (EditText) findViewById(R.id.edt_keterangan);

        rb_kaleng_ya.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    findViewById(R.id.layout_kaleng).setVisibility(View.VISIBLE);
                }
                else{
                    findViewById(R.id.layout_kaleng).setVisibility(View.GONE);
                }
            }
        });

        rgDonasi.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                if(checkedId == rb_donasi_ya.getId()){

                    llKaleng.setVisibility(View.VISIBLE);
                }else{

                    llKaleng.setVisibility(View.GONE);
                }
            }
        });

        findViewById(R.id.btn_simpan).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(donatur != null){
                    if(lat == 0 || lng == 0){
                        Toast.makeText(SalesSosialJadwalDetailActivity.this, "Lokasi tidak terdekteksi, " +
                                "tidak bisa melanjutkan survey", Toast.LENGTH_SHORT).show();
                    }
                    else{

                        if(edt_nama.getText().toString().length() == 0){

                            edt_nama.setError("Harap diisi");
                            edt_nama.requestFocus();
                            return;
                        }else{

                            edt_nama.setError(null);
                        }

                        if(edt_alamat.getText().toString().length() == 0){

                            edt_alamat.setError("Harap diisi");
                            edt_alamat.requestFocus();
                            return;
                        }else{

                            edt_alamat.setError(null);
                        }

                        if(edtRt.getText().toString().length() == 0){

                            edtRt.setError("RWHarap diisi");
                            edtRt.requestFocus();
                            return;
                        }else{

                            edtRt.setError(null);
                        }

                        if(edtRw.getText().toString().length() == 0){

                            edtRw.setError("RW Harap diisi");
                            edtRw.requestFocus();
                            return;
                        }else{

                            edtRw.setError(null);
                        }

                        /*if(edt_kontak.getText().toString().length() == 0){

                            edt_kontak.setError("Harap diisi");
                            edt_kontak.requestFocus();
                            return;
                        }else{
                            edt_kontak.setError(null);
                        }*/

                        //Cek jumlah kaleng jika menggunakan lobi kaleng (jika rb_kaleng_ya check)
                        if(rb_kaleng_ya.isChecked()){
                            if(txt_jumlah_kaleng.getText().toString().isEmpty()){
                                txt_jumlah_kaleng.setError("Harap diisi");
                                txt_jumlah_kaleng.requestFocus();
                                return;
                            }
                            else if(Integer.parseInt(txt_jumlah_kaleng.getText().toString()) < 1){
                                txt_jumlah_kaleng.setError("Jumlah minimal 1");
                                txt_jumlah_kaleng.requestFocus();
                                return;
                            }
                            else{
                                txt_jumlah_kaleng.setError(null);
                            }
                        }else{
                            txt_jumlah_kaleng.setError(null);
                        }

                        AlertDialog dialog = new AlertDialog.Builder(context)
                                .setTitle("Konfirmasi")
                                .setMessage("Apakah anda yakin ingin menyimpan data?")
                                .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        simpanData();
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

        RecyclerView rv_foto_detail = findViewById(R.id.rv_foto_detail);
        rv_foto_detail.setItemAnimator(new DefaultItemAnimator());
        rv_foto_detail.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        adapter = new FotoAdapter(this, listGambarDetail, false);
        rv_foto_detail.setAdapter(adapter);

        RecyclerView rv_foto = findViewById(R.id.rv_foto);
        rv_foto.setItemAnimator(new DefaultItemAnimator());
        rv_foto.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        adapter = new FotoAdapter(this, listGambarUpload);
        rv_foto.setAdapter(adapter);

        findViewById(R.id.img_gambar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Options options = Options.init()
                        .setRequestCode(ServerURL.PIX_REQUEST_CODE)
                        .setCount(10)
                        .setFrontfacing(false)
                        .setImageQuality(ImageQuality.REGULAR)
                        .setScreenOrientation(Options.SCREEN_ORIENTATION_PORTRAIT);
                Pix.start((FragmentActivity) SalesSosialJadwalDetailActivity.this, options);
            }
        });

        llBukaMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context, DetailCurrentPosActivity.class);
                intent.putExtra("nama", donatur.getItem2());
                intent.putExtra("alamat", donatur.getItem3());
                intent.putExtra("lat", donatur.getItem7());
                intent.putExtra("long", donatur.getItem8());
                startActivity(intent);
            }
        });

        dialogBox = new DialogBox(this);
    }

    private void simpanData(){
        dialogBox.showDialog(false);

        JSONBuilder body = new JSONBuilder();
        body.add("id_rk", donatur.getItem1());
        body.add("nama", edt_nama.getText().toString());
        body.add("alamat", edt_alamat.getText().toString());
        body.add("rt", edtRt.getText().toString());
        body.add("rw", edtRw.getText().toString());
        body.add("kontak", edt_kontak.getText().toString());
        body.add("note", edtKeterangan.getText().toString());
        body.add("id_sales", sessionManager.getId());
        body.add("donasi", rb_donasi_ya.isChecked() ? 1 : 0);
        body.add("lobi_kaleng", rb_kaleng_ya.isChecked()? "ya" : "tidak");
        body.add("total_kaleng", rb_kaleng_ya.isChecked()?txt_jumlah_kaleng.getText().toString():"");
        body.add("latitude", lat);
        body.add("longitude", lng);

        ArrayList<String> listFoto = new ArrayList<>();

        for(String path : listGambarUpload){
            File image = new File(path);
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            Bitmap bitmap = BitmapFactory.decodeFile(image.getAbsolutePath(), bmOptions);
            String bitmap_string = Converter.convertToBase64(bitmap);
            listFoto.add(bitmap_string);
        }

        body.add("image", new JSONArray(listFoto));

        new ApiVolley(this, body.create(), "POST", ServerURL.saveSosial,
                new AppRequestCallback(new AppRequestCallback.ResponseListener() {
                    @Override
                    public void onSuccess(String response, String message) {
                        dialogBox.dismissDialog();
                        Toast.makeText(SalesSosialJadwalDetailActivity.this, message, Toast.LENGTH_SHORT).show();

                        finish();
                    }

                    @Override
                    public void onEmpty(String message) {
                        dialogBox.dismissDialog();
                        Toast.makeText(SalesSosialJadwalDetailActivity.this, message, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFail(String message) {
                        dialogBox.dismissDialog();
                        Toast.makeText(SalesSosialJadwalDetailActivity.this, message, Toast.LENGTH_SHORT).show();
                    }
                }));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == Activity.RESULT_OK && requestCode == ServerURL.PIX_REQUEST_CODE) {
            if(data != null){
                ArrayList<String> returnValue = data.getStringArrayListExtra(Pix.IMAGE_RESULTS);
                for(String s : returnValue){
                    listGambarUpload.add(Uri.fromFile(new File(s)).getPath());
                }

                adapter.notifyDataSetChanged();
            }
        }
        else if(requestCode == GoogleLocationManager.ACTIVATE_LOCATION){
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
