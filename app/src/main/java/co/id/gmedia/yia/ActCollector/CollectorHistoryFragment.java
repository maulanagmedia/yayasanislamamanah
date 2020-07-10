package co.id.gmedia.yia.ActCollector;

import android.app.Activity;
import android.app.DatePickerDialog;
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
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gmedia.modul.bluetoothprinter.Model.Transaksi;
import com.gmedia.modul.bluetoothprinter.Printer;

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
public class CollectorHistoryFragment extends Fragment implements HistoryCollectorAdapter.HistoryCollectorAdapterCalback{

    private Activity activity;
    private HistoryCollectorAdapter adapter;
    private List<DonaturModel> listDonatur = new ArrayList<>();
    private DialogBox dialogBox;
    private RecyclerView rv_history;
    private TextView tvDate1, tvDate2;
    private EditText edtSearch;
    private Button btnProcess;
    private RelativeLayout rlDate1, rlDate2;
    private String dateFrom = "", dateTo = "";
    private ItemValidation iv = new ItemValidation();
    private CheckBox cbDD, cbDL;
    String total_data="0";
    private TextView tvDonasiYa, tvDonasiTidak, tvTotalHistory;
    Printer printer;

    public CollectorHistoryFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        activity = getActivity();
        View v = inflater.inflate(R.layout.fragment_collector_history, container, false);
        printer = new Printer(activity);
        printer.startService();

        rv_history = v.findViewById(R.id.rv_history);
        edtSearch = (EditText) v.findViewById(R.id.edt_search);
        rlDate1 = (RelativeLayout) v.findViewById(R.id.rl_date1);
        rlDate2 = (RelativeLayout) v.findViewById(R.id.rl_date2);
        tvDate1 = (TextView) v.findViewById(R.id.tv_date1);
        tvDate2 = (TextView) v.findViewById(R.id.tv_date2);
        btnProcess = (Button) v.findViewById(R.id.btn_proses);
        cbDD = (CheckBox) v.findViewById(R.id.cb_dd);
        cbDL = (CheckBox) v.findViewById(R.id.cb_dl);
        tvDonasiYa = (TextView) v.findViewById(R.id.tv_donasi_ya);
        tvDonasiTidak = (TextView) v.findViewById(R.id.tv_donasi_tidak);
        tvTotalHistory = (TextView) v.findViewById(R.id.tv_jumlah_history);

        rv_history.setItemAnimator(new DefaultItemAnimator());
        rv_history.setLayoutManager(new LinearLayoutManager(activity));
        adapter = new HistoryCollectorAdapter(activity, listDonatur,this);
        rv_history.setAdapter(adapter);

        dateFrom = iv.getCurrentDate(FormatItem.formatDateDisplay);
        dateTo = iv.getCurrentDate(FormatItem.formatDateDisplay);

        tvDate1.setText(dateFrom);
        tvDate2.setText(dateTo);

        dialogBox = new DialogBox(activity);

        initEvent();
        loadHistory();
//        totalHistory();

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
                new DatePickerDialog(activity ,date , iv.parseNullInteger(yearOnly.format(dateValue)),dateValue.getMonth(),dateValue.getDate()).show();
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
                new DatePickerDialog(activity ,date , iv.parseNullInteger(yearOnly.format(dateValue)),dateValue.getMonth(),dateValue.getDate()).show();
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
        super.onResume();
        printer.startService();
    }

    private void loadHistory(){

        totalHistory();
        dialogBox.showDialog(false);

        JSONBuilder body = new JSONBuilder();
        body.add("collector", new SessionManager(activity).getId());
        body.add("tgl_awal", iv.ChangeFormatDateString(dateFrom, FormatItem.formatDateDisplay, FormatItem.formatDate));
        body.add("tgl_akhir", iv.ChangeFormatDateString(dateTo, FormatItem.formatDateDisplay, FormatItem.formatDate));
        body.add("keyword", edtSearch.getText().toString());
        body.add("data_dalam", cbDD.isChecked() ? "1" : "0");
        body.add("data_luar", cbDL.isChecked() ? "1" : "0");

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

                                DonaturModel dn = new DonaturModel(
                                        donatur.getString("id")
                                        ,donatur.getString("id_donatur")
                                        ,donatur.getString("nama")
                                        ,donatur.getString("alamat")
                                        ,donatur.getString("kontak")
                                        ,donatur.getString("status").equals("Sudah dikunjungi"));

                                dn.setNominal(donatur.getString("nominal"));
                                dn.setJenisDonatur(donatur.getString("ket_status_data"));
                                dn.setTanggal(donatur.getString("tgl_insert"));
                                listDonatur.add(dn);
                            }
//                            if(activity instanceof CollectorActivity){
//                            }

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
                        listDonatur.clear();
                        adapter.notifyDataSetChanged();
                        dialogBox.dismissDialog();
                    }

                    @Override
                    public void onFail(String message) {
                        listDonatur.clear();
                        adapter.notifyDataSetChanged();
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

    private void totalHistory(){
        JSONBuilder body = new JSONBuilder();
        body.add("collector", new SessionManager(activity).getId());
        body.add("tgl_awal", iv.ChangeFormatDateString(dateFrom, FormatItem.formatDateDisplay, FormatItem.formatDate));
        body.add("tgl_akhir", iv.ChangeFormatDateString(dateTo, FormatItem.formatDateDisplay, FormatItem.formatDate));
        body.add("keyword", edtSearch.getText().toString());
        body.add("data_dalam", cbDD.isChecked() ? "1" : "0");
        body.add("data_luar", cbDL.isChecked() ? "1" : "0");

        new ApiVolley(activity, body.create(), "POST", ServerURL.getTotalHistoryCollector,
                new AppRequestCallback(new AppRequestCallback.ResponseListener() {
                    @Override
                    public void onSuccess(String response, String message) {
                        try{
                            Log.d("response>>",String.valueOf(response));
                            JSONObject obj = new JSONObject(response);
                            total_data  = obj.getString("total");
                            ((CollectorActivity)activity).updateJumlahHistory(total_data);
                        }
                        catch (JSONException e){
                            Log.e("json_log", e.getMessage());
                        }
                    }

                    @Override
                    public void onEmpty(String message) {
                        total_data  = "0";
                        ((CollectorActivity)activity).updateJumlahHistory(total_data);
                    }

                    @Override
                    public void onFail(String message) {
                        total_data  = "0";
                    }
                })
        );
    }

    @Override
    public void onRowPrintNota(Transaksi transaksi) {
        if(!printer.bluetoothAdapter.isEnabled()) {
            printer.dialogBluetooth.show();
            Toast.makeText(activity, "Hidupkan bluetooth anda kemudian klik cetak kembali", Toast.LENGTH_LONG).show();
        }else{
            if(printer.isPrinterReady()){
                printer.print(transaksi, true);
            }else{
                Toast.makeText(activity, "Harap pilih device printer telebih dahulu", Toast.LENGTH_LONG).show();
                printer.showDevices();
            }
        }
    }
    @Override
    public void onDestroy() {

        printer.stopService();
        super.onDestroy();
    }
}
