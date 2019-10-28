package co.id.gmedia.yia.ActNotifikasi;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.WindowManager;

import co.id.gmedia.coremodul.ItemValidation;
import co.id.gmedia.coremodul.SessionManager;
import co.id.gmedia.yia.R;

public class DetailNotifikasiActivity extends AppCompatActivity {

    private Context context;
    private ItemValidation iv = new ItemValidation();
    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_notifikasi);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );

        setTitle("Notifikasi");

        context = this;
        initUI();
    }

    private void initUI() {


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
