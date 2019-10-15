package co.id.gmedia.yia.ActSalesChecking;


import android.app.Activity;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import co.id.gmedia.yia.ActCollector.Adapter.JadwalKunjunganAdapter;
import co.id.gmedia.yia.Model.DonaturModel;
import co.id.gmedia.yia.R;
import co.id.gmedia.yia.Utils.AppRequestCallback;
import co.id.gmedia.yia.Utils.Converter;
import co.id.gmedia.yia.Utils.JSONBuilder;
import co.id.gmedia.yia.Utils.ServerURL;

/**
 * A simple {@link Fragment} subclass.
 */
public class CheckingJadwalFragment extends Fragment {

    private Activity activity;
    private JadwalKunjunganAdapter adapter;
    private List<DonaturModel> listDonatur = new ArrayList<>();

    private SessionManager session;
    private DialogBox dialogBox;

    private TextView txt_tanggal;

    public CheckingJadwalFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        activity = getActivity();
        View v = inflater.inflate(R.layout.fragment_checking_jadwal, container, false);

        txt_tanggal = v.findViewById(R.id.txt_tanggal);

        RecyclerView rv_jadwal = v.findViewById(R.id.rv_jadwal);
        rv_jadwal.setItemAnimator(new DefaultItemAnimator());
        rv_jadwal.setLayoutManager(new LinearLayoutManager(activity));
        adapter = new JadwalKunjunganAdapter(activity, listDonatur);
        rv_jadwal.setAdapter(adapter);

        session = new SessionManager(activity);
        dialogBox = new DialogBox(activity);

        return v;
    }

    @Override
    public void onResume() {
        loadData();
        loadJadwal();

        super.onResume();
    }

    private void loadData(){
        txt_tanggal.setText(Converter.getDateString(new Date()));
    }

    private void loadJadwal(){
        dialogBox.showDialog(false);
        JSONBuilder body = new JSONBuilder();
        body.add("id_sales", session.getId());
        body.add("tgl_awal", Converter.DToString(new Date()));
        body.add("tgl_akhir", Converter.DToString(new Date()));
        body.add("keywoard", "");
        body.add("status", "");

        new ApiVolley(activity, body.create(), "POST", ServerURL.getRencanaKerjaSurvey,
                new AppRequestCallback(new AppRequestCallback.ResponseListener() {
                    @Override
                    public void onSuccess(String response, String message) {
                        dialogBox.dismissDialog();
                        try{
                            listDonatur.clear();
                            JSONArray object = new JSONArray(response);
                            for(int i = 0; i < object.length(); i++){
                                JSONObject donatur = object.getJSONObject(i);
                                listDonatur.add(new DonaturModel(donatur.getString("id"),
                                        donatur.getString("id_donatur"), donatur.getString("nama"),
                                        donatur.getString("alamat"), donatur.getString("kontak"),
                                        donatur.getInt("status") == 0));
                            }

                            adapter.notifyDataSetChanged();
                        }
                        catch (JSONException e){
                            Log.e("json_log", e.getMessage());
                            View.OnClickListener clickListener = new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    dialogBox.dismissDialog();
                                    loadJadwal();
                                }
                            };

                            dialogBox.showDialog(clickListener, "Ulangi Proses",
                                    "Terjadi kesalahan saat mengambil data");
                        }
                    }

                    @Override
                    public void onEmpty(String message) {
                        dialogBox.dismissDialog();
                    }

                    @Override
                    public void onFail(String message) {
                        dialogBox.dismissDialog();
                        View.OnClickListener clickListener = new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialogBox.dismissDialog();
                                loadJadwal();
                            }
                        };

                        dialogBox.showDialog(clickListener, "Ulangi Proses", "Terjadi kesalahan saat mengambil data");
                    }
                }));
    }
}
