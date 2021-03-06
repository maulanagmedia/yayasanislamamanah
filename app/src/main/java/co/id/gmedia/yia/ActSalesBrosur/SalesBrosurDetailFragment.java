package co.id.gmedia.yia.ActSalesBrosur;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.fxn.pix.Options;
import com.fxn.pix.Pix;
import com.fxn.utility.ImageQuality;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import co.id.gmedia.coremodul.ApiVolley;
import co.id.gmedia.coremodul.CustomModel;
import co.id.gmedia.coremodul.DialogBox;
import co.id.gmedia.coremodul.ImageUtils;
import co.id.gmedia.coremodul.ItemValidation;
import co.id.gmedia.coremodul.OptionItem;
import co.id.gmedia.coremodul.PhotoModel;
import co.id.gmedia.coremodul.SessionManager;
import co.id.gmedia.yia.ActSalesBrosur.Adapter.SearchableSpinnerDialogOptionAdapter;
import co.id.gmedia.yia.ActSalesSosial.Adapter.ListPhotoAdapter;
import co.id.gmedia.yia.HomeActivity;
import co.id.gmedia.yia.R;
import co.id.gmedia.yia.Utils.DialogFactory;
import co.id.gmedia.yia.Utils.SearchableSpinnerDialog.SimpleObjectModel;
import co.id.gmedia.yia.Utils.ServerURL;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

public class SalesBrosurDetailFragment extends Fragment implements LocationListener {

    private View root;
    private Context context;
    private SessionManager session;
    private EditText edtNama, edtAlamat, edtKontak, edtRT, edtRW;
    private Spinner spKota, spKecamatan, spKelurahan;
    private TextView tvLatitude, tvLongitude;
    private LinearLayout llBukaMap;
    private RelativeLayout rlPhoto;
    private RecyclerView rvPhoto;
    private Button btnSimpan;
    private DialogBox dialogBox;
    private int imageRequestCode = 100;
    private List<PhotoModel> listPhoto;
    private ListPhotoAdapter adapterPhoto;
    private ItemValidation iv = new ItemValidation();

    //permission
    private String[] appPermission =  {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
    };
    private final int PERMIOSSION_REQUEST_CODE = 1240;

    // Location
    private double latitude, longitude;
    private LocationManager locationManager;
    private Criteria criteria;
    private String provider;
    private Location location;
    private final int REQUEST_PERMISSION_COARSE_LOCATION=2;
    private final int REQUEST_PERMISSION_FINE_LOCATION=3;
    public boolean isGPSEnabled = false;
    boolean isNetworkEnabled = false;
    boolean canGetLocation = false;
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 1; // 10 meters
    private static final long MIN_TIME_BW_UPDATES = 1; // 1 minute
    private TextView tvTitle;
    private String TAG = "DetailCustomer";
    private String address0 = "";
    private ProgressBar pbLoading;
    private boolean refreshMode = false;
    private boolean isMain = true;
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationCallback mLocationCallback;
    private LocationRequest mLocationRequest;
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2;
    private LocationSettingsRequest mLocationSettingsRequest;
    private SettingsClient mSettingsClient;
    private static final int REQUEST_CHECK_SETTINGS = 0x1;
    private Boolean mRequestingLocationUpdates;
    private Location mCurrentLocation;
    private List<OptionItem> listKota = new ArrayList<>(), listKecamatan = new ArrayList<>(), listKeluarahan = new ArrayList<>();
    private ArrayAdapter adapterKota, adapterKecamatan, adapterKelurahan;
    private String selectedKota = "", selectedKecamatan = "", selectedKelurahan = "";
    private ImageView ivKota, ivKecamatan, ivKelurahan;
    private TextView tvKota, tvKecamatan, tvKelurahan;
    private EditText edtKeterangan, edtWa;

    public SalesBrosurDetailFragment() {
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
        root = inflater.inflate(R.layout.fragment_sales_brosur_detail, container, false);
        context = root.getContext();

        // getLocation update by google
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
        mSettingsClient = LocationServices.getSettingsClient(context);
        mRequestingLocationUpdates = false;

        createLocationCallback();
        createLocationRequest();
        buildLocationSettingsRequest();

        if (checkPermission()){

            initLocation();
        }

        initUI();
        initEvent();
        initData();
        return root;
    }

