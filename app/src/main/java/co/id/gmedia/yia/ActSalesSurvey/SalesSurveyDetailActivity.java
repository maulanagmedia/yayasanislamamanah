package co.id.gmedia.yia.ActSalesSurvey;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.RadioButton;

import org.json.JSONException;
import org.json.JSONObject;

import co.id.gmedia.coremodul.ApiVolley;
import co.id.gmedia.coremodul.DialogBox;
import co.id.gmedia.coremodul.SessionManager;
import co.id.gmedia.yia.R;
import co.id.gmedia.yia.Utils.JSONBuilder;
import co.id.gmedia.yia.Utils.ServerURL;

public class SalesSurveyDetailActivity extends AppCompatActivity{

    private String id_rencana_kerja = "";
    private String id_donatur = "";

    private EditText edt_nama, edt_alamat, edt_kontak;
    private RadioButton rb_donasi_ya, rb_donasi_tidak;

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
    }

    private void initUI() {

        edt_nama = findViewById(R.id.edt_nama);
        edt_alamat = findViewById(R.id.edt_alamat);
        edt_kontak = findViewById(R.id.edt_kontak);

        rb_donasi_ya = findViewById(R.id.rb_donasi_ya);
        rb_donasi_tidak = findViewById(R.id.rb_donasi_tidak);

        findViewById(R.id.btn_simpan).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                simpanSurvey();
            }
        });

        dialogBox = new DialogBox(this);
    }

    private void simpanSurvey(){
        dialogBox.showDialog(false);

        JSONBuilder body = new JSONBuilder();
        body.add("id_rk", id_rencana_kerja);
        body.add("id_sales", sessionManager.getId());
        body.add("id_donatur", id_donatur);
        body.add("status_donasi", rb_donasi_ya.isChecked()?1:0);

        new ApiVolley(this, body.create(), "POST", ServerURL.saveSurvey,
                new ApiVolley.VolleyCallback() {
                    @Override
                    public void onSuccess(String result) {
                        dialogBox.dismissDialog();
                        Log.d("savesurvey_log", result);
                        try{
                            JSONObject object = new JSONObject(result);
                        }
                        catch (JSONException e){
                            e.printStackTrace();
                            View.OnClickListener clickListener = new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    dialogBox.dismissDialog();
                                    simpanSurvey();
                                }
                            };

                            dialogBox.showDialog(clickListener, "Ulangi Proses", "Terjadi kesalahan saat mengambil data");
                        }
                    }

                    @Override
                    public void onError(String result) {
                        dialogBox.dismissDialog();
                        View.OnClickListener clickListener = new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialogBox.dismissDialog();
                                simpanSurvey();

                            }
                        };

                        dialogBox.showDialog(clickListener, "Ulangi Proses", "Terjadi kesalahan saat mengambil data");
                    }
                });
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
