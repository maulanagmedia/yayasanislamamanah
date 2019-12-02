package co.id.gmedia.yia.ActSalesSosial;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import co.id.gmedia.coremodul.ApiVolley;
import co.id.gmedia.coremodul.CustomModel;
import co.id.gmedia.coremodul.DialogBox;
import co.id.gmedia.coremodul.FormatItem;
import co.id.gmedia.coremodul.ItemValidation;
import co.id.gmedia.coremodul.SessionManager;
import co.id.gmedia.yia.ActSalesSosial.Adapter.ListJadwalSSAdapter;
import co.id.gmedia.yia.HomeSocialSalesActivity;
import co.id.gmedia.yia.R;
import co.id.gmedia.yia.Utils.AppRequestCallback;
import co.id.gmedia.yia.Utils.Converter;
import co.id.gmedia.yia.Utils.DateTimeChooser;
import co.id.gmedia.yia.Utils.GoogleLocationManager;
import co.id.gmedia.yia.Utils.JSONBuilder;
import co.id.gmedia.yia.Utils.ServerURL;

public class SalesSosialJadwalFragment extends Fragment {

    private View root;
    private Context context;
    private Activity activity;
    private DialogBox dialogBox;
    private List<CustomModel> listData = new ArrayList<>();
    private ListJadwalSSAdapter adapter;
    private TextView tvTanggal;
    private ListView lvJadwal;
    private ItemValidation iv = new ItemValidation();
    private RelativeLayout rlDate1, rlDate2;
    private TextView tvDate1, tvDate2;
    private EditText edtSearch;
    private SessionManager session;
    private String dateFrom = Converter.DToString(new Date());
    private String dateTo = Converter.DToString(new Date());
    private ImageView ivSort;
    private GoogleLocationManager locationManager;
    private boolean isLocationReloaded;
    private double lat = 0, lng = 0;

    public SalesSosialJadwalFragment() {
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
        root = inflater.inflate(R.layout.fragment_sales_sosial_jadwal, container, false);
        context = root.getContext();
        activity = getActivity();
        session = new SessionManager(context);
        initUI();
        initEvent();

        return root;
    }

    private void initUI() {

        tvTanggal = (TextView) root.findViewById(R.id.tv_tanggal);
        lvJadwal = (ListView) root.findViewById(R.id.lv_jadwal);
        rlDate1 = (RelativeLayout) root.findViewById(R.id.rl_date1);
        rlDate2 = (RelativeLayout) root.findViewById(R.id.rl_date2);
        tvDate1 = (TextView) root.findViewById(R.id.tv_date1);
        tvDate2 = (TextView) root.findViewById(R.id.tv_date2);
        edtSearch = (EditText) root.findViewById(R.id.edt_search);
        ivSort = (ImageView) root.findViewById(R.id.iv_sort);

        isLocationReloaded = false;

    }

    private void initEvent() {

        rlDate1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DateTimeChooser.getInstance().selectDate(context, new DateTimeChooser.DateTimeListener() {
                    @Override
                    public void onFinished(String dateString) {
                        dateFrom = dateString;
                        tvDate1.setText(Converter.getSlashedDateString(Converter.stringDToDate(dateString)));
                    }
                });
            }
        });

        rlDate2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DateTimeChooser.getInstance().selectDate(context, new DateTimeChooser.DateTimeListener() {
                    @Override
                    public void onFinished(String dateString) {
                        dateTo = dateString;
                        tvDate2.setText(Converter.getSlashedDateString(Converter.stringDToDate(dateString)));
                    }
                });
            }
        });

        root.findViewById(R.id.btn_proses).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initData(false);
            }
        });

        tvDate1.setText(Converter.getSlashedDateString(Converter.stringDToDate(dateFrom)));
        tvDate2.setText(Converter.getSlashedDateString(Converter.stringDToDate(dateTo)));
        dialogBox = new DialogBox(context);

        String curdate = iv.getCurrentDate(FormatItem.formatDate1);
        tvTanggal.setText(curdate);

        adapter = new ListJadwalSSAdapter((Activity) context, listData);
        lvJadwal.setAdapter(adapter);

        adapter.notifyDataSetChanged();

        locationManager = new GoogleLocationManager((AppCompatActivity) activity, new GoogleLocationManager.LocationUpdateListener() {
            @Override
            public void onChange(Location location) {

                if(isLocationReloaded){

                    isLocationReloaded = false;
                    lat = location.getLatitude();
                    lng = location.getLongitude();
                    initData(true);
                }

            }
        });

        locationManager.startLocationUpdates();

        ivSort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                isLocationReloaded = true;
                retryLocation();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        initData(false);
    }

    private void initData(final boolean withLocation) {
        dialogBox.showDialog(false);
        JSONBuilder body = new JSONBuilder();
        body.add("id_sales", session.getId());
        //body.add("tgl_awal", dateFrom);
        //body.add("tgl_akhir", dateTo);
        body.add("keyword", edtSearch.getText().toString());
        body.add("status", "1");

        if(withLocation){

            body.add("lat", lat);
            body.add("long", lng);
        }

        new ApiVolley(context, body.create(), "POST", ServerURL.getJadwalSosial,
                new AppRequestCallback(new AppRequestCallback.ResponseListener() {
                    @Override
                    public void onSuccess(String response, String message) {
                        dialogBox.dismissDialog();
                        try{
                            listData.clear();
                            JSONArray obj = new JSONArray(response);
                            for(int i = 0; i < obj.length(); i++){
                                JSONObject jadwal = obj.getJSONObject(i);
                                listData.add(new CustomModel(
                                        jadwal.getString("id")
                                        ,jadwal.getString("nama")
                                        ,jadwal.getString("alamat")
                                        ,jadwal.getString("kontak")
                                        ,"tanggal"
                                        ,jadwal.getString("status").equals("0")?"1":"0"
                                        ,jadwal.getString("lat")
                                        ,jadwal.getString("long")
                                        ,jadwal.getString("image")
                                        ,jadwal.getString("note")
                                ));
                            }

                            //Update teks jumlah di Activity
                            if(context instanceof HomeSocialSalesActivity){
                                ((HomeSocialSalesActivity)context).updateJumlahJadwal(obj.length());
                            }

                            adapter.notifyDataSetChanged();
                        }
                        catch (JSONException e){
                            dialogBox.dismissDialog();
                            View.OnClickListener clickListener = new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                    dialogBox.dismissDialog();
                                    initData(withLocation);

                                }
                            };

                            dialogBox.showDialog(clickListener, "Ulangi Proses", "Terjadi kesalahan saat mengambil data");
                        }
                    }

                    @Override
                    public void onEmpty(String message) {

                        listData.clear();
                        adapter.notifyDataSetChanged();

                        //Update teks jumlah di Activity
                        if(context instanceof HomeSocialSalesActivity){
                            ((HomeSocialSalesActivity)context).updateJumlahJadwal(0);
                        }

                        dialogBox.dismissDialog();
                    }

                    @Override
                    public void onFail(String message) {

                        listData.clear();
                        adapter.notifyDataSetChanged();

                        dialogBox.dismissDialog();
                        View.OnClickListener clickListener = new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                dialogBox.dismissDialog();
                                initData(withLocation);

                            }
                        };

                        dialogBox.showDialog(clickListener, "Ulangi Proses", "Terjadi kesalahan saat mengambil data");
                    }
                }));
    }

    public void retryLocation(){
        locationManager.startLocationUpdates();
    }
}
