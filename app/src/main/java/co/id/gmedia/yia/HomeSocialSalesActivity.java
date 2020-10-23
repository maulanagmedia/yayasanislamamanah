package co.id.gmedia.yia;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fxn.pix.Pix;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import co.id.gmedia.coremodul.ApiVolley;
import co.id.gmedia.coremodul.DialogBox;
import co.id.gmedia.coremodul.ImageUtils;
import co.id.gmedia.coremodul.ItemValidation;
import co.id.gmedia.coremodul.SessionManager;
import co.id.gmedia.yia.ActAkun.DetailAkunActivity;
import co.id.gmedia.yia.ActAkun.RequestActivity;
import co.id.gmedia.yia.ActNotifikasi.ListNotificationActivity;
import co.id.gmedia.yia.ActSalesSosial.DonaturLSosialFragment;
import co.id.gmedia.yia.ActSalesSosial.SalesSosialJadwalFragment;
import co.id.gmedia.yia.ActSalesSosial.SalesSosialRiwayatFragment;
import co.id.gmedia.yia.Utils.AppRequestCallback;
import co.id.gmedia.yia.Utils.GoogleLocationManager;
import co.id.gmedia.yia.Utils.JSONBuilder;
import co.id.gmedia.yia.Utils.ServerURL;
import co.id.gmedia.yia.Utils.TopCropCircularImageView;

public class HomeSocialSalesActivity extends AppCompatActivity {

    private CollapsingToolbarLayout collapsingToolbarLayout;
    private LinearLayout llInfo, llRiwayat;
    private Context context;
    private TextView tvjadwalKunjungan, tvRiwayat, tvDonatur, tvTitle1, tvTitle2, tvTitle3;
    private View vInfo, vRiwayat, vDonatur;
    private TopCropCircularImageView img_foto;
    private TextView tvAdmin;
    private SessionManager session;
    private LinearLayout llDonatur;
    private DialogBox dialogBox;
    private ItemValidation iv = new ItemValidation();
    private ImageView ivBackground;
    private ImageUtils iu = new ImageUtils();

