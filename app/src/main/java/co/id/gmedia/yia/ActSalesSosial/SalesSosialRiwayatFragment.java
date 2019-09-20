package co.id.gmedia.yia.ActSalesSosial;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import co.id.gmedia.coremodul.CustomModel;
import co.id.gmedia.coremodul.DialogBox;
import co.id.gmedia.coremodul.FormatItem;
import co.id.gmedia.coremodul.ItemValidation;
import co.id.gmedia.yia.ActSalesSosial.Adapter.ListRiwayatSSAdapter;
import co.id.gmedia.yia.R;

public class SalesSosialRiwayatFragment extends Fragment {

    private View root;
    private Context context;
    private RelativeLayout rlDate1, rlDate2;
    private String dateFrom = "", dateTo = "";
    private ItemValidation iv = new ItemValidation();
    private TextView tvDate1, tvDate2;
    private DialogBox dialogBox;
    private ListView lvRiwayat;
    private ListRiwayatSSAdapter adapter;
    private List<CustomModel> listData = new ArrayList<>();

    public SalesSosialRiwayatFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        root = inflater.inflate(R.layout.fragment_list_riwayat_s, container, false);

        context = root.getContext();
        initUI();
        initEvent();
        return root;
    }

    private void initUI() {

        rlDate1 = (RelativeLayout) root.findViewById(R.id.rl_date1);
        rlDate2 = (RelativeLayout) root.findViewById(R.id.rl_date2);
        tvDate1 = (TextView) root.findViewById(R.id.tv_date1);
        tvDate2 = (TextView) root.findViewById(R.id.tv_date2);

        lvRiwayat = (ListView) root.findViewById(R.id.lv_riwayat);

        adapter = new ListRiwayatSSAdapter((Activity) context, listData);
        lvRiwayat.setAdapter(adapter);

        dateFrom = iv.sumDate(iv.getCurrentDate(FormatItem.formatDateDisplay), -1, FormatItem.formatDateDisplay) ;
        dateTo = iv.getCurrentDate(FormatItem.formatDateDisplay);

        tvDate1.setText(dateFrom);
        tvDate2.setText(dateTo);
        dialogBox = new DialogBox(context);

        listData.add(new CustomModel("1", "Rokhim", "Jl. Mataram", "080989999", "2019-09-20 10:10:10", "1", "Donatur setuju mendaftar"));
        listData.add(new CustomModel("2", "Victor", "Jl. Pandanaran", "080989999", "2019-09-20 10:10:10", "0", "Donatur menolak mendaftar"));
        listData.add(new CustomModel("3", "Maul", "Jl. Pamularsih", "", "2019-09-20 10:10:10", "1", "Donatur setuju mendaftar"));
        listData.add(new CustomModel("4", "Bayu", "Jl. Singosari", "", "2019-09-20 10:10:10", "1", "Donatur setuju mendaftar"));
        listData.add(new CustomModel("5", "Bayu", "Jl. Singosari", "", "2019-09-20 10:10:10", "1", "Donatur setuju mendaftar"));
        listData.add(new CustomModel("6", "Bayu", "Jl. Singosari", "", "2019-09-20 10:10:10", "1", "Donatur setuju mendaftar"));
        listData.add(new CustomModel("7", "Bayu", "Jl. Singosari", "", "2019-09-20 10:10:10", "1", "Donatur setuju mendaftar"));
        listData.add(new CustomModel("8", "Bayu", "Jl. Singosari", "", "2019-09-20 10:10:10", "1", "Donatur setuju mendaftar"));

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

}
