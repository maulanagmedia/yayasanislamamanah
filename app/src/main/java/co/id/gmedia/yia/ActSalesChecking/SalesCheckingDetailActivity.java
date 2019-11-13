package co.id.gmedia.yia.ActSalesChecking;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fxn.pix.Options;
import com.fxn.pix.Pix;
import com.fxn.utility.ImageQuality;
import com.google.gson.Gson;

import org.json.JSONArray;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import co.id.gmedia.coremodul.ApiVolley;
import co.id.gmedia.coremodul.DialogBox;
import co.id.gmedia.coremodul.ImageUtils;
import co.id.gmedia.coremodul.PhotoModel;
import co.id.gmedia.coremodul.SessionManager;
import co.id.gmedia.yia.ActSalesBrosur.DetailCurrentPosActivity;
import co.id.gmedia.yia.ActSalesSosial.Adapter.ListPhotoAdapter;
import co.id.gmedia.yia.Model.DonaturModel;
import co.id.gmedia.yia.R;
import co.id.gmedia.yia.Utils.AppRequestCallback;
import co.id.gmedia.yia.Utils.GoogleLocationManager;
import co.id.gmedia.yia.Utils.JSONBuilder;
import co.id.gmedia.yia.Utils.ServerURL;

public class SalesCheckingDetailActivity extends AppCompatActivity{

    private Context context;
    private DonaturModel donatur;

    private TextView tv_latitude, tv_longitude;
    private EditText edt_nama, edt_alamat, edt_kontak, txt_jumlah_kaleng;
    private RadioButton rb_donasi_ya, rb_kaleng_ya, rb_kaleng_tidak;

    private SessionManager sessionManager;
    private DialogBox dialogBox;

    private GoogleLocationManager locationManager;
    private double lat = 0, lng = 0;
    private LinearLayout llBukaMap;

    //permission
    private String[] appPermission =  {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
    };
    private final int PERMIOSSION_REQUEST_CODE = 1240;

