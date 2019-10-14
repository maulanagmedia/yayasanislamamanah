package co.id.gmedia.yia.ActCollector;

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
import java.util.List;

import co.id.gmedia.coremodul.ApiVolley;
import co.id.gmedia.coremodul.DialogBox;
import co.id.gmedia.coremodul.SessionManager;
import co.id.gmedia.yia.ActCollector.Adapter.HistoryCollectorAdapter;
import co.id.gmedia.yia.Model.DonaturModel;
import co.id.gmedia.yia.Model.HistoryDonaturModel;
import co.id.gmedia.yia.R;
import co.id.gmedia.yia.Utils.AppRequestCallback;
import co.id.gmedia.yia.Utils.JSONBuilder;
import co.id.gmedia.yia.Utils.ServerURL;

/**
 * A simple {@link Fragment} subclass.
 */
public class CollectorHistoryFragment extends Fragment {

    private Activity activity;
    private HistoryCollectorAdapter adapter;
    private List<DonaturModel> listDonatur = new ArrayList<>();

    private DialogBox dialogBox;

    public CollectorHistoryFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        activity = getActivity();
        View v = inflater.inflate(R.layout.fragment_collector_history, container, false);

        RecyclerView rv_history = v.findViewById(R.id.rv_history);
        rv_history.setItemAnimator(new DefaultItemAnimator());
        rv_history.setLayoutManager(new LinearLayoutManager(activity));
        adapter = new HistoryCollectorAdapter(activity, listDonatur);
        rv_history.setAdapter(adapter);

        dialogBox = new DialogBox(activity);

        loadHistory();

        return v;
    }

    private void loadHistory(){
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
                                listDonatur.add(new DonaturModel(donatur.getString("id"),
                                        donatur.getString("id_donatur"), donatur.getString("nama"),
                                        donatur.getString("alamat"), donatur.getString("kontak"),
                                        donatur.getString("status").equals("Sudah dikunjungi")));
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
                }));
    }
}
