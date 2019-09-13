package co.id.gmedia.yia;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.widget.Button;

import co.id.gmedia.yia.ui.gallery.GalleryFragment;
import co.id.gmedia.yia.ui.home.HomeFragment;
import co.id.gmedia.yia.ui.send.SendFragment;
import co.id.gmedia.yia.ui.share.ShareFragment;
import co.id.gmedia.yia.ui.slideshow.SlideshowFragment;
import co.id.gmedia.yia.ui.tools.ToolsFragment;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private Context context;
    private AppBarConfiguration mAppBarConfiguration;
    private int state = 0;
    private final String TAG = "data";
    private static boolean doubleBackToExitPressedOnce;
    private boolean exitState = false;
    private int timerClose = 2000;
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);

        context = this;
        setSupportActionBar(toolbar);

        //Check close statement
        doubleBackToExitPressedOnce = false;
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {

            if (bundle.getBoolean("exit", false)) {

                exitState = true;
                finish();
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        setTitle("Home");

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    private void ChangeFragment(int stateChecked){

        int position = 0;

        MenuItem item = navigationView.getMenu().findItem(stateChecked);
        item.setCheckable(true);
        item.setChecked(true);
        switch (stateChecked){
            case R.id.nav_home:
                setTitle("Home");
                fragment = new HomeFragment();
                position = 0;
                break;
            case R.id.nav_gallery:
                setTitle("Gallery");
                fragment = new GalleryFragment();
                position = 1;
                break;
            case R.id.nav_share:
                setTitle("Riwayat Penjualan");
                fragment = new ShareFragment();
                position = 2;
                break;
            case R.id.nav_slideshow:
                setTitle("Promo");
                fragment = new SlideshowFragment();
                position = 3;
                break;
            default:
                setTitle("Home");
                fragment = new ToolsFragment();
                position = 4;
                break;
        }

        if(position > state){

            callFragment(context, fragment);
        }else if (position < state){
            callFragmentBack(context, fragment);
        }

        state = position;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

        int id = menuItem.getItemId();
        Log.d(TAG, "onNavigationItemSelected: ");
        ChangeFragment(id);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private static Fragment fragment;
    private static void callFragment(Context context, Fragment fragment) {
        ((AppCompatActivity)context).getSupportFragmentManager()
                .beginTransaction()
                //.setCustomAnimations(R.anim.slide_in_up, R.anim.slide_out_up)
                //.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_right)
                .replace(R.id.nav_host_fragment, fragment, fragment.getClass().getSimpleName())
                .addToBackStack(null)
                .commit();
    }

    private static void callFragmentBack(Context context, Fragment fragment) {
        ((AppCompatActivity)context).getSupportFragmentManager()
                .beginTransaction()
                //.setCustomAnimations(R.anim.slide_in_down, R.anim.slide_out_down)
                //.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_left)
                .replace(R.id.nav_host_fragment, fragment, fragment.getClass().getSimpleName())
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if(state != 0){

                ChangeFragment(R.id.nav_home);

            }else{

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

                        Intent intent = new Intent(context, MainActivity.class);
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
    }
}
