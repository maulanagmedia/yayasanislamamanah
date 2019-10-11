package co.id.gmedia.yia.ActSalesBrosur;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListView;
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
import co.id.gmedia.coremodul.CustomModel;
import co.id.gmedia.coremodul.DialogBox;
import co.id.gmedia.coremodul.FormatItem;
import co.id.gmedia.coremodul.ItemValidation;
import co.id.gmedia.coremodul.SessionManager;
import co.id.gmedia.yia.ActSalesBrosur.Adapter.ListSalesBrosurAdapter;
import co.id.gmedia.yia.R;
import co.id.gmedia.yia.Utils.ServerURL;


public class SalesBrosurRiwayatFragment extends Fragment {

    private View root;
    private Context context;
    private DialogBox dialogBox;
    private SessionManager session;
    private ListView lvCalon;
    private List<CustomModel> listData = new ArrayList<>();
    private ListSalesBrosurAdapter adapter;
    private RelativeLayout rlDate1, rlDate2;
    private String dateFrom = "", dateTo = "";
    private ItemValidation iv = new ItemValidation();
    private TextView tvDate1, tvDate2;
    private EditText edtSearch;

    public SalesBrosurRiwayatFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_sales_brosur_riwayat, container, false);
        context = root.getContext();
        session = new SessionManager(context);
        initUI();
        initEvent();
        initData();
        return root;
    }

    private void initUI() {

        edtSearch = (EditText) root.findViewById(R.id.edt_search);
        rlDate1 = (RelativeLayout) root.findViewById(R.id.rl_date1);
        rlDate2 = (RelativeLayout) root.findViewById(R.id.rl_date2);
        tvDate1 = (TextView) root.findViewById(R.id.tv_date1);
        tvDate2 = (TextView) root.findViewById(R.id.tv_date2);

        dateFrom = iv.sumDate(iv.getCurrentDate(FormatItem.formatDateDisplay), -1, FormatItem.formatDateDisplay) ;
        dateTo = iv.getCurrentDate(FormatItem.formatDateDisplay);

        tvDate1.setText(dateFrom);
        tvDate2.setText(dateTo);
        dialogBox = new DialogBox(context);

        lvCalon = (ListView) root.findViewById(R.id.lv_calon);

        adapter = new ListSalesBrosurAdapter((Activity) context, listData);
        lvCalon.setAdapter(adapter);

        /*listData.add(new CustomModel("1", "Rokhim", "Jl. Mataram", "080989999", "2019-09-20 10:10:10"));
        listData.add(new CustomModel("2", "Victor", "Jl. Pandanaran", "080989999", "2019-09-20 10:10:10"));
        listData.add(new CustomModel("3", "Maul", "Jl. Pamularsih", "", "2019-09-20 10:10:10"));
        listData.add(new CustomModel("4", "Bayu", "Jl. Singosari", "", "2019-09-20 10:10:10"));
        listData.add(new CustomModel("5", "Bayu", "Jl. Singosari", "", "2019-09-20 10:10:10"));
        listData.add(new CustomModel("6", "Bayu", "Jl. Singosari", "", "2019-09-20 10:10:10"));
        listData.add(new CustomModel("7", "Bayu", "Jl. Singosari", "", "2019-09-20 10:10:10"));
        listData.add(new CustomModel("8", "Bayu", "Jl. Singosari", "", "2019-09-20 10:10:10"));*/

        adapter.notifyDataSetChanged();
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


    }

    private void initData() {

        dialogBox.showDialog(false);

        JSONObject jBody = new JSONObject();
        try {
            jBody.put("id_sales", session.getId());
            jBody.put("tgl_awal", iv.ChangeFormatDateString(dateFrom, FormatItem.formatDateDisplay, FormatItem.formatDate));
            jBody.put("tgl_akhir", iv.ChangeFormatDateString(dateTo, FormatItem.formatDateDisplay, FormatItem.formatDate));
            jBody.put("keyword", edtSearch.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        new ApiVolley(context, jBody, "POST", ServerURL.getCalonDonatur, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {

                dialogBox.dismissDialog();
                try {

                    JSONObject response = new JSONObject(result);
                    String status = response.getJSONObject("metadata").getString("status");
                    String message = response.getJSONObject("metadata").getString("message");

                    if(iv.parseNullInteger(status) == 200){

                        JSONArray ja = response.getJSONArray("response");
                        for(int i = 0; i < ja.length(); i ++){

                            JSONObject jo = ja.getJSONObject(i);
                            listData.add(
                                    new CustomModel(
                                            jo.getString("id")
                                            ,jo.getString("nama")
                                            ,jo.getString("alamat")
                                            ,jo.getString("kontak")
                                            ,jo.getString("insert_at")
                                    )
                            );
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
}
