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

public class LoginActivity extends AppCompatActivity {

    private ImageView ivVisibility;
    private Button btnMasuk;
    private EditText edtUsername, edtPassword;
    private boolean visibleTapped = true;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        context = this;
        initUI();
        initEvent();
    }

    private void initUI() {

        edtUsername = (EditText) findViewById(R.id.edt_username);
        edtPassword = (EditText) findViewById(R.id.edt_password);
        ivVisibility = (ImageView) findViewById(R.id.iv_visibility);
        btnMasuk = (Button) findViewById(R.id.btn_masuk);
        visibleTapped = true;
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

                Intent intent;
                switch (edtUsername.getText().toString()){

                    case "promo" :
                        intent = new Intent(context, HomeActivity.class);
                        break;
                    case "sosial" :
                        intent = new Intent(context, HomeSocialSalesActivity.class);
                        break;
                    default:
                        intent = new Intent(context, HomeActivity.class);
                        break;
                }

                startActivity(intent);
                finish();
            }
        });
    }
}
