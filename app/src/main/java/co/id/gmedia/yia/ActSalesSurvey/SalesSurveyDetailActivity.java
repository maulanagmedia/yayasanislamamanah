package co.id.gmedia.yia.ActSalesSurvey;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.gson.Gson;

import co.id.gmedia.coremodul.ApiVolley;
import co.id.gmedia.coremodul.DialogBox;
import co.id.gmedia.coremodul.SessionManager;
import co.id.gmedia.yia.Model.DonaturModel;
import co.id.gmedia.yia.R;
import co.id.gmedia.yia.Utils.AppRequestCallback;
import co.id.gmedia.yia.Utils.JSONBuilder;
import co.id.gmedia.yia.Utils.ServerURL;

public class SalesSurveyDetailActivity extends AppCompatActivity{

    private DonaturModel donatur;

    private EditText edt_nama, edt_alamat, edt_kontak;
    private RadioButton rb_donasi_ya;

    private SessionManager sessionManager;
    private DialogBox dialogBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales_survey_detail);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );

        sessionManager = new SessionManager(this);

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

        rb_donasi_ya = findViewById(R.id.rb_donasi_ya);

        findViewById(R.id.btn_simpan).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(donatur != null){
                    simpanSurvey();
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
