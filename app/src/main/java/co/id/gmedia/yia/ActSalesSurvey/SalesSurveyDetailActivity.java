package co.id.gmedia.yia.ActSalesSurvey;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import co.id.gmedia.coremodul.ApiVolley;
import co.id.gmedia.coremodul.DialogBox;
import co.id.gmedia.coremodul.SessionManager;
import co.id.gmedia.yia.Model.DonaturModel;
import co.id.gmedia.yia.R;
import co.id.gmedia.yia.Utils.AppRequestCallback;
import co.id.gmedia.yia.Utils.GoogleLocationManager;
import co.id.gmedia.yia.Utils.JSONBuilder;
import co.id.gmedia.yia.Utils.ServerURL;

public class SalesSurveyDetailActivity extends AppCompatActivity{

    private DonaturModel donatur;

    private TextView tv_latitude, tv_longitude;
    private EditText edt_nama, edt_alamat, edt_kontak, txt_jumlah_kaleng;
    private RadioButton rb_donasi_ya, rb_kaleng_ya;

    private SessionManager sessionManager;
    private DialogBox dialogBox;

    private GoogleLocationManager locationManager;
    private double lat = 0, lng = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales_survey_detail);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );

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

        setTitle("Detail Survey");
        initUI();

        if(getIntent().hasExtra("donatur")){
            Gson gson = new Gson();
            donatur = gson.fromJson(getIntent().getStringExtra("donatur"), DonaturModel.class);
            initDonatur();
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
        rb_kaleng_ya = findViewById(R.id.rb_kaleng_ya);
        rb_donasi_ya = findViewById(R.id.rb_donasi_ya);
        tv_latitude = findViewById(R.id.tv_latitude);
        tv_longitude = findViewById(R.id.tv_longitude);
        txt_jumlah_kaleng = findViewById(R.id.txt_jumlah_kaleng);
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

        findViewById(R.id.btn_simpan).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(donatur != null){
                    if(lat == 0 || lng == 0){
                        Toast.makeText(SalesSurveyDetailActivity.this, "Lokasi tidak terdekteksi, " +
                                "tidak bisa melanjutkan survey", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        simpanSurvey();
                    }
                }
            }
        });

        dialogBox = new DialogBox(this);
    }

    private void simpanSurvey(){
        dialogBox.showDialog(false);

        JSONBuilder body = new JSONBuilder();
        body.add("id_rk", donatur.getId());
        body.add("id_sales", sessionManager.getId());
        body.add("id_donatur", donatur.getId_donatur());
        body.add("status_donasi", rb_donasi_ya.isChecked()?1:0);
        body.add("lobi_kaleng", rb_kaleng_ya.isChecked()?"ya":"tidak");
        body.add("total_kaleng", rb_kaleng_ya.isChecked()?txt_jumlah_kaleng.getText().toString():"");
        body.add("latitude", lat);
        body.add("longitude", lng);

        new ApiVolley(this, body.create(), "POST", ServerURL.saveSurvey,
                new AppRequestCallback(new AppRequestCallback.ResponseListener() {
                    @Override
                    public void onSuccess(String response, String message) {
                        dialogBox.dismissDialog();
                        Toast.makeText(SalesSurveyDetailActivity.this, message, Toast.LENGTH_SHORT).show();

                        finish();
                    }

                    @Override
                    public void onEmpty(String message) {
                        dialogBox.dismissDialog();
                        Toast.makeText(SalesSurveyDetailActivity.this, message, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFail(String message) {
                        dialogBox.dismissDialog();
                        Toast.makeText(SalesSurveyDetailActivity.this, message, Toast.LENGTH_SHORT).show();
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
