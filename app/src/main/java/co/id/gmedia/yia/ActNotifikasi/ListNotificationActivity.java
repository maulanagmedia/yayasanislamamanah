package co.id.gmedia.yia.ActNotifikasi;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import co.id.gmedia.coremodul.ApiVolley;
import co.id.gmedia.coremodul.CustomModel;
import co.id.gmedia.coremodul.DialogBox;
import co.id.gmedia.coremodul.ItemValidation;
import co.id.gmedia.coremodul.SessionManager;
import co.id.gmedia.yia.ActNotifikasi.Adapter.ListNotifikasiAdapter;
import co.id.gmedia.yia.R;
import co.id.gmedia.yia.Utils.ServerURL;

public class ListNotificationActivity extends AppCompatActivity {

    private Context context;
    private ListView lvNotif;
    private ItemValidation iv = new ItemValidation();
    private int start = 0, count = 10;
    private String keyword = "";
    private View footerList;
    private DialogBox dialogBox;
    private boolean isLoading = false;
    private ListNotifikasiAdapter adapter;
    private List<CustomModel> listData = new ArrayList<>();
    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_notification);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );

        setTitle("Notifikasi");

        context = this;
        initUI();
        initData();
    }

    private void initUI() {

        lvNotif = (ListView) findViewById(R.id.lv_notif);
        LayoutInflater li = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        footerList = li.inflate(R.layout.footer_list, null);
        dialogBox = new DialogBox(context);
        session = new SessionManager(context);

        adapter = new ListNotifikasiAdapter((Activity) context, listData);
        lvNotif.setAdapter(adapter);

        lvNotif.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {

                int threshold = 1;
                int countMerchant = lvNotif.getCount();

                if (i == SCROLL_STATE_IDLE) {
                    if (lvNotif.getLastVisiblePosition() >= countMerchant - threshold && !isLoading) {

                        isLoading = true;
                        lvNotif.addFooterView(footerList);
                        start += count;
                        initData();
                        //Log.i(TAG, "onScroll: last ");
                    }
                }
            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {

            }
        });
    }

    private void initData() {

        isLoading = true;
//        start = 0;

        if(start == 0){
            dialogBox.showDialog(false);
        }

        JSONObject jBody = new JSONObject();
        try {
            jBody.put("start", String.valueOf(start));
            jBody.put("count", String.valueOf(count));
            jBody.put("keyword", keyword);
            jBody.put("sales", session.getId());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ApiVolley request = new ApiVolley(context, jBody, "POST", ServerURL.getNotification, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {

                isLoading = false;
                dialogBox.dismissDialog();
                lvNotif.removeFooterView(footerList);

                if(start == 0){
                    listData.clear();
                }

                try {
                    JSONObject response = new JSONObject(result);
                    String status = response.getJSONObject("metadata").getString("status");
                    if(status.equals("200")) {

                        JSONArray jsonArray = response.getJSONArray("response");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jo = jsonArray.getJSONObject(i);
                            listData.add(
                                    new CustomModel(jo.getString("id"),
                                            jo.getString("pesan"),
                                            jo.getString("timestamp")));
                        }

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onError(String result) {

                lvNotif.removeFooterView(footerList);
                dialogBox.dismissDialog();
                View.OnClickListener clickListener = new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        dialogBox.dismissDialog();
                        initData();
                    }
                };

                dialogBox.showDialog(clickListener, "Ulangi Proses", "Terjadi kesalahan, harap ulangi proses");
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
