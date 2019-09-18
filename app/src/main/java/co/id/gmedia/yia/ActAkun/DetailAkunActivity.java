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

import com.fxn.pix.Options;
import com.fxn.pix.Pix;
import com.fxn.utility.ImageQuality;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import co.id.gmedia.coremodul.ImageUtils;
import co.id.gmedia.yia.R;

public class DetailAkunActivity extends AppCompatActivity {

    private Context context;
    private ImageView ivProfileMain, ivProfile, ivNama, ivKontak, ivPassword;
    private TextView tvNama, tvKontak, tvPassword;
    private RelativeLayout rlLogout;

    private int imageRequestCode = 100;

    //permission
    private String[] appPermission =  {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
    };
    private final int PERMIOSSION_REQUEST_CODE = 1240;

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

        if (checkPermission()){

        }

        initUI();
        initEvent();
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

                showLabel("Masukkan Nama Anda", "", "", false);
            }
        });

        ivKontak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showLabel("Masukkan Kontak Anda", "", "", false);
            }
        });

        ivPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showLabel("Masukkan Password Lama", "Masukkan Password Baru", "Ketik Ulang Password Baru", true);
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
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == imageRequestCode) {
            ArrayList<String> returnValue = data.getStringArrayListExtra(Pix.IMAGE_RESULTS);

            if(returnValue.size() > 0) {

                ImageUtils ui = new ImageUtils();
                File f = new File(returnValue.get(0));
                Bitmap d = new BitmapDrawable(context.getResources(), f.getAbsolutePath()).getBitmap();
                ui.LoadCircleRealImage((ImageUtils.getImageUri(context, d)).toString(), ivProfileMain);
            }
        }
    }

    private void showLabel(String label, String label1, String label2, boolean isPassword){

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