    //permission
    private String[] appPermission =  {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
    };
    private final int PERMIOSSION_REQUEST_CODE = 1240;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_social_sales);

        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        context = this;
        dialogBox = new DialogBox(context);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {

            if (bundle.getBoolean("exit", false)) {

                finish();
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        }

        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.ctl_home);
        collapsingToolbarLayout.setTitle("Home");

        collapsingToolbarLayout.setCollapsedTitleTextColor(
                ContextCompat.getColor(this, R.color.colorWhite));
        collapsingToolbarLayout.setExpandedTitleColor(
                ContextCompat.getColor(this, R.color.colorPrimary));

        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.appbar);
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = true;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    collapsingToolbarLayout.setTitle("Home");
                    isShow = true;
                } else if(isShow) {
                    collapsingToolbarLayout.setTitle(" ");
                    isShow = false;
                }
            }
        });

        initUI();
        initEvent();
    }

    private boolean checkPermission(){

        List<String> permissionList = new ArrayList<>();
        for (String perm : appPermission) {

            if (ContextCompat.checkSelfPermission(context, perm) != PackageManager.PERMISSION_GRANTED){

                permissionList.add(perm);
            }
        }

        if (!permissionList.isEmpty()) {

            ActivityCompat.requestPermissions((Activity) context, permissionList.toArray(new String[permissionList.size()]), PERMIOSSION_REQUEST_CODE);

            return  false;
        }

        return  true;
    }

    @Override
    protected void onResume() {
        initAkun();
        super.onResume();

        if (!checkPermission()){

            checkPermission();
        }
    }

    private void initAkun(){
        tvAdmin.setText(session.getNama());
        if(session.getFoto().charAt(session.getFoto().length() - 1) != '/'){
            ImageUtils imageUtils = new ImageUtils();
            imageUtils.LoadRealImage(session.getFoto(), img_foto);
        }
    }

    public void updateJumlahJadwal(int jumlah){
        tvTitle1.setText(String.valueOf(jumlah));
    }

    public void updateJumlahRiwayat(int jumlah){
        tvTitle2.setText(String.valueOf(jumlah));
    }

    private void initUI() {

        llInfo = (LinearLayout) findViewById(R.id.ll_info);
        llRiwayat = (LinearLayout) findViewById(R.id.ll_riwayat);
        llDonatur = (LinearLayout) findViewById(R.id.ll_donatur);
        tvjadwalKunjungan = (TextView) findViewById(R.id.tv_jadwal_kunjungan);
        tvRiwayat = (TextView) findViewById(R.id.tv_riwayat);
        tvDonatur= (TextView) findViewById(R.id.tv_donatur);
        tvTitle1 = (TextView) findViewById(R.id.tv_title1);
        tvTitle2 = (TextView) findViewById(R.id.tv_title2);
        tvTitle3 = (TextView) findViewById(R.id.tv_title3);
        vInfo = (View) findViewById(R.id.v_info);
        vRiwayat = (View) findViewById(R.id.v_riwayat);
        vDonatur = (View) findViewById(R.id.v_donatur);
        tvAdmin = (TextView) findViewById(R.id.tv_admin);
        img_foto = findViewById(R.id.img_foto);
        ivBackground = (ImageView) findViewById(R.id.iv_background);

        session = new SessionManager(context);

        changeState(1);

        getDashboard();
    }

    private void initEvent() {

        llInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                changeState(1);
            }
        });

        llRiwayat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                changeState(2);
            }
        });

        llDonatur.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                changeState(3);
            }
        });

    }

    public void changeState(int state){

        if(state == 1){

            fragment = new SalesSosialJadwalFragment();
            callFragment(context, fragment);

            //UI
            tvjadwalKunjungan.setTextColor(getResources().getColor(R.color.colorTitle));
            tvRiwayat.setTextColor(getResources().getColor(R.color.colorWhite));
            tvDonatur.setTextColor(getResources().getColor(R.color.colorWhite));
            vInfo.setVisibility(View.VISIBLE);
            vRiwayat.setVisibility(View.GONE);
            vDonatur.setVisibility(View.GONE);
        }else if (state == 2){

            fragment = new SalesSosialRiwayatFragment();
            callFragment(context, fragment);

            //UI
            tvjadwalKunjungan.setTextColor(getResources().getColor(R.color.colorWhite));
            tvRiwayat.setTextColor(getResources().getColor(R.color.colorTitle));
            tvDonatur.setTextColor(getResources().getColor(R.color.colorWhite));
            vInfo.setVisibility(View.GONE);
            vRiwayat.setVisibility(View.VISIBLE);
            vDonatur.setVisibility(View.GONE);
        }else if (state == 3){

            fragment = new DonaturLSosialFragment();
            callFragment(context, fragment);

            //UI
            tvjadwalKunjungan.setTextColor(getResources().getColor(R.color.colorWhite));
            tvRiwayat.setTextColor(getResources().getColor(R.color.colorWhite));
            tvDonatur.setTextColor(getResources().getColor(R.color.colorTitle));
            vInfo.setVisibility(View.GONE);
            vRiwayat.setVisibility(View.GONE);
            vDonatur.setVisibility(View.VISIBLE);
        }
    }

    private static Fragment fragment;
    private static void callFragment(Context context, Fragment fragment) {
        ((AppCompatActivity)context).getSupportFragmentManager()
                .beginTransaction()
                //.setCustomAnimations(R.anim.slide_in_up, R.anim.slide_out_up)
                //.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_right)
                .replace(R.id.fl_container, fragment, fragment.getClass().getSimpleName())
                .addToBackStack(null)
                .commit();
    }

    private static void callFragmentBack(Context context, Fragment fragment) {
        ((AppCompatActivity)context).getSupportFragmentManager()
                .beginTransaction()
                //.setCustomAnimations(R.anim.slide_in_down, R.anim.slide_out_down)
                //.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_left)
                .replace(R.id.fl_container, fragment, fragment.getClass().getSimpleName())
                .addToBackStack(null)
                .commit();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == ServerURL.PIX_REQUEST_CODE) {
            if(data != null){
                if(fragment instanceof DonaturLSosialFragment){
                    ArrayList<String> returnValue = data.getStringArrayListExtra(Pix.IMAGE_RESULTS);
                    ArrayList<String> listGambar = new ArrayList<>();
                    for(String s : returnValue){
                        listGambar.add(Uri.fromFile(new File(s)).getPath());
                    }
                    ((DonaturLSosialFragment)fragment).updateGambar(listGambar);
                }

            }
        }
        else if(requestCode == GoogleLocationManager.ACTIVATE_LOCATION){
            if(fragment instanceof DonaturLSosialFragment){

                ((DonaturLSosialFragment)fragment).retryLocation();
            }else if (fragment instanceof SalesSosialJadwalFragment){

                ((SalesSosialJadwalFragment)fragment).retryLocation();
            }
        }else if(requestCode == 1102){
            if(data != null){
                changeState(2);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.home, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.option_profile) {

            Intent intent = new Intent(context, DetailAkunActivity.class);
            startActivity(intent);
            return true;
        }else if(id == R.id.option_notif){

            Intent intent = new Intent(context, ListNotificationActivity.class);
            startActivity(intent);
            return true;
//        }else if(id == R.id.option_request){
//            Intent intent = new Intent(context, RequestActivity.class);
//            startActivity(intent);
//            return true;
        }else{
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater) ((Activity)context).getSystemService(LAYOUT_INFLATER_SERVICE);
        View viewDialog = inflater.inflate(R.layout.layout_exit_dialog, null);
        builder.setView(viewDialog);
        builder.setCancelable(false);

        final Button btnYa = (Button) viewDialog.findViewById(R.id.btn_ya);
        final Button btnTidak = (Button) viewDialog.findViewById(R.id.btn_tidak);

        final AlertDialog alert = builder.create();
        alert.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        btnYa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view2) {

                if(alert != null) alert.dismiss();

                Intent intent = new Intent(context, HomeSocialSalesActivity.class);
                intent.putExtra("exit", true);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });

        btnTidak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view2) {

                if(alert != null) alert.dismiss();
            }
        });

        alert.show();
    }

    private void getDashboard() {

        dialogBox.showDialog(false);
        new ApiVolley(context, new JSONObject(), "GET", ServerURL.getBackgroundDashboard,
                new ApiVolley.VolleyCallback() {
                    @Override
                    public void onSuccess(String result) {

                        dialogBox.dismissDialog();
                        try {

                            JSONObject response = new JSONObject(result);
                            String status = response.getJSONObject("metadata").getString("status");
                            String message = response.getJSONObject("metadata").getString("message");

                            if(iv.parseNullInteger(status) == 200){

                                String gambarBg = response.getJSONObject("response").getString("gambar");
                                iu.LoadRealImage(gambarBg, ivBackground);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            View.OnClickListener clickListener = new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                    dialogBox.dismissDialog();
                                    getDashboard();

                                }
                            };

                            dialogBox.showDialog(clickListener, "Ulangi Proses", "Terjadi kesalahan saat mengambil data");
                        }
                    }

                    @Override
                    public void onError(String result) {

                        View.OnClickListener clickListener = new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                dialogBox.dismissDialog();
                                getDashboard();

                            }
                        };

                        dialogBox.showDialog(clickListener, "Ulangi Proses", result);
                    }
                });
    }
}
