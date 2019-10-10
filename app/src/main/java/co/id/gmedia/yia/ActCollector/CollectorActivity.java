package co.id.gmedia.yia.ActCollector;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import com.fxn.pix.Pix;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.tabs.TabLayout;

import java.io.File;
import java.util.ArrayList;

import co.id.gmedia.coremodul.ImageUtils;
import co.id.gmedia.coremodul.SessionManager;
import co.id.gmedia.yia.ActAkun.DetailAkunActivity;
import co.id.gmedia.yia.R;
import co.id.gmedia.yia.Utils.GoogleLocationManager;
import co.id.gmedia.yia.Utils.ServerURL;
import co.id.gmedia.yia.Utils.TopCropCircularImageView;

public class CollectorActivity extends AppCompatActivity {

    private TextView txt_nama, txt_jumlah;
    private TopCropCircularImageView img_foto;

    private CollapsingToolbarLayout collapsingToolbarLayout;
    private Fragment active_fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collector);

        txt_nama = findViewById(R.id.txt_nama);
        txt_jumlah = findViewById(R.id.txt_jumlah);
        img_foto = findViewById(R.id.img_foto);

        initToolbar();
        TabLayout tab_collector = findViewById(R.id.tab_collector);
        tab_collector.addTab(tab_collector.newTab().setText("Jadwal Kunjungan"));
        tab_collector.addTab(tab_collector.newTab().setText("History Collector"));
        tab_collector.addTab(tab_collector.newTab().setText("Donatur Luar"));
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

        loadFragment(new CollectorJadwalFragment());
        loadProfil();
    }

    private void loadProfil(){
        SessionManager session = new SessionManager(this);
        txt_nama.setText(session.getNama());
        ImageUtils imageUtils = new ImageUtils();
        imageUtils.LoadRealImage(session.getFoto(), img_foto, 100, 100);
    }

    public void updateJumlah(int jumlah){
        txt_jumlah.setText(String.valueOf(jumlah));
    }

    private void switchTab(int position){
        switch (position){
            case 0 : loadFragment(new CollectorJadwalFragment());break;
            case 1 : loadFragment(new CollectorHistoryFragment());break;
            case 2 : loadFragment(new CollectorTambahDonaturFragment());break;
        }
    }

    public void loadFragment(Fragment fragment){
        active_fragment = fragment;
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == ServerURL.PIX_REQUEST_CODE) {
            if(data != null){
                if(active_fragment instanceof CollectorTambahDonaturFragment){
                    ArrayList<String> returnValue = data.getStringArrayListExtra(Pix.IMAGE_RESULTS);
                    ArrayList<String> listGambar = new ArrayList<>();
                    for(String s : returnValue){
                        listGambar.add(Uri.fromFile(new File(s)).getPath());
                    }
                    ((CollectorTambahDonaturFragment)active_fragment).updateGambar(listGambar);
                }
            }
        }
        else if(requestCode == GoogleLocationManager.ACTIVATE_LOCATION){
            if(active_fragment instanceof CollectorTambahDonaturFragment){
                ((CollectorTambahDonaturFragment)active_fragment).retryLocation();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == GoogleLocationManager.PERMISSION_LOCATION){
            if(active_fragment instanceof CollectorTambahDonaturFragment){
                ((CollectorTambahDonaturFragment)active_fragment).retryLocation();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
