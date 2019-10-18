package co.id.gmedia.yia.ActSalesChecking;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import co.id.gmedia.coremodul.ApiVolley;
import co.id.gmedia.coremodul.DialogBox;
import co.id.gmedia.coremodul.FormatItem;
import co.id.gmedia.coremodul.ItemValidation;
import co.id.gmedia.coremodul.SessionManager;
import co.id.gmedia.yia.ActSalesChecking.Adapter.CheckingRiwayatAdapter;
import co.id.gmedia.yia.Model.DonaturModel;
import co.id.gmedia.yia.Model.SurveyRiwayatModel;
import co.id.gmedia.yia.R;
import co.id.gmedia.yia.Utils.AppRequestCallback;
import co.id.gmedia.yia.Utils.JSONBuilder;
import co.id.gmedia.yia.Utils.ServerURL;

public class CheckingRiwayatFragment extends Fragment {

    private Activity activity;
    private Context context;
    private CheckingRiwayatAdapter adapter;
    private List<SurveyRiwayatModel> listDonatur = new ArrayList<>();
    private ItemValidation iv = new ItemValidation();

    private SessionManager session;
    private DialogBox dialogBox;
    private RelativeLayout rlDate1, rlDate2;
    private TextView tvDate1, tvDate2;
    private EditText edtSearch;
    private String dateFrom = "", dateTo = "";
    private Button btnProcess;

    public CheckingRiwayatFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        activity = getActivity();
        context = getContext();
        View v = inflater.inflate(R.layout.fragment_checking_riwayat, container, false);

        RecyclerView rv_history = v.findViewById(R.id.rv_history);
        rlDate1 = (RelativeLayout) v.findViewById(R.id.rl_date1);
        rlDate2 = (RelativeLayout) v.findViewById(R.id.rl_date2);
        tvDate1 = (TextView) v.findViewById(R.id.tv_date1);
        tvDate2 = (TextView) v.findViewById(R.id.tv_date2);
        edtSearch = (EditText) v.findViewById(R.id.edt_search);
        btnProcess = (Button) v.findViewById(R.id.btn_proses);

        rv_history.setItemAnimator(new DefaultItemAnimator());
        rv_history.setLayoutManager(new LinearLayoutManager(activity));
        adapter = new CheckingRiwayatAdapter(activity, listDonatur);
        rv_history.setAdapter(adapter);

        dateFrom = iv.getCurrentDate(FormatItem.formatDateDisplay);
        dateTo = iv.getCurrentDate(FormatItem.formatDateDisplay);

        tvDate1.setText(dateFrom);
        tvDate2.setText(dateTo);

        session = new SessionManager(activity);
        dialogBox = new DialogBox(activity);

        initEvent();

        return v;
    }

    private void initEvent() {

        rlDate1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final Calendar customDate;
                SimpleDateFormat sdf = new SimpleDateFormat(FormatItem.formatDateDisplay);

                Date dateValue = null;

                try {
                    dateValue = sdf.parse(dateFrom);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                customDate = Calendar.getInstance();
                final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int date) {
                        customDate.set(Calendar.YEAR,year);
                        customDate.set(Calendar.MONTH,month);
                        customDate.set(Calendar.DATE,date);

                        SimpleDateFormat sdFormat = new SimpleDateFormat(FormatItem.formatDateDisplay, Locale.US);
                        dateFrom = sdFormat.format(customDate.getTime());
                        tvDate1.setText(dateFrom);
                    }
                };

                SimpleDateFormat yearOnly = new SimpleDateFormat("yyyy");
                new DatePickerDialog(context ,date , iv.parseNullInteger(yearOnly.format(dateValue)),dateValue.getMonth(),dateValue.getDate()).show();
            }
        });

        rlDate2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final Calendar customDate;
                SimpleDateFormat sdf = new SimpleDateFormat(FormatItem.formatDateDisplay);

                Date dateValue = null;

                try {
                    dateValue = sdf.parse(dateTo);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                customDate = Calendar.getInstance();
                final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int date) {
                        customDate.set(Calendar.YEAR,year);
                        customDate.set(Calendar.MONTH,month);
                        customDate.set(Calendar.DATE,date);

                        SimpleDateFormat sdFormat = new SimpleDateFormat(FormatItem.formatDateDisplay, Locale.US);
                        dateTo = sdFormat.format(customDate.getTime());
                        tvDate2.setText(dateTo);
                    }
                };

                SimpleDateFormat yearOnly = new SimpleDateFormat("yyyy");
                new DatePickerDialog(context ,date , iv.parseNullInteger(yearOnly.format(dateValue)),dateValue.getMonth(),dateValue.getDate()).show();
            }
        });

        btnProcess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                loadHistory();
            }
        });
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
        body.add("tgl_awal", iv.ChangeFormatDateString(dateFrom, FormatItem.formatDateDisplay, FormatItem.formatDate));
        body.add("tgl_akhir", iv.ChangeFormatDateString(dateTo, FormatItem.formatDateDisplay, FormatItem.formatDate));
        body.add("keywoard", edtSearch.getText().toString());
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
                                listDonatur.add(new SurveyRiwayatModel(new DonaturModel(
                                        donatur.getString("id")
                                        ,donatur.getString("id_donatur")
                                        ,donatur.getString("nama"),
                                        donatur.getString("alamat")
                                        ,donatur.getString("kontak")
                                        ,donatur.getInt("status") == 0)
                                        ,""
                                        ,"",
                                        new Date(),
                                        "",
                                        donatur.getString("status_donasi")));
                            }

                            //Update teks jumlah di Activity
                            if(activity instanceof SalesCheckingActivity){
                                ((SalesCheckingActivity)activity).updateJumlahRiwayat(object.length());
                            }
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

                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onEmpty(String message) {
                        dialogBox.dismissDialog();
                        listDonatur.clear();

                        //Update teks jumlah di Activity
                        if(activity instanceof SalesCheckingActivity){
                            ((SalesCheckingActivity)activity).updateJumlahRiwayat(0);
                        }

                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onFail(String message) {
                        dialogBox.dismissDialog();
                        listDonatur.clear();
                        adapter.notifyDataSetChanged();
                    }
                }));
    }
}
