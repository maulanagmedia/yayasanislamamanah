package co.id.gmedia.yia;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONException;
import org.json.JSONObject;

import co.id.gmedia.coremodul.ApiVolley;
import co.id.gmedia.coremodul.DialogBox;
import co.id.gmedia.coremodul.ItemValidation;
import co.id.gmedia.coremodul.SessionManager;
import co.id.gmedia.yia.ActCollector.CollectorActivity;
import co.id.gmedia.yia.ActSalesChecking.SalesCheckingActivity;
import co.id.gmedia.yia.NotificationUtils.InitFirebaseSetting;
import co.id.gmedia.yia.Utils.ServerURL;
import co.id.gmedia.yia.ActAdmin.AdminActivity;

public class LoginActivity extends AppCompatActivity {

    private ImageView ivVisibility;
    private Button btnMasuk;
    private EditText edtUsername, edtPassword;
    private boolean visibleTapped = true;
    private Context context;
    private ItemValidation iv = new ItemValidation();
    private DialogBox dialogBox;
    private SessionManager session;
    private String refreshToken = "";
    private String version = "";
    private String link = "";
    private Dialog dialogVersion;

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

        InitFirebaseSetting.getFirebaseSetting(LoginActivity.this);

        refreshToken = FirebaseInstanceId.getInstance().getToken();
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

        new ApiVolley(context, jBody, "POST", ServerURL.login, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                dialogBox.dismissDialog();
                try {
                    Log.d(">>login","res "+result);
                    JSONObject response = new JSONObject(result);
                    String status = response.getJSONObject("metadata").getString("status");
                    String message = response.getJSONObject("metadata").getString("message");

                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                    if(iv.parseNullInteger(status) == 200){

                        JSONObject jo = response.getJSONObject("response");
                        session.createLoginSession(
                                jo.getString("id")
                                , jo.getString("username")
                                , jo.getString("nik")
                                , jo.getString("nama")
                                , jo.getString("level")
                                , jo.getString("ket_level")
                                , jo.getString("email")
                                , jo.getString("foto_profil")
                                , jo.getString("no_telp")
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

    private void updateFCM(final Intent intent){

        dialogBox.showDialog(false);
        JSONObject jBody = new JSONObject();
        try {
            jBody.put("id", session.getId());
            jBody.put("fcm_id", refreshToken);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        new ApiVolley(context, jBody, "POST", ServerURL.updateFCM, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                dialogBox.dismissDialog();
                try {
                    JSONObject response = new JSONObject(result);
                    String status = response.getJSONObject("metadata").getString("status");
                    String message = response.getJSONObject("metadata").getString("message");

                    //Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                    if(iv.parseNullInteger(status) == 200){

                        startActivity(intent);
                        finish();
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    View.OnClickListener clickListener = new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            dialogBox.dismissDialog();
                            updateFCM(intent);

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
                        updateFCM(intent);

                    }
                };

                dialogBox.showDialog(clickListener, "Ulangi Proses", "Terjadi kesalahan saat mengambil data");
            }
        });
    }

    private void getApplicationVersion(final Intent intent){

        PackageInfo pInfo = null;
        version = "";

        try {
            pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            Log.e("Versi", e.getMessage());
        }

        if(pInfo != null){
            version = pInfo.versionName;
        }

        dialogBox.showDialog(false);
        JSONObject jBody = new JSONObject();

        new ApiVolley(context, jBody, "GET", ServerURL.getApplicationVersion, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                dialogBox.dismissDialog();
                try {

                    JSONObject response = new JSONObject(result);
                    String status = response.getJSONObject("metadata").getString("status");
                    String message = response.getJSONObject("metadata").getString("message");

                    //Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                    if(iv.parseNullInteger(status) == 200){

                        String latestVersion = response.getJSONObject("response").getString("version");
                        link = response.getJSONObject("response").getString("link_update");

                        if (!version.trim().equals(latestVersion.trim()) && link.length() > 0) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                            builder.setIcon(R.mipmap.ic_launcher)
                                    .setTitle("Update")
                                    .setMessage("Versi terbaru " + latestVersion +
                                            " telah tersedia, mohon download versi terbaru.")
                                    .setPositiveButton("Update Sekarang", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
                                            startActivity(browserIntent);
                                        }
                                    });
                            dialogVersion = builder.create();
                            dialogVersion.setCancelable(false);

                            if(!dialogVersion.isShowing()){

                                dialogVersion.show();
                            }
                        }else{

                            updateFCM(intent);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    View.OnClickListener clickListener = new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            dialogBox.dismissDialog();
                            getApplicationVersion(intent);

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
                        getApplicationVersion(intent);

                    }
                };

                dialogBox.showDialog(clickListener, "Ulangi Proses", "Terjadi kesalahan saat mengambil data");
            }
        });
    }

    private void redirectToLogin(){

        Intent intent;
        Log.d(">>level",session.getLevel());
        switch (session.getLevel()){

            case "2" :
                intent = new Intent(context, HomeSocialSalesActivity.class);
                break;
            case "3" :
                intent = new Intent(context, SalesCheckingActivity.class);
                break;
            case "4":
                intent = new Intent(context, CollectorActivity.class);
                break;
            case "6":
                intent = new Intent(context, AdminActivity.class);
                break;
            default:
                intent = new Intent(context, HomeActivity.class);
                break;
        }

        getApplicationVersion(intent);
    }

    private void initData() {

        if(session.isLoggedIn()){

            redirectToLogin();
        }
    }
}
