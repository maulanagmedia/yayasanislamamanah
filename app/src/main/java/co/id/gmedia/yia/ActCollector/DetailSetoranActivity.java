package co.id.gmedia.yia.ActCollector;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import co.id.gmedia.coremodul.ApiVolley;
import co.id.gmedia.coremodul.CustomModel;
import co.id.gmedia.coremodul.DialogBox;
import co.id.gmedia.coremodul.FormatItem;
import co.id.gmedia.coremodul.ItemValidation;
import co.id.gmedia.coremodul.SessionManager;
import co.id.gmedia.yia.ActCollector.Adapter.ListDetailSetoranAdapter;
import co.id.gmedia.yia.R;
import co.id.gmedia.yia.Utils.ServerURL;

public class DetailSetoranActivity extends AppCompatActivity {

    private Context context;
    private SessionManager session;
    private ItemValidation iv = new ItemValidation();
    private ListView lvSetoran;
    private DialogBox dialogBox;
    private List<CustomModel> listData = new ArrayList<>();
    private ListDetailSetoranAdapter adapter;
    private String tgl = "", filter = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_setoran);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );

        setTitle("Detail Setoran");

        context = this;
        session = new SessionManager(context);

        Bundle bundle = getIntent().getExtras();
        if(bundle != null){

            tgl = bundle.getString("tgl","");
            filter = bundle.getString("filter","");
        }

        initUI();
        initData();
    }

    private void initUI() {

        lvSetoran = (ListView) findViewById(R.id.lv_setoran);

        adapter = new ListDetailSetoranAdapter((Activity) context, listData);
        lvSetoran.setAdapter(adapter);
        dialogBox = new DialogBox(context);
    }

    private void initData() {

        dialogBox.showDialog(false);

        JSONObject jBody = new JSONObject();
        try {
            jBody.put("id_sales", session.getId());
            jBody.put("tgl", iv.ChangeFormatDateString(tgl, FormatItem.formatDateDisplay, FormatItem.formatDate));
            jBody.put("filter", filter);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        new ApiVolley(context, jBody, "POST", filter.isEmpty() ? ServerURL.getRekapSetoranByOut : ServerURL.getRekapSetoranByJenis, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {

                dialogBox.dismissDialog();
                listData.clear();
                try {

                    JSONObject response = new JSONObject(result);
                    String status = response.getJSONObject("metadata").getString("status");
                    String message = response.getJSONObject("metadata").getString("message");

                    if(iv.parseNullInteger(status) == 200){

                        JSONArray ja = response.getJSONArray("response");
                        for(int i = 0; i < ja.length(); i ++){

                            JSONObject jo = ja.getJSONObject(i);

                            if(filter.isEmpty()) { // Donatur Out

                                listData.add(
                                        new CustomModel(
                                                jo.getString("id")
                                                ,jo.getString("nama")
                                                ,jo.getString("alamat")
                                                ,jo.getString("kontak")
                                                ,jo.getString("tgl_berhenti")
                                                ,jo.getString("keterangan")
                                                ,"1"
                                        )
                                );
                            }else{

                                listData.add(
                                        new CustomModel(
                                                jo.getString("id")
                                                ,jo.getString("nama")
                                                ,jo.getString("alamat")
                                                ,jo.getString("kontak")
                                                ,jo.getString("doansi")
                                                ,""
                                                ,"0"
                                        )
                                );
                            }

                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    View.OnClickListener clickListener = new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            dialogBox.dismissDialog();
                            initData();

                        }
                    };

                    dialogBox.showDialog(clickListener, "Ulangi Proses", "Terjadi kesalahan saat mengambil data");
                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onError(String result) {

                dialogBox.dismissDialog();
                View.OnClickListener clickListener = new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        dialogBox.dismissDialog();
                        initData();

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
