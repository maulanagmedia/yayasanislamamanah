package co.id.gmedia.yia.ActCollector;

import androidx.annotation.NonNull;
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
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.fxn.pix.Pix;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.tabs.TabLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

import co.id.gmedia.coremodul.ApiVolley;
import co.id.gmedia.coremodul.DialogBox;
import co.id.gmedia.coremodul.ImageUtils;
import co.id.gmedia.coremodul.ItemValidation;
import co.id.gmedia.coremodul.SessionManager;
import co.id.gmedia.yia.ActAkun.DetailAkunActivity;
import co.id.gmedia.yia.ActAkun.RequestActivity;
import co.id.gmedia.yia.ActNotifikasi.ListNotificationActivity;
import co.id.gmedia.yia.R;
import co.id.gmedia.yia.Utils.GoogleLocationManager;
import co.id.gmedia.yia.Utils.ServerURL;
import co.id.gmedia.yia.Utils.TopCropCircularImageView;

public class CollectorActivity extends AppCompatActivity {

    private Context context;
    private TextView txt_nama, txt_jumlah, tvJumlahHistory;
    private TopCropCircularImageView img_foto;

    private CollapsingToolbarLayout collapsingToolbarLayout;
    private Fragment active_fragment;
    private ImageUtils imageUtils = new ImageUtils();
    private ItemValidation iv = new ItemValidation();
    private DialogBox dialogBox;
    private ImageView ivBackground;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collector);

        context = this;
        dialogBox = new DialogBox(context);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {

            if (bundle.getBoolean("exit", false)) {

                finish();
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        }

        txt_nama = findViewById(R.id.txt_nama);
        txt_jumlah = findViewById(R.id.txt_jumlah);
        img_foto = findViewById(R.id.img_foto);
        ivBackground = (ImageView) findViewById(R.id.img_sampul);
        tvJumlahHistory= (TextView) findViewById(R.id.tv_jumlah_history);

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
        getDashboard();
    }

    @Override
    protected void onResume() {
        loadProfil();
        super.onResume();
    }

    private void loadProfil(){
        SessionManager session = new SessionManager(this);
        txt_nama.setText(session.getNama());
        ImageUtils imageUtils = new ImageUtils();
        imageUtils.LoadRealImage(session.getFoto(), img_foto);
    }

    public void updateJumlah(String jumlah){
        txt_jumlah.setText(jumlah);
    }

    public void updateJumlahHistory(String jumlah){
        tvJumlahHistory.setText(jumlah);
//        Toast.makeText(context, "Total "+jumlah, Toast.LENGTH_SHORT).show();
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
        if (id == R.id.option_profile) {

            Intent intent = new Intent(context, DetailAkunActivity.class);
            startActivity(intent);
            return true;
        }else if(id == R.id.option_notif) {

            Intent intent = new Intent(context, ListNotificationActivity.class);
            startActivity(intent);
            return true;
        }else if(id == R.id.option_request){
            Intent intent = new Intent(context, RequestActivity.class);
            startActivity(intent);
            return true;
        }else{
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
        }else if(requestCode == 1102){
            if(data != null){
                switchTab(0);
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

                Intent intent = new Intent(context, CollectorActivity.class);
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
                                imageUtils.LoadRealImage(gambarBg, ivBackground);
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
