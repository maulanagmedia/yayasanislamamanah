package co.id.gmedia.yia.ActAkun;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fxn.pix.Options;
import com.fxn.pix.Pix;
import com.fxn.utility.ImageQuality;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import co.id.gmedia.coremodul.ApiVolley;
import co.id.gmedia.coremodul.DialogBox;
import co.id.gmedia.coremodul.ImageUtils;
import co.id.gmedia.coremodul.ItemValidation;
import co.id.gmedia.coremodul.SessionManager;
import co.id.gmedia.yia.LoginActivity;
import co.id.gmedia.yia.R;
import co.id.gmedia.yia.Utils.ServerURL;

public class DetailAkunActivity extends AppCompatActivity {

    private Context context;
    private SessionManager session;
    private ItemValidation iv = new ItemValidation();
    private ImageView ivProfileMain, ivProfile, ivNama, ivKontak, ivPassword;
    private TextView tvNama, tvKontak, tvPassword;
    private RelativeLayout rlLogout;
    private ImageUtils imageUtils = new ImageUtils();

    private int imageRequestCode = 100;

    //permission
    private String[] appPermission =  {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
    };
    private final int PERMIOSSION_REQUEST_CODE = 1240;
    private DialogBox dialogBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_akun);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );

        setTitle("Setelan Akun");
        context = this;
        session = new SessionManager(context);
        dialogBox = new DialogBox(context);

        if (checkPermission()){

        }

        initUI();
        initEvent();
        initData();
    }

    private boolean checkPermission(){

        List<String> permissionList = new ArrayList<>();
        for (String perm : appPermission) {

            if (ContextCompat.checkSelfPermission(context, perm) != PackageManager.PERMISSION_GRANTED){

                permissionList.add(perm);
            }
        }

        if (!permissionList.isEmpty()) {

            ActivityCompat.requestPermissions((Activity) context, permissionList.toArray(new String[permissionList.size()]), PERMIOSSION_REQUEST_CODE);

            return  false;
        }

        return  true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        for(int i : grantResults){

            if(i == -1){

                AlertDialog dialog = new AlertDialog.Builder(context)
                        .setTitle("Informasi")
                        .setMessage("Mohon ijinkan semua akses untuk menunjang fitur aplikasi")
                        .setNeutralButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                onBackPressed();
                            }
                        })
                        .show();
            }
        }
    }

    private void initUI() {

        ivProfileMain = (ImageView) findViewById(R.id.iv_profile_main);
        ivProfile = (ImageView) findViewById(R.id.iv_profile);
        tvNama = (TextView) findViewById(R.id.tv_nama);
        tvKontak = (TextView) findViewById(R.id.tv_kontak);
        tvPassword = (TextView) findViewById(R.id.tv_password);
        ivNama = (ImageView) findViewById(R.id.iv_nama);
        ivKontak = (ImageView) findViewById(R.id.iv_kontak);
        ivPassword = (ImageView) findViewById(R.id.iv_pass);
        rlLogout = (RelativeLayout) findViewById(R.id.rl_logout);

    }

    private void initEvent() {

        ivNama.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showLabel("Masukkan Nama Anda", "", "",1, session.getNama(), false);
            }
        });

        ivKontak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showLabel("Masukkan Kontak Anda", "", "",2, session.getKontak(), false);
            }
        });

        ivPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showLabel("Masukkan Password Lama", "Masukkan Password Baru", "Ketik Ulang Password Baru", 3, "", true);
            }
        });

        ivProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Options options = Options.init()
                        .setRequestCode(imageRequestCode)                                    //Request code for activity results
                        .setCount(1)                                                         //Number of images to restict selection count
                        .setFrontfacing(false)                                               //Front Facing camera on start
                        .setImageQuality(ImageQuality.HIGH)                                  //Image Quality
                        .setScreenOrientation(Options.SCREEN_ORIENTATION_PORTRAIT)           //Orientaion
                        .setPath("/yia/images");                                             //Custom Path For Image Storage

                Pix.start(DetailAkunActivity.this, options);
            }
        });

        rlLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog dialog = new AlertDialog.Builder(context)
                        .setTitle("Konfirmasi")
                        .setMessage("Anda yakin ingin logout?")
                        .setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                Intent intent = new Intent(context, LoginActivity.class);
                                session.logoutUser(intent);
                            }
                        })
                        .show();


            }
        });
    }

    private void initData() {

        tvNama.setText(session.getNama());
        tvKontak.setText(session.getKontak());
        imageUtils.LoadCircleRealImage(session.getFoto(), ivProfileMain);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == imageRequestCode) {
            ArrayList<String> returnValue = data.getStringArrayListExtra(Pix.IMAGE_RESULTS);

            if(returnValue.size() > 0) {

                File f = new File(returnValue.get(0));
                Bitmap b = new BitmapDrawable(context.getResources(), f.getAbsolutePath()).getBitmap();
                imageUtils.LoadCircleRealImage((ImageUtils.getImageUri(context, b)).toString(), ivProfileMain);

                saveDataAkun(session.getNama(), session.getKontak(), ImageUtils.convert(b));
            }
        }
    }

    private void saveDataAkun(final String nama,final String kontak,final String foto) {

        dialogBox.showDialog(false);

        JSONObject jBody = new JSONObject();
        try {
            jBody.put("id", session.getId());
            jBody.put("nik", session.getNik());
            jBody.put("nama", nama);
            jBody.put("no_telp", kontak);
            jBody.put("email", session.getEmail());
            jBody.put("foto_profil", foto);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        new ApiVolley(context, jBody, "POST", ServerURL.updateAkun, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {

                dialogBox.dismissDialog();
                try {

                    JSONObject response = new JSONObject(result);
                    String status = response.getJSONObject("metadata").getString("status");
                    String message = response.getJSONObject("metadata").getString("message");

                    Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                    if(iv.parseNullInteger(status) == 200){

                        JSONObject jo = response.getJSONObject("response");

                        session.saveSession(
                                jo.getString("nama")
                                , jo.getString("no_telp")
                                , jo.getString("foto_profil"));

                        tvNama.setText(jo.getString("nama"));
                        tvKontak.setText(jo.getString("no_telp"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    View.OnClickListener clickListener = new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            dialogBox.dismissDialog();
                            saveDataAkun(nama, kontak, foto);

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
                        saveDataAkun(nama, kontak, foto);

                    }
                };

                dialogBox.showDialog(clickListener, "Ulangi Proses", "Terjadi kesalahan saat mengambil data");
            }
        });
    }

    private void savePassword(final String passlama,final String passBaru) {

        dialogBox.showDialog(false);

        JSONObject jBody = new JSONObject();

        try {
            jBody.put("id", session.getId());
            jBody.put("password_lama", passlama);
            jBody.put("password_baru", passBaru);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        new ApiVolley(context, jBody, "POST", ServerURL.changePassword, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {

                dialogBox.dismissDialog();
                try {

                    JSONObject response = new JSONObject(result);
                    String status = response.getJSONObject("metadata").getString("status");
                    String message = response.getJSONObject("metadata").getString("message");

                    Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                    if(iv.parseNullInteger(status) == 200){


                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    View.OnClickListener clickListener = new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            dialogBox.dismissDialog();
                            savePassword(passlama, passBaru);

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
                        savePassword(passlama, passBaru);

                    }
                };

                dialogBox.showDialog(clickListener, "Ulangi Proses", "Terjadi kesalahan saat mengambil data");
            }
        });
    }

    private void showLabel(String label, String label1, String label2, final int jenis, String value, boolean isPassword){

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater) ((Activity)context).getSystemService(LAYOUT_INFLATER_SERVICE);
        View viewDialog = inflater.inflate(R.layout.dialog_input, null);
        builder.setView(viewDialog);
        builder.setCancelable(false);

        final TextView tvLabel = (TextView) viewDialog.findViewById(R.id.tv_label);
        final EditText edtValue = (EditText) viewDialog.findViewById(R.id.edt_value);

        final TextView tvLabel1 = (TextView) viewDialog.findViewById(R.id.tv_label1);
        final EditText edtValue1 = (EditText) viewDialog.findViewById(R.id.edt_value1);

        final TextView tvLabel2 = (TextView) viewDialog.findViewById(R.id.tv_label2);
        final EditText edtValue2 = (EditText) viewDialog.findViewById(R.id.edt_value2);

        final LinearLayout llCustom = (LinearLayout) viewDialog.findViewById(R.id.ll_custom);

        tvLabel.setText(label);
        tvLabel1.setText(label1);
        tvLabel2.setText(label2);

        edtValue.setText(value);

        if(isPassword){

            llCustom.setVisibility(View.VISIBLE);
            edtValue.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            edtValue1.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            edtValue2.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        }else{

            llCustom.setVisibility(View.GONE);
            edtValue.setInputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
            edtValue1.setInputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
            edtValue2.setInputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
        }

        final TextView tvBatal = (TextView) viewDialog.findViewById(R.id.tv_batal);
        final TextView tvSimpan = (TextView) viewDialog.findViewById(R.id.tv_simpan);

        AlertDialog alert = builder.create();
        alert.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        alert.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        alert.getWindow().setGravity(Gravity.BOTTOM);

        final AlertDialog alertDialogs = alert;

        tvBatal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view2) {

                if(alertDialogs != null) {

                    try {
                        alertDialogs.dismiss();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        });

        tvSimpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view2) {

                if(alertDialogs != null) {

                    if(edtValue.getText().toString().length() == 0){

                        edtValue.setError("Harap diisi");
                        edtValue.requestFocus();
                        return;
                    }else{

                        edtValue.setError(null);
                    }

                    if(jenis == 1) {

                        Bitmap b = ((BitmapDrawable)ivProfileMain.getDrawable()).getBitmap();
                        saveDataAkun(edtValue.getText().toString(), session.getKontak(), ImageUtils.convert(b));
                    }else if (jenis == 2){

                        Bitmap b = ((BitmapDrawable)ivProfileMain.getDrawable()).getBitmap();
                        saveDataAkun(session.getNama(), edtValue.getText().toString(), ImageUtils.convert(b));
                    }else if (jenis == 3){

                        if(edtValue1.getText().toString().length() == 0){

                            edtValue1.setError("Harap diisi");
                            edtValue1.requestFocus();
                            return;
                        }else{

                            edtValue1.setError(null);
                        }

                        if(edtValue2.getText().toString().length() == 0){

                            edtValue2.setError("Harap diisi");
                            edtValue2.requestFocus();
                            return;
                        }else{

                            edtValue2.setError(null);
                        }

                        if(!edtValue1.getText().toString().equals(edtValue2.getText().toString())){

                            edtValue2.setError("Password ulang tidak sama");
                            edtValue2.requestFocus();
                            return;
                        }else{
                            edtValue2.setError(null);
                        }

                        savePassword(edtValue.getText().toString(), edtValue1.getText().toString());
                    }

                    try {
                        alertDialogs.dismiss();
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                }
            }
        });

        try {

            alert.show();
        }catch (Exception e){
            e.printStackTrace();
        }
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
