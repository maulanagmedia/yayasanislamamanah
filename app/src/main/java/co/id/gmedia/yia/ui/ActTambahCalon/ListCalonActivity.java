package co.id.gmedia.yia.ui.ActTambahCalon;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.media.Image;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import co.id.gmedia.coremodul.CustomModel;
import co.id.gmedia.coremodul.SessionManager;
import co.id.gmedia.yia.R;

public class ListCalonActivity extends AppCompatActivity {

    private Context context;
    private SessionManager session;
    private EditText edtKeyword, edtTanggalAwal, edtTanggalAkhir;
    private ImageView ivNext;
    private ListView lvData;
    private Button btnAdd;
    private List<CustomModel> listData = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_calon);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );

        context = this;
        session = new SessionManager(context);
        initUI();
    }

    private void initUI() {

        edtKeyword = (EditText) findViewById(R.id.edt_keyword);
        edtTanggalAwal = (EditText) findViewById(R.id.edt_tanggal_awal);
        edtTanggalAkhir = (EditText) findViewById(R.id.edt_tanggal_akhir);
        ivNext = (ImageView) findViewById(R.id.iv_next);
        lvData = (ListView) findViewById(R.id.lv_data);
        btnAdd = (Button) findViewById(R.id.btn_add);

        listData = new ArrayList<>();
        /*adapter = new ListPengeluaranAdapter((Activity) context, listData);
        lvPengeluaran.setAdapter(adapter);*/
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