    private RelativeLayout rlPhoto;
    private RecyclerView rvPhoto, rv_PhotoDetail;
    private int imageRequestCode = 100;
    private List<PhotoModel> listPhoto;
    private List<PhotoModel> listPhotoDetail = new ArrayList<>();
    private ListPhotoAdapter adapterPhoto;
    private ListPhotoAdapter adapterPhotoDetail;
    private EditText edtKeterangan;
    private RadioGroup rgDonasi;
    private LinearLayout llKaleng;
    private RadioGroup rgKaleng;
    private LinearLayout llIsiKaleng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales_checking_detail);

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
        context = this;
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
        txt_jumlah_kaleng.setText(donatur.getKaleng());
        edtKeterangan.setText(donatur.getKeterangan());

        for(String url : donatur.getListUrlPhoto()){
            listPhotoDetail.add(new PhotoModel("", url));
        }

        adapterPhotoDetail.notifyDataSetChanged();
    }

    private void initUI() {

        edt_nama = findViewById(R.id.edt_nama);
        edt_alamat = findViewById(R.id.edt_alamat);
        edt_kontak = findViewById(R.id.edt_kontak);
        rb_kaleng_ya = findViewById(R.id.rb_kaleng_ya);
        rb_kaleng_tidak = findViewById(R.id.rb_kaleng_tidak);
        rb_donasi_ya = findViewById(R.id.rb_donasi_ya);
        tv_latitude = findViewById(R.id.tv_latitude);
        tv_longitude = findViewById(R.id.tv_longitude);
        txt_jumlah_kaleng = findViewById(R.id.txt_jumlah_kaleng);
        llBukaMap = (LinearLayout) findViewById(R.id.ll_buka_map);
        rgDonasi = (RadioGroup) findViewById(R.id.rg_donasi);
        llKaleng = (LinearLayout) findViewById(R.id.ll_kaleng);
        rgKaleng = (RadioGroup) findViewById(R.id.rg_kaleng);
        llIsiKaleng = (LinearLayout) findViewById(R.id.layout_kaleng);
        edtKeterangan = (EditText) findViewById(R.id.edt_keterangan);

        rlPhoto = (RelativeLayout) findViewById(R.id.rl_photo);
        rvPhoto = (RecyclerView) findViewById(R.id.rv_photo);
        rv_PhotoDetail = findViewById(R.id.rv_foto_detail);

        listPhoto = new ArrayList<>();
        LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        adapterPhoto = new ListPhotoAdapter(context, listPhoto);
        rvPhoto.setLayoutManager(layoutManager);
        rvPhoto.setAdapter(adapterPhoto);

        LinearLayoutManager layoutManagerDetail = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        adapterPhotoDetail = new ListPhotoAdapter(context, listPhotoDetail);
        rv_PhotoDetail.setLayoutManager(layoutManagerDetail);
        rv_PhotoDetail.setAdapter(adapterPhotoDetail);

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
                        Toast.makeText(SalesCheckingDetailActivity.this, "Lokasi tidak terdekteksi, " +
                                "tidak bisa melanjutkan survey", Toast.LENGTH_SHORT).show();
                    }
                    else{

                        AlertDialog dialog = new AlertDialog.Builder(context)
                                .setTitle("Konfirmasi")
                                .setMessage("Apakah anda yakin ingin menyimpan data?")
                                .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        simpanSurvey();
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

        llBukaMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context, DetailCurrentPosActivity.class);
                intent.putExtra("nama", donatur.getNama());
                intent.putExtra("alamat", donatur.getAlamat());
                intent.putExtra("lat", donatur.getLatitude());
                intent.putExtra("long", donatur.getLognitude());
                startActivity(intent);
            }
        });

        dialogBox = new DialogBox(this);

        rlPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Options options = Options.init()
                        .setRequestCode(imageRequestCode)                                    //Request code for activity results
                        .setCount(3)                                                         //Number of images to restict selection count
                        .setFrontfacing(false)                                               //Front Facing camera on start
                        .setImageQuality(ImageQuality.HIGH)                                  //Image Quality
                        .setScreenOrientation(Options.SCREEN_ORIENTATION_PORTRAIT)           //Orientaion
                        .setPath("/yia/images");                                             //Custom Path For Image Storage

                Pix.start(SalesCheckingDetailActivity.this, options);
            }
        });
    }

    private void simpanSurvey(){

        dialogBox.showDialog(false);

        JSONArray jFoto = new JSONArray();

        for(PhotoModel photo: listPhoto){

            jFoto.put(photo.getKeterangan());
        }

        JSONBuilder body = new JSONBuilder();
        body.add("id_rk", donatur.getId());
        body.add("nama", edt_nama.getText().toString());
        body.add("alamat", edt_alamat.getText().toString());
        body.add("kontak", edt_kontak.getText().toString());
        body.add("note", edtKeterangan.getText().toString());
        body.add("id_sales", sessionManager.getId());
        body.add("id_donatur", donatur.getId_donatur());
        body.add("status_donasi", rb_donasi_ya.isChecked() ? 1 : 2);
        body.add("lobi_kaleng", rb_kaleng_ya.isChecked()?"ya":"tidak");
        body.add("total_kaleng", rb_kaleng_ya.isChecked()?txt_jumlah_kaleng.getText().toString():"");
        body.add("latitude", lat);
        body.add("longitude", lng);
        body.add("foto", jFoto);

        new ApiVolley(this, body.create(), "POST", ServerURL.saveSurvey,
                new AppRequestCallback(new AppRequestCallback.ResponseListener() {
                    @Override
                    public void onSuccess(String response, String message) {
                        dialogBox.dismissDialog();
                        Toast.makeText(SalesCheckingDetailActivity.this, message, Toast.LENGTH_SHORT).show();

                        finish();
                    }

                    @Override
                    public void onEmpty(String message) {
                        dialogBox.dismissDialog();
                        Toast.makeText(SalesCheckingDetailActivity.this, message, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFail(String message) {
                        dialogBox.dismissDialog();
                        Toast.makeText(SalesCheckingDetailActivity.this, message, Toast.LENGTH_SHORT).show();
                    }
                }));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if(requestCode == GoogleLocationManager.ACTIVATE_LOCATION){

            if(resultCode == RESULT_OK){

                locationManager.startLocationUpdates();
            }
        }else if(resultCode == RESULT_OK){

            if (requestCode == imageRequestCode) {
                ArrayList<String> returnValue = data.getStringArrayListExtra(Pix.IMAGE_RESULTS);

                if(returnValue.size() > 0) {

                    ImageUtils ui = new ImageUtils();

                    for(String filePath : returnValue){

                        File f = new File(filePath);

                        Bitmap d = new BitmapDrawable(context.getResources(), f.getAbsolutePath()).getBitmap();

                        if(d != null){

                            listPhoto.add(new PhotoModel(f.getAbsolutePath(),  "", ImageUtils.convert(d)));
                            adapterPhoto.notifyDataSetChanged();
                        }
                    }
                }

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