    private void initUI() {

        session = new SessionManager(context);
        edtNama = (EditText) root.findViewById(R.id.edt_nama);
        spKota = (Spinner) root.findViewById(R.id.sp_kota);
        spKecamatan = (Spinner) root.findViewById(R.id.sp_kecamatan);
        spKelurahan = (Spinner) root.findViewById(R.id.sp_kelurahan);
        edtAlamat = (EditText) root.findViewById(R.id.edt_alamat);
        edtRT = (EditText) root.findViewById(R.id.edt_rt);
        edtRW = (EditText) root.findViewById(R.id.edt_rw);
        edtKontak = (EditText) root.findViewById(R.id.edt_kontak);
        edtWa = (EditText) root.findViewById(R.id.edt_wa);
        edtKeterangan = (EditText) root.findViewById(R.id.edt_keterangan);
        rlPhoto = (RelativeLayout) root.findViewById(R.id.rl_photo);
        rvPhoto = (RecyclerView) root.findViewById(R.id.rv_photo);

        ivKota = (ImageView) root.findViewById(R.id.iv_kota);
        tvKota = (TextView) root.findViewById(R.id.tv_kota);
        ivKecamatan = (ImageView) root.findViewById(R.id.iv_kecamatan);
        tvKecamatan = (TextView) root.findViewById(R.id.tv_kecamatan);
        ivKelurahan = (ImageView) root.findViewById(R.id.iv_kelurahan);
        tvKelurahan = (TextView) root.findViewById(R.id.tv_kelurahan);

        tvLatitude = (TextView) root.findViewById(R.id.tv_latitude);
        tvLongitude = (TextView) root.findViewById(R.id.tv_longitude);
        llBukaMap = (LinearLayout) root.findViewById(R.id.ll_buka_map);
        btnSimpan = (Button) root.findViewById(R.id.btn_simpan);

        dialogBox = new DialogBox(context);
        listPhoto = new ArrayList<>();
        LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        adapterPhoto = new ListPhotoAdapter(context, listPhoto);
        rvPhoto.setLayoutManager(layoutManager);
        rvPhoto.setAdapter(adapterPhoto);
    }

