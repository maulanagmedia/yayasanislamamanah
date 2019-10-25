package co.id.gmedia.yia.ActSalesChecking;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.tabs.TabLayout;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.Date;

import co.id.gmedia.coremodul.ApiVolley;
import co.id.gmedia.coremodul.DialogBox;
import co.id.gmedia.coremodul.ImageUtils;
import co.id.gmedia.coremodul.SessionManager;
import co.id.gmedia.yia.ActAkun.DetailAkunActivity;
import co.id.gmedia.yia.R;
import co.id.gmedia.yia.Utils.AppRequestCallback;
import co.id.gmedia.yia.Utils.Converter;
import co.id.gmedia.yia.Utils.GoogleLocationManager;
import co.id.gmedia.yia.Utils.JSONBuilder;
import co.id.gmedia.yia.Utils.ServerURL;
import co.id.gmedia.yia.Utils.TopCropCircularImageView;

public class SalesCheckingActivity extends AppCompatActivity {

    private Context context;
    private TextView txt_nama, txt_jumlah_jadwal, txt_jumlah_riwayat;
    private TopCropCircularImageView img_foto;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private Fragment active_fragment;

    private DialogBox dialogBox;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales_checking);

        context = this;

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {

            if (bundle.getBoolean("exit", false)) {

                finish();
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        }

        txt_nama = findViewById(R.id.txt_nama);
        txt_jumlah_jadwal = (TextView) findViewById(R.id.txt_jumlah_jadwal);
        txt_jumlah_riwayat = findViewById(R.id.txt_jumlah_riwayat);
        img_foto = findViewById(R.id.img_foto);

        initToolbar();
        TabLayout tab_collector = findViewById(R.id.tab_collector);
        tab_collector.addTab(tab_collector.newTab().setText("Jadwal Kunjungan"));
        tab_collector.addTab(tab_collector.newTab().setText("Riwayat Kunjungan"));
        tab_collector.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switchTab(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        dialogBox = new DialogBox(this);
        sessionManager = new SessionManager(this);

        active_fragment = new CheckingJadwalFragment();
        loadFragment(active_fragment);
    }

    private void initAkun(){
        SessionManager session = new SessionManager(this);
        txt_nama.setText(session.getNama());
        ImageUtils imageUtils = new ImageUtils();
        imageUtils.LoadRealImage(session.getFoto(), img_foto);
    }

    @Override
    protected void onResume() {
        initAkun();
        super.onResume();
    }

    private void switchTab(int position){
        switch (position){
            case 0 : active_fragment = new CheckingJadwalFragment();break;
            case 1 : active_fragment = new CheckingRiwayatFragment();break;
        }
        loadFragment(active_fragment);
    }

    private void loadFragment(Fragment fragment){
        FragmentTransaction trans = getSupportFragmentManager().beginTransaction();
        trans.replace(R.id.layout_container, fragment);
        trans.commit();
    }

    private void initToolbar(){
        //Inisialisasi Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null){
            getSupportActionBar().setTitle("");
        }

        collapsingToolbarLayout = findViewById(R.id.collapsing);
        collapsingToolbarLayout.setTitle("");
        collapsingToolbarLayout.setCollapsedTitleTextColor(
                ContextCompat.getColor(this, R.color.colorWhite));
        collapsingToolbarLayout.setExpandedTitleColor(
                ContextCompat.getColor(this, R.color.colorPrimary));

        AppBarLayout appBarLayout = findViewById(R.id.appbar);
        appBarLayout.setExpanded(true);
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
                    collapsingToolbarLayout.setTitle("");
                    isShow = false;
                }
            }
        });
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
        if (id == R.id.nav_akun) {
            Intent intent = new Intent(this, DetailAkunActivity.class);
            startActivity(intent);
            return true;
        }
        else{
            return super.onOptionsItemSelected(item);
        }
    }

    public void updateJumlahJadwal(int jumlah){
        txt_jumlah_jadwal.setText(String.valueOf(jumlah));
    }

    public void updateJumlahRiwayat(int jumlah){
        txt_jumlah_riwayat.setText(String.valueOf(jumlah));
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

                Intent intent = new Intent(context, SalesCheckingActivity.class);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == GoogleLocationManager.ACTIVATE_LOCATION){

            if (active_fragment instanceof CheckingJadwalFragment){

                ((CheckingJadwalFragment) active_fragment).retryLocation();
            }
        }
    }
}
