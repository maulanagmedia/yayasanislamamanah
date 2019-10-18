package co.id.gmedia.yia.ActSalesChecking;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
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
import co.id.gmedia.yia.Utils.JSONBuilder;
import co.id.gmedia.yia.Utils.ServerURL;
import co.id.gmedia.yia.Utils.TopCropCircularImageView;

public class SalesCheckingActivity extends AppCompatActivity {

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
}
