package co.id.gmedia.yia;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import co.id.gmedia.coremodul.ApiVolley;
import co.id.gmedia.coremodul.DialogBox;
import co.id.gmedia.coremodul.ItemValidation;
import co.id.gmedia.coremodul.SessionManager;
import co.id.gmedia.yia.Utils.ServerURL;

public class LoginActivity extends AppCompatActivity {

    private ImageView ivVisibility;
    private Button btnMasuk;
    private EditText edtUsername, edtPassword;
    private boolean visibleTapped = true;
    private Context context;
    private ItemValidation iv = new ItemValidation();
    private DialogBox dialogBox;
    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        context = this;
        initUI();
        initEvent();
        initData();
    }

    private void initUI() {

        edtUsername = (EditText) findViewById(R.id.edt_username);
        edtPassword = (EditText) findViewById(R.id.edt_password);
        ivVisibility = (ImageView) findViewById(R.id.iv_visibility);
        btnMasuk = (Button) findViewById(R.id.btn_masuk);
        visibleTapped = true;
        session = new SessionManager(context);

        dialogBox = new DialogBox(context);
    }

    private void initEvent() {

        ivVisibility.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (visibleTapped) {
                    edtPassword.setTransformationMethod(null);
                    edtPassword.setSelection(edtPassword.getText().length());
                    visibleTapped = false;
                    ivVisibility.setImageResource(R.mipmap.ic_invisible);
                } else {
                    edtPassword.setTransformationMethod(new PasswordTransformationMethod());
                    edtPassword.setSelection(edtPassword.getText().length());
                    visibleTapped = true;
                    ivVisibility.setImageResource(R.mipmap.ic_visible);
                }
            }
        });

        btnMasuk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(edtUsername.getText().toString().length() == 0){

                    edtUsername.setError("Username harap diisi");
                    edtUsername.requestFocus();
                    return;

                }else{
                    edtUsername.setError(null);
                }

                if(edtPassword.getText().toString().length() == 0){

                    edtPassword.setError("Password harap diisi");
                    edtPassword.requestFocus();
                    return;

                }else{
                    edtPassword.setError(null);
                }

                doLogin();
            }
        });
    }

    private void doLogin(){

        dialogBox.showDialog(false);
        JSONObject jBody = new JSONObject();
        try {
            jBody.put("username", edtUsername.getText().toString());
            jBody.put("password", edtPassword.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ApiVolley request = new ApiVolley(context, jBody, "POST", ServerURL.login, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {

                dialogBox.dismissDialog();
                try {

                    JSONObject response = new JSONObject(result);
                    String status = response.getJSONObject("metadata").getString("status");
                    String message = response.getJSONObject("metadata").getString("message");

                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                    if(iv.parseNullInteger(status) == 200){

                        JSONObject jo = response.getJSONObject("response");
                        session.createLoginSession(jo.getString("username")
                                , jo.getString("nik")
                                , jo.getString("nama")
                                , jo.getString("level")
                                , jo.getString("ket_level")
                                );

                        redirectToLogin();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    View.OnClickListener clickListener = new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            dialogBox.dismissDialog();
                            doLogin();

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
                        doLogin();

                    }
                };

                dialogBox.showDialog(clickListener, "Ulangi Proses", "Terjadi kesalahan saat mengambil data");
            }
        });
    }

    private void redirectToLogin(){

        Intent intent;
        switch (session.getLevel()){

            case "1" :
                intent = new Intent(context, HomeActivity.class);
                break;
            case "2" :
                intent = new Intent(context, HomeSocialSalesActivity.class);
                break;
            default:
                intent = new Intent(context, HomeActivity.class);
                break;
        }

        startActivity(intent);
        finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    private void initData() {

        if(session.isLoggedIn()){

            redirectToLogin();
        }
    }
}
