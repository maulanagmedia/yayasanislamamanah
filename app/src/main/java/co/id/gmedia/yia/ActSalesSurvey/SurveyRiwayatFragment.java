package co.id.gmedia.yia.ActSalesSurvey;

import android.app.Activity;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import co.id.gmedia.coremodul.ApiVolley;
import co.id.gmedia.coremodul.DialogBox;
import co.id.gmedia.coremodul.SessionManager;
import co.id.gmedia.yia.ActSalesSurvey.Adapter.SurveyRiwayatAdapter;
import co.id.gmedia.yia.Model.SurveyRiwayatModel;
import co.id.gmedia.yia.R;
import co.id.gmedia.yia.Utils.AppRequestCallback;
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

        loadHistory();

        return v;
    }

    private void loadHistory(){
        dialogBox.showDialog(false);
        JSONBuilder body = new JSONBuilder();
        body.add("id_sales", session.getId());
        body.add("tgl_awal", "");
        body.add("tgl_akhir", "");
        body.add("keywoard", "");

        new ApiVolley(activity, body.create(), "POST", ServerURL.getRencanaKerjaSurvey,
                new AppRequestCallback(new AppRequestCallback.ResponseListener() {
                    @Override
                    public void onSuccess(String response, String message) {
                        dialogBox.dismissDialog();
                        try{
                            JSONObject object = new JSONObject(response);
                        }
                        catch (JSONException e){
                            e.printStackTrace();
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