    private void initEvent() {

        rlPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Options options = Options.init()
                        .setRequestCode(imageRequestCode)                                    //Request code for activity results
                        .setCount(3)                                                         //Number of images to restict selection count
                        .setFrontfacing(false)                                               //Front Facing camera on start
                        .setImageQuality(ImageQuality.HIGH)                                  //Image Quality
                        .setScreenOrientation(Options.SCREEN_ORIENTATION_PORTRAIT)           //Orientaion
                        .setPath("/yia/images");                                             //Custom Path For Image Storage

                Pix.start(SalesBrosurDetailFragment.this, options);
            }
        });

        llBukaMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context, DetailCurrentPosActivity.class);
                startActivity(intent);
            }
        });

        btnSimpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(edtNama.getText().toString().length() == 0){

                    edtNama.setError("Nama harap diisi");
                    edtNama.requestFocus();
                    return;
                }else{

                    edtNama.setError(null);
                }

                AlertDialog dialog = new AlertDialog.Builder(context)
                        .setTitle("Konfirmasi")
                        .setMessage("Apakah anda yakin ingin menyimpan data?")
                        .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                saveData();
                            }
                        })
                        .setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .show();
            }
        });

        ivKota.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(listKota.size() > 0){

                    showDialog(listKota, 1);
                }
            }
        });

        ivKecamatan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(listKota.size() > 0){

                    showDialog(listKecamatan, 2);
                }
            }
        });

        ivKelurahan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(listKota.size() > 0){

                    showDialog(listKeluarahan, 3);
                }
            }
        });
    }

    private void showdDialog(final List<OptionItem> listData, final int type){

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater) ((Activity)context).getSystemService(LAYOUT_INFLATER_SERVICE);
        View viewDialog = inflater.inflate(R.layout.dialog_list, null);
        builder.setView(viewDialog);
        builder.setCancelable(false);

        final EditText edtSearch = (EditText) viewDialog.findViewById(R.id.edt_search);
        final ListView lvList = (ListView) viewDialog.findViewById(R.id.lv_list);

        final TextView tvBatal = (TextView) viewDialog.findViewById(R.id.tv_batal);
        final TextView tvSimpan = (TextView) viewDialog.findViewById(R.id.tv_simpan);

        AlertDialog alert = builder.create();
        alert.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        alert.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        final ArrayAdapter[] adapterList = {new ArrayAdapter(context, R.layout.layout_simple_list, listData)};
        lvList.setAdapter(adapterList[0]);

        edtSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if(actionId == EditorInfo.IME_ACTION_SEARCH){

                    String keyword = edtSearch.getText().toString().toLowerCase();

                    List<OptionItem> newList = new ArrayList<>();

                    for(OptionItem item : listData){

                        if(item.getText().toLowerCase().contains(keyword)){

                            newList.add(item);
                        }
                    }

                    adapterList[0] = new ArrayAdapter(context, R.layout.layout_simple_list, newList);
                    lvList.setAdapter(adapterList[0]);

                    iv.hideSoftKey(context);
                    return true;
                }
                return false;
            }
        });

        final AlertDialog alertDialogs = alert;

        lvList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                OptionItem item = (OptionItem) parent.getItemAtPosition(position);
                if(type == 1){

                    if(alertDialogs != null) {

                        try {
                            alertDialogs.dismiss();
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }

                    tvKota.setText(item.getText());
                    selectedKota = item.getValue();
                    getDataKecamatan(item.getValue());

                }else if(type == 2){

                    if(alertDialogs != null) {

                        try {
                            alertDialogs.dismiss();
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }

                    tvKecamatan.setText(item.getText());
                    selectedKecamatan = item.getValue();
                    getDataKelurahan(item.getValue());

                }else if (type == 3){

                    if(alertDialogs != null) {

                        try {
                            alertDialogs.dismiss();
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }

                    tvKelurahan.setText(item.getText());
                    selectedKelurahan= item.getValue();
                }
            }
        });

        tvBatal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view2) {

                if(alertDialogs != null) {

                    try {
                        alertDialogs.dismiss();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        });

        tvSimpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view2) {

                if(alertDialogs != null) {

                    try {
                        alertDialogs.dismiss();
                    }catch (Exception e){
                        e.printStackTrace();
                    }


                }
            }
        });

        try {

            alert.show();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void showDialog(final List<OptionItem> listData, final int type){

        final Dialog dialogChooser = DialogFactory.getInstance().createDialog((Activity) context,
                R.layout.dialog_searchable_spinner, 90, 70);

        final EditText txt_search = dialogChooser.findViewById(R.id.txt_search);

        final SearchableSpinnerDialogOptionAdapter.ChooserListener listener = new SearchableSpinnerDialogOptionAdapter.ChooserListener() {
            @Override
            public void onSelected(String value, String text) {

                if(type == 1){

                    if(dialogChooser != null) {

                        try {
                            dialogChooser.dismiss();
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }

                    tvKota.setText(text);
                    selectedKota = value;
                    getDataKecamatan(value);

                }else if(type == 2){

                    if(dialogChooser != null) {

                        try {
                            dialogChooser.dismiss();
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }

                    tvKecamatan.setText(text);
                    selectedKecamatan = value;
                    getDataKelurahan(value);

                }else if (type == 3){

                    if(dialogChooser != null) {

                        try {
                            dialogChooser.dismiss();
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }

                    tvKelurahan.setText(text);
                    selectedKelurahan= value;
                }
            }
        };

        final RecyclerView rv_items = dialogChooser.findViewById(R.id.rv_items);
        final SearchableSpinnerDialogOptionAdapter[] dialogAdapter = {new SearchableSpinnerDialogOptionAdapter((Activity) context,
                listData, listener)};

        txt_search.setHint("Keyword");
        txt_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String search = s.toString();

                String keyword = txt_search.getText().toString().toLowerCase();

                List<OptionItem> newList = new ArrayList<>();

                for(OptionItem item : listData){

                    if(item.getText().toLowerCase().contains(keyword)){

                        newList.add(item);
                    }
                }

                dialogAdapter[0] = new SearchableSpinnerDialogOptionAdapter((Activity)context, newList, listener);
                rv_items.setItemAnimator(new DefaultItemAnimator());
                rv_items.setLayoutManager(new LinearLayoutManager((Activity) context));
                rv_items.setAdapter(dialogAdapter[0]);
                dialogAdapter[0].notifyDataSetChanged();
            }
        });

        rv_items.setItemAnimator(new DefaultItemAnimator());
        rv_items.setLayoutManager(new LinearLayoutManager((Activity) context));
        rv_items.setAdapter(dialogAdapter[0]);

        dialogChooser.show();
    }

    private void initData() {

        /*adapterKota = new ArrayAdapter(context, R.layout.layout_simple_list, listKota);
        adapterKecamatan = new ArrayAdapter(context, R.layout.layout_simple_list, listKecamatan);
        adapterKelurahan = new ArrayAdapter(context, R.layout.layout_simple_list, listKeluarahan);

        spKota.setAdapter(adapterKota);
        spKecamatan.setAdapter(adapterKecamatan);
        spKelurahan.setAdapter(adapterKelurahan);

        spKota.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                OptionItem item = listKota.get(position);
                selectedKota = item.getValue();
                getDataKecamatan(item.getValue());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spKecamatan.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                OptionItem item = listKecamatan.get(position);
                selectedKecamatan = item.getValue();
                getDataKelurahan(item.getValue());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spKelurahan.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                OptionItem item = listKeluarahan.get(position);
                selectedKelurahan = item.getValue();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });*/

        getDataKota();
    }

    private void getDataKota() {

        dialogBox.showDialog(false);
        JSONObject jBody = new JSONObject();

        new ApiVolley(context, jBody, "GET", ServerURL.getKota, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {

                dialogBox.dismissDialog();
                try {

                    JSONObject response = new JSONObject(result);
                    String status = response.getJSONObject("metadata").getString("status");
                    String message = response.getJSONObject("metadata").getString("message");
                    listKota.clear();

                    if(iv.parseNullInteger(status) == 200){

                        JSONArray ja = response.getJSONArray("response");
                        for(int i = 0; i < ja.length(); i ++){

                            JSONObject jo = ja.getJSONObject(i);
                            listKota.add(
                                    new OptionItem(
                                            jo.getString("id")
                                            ,jo.getString("kota")
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
                            getDataKota();

                        }
                    };

                    dialogBox.showDialog(clickListener, "Ulangi Proses", "Terjadi kesalahan saat mengambil data");
                }

                /*adapterKota.notifyDataSetChanged();

                if(listKota.size() > 0){

                    spKota.setSelection(0);
                }*/
            }

            @Override
            public void onError(String result) {
                dialogBox.dismissDialog();
                View.OnClickListener clickListener = new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        dialogBox.dismissDialog();
                        getDataKota();

                    }
                };

                dialogBox.showDialog(clickListener, "Ulangi Proses", "Terjadi kesalahan saat mengambil data");
            }
        });
    }

    private void getDataKecamatan(final String idKota) {

        dialogBox.showDialog(false);

        selectedKecamatan = "";
        tvKecamatan.setText("");
        selectedKelurahan = "";
        tvKelurahan.setText("");

        JSONObject jBody = new JSONObject();
        try {
            jBody.put("id_kota", idKota);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        new ApiVolley(context, jBody, "POST", ServerURL.getKecamatan, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {

                dialogBox.dismissDialog();
                try {

                    JSONObject response = new JSONObject(result);
                    String status = response.getJSONObject("metadata").getString("status");
                    String message = response.getJSONObject("metadata").getString("message");
                    listKecamatan.clear();

                    if(iv.parseNullInteger(status) == 200){

                        JSONArray ja = response.getJSONArray("response");
                        for(int i = 0; i < ja.length(); i ++){

                            JSONObject jo = ja.getJSONObject(i);
                            listKecamatan.add(
                                    new OptionItem(
                                            jo.getString("id")
                                            ,jo.getString("kecamatan")
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
                            getDataKecamatan(idKota);

                        }
                    };

                    dialogBox.showDialog(clickListener, "Ulangi Proses", "Terjadi kesalahan saat mengambil data");
                }

                /*adapterKecamatan.notifyDataSetChanged();

                if(listKecamatan.size() > 0){

                    spKecamatan.setSelection(0);
                }*/
            }

            @Override
            public void onError(String result) {
                dialogBox.dismissDialog();
                View.OnClickListener clickListener = new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        dialogBox.dismissDialog();
                        getDataKecamatan(idKota);

                    }
                };

                dialogBox.showDialog(clickListener, "Ulangi Proses", "Terjadi kesalahan saat mengambil data");
            }
        });
    }

    private void getDataKelurahan(final String idKecamatan) {

        dialogBox.showDialog(false);

        selectedKelurahan = "";
        tvKelurahan.setText("");

        JSONObject jBody = new JSONObject();
        try {
            jBody.put("id_kecamatan", idKecamatan);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        new ApiVolley(context, jBody, "POST", ServerURL.getKelurahan, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {

                dialogBox.dismissDialog();
                try {

                    JSONObject response = new JSONObject(result);
                    String status = response.getJSONObject("metadata").getString("status");
                    String message = response.getJSONObject("metadata").getString("message");
                    listKeluarahan.clear();

                    if(iv.parseNullInteger(status) == 200){

                        JSONArray ja = response.getJSONArray("response");
                        for(int i = 0; i < ja.length(); i ++){

                            JSONObject jo = ja.getJSONObject(i);
                            listKeluarahan.add(
                                    new OptionItem(
                                            jo.getString("id")
                                            ,jo.getString("kelurahan")
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
                            getDataKelurahan(idKecamatan);

                        }
                    };

                    dialogBox.showDialog(clickListener, "Ulangi Proses", "Terjadi kesalahan saat mengambil data");
                }

                /*adapterKelurahan.notifyDataSetChanged();

                if(listKeluarahan.size() > 0){

                    spKelurahan.setSelection(0);
                }*/
            }

            @Override
            public void onError(String result) {

                dialogBox.dismissDialog();
                View.OnClickListener clickListener = new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        dialogBox.dismissDialog();
                        getDataKelurahan(idKecamatan);

                    }
                };

                dialogBox.showDialog(clickListener, "Ulangi Proses", "Terjadi kesalahan saat mengambil data");
            }
        });
    }

    private void saveData() {

        dialogBox.showDialog(false);

        JSONArray jFoto = new JSONArray();

        for(PhotoModel photo: listPhoto){

            jFoto.put(photo.getKeterangan());
        }

        JSONObject jBody = new JSONObject();
        try {
            jBody.put("nama", edtNama.getText().toString());
            jBody.put("alamat", edtAlamat.getText().toString());
            jBody.put("rt", edtRT.getText().toString());
            jBody.put("rw", edtRW.getText().toString());
            jBody.put("kontak", edtKontak.getText().toString());
            jBody.put("wa", edtWa.getText().toString());
            jBody.put("note", edtKeterangan.getText().toString());
            jBody.put("lat", tvLatitude.getText().toString());
            jBody.put("long", tvLongitude.getText().toString());
            jBody.put("kota", selectedKota);
            jBody.put("kecamatan", selectedKecamatan);
            jBody.put("kelurahan", selectedKelurahan);
            jBody.put("id_sales", session.getId());
            jBody.put("foto", jFoto);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        new ApiVolley(context, jBody, "POST", ServerURL.saveCalonDonatur, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {

                dialogBox.dismissDialog();
                try {

                    JSONObject response = new JSONObject(result);
                    String status = response.getJSONObject("metadata").getString("status");
                    String message = response.getJSONObject("metadata").getString("message");

                    Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                    if(iv.parseNullInteger(status) == 200){


                        ((Activity)context).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                HomeActivity.changeState(2);
                            }
                        });


                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    View.OnClickListener clickListener = new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            dialogBox.dismissDialog();
                            saveData();

                        }
                    };

                    dialogBox.showDialog(clickListener, "Ulangi Proses", "Terjadi kesalahan saat mengambil data");
                }
            }

            @Override
            public void onError(String result) {
                dialogBox.dismissDialog();
                View.OnClickListener clickListener = new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        dialogBox.dismissDialog();
                        saveData();

                    }
                };

                dialogBox.showDialog(clickListener, "Ulangi Proses", "Terjadi kesalahan saat mengambil data");
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUEST_CHECK_SETTINGS){

            if(resultCode == Activity.RESULT_CANCELED){

                mRequestingLocationUpdates = false;
            }else if(resultCode == Activity.RESULT_OK){

                startLocationUpdates();
            }

        }else if (resultCode == Activity.RESULT_OK && requestCode == imageRequestCode) {
            ArrayList<String> returnValue = data.getStringArrayListExtra(Pix.IMAGE_RESULTS);

            if(returnValue.size() > 0) {

                ImageUtils ui = new ImageUtils();

                for(String filePath : returnValue){

                    File f = new File(filePath);

                    Bitmap d = new BitmapDrawable(context.getResources(), f.getAbsolutePath()).getBitmap();

                    if(d != null){

                        listPhoto.add(new PhotoModel(f.getAbsolutePath(),  "", ImageUtils.convert(d)));
                        adapterPhoto.notifyDataSetChanged();
                    }
                }
            }
        }
    }

    private boolean checkPermission(){

        List<String> permissionList = new ArrayList<>();
        for (String perm : appPermission) {

            if (ContextCompat.checkSelfPermission(context, perm) != PackageManager.PERMISSION_GRANTED){

                permissionList.add(perm);
            }
        }

        if (!permissionList.isEmpty()) {

            ActivityCompat.requestPermissions((Activity) context, permissionList.toArray(new String[permissionList.size()]), PERMIOSSION_REQUEST_CODE);

            return  false;
        }

        return  true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        for(int i : grantResults){

            if(i == -1){

                AlertDialog dialog = new AlertDialog.Builder(context)
                        .setTitle("Informasi")
                        .setMessage("Mohon ijinkan semua akses untuk menunjang fitur aplikasi")
                        .setNeutralButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {


                            }
                        })
                        .show();
            }
        }
    }

    private void createLocationRequest() {

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private void createLocationCallback() {
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);

                mCurrentLocation = locationResult.getLastLocation();
                //mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
                onLocationChanged(mCurrentLocation);
            }
        };
    }

    private void buildLocationSettingsRequest() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        mLocationSettingsRequest = builder.build();
    }

    private void stopLocationUpdates() {
        if (!mRequestingLocationUpdates) {
            Log.d(TAG, "stopLocationUpdates: updates never requested, no-op.");
            return;
        }

        // It is a good practice to remove location requests when the activity is in a paused or
        // stopped state. Doing so helps battery performance and is especially
        // recommended in applications that request frequent location updates.
        mFusedLocationClient.removeLocationUpdates(mLocationCallback)
                .addOnCompleteListener((Activity) context, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        mRequestingLocationUpdates = false;
                    }
                });
    }

    private void startLocationUpdates() {
        // Begin by checking if the device has the necessary location settings.

        isUpdateLocation = true;
        mSettingsClient.checkLocationSettings(mLocationSettingsRequest)
                .addOnSuccessListener((Activity) context, new OnSuccessListener<LocationSettingsResponse>() {
                    @Override
                    public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                        Log.i(TAG, "All location settings are satisfied.");

                        //isLocationRefresh = false;
                        isUpdateLocation = false;
                        //noinspection MissingPermission
                        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                            return;
                        }
                        mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                                mLocationCallback, Looper.myLooper());

                        mFusedLocationClient.getLastLocation()
                                .addOnSuccessListener((Activity) context, new OnSuccessListener<Location>() {
                                    @Override
                                    public void onSuccess(Location clocation) {

                                        if (clocation != null) {

                                            location = clocation;
                                            onLocationChanged(location);
                                        }else if(location != null && location.getLongitude() != 0 && location.getLatitude() != 0){
                                            onLocationChanged(location);
                                        }else{
                                            location = getLocation();
                                        }
                                    }
                                });
                    }
                })
                .addOnFailureListener((Activity) context, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        int statusCode = ((ApiException) e).getStatusCode();
                        switch (statusCode) {
                            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                Log.i(TAG, "Location settings are not satisfied. Attempting to upgrade " +
                                        "location settings ");
                                try {
                                    // Show the dialog by calling startResolutionForResult(), and check the
                                    // result in onActivityResult().
                                    ResolvableApiException rae = (ResolvableApiException) e;
                                    rae.startResolutionForResult((Activity) context, REQUEST_CHECK_SETTINGS);
                                } catch (IntentSender.SendIntentException sie) {
                                    Log.i(TAG, "PendingIntent unable to execute request.");
                                }
                                break;
                            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                String errorMessage = "Location settings are inadequate, and cannot be " +
                                        "fixed here. Fix in Settings.";
                                Log.e(TAG, errorMessage);
                                Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show();
                                mRequestingLocationUpdates = false;
                                //refreshMode = false;
                        }

                        //get Location
                        isUpdateLocation = false;
                        location = getLocation();
                    }
                });
    }

    private void updateAllLocation(){
        mRequestingLocationUpdates = true;
        startLocationUpdates();
    }

    private void initLocation() {

        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        setCriteria();
        latitude = 0;
        longitude = 0;
        location = new Location("set");
        location.setLatitude(latitude);
        location.setLongitude(longitude);

        refreshMode = true;
        //location = getLocation();
        updateAllLocation();

        /*btnResetPosition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                refreshMode = true;
                location = getLocation();
            }
        });*/
    }

    public void setCriteria() {
        criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setCostAllowed(true);
        criteria.setPowerRequirement(Criteria.POWER_MEDIUM);
        provider = locationManager.getBestProvider(criteria, true);
    }

    private boolean isUpdateLocation = false;

    public Location getLocation() {

        isUpdateLocation = true;
        try {

            locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

            // getting GPS status
            isGPSEnabled = locationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER);

            Log.v("isGPSEnabled", "=" + isGPSEnabled);

            // getting network status
            isNetworkEnabled = locationManager
                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            Log.v("isNetworkEnabled", "=" + isNetworkEnabled);

            if (isGPSEnabled == false && isNetworkEnabled == false) {
                // no network provider is enabled
                Toast.makeText(context, "Cannot identify the location.\nPlease turn on GPS or turn on your data.",
                        Toast.LENGTH_LONG).show();

            } else {
                this.canGetLocation = true;
                if (isNetworkEnabled) {
                    location = null;

                    // Granted the permission first
                    if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context,
                                Manifest.permission.ACCESS_COARSE_LOCATION)) {
                            showExplanation("Permission Needed", "Rationale", Manifest.permission.ACCESS_COARSE_LOCATION, REQUEST_PERMISSION_COARSE_LOCATION);
                        } else {
                            requestPermission(Manifest.permission.ACCESS_COARSE_LOCATION, REQUEST_PERMISSION_COARSE_LOCATION);
                        }

                        if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context,
                                Manifest.permission.ACCESS_FINE_LOCATION)) {
                            showExplanation("Permission Needed", "Rationale", Manifest.permission.ACCESS_FINE_LOCATION, REQUEST_PERMISSION_FINE_LOCATION);
                        } else {
                            requestPermission(Manifest.permission.ACCESS_FINE_LOCATION, REQUEST_PERMISSION_FINE_LOCATION);
                        }

                        isUpdateLocation = false;
                        return null;
                    }

                    locationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                    Log.d("Network", "Network");

                    if (locationManager != null) {

                        Location bufferLocation = locationManager
                                .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

                        if (bufferLocation != null) {
                            location = bufferLocation;
                        }
                    }
                }

                // if GPS Enabled get lat/long using GPS Services
                if (isGPSEnabled) {
                    locationManager.requestLocationUpdates(
                            LocationManager.GPS_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                    Log.d("GPS Enabled", "GPS Enabled");

                    if (locationManager != null) {

                        Location bufferLocation = locationManager
                                .getLastKnownLocation(LocationManager.GPS_PROVIDER);

                        if (bufferLocation != null) {
                            location = bufferLocation;
                        }
                    }
                }else{
                    //Toast.makeText(context, "Turn on your GPS for better accuracy", Toast.LENGTH_SHORT).show();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        isUpdateLocation = false;
        if(location != null){
            onLocationChanged(location);
        }
        return location;
    }

    private void showExplanation(String title,
                                 String message,
                                 final String permission,
                                 final int permissionRequestCode) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        requestPermission(permission, permissionRequestCode);
                    }
                });
        builder.create().show();
    }

    private void requestPermission(String permissionName, int permissionRequestCode) {
        ActivityCompat.requestPermissions((Activity) context,
                new String[]{permissionName}, permissionRequestCode);
    }

    @Override
    public void onLocationChanged(Location location) {

        if(refreshMode){
            refreshMode = false;
            this.location = location;
            this.latitude = location.getLatitude();
            this.longitude = location.getLongitude();

            tvLatitude.setText(iv.doubleToStringFull(this.latitude));
            tvLongitude.setText(iv.doubleToStringFull(this.longitude));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshMode = true;
        updateAllLocation();
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }
}
