package co.id.gmedia.yia.ActSalesSurvey;

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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import co.id.gmedia.coremodul.ApiVolley;
import co.id.gmedia.coremodul.DialogBox;
import co.id.gmedia.coremodul.SessionManager;
import co.id.gmedia.yia.ActSalesSurvey.Adapter.SurveyRiwayatAdapter;
import co.id.gmedia.yia.Model.DonaturModel;
import co.id.gmedia.yia.Model.SurveyRiwayatModel;
import co.id.gmedia.yia.R;
import co.id.gmedia.yia.Utils.AppRequestCallback;
import co.id.gmedia.yia.Utils.Converter;
import co.id.gmedia.yia.Utils.JSONBuilder;
import co.id.gmedia.yia.Utils.ServerURL;

public class SurveyRiwayatFragment extends Fragment {

    private Activity activity;
    private SurveyRiwayatAdapter adapter;
    private List<SurveyRiwayatModel> listDonatur = new ArrayList<>();

    private SessionManager session;
    private DialogBox dialogBox;

    public SurveyRiwayatFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        activity = getActivity();
        View v = inflater.inflate(R.layout.fragment_survey_riwayat, container, false);

        RecyclerView rv_history = v.findViewById(R.id.rv_history);
        rv_history.setItemAnimator(new DefaultItemAnimator());
        rv_history.setLayoutManager(new LinearLayoutManager(activity));
        adapter = new SurveyRiwayatAdapter(activity, listDonatur);
        rv_history.setAdapter(adapter);

        session = new SessionManager(activity);
        dialogBox = new DialogBox(activity);

        return v;
    }

    @Override
    public void onResume() {
        loadHistory();
        super.onResume();
    }

    private void loadHistory(){
        dialogBox.showDialog(false);
        JSONBuilder body = new JSONBuilder();
        body.add("id_sales", session.getId());
        body.add("tgl_awal", Converter.DToFirstDayOfMonthString(new Date()));
        body.add("tgl_akhir", Converter.DToString(new Date()));
        body.add("keywoard", "");
        body.add("status", "0");

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
                                listDonatur.add(new SurveyRiwayatModel(new DonaturModel(donatur.getString("id"),
                                        donatur.getString("id_donatur"), donatur.getString("nama"),
                                        donatur.getString("alamat"), donatur.getString("kontak"),
                                        donatur.getInt("status") == 0), "", "", new Date(), ""));
                            }

                            adapter.notifyDataSetChanged();
                        }
                        catch (JSONException e){
                            Log.e("json_log", e.getMessage());
                            View.OnClickListener clickListener = new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    dialogBox.dismissDialog();
                                    loadHistory();
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
                    }
                }));
    }
}
