package co.id.gmedia.yia;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
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

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;

import co.id.gmedia.coremodul.ImageUtils;
import co.id.gmedia.coremodul.SessionManager;
import co.id.gmedia.yia.ActAkun.DetailAkunActivity;
import co.id.gmedia.yia.ActSalesBrosur.SalesBrosurDetailFragment;
import co.id.gmedia.yia.ActSalesBrosur.SalesBrosurRiwayatFragment;

public class HomeActivity extends AppCompatActivity {

    private CollapsingToolbarLayout collapsingToolbarLayout;
    private LinearLayout llInfo, llRiwayat;
    private Context context;
    private TextView tvInfo, tvRiwayat, tvTitle2;
    private View vInfo, vRiwayat;
    private ImageView ivAkun;
    private LinearLayout llAkun;
    private TextView tvAdmin;
    private SessionManager session;
    private ImageView ivProfile;
    private ImageUtils imageUtils = new ImageUtils();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        context = this;

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

    private void initUI() {

        llInfo = (LinearLayout) findViewById(R.id.ll_info);
        llRiwayat = (LinearLayout) findViewById(R.id.ll_riwayat);
        tvInfo = (TextView) findViewById(R.id.tv_info);
        tvRiwayat = (TextView) findViewById(R.id.tv_riwayat);
        tvTitle2 = (TextView) findViewById(R.id.tv_title2);
        vInfo = (View) findViewById(R.id.v_info);
        vRiwayat = (View) findViewById(R.id.v_riwayat);
        tvAdmin = (TextView) findViewById(R.id.tv_admin);
        ivProfile = (ImageView) findViewById(R.id.iv_profile);

        session = new SessionManager(context);

        changeState(2);
    }

    @Override
    protected void onResume() {
        super.onResume();

        tvAdmin.setText(session.getNama());
        imageUtils.LoadCircleRealImage(session.getFoto(), ivProfile);
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

    }

    private void changeState(int state){

        if(state == 1){

            fragment = new SalesBrosurDetailFragment();
            callFragment(context, fragment);

            //UI
            tvInfo.setTextColor(getResources().getColor(R.color.colorTitle));
            tvRiwayat.setTextColor(getResources().getColor(R.color.colorWhite));
            vInfo.setVisibility(View.VISIBLE);
            vRiwayat.setVisibility(View.GONE);
        }else if (state == 2){

            fragment = new SalesBrosurRiwayatFragment();
            callFragment(context, fragment);

            //UI
            tvInfo.setTextColor(getResources().getColor(R.color.colorWhite));
            tvRiwayat.setTextColor(getResources().getColor(R.color.colorTitle));
            vInfo.setVisibility(View.GONE);
            vRiwayat.setVisibility(View.VISIBLE);
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
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.home, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.nav_akun) {
            Intent intent = new Intent(context, DetailAkunActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
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

                Intent intent = new Intent(context, HomeActivity.class);
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
}
