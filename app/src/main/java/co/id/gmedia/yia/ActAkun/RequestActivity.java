package co.id.gmedia.yia.ActAkun;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import co.id.gmedia.coremodul.ApiVolley;
import co.id.gmedia.coremodul.DialogBox;
import co.id.gmedia.coremodul.ItemValidation;
import co.id.gmedia.coremodul.SessionManager;
import co.id.gmedia.yia.Model.DonaturModel;
import co.id.gmedia.yia.R;
import co.id.gmedia.yia.Utils.ServerURL;
import es.dmoral.toasty.Toasty;

public class RequestActivity extends AppCompatActivity {

    private EditText edtSubjek, edtKet;
    private TextView tvDonatur;
    private Button btnSimpan;
    private DialogBox dialogBox;
    private SessionManager session;
    private ItemValidation iv = new ItemValidation();

    public static final String DONATUR_ITEM ="donatur_item";
    String donatur_item="";
    DonaturModel donaturModel;
    private Gson gson = new Gson();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );

        setTitle("Request donatur");

        initUi();

    }

    private void initUi(){
        tvDonatur = findViewById(R.id.tv_donatur);
        edtSubjek = findViewById(R.id.edt_subjek);
        edtKet = findViewById(R.id.edt_keterangan);
        btnSimpan = findViewById(R.id.btn_simpan);
        dialogBox = new DialogBox(RequestActivity.this);
        session = new SessionManager(RequestActivity.this);

        donatur_item = getIntent().getStringExtra(DONATUR_ITEM);
        donaturModel = gson.fromJson(donatur_item, DonaturModel.class);
        tvDonatur.setText(donaturModel.getNama());

        btnSimpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendRequest();
            }
        });
    }

    private void sendRequest(){
        dialogBox.showDialog(false);

        JSONObject obj = new JSONObject();
        try {
            obj.put("id_sales",session.getId());
            obj.put("id_donatur",donaturModel.getId_donatur());
            obj.put("subjek",edtSubjek.getText().toString());
            obj.put("ket",edtKet.getText().toString());
        }catch (JSONException e){
            e.printStackTrace();
        }
        new ApiVolley(RequestActivity.this, obj, "POST", ServerURL.urlRequestDonatur, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {

                dialogBox.dismissDialog();
                try {

                    JSONObject response = new JSONObject(result);
                    String status = response.getJSONObject("metadata").getString("status");
                    String message = response.getJSONObject("metadata").getString("message");

                    if(iv.parseNullInteger(status) == 200){
                        Toasty.success(RequestActivity.this, message, Toast.LENGTH_SHORT, true).show();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                finish();
                            }
                        },1000);
                    }else{
                        Toasty.error(RequestActivity.this, message, Toast.LENGTH_SHORT, true).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    View.OnClickListener clickListener = new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            dialogBox.dismissDialog();

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
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
