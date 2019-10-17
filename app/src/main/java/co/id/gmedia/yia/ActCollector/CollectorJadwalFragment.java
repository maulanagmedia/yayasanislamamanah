package co.id.gmedia.yia.ActCollector;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import co.id.gmedia.coremodul.ApiVolley;
import co.id.gmedia.coremodul.DialogBox;
import co.id.gmedia.coremodul.SessionManager;
import co.id.gmedia.yia.ActCollector.Adapter.JadwalKunjunganCollectorAdapter;
import co.id.gmedia.yia.Model.DonaturModel;
import co.id.gmedia.yia.R;
import co.id.gmedia.yia.Utils.AppRequestCallback;
import co.id.gmedia.yia.Utils.Converter;
import co.id.gmedia.yia.Utils.JSONBuilder;
import co.id.gmedia.yia.Utils.ServerURL;

public class CollectorJadwalFragment extends Fragment {

    private Activity activity;
    private JadwalKunjunganCollectorAdapter adapter;
    private List<DonaturModel> listDonatur = new ArrayList<>();

    //private TextView txt_tanggal;

    private DialogBox dialogBox;
    private Button btnRekapSetoran;

    public CollectorJadwalFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        activity = getActivity();
        View v = inflater.inflate(R.layout.fragment_collector_jadwal, container, false);

        dialogBox = new DialogBox(activity);
        //txt_tanggal = v.findViewById(R.id.txt_tanggal);

        RecyclerView rv_jadwal = v.findViewById(R.id.rv_jadwal);
        rv_jadwal.setItemAnimator(new DefaultItemAnimator());
        rv_jadwal.setLayoutManager(new LinearLayoutManager(activity));
        btnRekapSetoran = (Button) v.findViewById(R.id.btn_rekap_setoran);
        adapter = new JadwalKunjunganCollectorAdapter(activity, listDonatur);
        rv_jadwal.setAdapter(adapter);

        initEvent();
        loadData();

        return v;
    }

    private void initEvent() {

        btnRekapSetoran.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(activity, ListRekapSetoranActivity.class);
                startActivity(intent);
            }
        });
    }

    private void loadData(){
        //txt_tanggal.setText(Converter.getDateString(new Date()));
        dialogBox.showDialog(false);
        JSONBuilder body = new JSONBuilder();
        body.add("collector", new SessionManager(activity).getId());
        body.add("keyword", "");

        new ApiVolley(activity, body.create(), "POST", ServerURL.getRencanaKerjaCollector,
                new AppRequestCallback(new AppRequestCallback.ResponseListener() {
                    @Override
                    public void onSuccess(String response, String message) {
                        dialogBox.dismissDialog();
                        try{
                            listDonatur.clear();
                            JSONArray object = new JSONArray(response);
                            for(int i = 0; i < object.length(); i++){
                                JSONObject donatur = object.getJSONObject(i);
                                listDonatur.add(new DonaturModel(
                                        donatur.getString("id")
                                        ,donatur.getString("id_donatur")
                                        ,donatur.getString("nama")
                                        ,donatur.getString("alamat")
                                        ,donatur.getString("kontak")
                                        ,donatur.getString("lat")
                                        ,donatur.getString("long")
                                        ,donatur.getString("status").equals("0")
                                ));
                            }

                            adapter.notifyDataSetChanged();

                            if(activity instanceof CollectorActivity){
                                ((CollectorActivity)activity).updateJumlah(object.length());
                            }
                        }
                        catch (JSONException e){
                            Log.e("json_log", e.getMessage());
                            View.OnClickListener clickListener = new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    dialogBox.dismissDialog();
                                    loadData();
                                }
                            };

                            dialogBox.showDialog(clickListener, "Ulangi Proses",
                                    "Terjadi kesalahan saat mengambil data");
                        }
                    }

                    @Override
                    public void onEmpty(String message) {
                        dialogBox.dismissDialog();
                        if(activity instanceof CollectorActivity){
                            ((CollectorActivity)activity).updateJumlah(0);
                        }
                    }

                    @Override
                    public void onFail(String message) {
                        dialogBox.dismissDialog();
                        View.OnClickListener clickListener = new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialogBox.dismissDialog();
                                loadData();

                            }
                        };

                        dialogBox.showDialog(clickListener, "Ulangi Proses",
                                "Terjadi kesalahan saat mengambil data");
                    }
                }));
    }
}
