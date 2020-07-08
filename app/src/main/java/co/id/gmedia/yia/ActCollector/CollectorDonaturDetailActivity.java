package co.id.gmedia.yia.ActCollector;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gmedia.modul.bluetoothprinter.Model.Item;
import com.gmedia.modul.bluetoothprinter.Model.Transaksi;
import com.gmedia.modul.bluetoothprinter.PrintFormatter;
import com.gmedia.modul.bluetoothprinter.Printer;
import com.gmedia.modul.bluetoothprinter.RupiahFormatter;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

import co.id.gmedia.coremodul.ApiVolley;
import co.id.gmedia.coremodul.DialogBox;
import co.id.gmedia.coremodul.ItemValidation;
import co.id.gmedia.coremodul.OptionItem;
import co.id.gmedia.coremodul.PhotoModel;
import co.id.gmedia.coremodul.SessionManager;
import co.id.gmedia.yia.ActCollector.Adapter.ImageDonaturAdapter;
import co.id.gmedia.yia.ActSalesBrosur.Adapter.SearchableSpinnerDialogOptionAdapter;
import co.id.gmedia.yia.ActSalesBrosur.DetailCurrentPosActivity;
import co.id.gmedia.yia.ActSalesSosial.Adapter.ListPhotoAdapter;
import co.id.gmedia.yia.Model.DonaturModel;
import co.id.gmedia.yia.R;
import co.id.gmedia.yia.Utils.AppRequestCallback;
import co.id.gmedia.yia.Utils.Converter;
import co.id.gmedia.yia.Utils.DialogFactory;
import co.id.gmedia.yia.Utils.GoogleLocationManager;
import co.id.gmedia.yia.Utils.JSONBuilder;
import co.id.gmedia.yia.Utils.ServerURL;

public class CollectorDonaturDetailActivity extends AppCompatActivity {

    private DonaturModel donatur;

    private TextView tv_latitude, tv_longitude;
    private EditText edt_nama, edt_alamat, edt_kontak, txt_jumlah_kaleng, txt_nominal, edtRt, edtRw;

    private Context context;
    private SessionManager session;
    private DialogBox dialogBox;

    private GoogleLocationManager locationManager;
    private double lat = 0, lng = 0;

    private String current = "";
    private LinearLayout llBukaMap;
    private ImageView ivKota, ivKecamatan, ivKelurahan;
    private TextView tvKota, tvKecamatan, tvKelurahan;
    private List<OptionItem> listKota = new ArrayList<>(), listKecamatan = new ArrayList<>(), listKeluarahan = new ArrayList<>();
    private String selectedKota = "", selectedKecamatan = "", selectedKelurahan = "";
    private ItemValidation iv = new ItemValidation();
    private RelativeLayout rlLokasi;
    private LinearLayout llNominal;
    private boolean isEdit = false;
    private EditText edtKeterangan;
    private CheckBox cbCkecing;
    private RecyclerView rv_PhotoDetail;
    private List<PhotoModel> listPhotoDetail = new ArrayList<>();
    private ImageDonaturAdapter adapterPhotoDetail;

    Printer printer;

    // set printer
    private final UUID BLUETOOTH_PRINTER_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");

    public BluetoothAdapter bluetoothAdapter;
    public static BluetoothSocket socket;
    public static BluetoothDevice bluetoothDevice;
    public static OutputStream outputStream;
    private InputStream inputStream;
    private ProgressBar progressbar;
    private Button btn_devices;

    private Thread workerThread;
    private byte[] readBuffer;
    private int readBufferPosition;
    private volatile boolean stopWorker;

    private Dialog dialogDevices;
    public Dialog dialogBluetooth;
    public Dialog dialogConfirm;
    private BroadcastReceiver broadcastReceiver;

    private ArrayAdapter<String> deviceAdapter;
    private List<String> listDevicesData = new ArrayList<>();
    private List<BluetoothDevice> listDevices = new ArrayList<>();

    private ArrayAdapter<String> discoveredAdapter;
    private List<String> listDiscoveredData = new ArrayList<>();
    private List<BluetoothDevice> listDiscovered = new ArrayList<>();
    public static ListView list_devices;
    Transaksi transaksi;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collector_donatur_detail);
        printer = new Printer(CollectorDonaturDetailActivity.this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );

        setTitle("Detail Donasi");
        context = this;
        session = new SessionManager(context);
        locationManager = new GoogleLocationManager(this, new GoogleLocationManager.LocationUpdateListener() {
            @Override
            public void onChange(Location location) {
                lat = location.getLatitude();
                lng = location.getLongitude();

                tv_latitude.setText(String.valueOf(lat));
                tv_longitude.setText(String.valueOf(lng));
            }
        });
        locationManager.startLocationUpdates();

        initUI();
        initEvent();
        getDataKota();

        if(getIntent().hasExtra("donatur")){
            Gson gson = new Gson();
            donatur = gson.fromJson(getIntent().getStringExtra("donatur"), DonaturModel.class);
            initDonatur();
        }

        Bundle bundle = getIntent().getExtras();
        if(bundle != null){

            isEdit = bundle.getBoolean("edit", false);
            if(isEdit){

                rlLokasi.setVisibility(View.VISIBLE);
                llNominal.setVisibility(View.GONE);

                setTitle("Detail Donatur");
                selectedKota = donatur.getIdKota();
                selectedKecamatan = donatur.getIdKecamatan();
                selectedKelurahan = donatur.getIdKelurahan();

                tvKota.setText(donatur.getKota());
                tvKecamatan.setText(donatur.getKecamatan());
                tvKelurahan.setText(donatur.getKelurahan());
            }else{

                rlLokasi.setVisibility(View.GONE);
                llNominal.setVisibility(View.VISIBLE);
            }
        }
        rv_PhotoDetail = findViewById(R.id.rv_foto_detail);

        LinearLayoutManager layoutManagerDetail = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        adapterPhotoDetail = new ImageDonaturAdapter(context, listPhotoDetail);
        rv_PhotoDetail.setLayoutManager(layoutManagerDetail);
        rv_PhotoDetail.setAdapter(adapterPhotoDetail);

    }

    private void initDonatur(){
        edt_nama.setText(donatur.getNama());
        edt_alamat.setText(donatur.getAlamat());
        edt_kontak.setText(donatur.getKontak());
        edtRt.setText(donatur.getRt());
        edtRw.setText(donatur.getRw());
        for(String url : donatur.getListUrlPhoto()){
            listPhotoDetail.add(new PhotoModel("", url));
        }
//        adapterPhotoDetail.notifyDataSetChanged();
//        Toast.makeText(context, String.valueOf(listPhotoDetail), Toast.LENGTH_SHORT).show();
    }

    private void initUI() {

        edt_nama = findViewById(R.id.edt_nama);
        edt_alamat = findViewById(R.id.edt_alamat);
        edt_kontak = findViewById(R.id.edt_kontak);
        edtRt = findViewById(R.id.edt_rt);
        edtRw = findViewById(R.id.edt_rw);
        tv_latitude = findViewById(R.id.tv_latitude);
        tv_longitude = findViewById(R.id.tv_longitude);
        txt_jumlah_kaleng = findViewById(R.id.txt_jumlah_kaleng);
        txt_nominal = findViewById(R.id.txt_nominal);
        llBukaMap = (LinearLayout) findViewById(R.id.ll_buka_map);
        edtKeterangan = (EditText) findViewById(R.id.edt_keterangan);
        cbCkecing = (CheckBox) findViewById(R.id.cb_checking);

        rlLokasi = (RelativeLayout) findViewById(R.id.rl_lokasi);
        llNominal = (LinearLayout) findViewById(R.id.ll_nominal);

        ivKota = (ImageView) findViewById(R.id.iv_kota);
        tvKota = (TextView) findViewById(R.id.tv_kota);
        ivKecamatan = (ImageView) findViewById(R.id.iv_kecamatan);
        tvKecamatan = (TextView) findViewById(R.id.tv_kecamatan);
        ivKelurahan = (ImageView) findViewById(R.id.iv_kelurahan);
        tvKelurahan = (TextView) findViewById(R.id.tv_kelurahan);

        llBukaMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context, DetailCurrentPosActivity.class);
                intent.putExtra("nama", donatur.getNama());
                intent.putExtra("alamat", donatur.getAlamat());
                intent.putExtra("lat", donatur.getLatitude());
                intent.putExtra("long", donatur.getLognitude());
                startActivity(intent);
            }
        });

        txt_nominal.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(!s.toString().equals(current)){
                    txt_nominal.removeTextChangedListener(this);

                    String cleanString = s.toString().replaceAll("[Rp,.\\s]", "");
                    String formatted;
                    if(!cleanString.isEmpty()){
                        double parsed = Double.parseDouble(cleanString);
                        formatted = Converter.doubleToRupiah(parsed);
                    }
                    else{
                        formatted = "";
                    }

                    current = formatted;
                    txt_nominal.setText(formatted);
                    txt_nominal.setSelection(formatted.length());
                    txt_nominal.addTextChangedListener(this);
                }
            }
        });

        findViewById(R.id.btn_simpan).setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                if(donatur != null){
                    if(lat == 0 || lng == 0){
                        Toast.makeText(CollectorDonaturDetailActivity.this, "Lokasi tidak terdekteksi, " +
                                "tidak bisa melanjutkan survey", Toast.LENGTH_SHORT).show();
                    }
                    else{

                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        LayoutInflater inflater = (LayoutInflater) ((Activity)context).getSystemService(LAYOUT_INFLATER_SERVICE);
                        View viewDialog = inflater.inflate(R.layout.dialog_simpan_donasi, null);
                        builder.setView(viewDialog);
                        //builder.setCancelable(true);

                        final Button btnSimpan = (Button) viewDialog.findViewById(R.id.btn_simpan);
                        final Button btnBatal = (Button) viewDialog.findViewById(R.id.btn_batal);
                        final TextView tvNama = (TextView) viewDialog.findViewById(R.id.tv_nama);
                        final TextView tvAlamat = (TextView) viewDialog.findViewById(R.id.tv_alamat);
                        final TextView tvDonasi = (TextView) viewDialog.findViewById(R.id.tv_donasi);
                        final TextView tvKaleng = (TextView) viewDialog.findViewById(R.id.tv_kaleng);
                        final TextView tvKeterangan = (TextView) viewDialog.findViewById(R.id.tv_keterangan);

                        String rr="";
                        if(!edtRt.getText().toString().equalsIgnoreCase("") && !edtRw.getText().toString().equalsIgnoreCase("")){
                            rr = " RT "+edtRt.getText().toString()+"/"+edtRw.getText().toString();
                        }else {
                            rr="";
                        }
                        tvNama.setText(edt_nama.getText().toString());
                        tvAlamat.setText(edt_alamat.getText().toString()+rr);
                        tvDonasi.setText(txt_nominal.getText().toString());
                        tvKaleng.setText(txt_jumlah_kaleng.getText().toString());
                        tvKeterangan.setText(tvKeterangan.getText().toString());

                        AlertDialog alert = builder.create();
                        alert.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                        alert.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

                        alert.getWindow().setGravity(Gravity.CENTER);

                        final AlertDialog alertDialogs = alert;

                        btnSimpan.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view2) {

                                if(alertDialogs != null) {

                                    try {
                                        alertDialogs.dismiss();
                                    }catch (Exception e){
                                        e.printStackTrace();
                                    }
                                }

                                jemputInfaq();
                            }
                        });

                        btnBatal.setOnClickListener(new View.OnClickListener() {
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
                }
            }
        });

        dialogBox = new DialogBox(this);


        //Inisialisasi UI bluetooth
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Bluetooth tidak menyala");
        builder.setMessage("Bluetooth anda tidak menyala. Nyalakan bluetooth sekarang?");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Meminta user menyalakan bluetooth
                Intent intentOpenBluetoothSettings = new Intent();
                intentOpenBluetoothSettings.setAction(android.provider.Settings.ACTION_BLUETOOTH_SETTINGS);
                context.startActivity(intentOpenBluetoothSettings);
            }
        });
        builder.setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        dialogBluetooth = builder.create();

        //Inisialisasi Bluetooth
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(bluetoothAdapter == null) {
            Toast.makeText(context, "Adapter Bluetooth tidak tersedia", Toast.LENGTH_SHORT).show();
        }
        /*if(!bluetoothAdapter.isEnabled()) {
            dialogBluetooth.show();
        }*/

        //init dialog UI
        dialogDevices = new Dialog(context);
        dialogDevices.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogDevices.setContentView(com.gmedia.modul.bluetoothprinter.R.layout.popup_devices);
        list_devices = dialogDevices.findViewById(com.gmedia.modul.bluetoothprinter.R.id.list_devices);
        ListView list_discovered = dialogDevices.findViewById(com.gmedia.modul.bluetoothprinter.R.id.list_discovered);
        btn_devices = dialogDevices.findViewById(com.gmedia.modul.bluetoothprinter.R.id.btn_devices);
        progressbar = dialogDevices.findViewById(com.gmedia.modul.bluetoothprinter.R.id.progressbar);

        deviceAdapter = new ArrayAdapter<>(context, com.gmedia.modul.bluetoothprinter.R.layout.item_devices, com.gmedia.modul.bluetoothprinter.R.id.txt_device, listDevicesData);
        list_devices.setAdapter(deviceAdapter);
        list_devices.setOnItemClickListener(new DeviceClicked());

        discoveredAdapter = new ArrayAdapter<>(context, com.gmedia.modul.bluetoothprinter.R.layout.item_devices, com.gmedia.modul.bluetoothprinter.R.id.txt_device, listDiscoveredData);
        list_discovered.setAdapter(discoveredAdapter);
        list_discovered.setOnItemClickListener(new DiscoveredClicked());

        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        context.registerReceiver(broadcastReceiver, filter);

        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
                        listDiscovered.add(device);
                        listDiscoveredData.add(device.getName() + "\n" + device.getAddress());
                        discoveredAdapter.notifyDataSetChanged();
                    }
                }
                else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                    progressbar.setVisibility(View.GONE);
                    btn_devices.setText(com.gmedia.modul.bluetoothprinter.R.string.cari_device);
                    //Toast.makeText(context, "Pencarian Device Selesai", Toast.LENGTH_SHORT).show();
                }
            }
        };

        btn_devices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bluetoothAdapter.isDiscovering()) {

                    progressbar.setVisibility(View.GONE);
                    bluetoothAdapter.cancelDiscovery();
                    btn_devices.setText(com.gmedia.modul.bluetoothprinter.R.string.cari_device);
                    //mBluetoothAdapter.startDiscovery();
                }
                else {
                    btn_devices.setText(com.gmedia.modul.bluetoothprinter.R.string.berhenti);
                    progressbar.setVisibility(View.VISIBLE);
                    bluetoothAdapter.startDiscovery();
                    context.registerReceiver(broadcastReceiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));
                }
            }
        });
    }

    private void initEvent() {


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

    private void jemputInfaq(){

        dialogBox.showDialog(false);
        JSONBuilder body = new JSONBuilder();

        String url = ServerURL.saveCollector;

        if(isEdit){

            url = ServerURL.updateDonatur;
            body.add("id", donatur.getId_donatur());
            body.add("nama", edt_nama.getText().toString());
            body.add("alamat", edt_alamat.getText().toString());
            body.add("kontak", edt_kontak.getText().toString());
            body.add("kota", selectedKota);
            body.add("kecamatan", selectedKecamatan);
            body.add("kelurahan", selectedKelurahan);
            body.add("note", edtKeterangan.getText().toString());
        }else{

            body.add("id_sales", session.getId());
            body.add("id_template", donatur.getId());
            body.add("id_donatur", donatur.getId_donatur());
            body.add("nominal", txt_nominal.getText().toString().replaceAll("[Rp,.\\s]", ""));
            body.add("kaleng_kembali", txt_jumlah_kaleng.getText().toString());
            body.add("latitude", tv_latitude.getText().toString());
            body.add("longitude", tv_longitude.getText().toString());
            body.add("note", edtKeterangan.getText().toString());
            body.add("ceklist", cbCkecing.isChecked() ? "1" : "0");
        }

        new ApiVolley(context, body.create(), "POST", url,
                new AppRequestCallback(new AppRequestCallback.ResponseListener() {
                    @Override
                    public void onSuccess(String response, String message) {
                        dialogBox.dismissDialog();
                        Toast.makeText(CollectorDonaturDetailActivity.this, message, Toast.LENGTH_SHORT).show();

                        final Calendar date = Calendar.getInstance();
                        final List<Item> items = new ArrayList<>();
                        items.add(new Item("Teknolgi", "-", 20000));
                        if(message.equals("Infaq berhasil diambil")){
                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            builder.setTitle("Print nota infaq");
                            builder.setMessage("Apakah anda ingin mengeprint nota infaq ?");
                            builder.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    String nominal ="";
                                    if(!txt_nominal.getText().toString().equals("0")){
                                        nominal = txt_nominal.getText().toString().replaceAll("[Rp,.\\s]", "");
                                    }else{
                                        nominal = "0";
                                    }
                                    transaksi = new Transaksi(edt_nama.getText().toString(), edt_alamat.getText().toString(), Double.parseDouble(nominal), date.getTime(), items,session.getNama());
                                    if(!bluetoothAdapter.isEnabled()) {
                                        dialogBluetooth.show();
                                        Toast.makeText(context, "Hidupkan bluetooth anda kemudian klik cetak kembali", Toast.LENGTH_LONG).show();
                                    }else{
                                        if(isPrinterReady()){
                                            print(transaksi, true);
                                            finish();
                                        }else{
                                            Toast.makeText(context, "Harap pilih device printer telebih dahulu", Toast.LENGTH_LONG).show();
                                            showDevices();
                                        }
                                    }
                                }
                            });
                            builder.setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                    finish();
                                }
                            });
                            dialogConfirm = builder.create();
                            dialogConfirm.show();
                        }else{
                            finish();
                        }
                    }

                    @Override
                    public void onEmpty(String message) {
                        dialogBox.dismissDialog();
                        Toast.makeText(CollectorDonaturDetailActivity.this, message, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFail(String message) {
                        dialogBox.dismissDialog();
                        Toast.makeText(CollectorDonaturDetailActivity.this, message, Toast.LENGTH_SHORT).show();
                    }
                }));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == GoogleLocationManager.ACTIVATE_LOCATION){
            if(resultCode == RESULT_OK){
                locationManager.startLocationUpdates();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == GoogleLocationManager.PERMISSION_LOCATION){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                locationManager.startLocationUpdates();
            }
            else{
                Toast.makeText(this, "Ijin ditolak, gagal mendeteksi lokasi", Toast.LENGTH_SHORT).show();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {

        super.onBackPressed();
        overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right);
    }


    public void showDevices(){
        if(!bluetoothAdapter.isEnabled()) {
            dialogBluetooth.show();
            return;
        }

        listDiscovered.clear();
        listDiscoveredData.clear();
        discoveredAdapter.notifyDataSetChanged();

        initDevices();
        dialogDevices.show();
    }

    public void stopService(){
        try {
            closeBT();
            if (bluetoothAdapter != null) {
                bluetoothAdapter.cancelDiscovery();
            }
            context.unregisterReceiver(broadcastReceiver);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    private void createBond(BluetoothDevice device)throws Exception
    {
        Class class1 = Class.forName("android.bluetooth.BluetoothDevice");
        Method createBondMethod = class1.getMethod("createBond");
        Boolean returnValue = (Boolean) createBondMethod.invoke(device);
        if(!returnValue){
            Toast.makeText(context, "Pairing device gagal!", Toast.LENGTH_SHORT).show();
        }
    }

    private void initDevices(){
        listDevices.clear();
        listDevicesData.clear();

        final Set<BluetoothDevice> paired = bluetoothAdapter.getBondedDevices();
        Object[] objectList = paired.toArray();

        if(objectList != null){

            if(objectList.length > 0) {
                for (Object device : objectList) {
                    BluetoothDevice bluetooth = (BluetoothDevice) device;
                    try {

                        if(bluetooth.getUuids()[0].getUuid().equals(BLUETOOTH_PRINTER_UUID)){
                            listDevicesData.add(bluetooth.getName() + "\n" + bluetooth.getAddress());
                            listDevices.add(bluetooth);
                        }

                    }catch (Exception e){
                        e.printStackTrace();
                    }

                }
            }
        }

        deviceAdapter.notifyDataSetChanged();
    }

    private void connectBluetooth() throws IOException {

        try {
            socket = bluetoothDevice.createRfcommSocketToServiceRecord(BLUETOOTH_PRINTER_UUID);
            socket.connect();
            outputStream = socket.getOutputStream();
            inputStream = socket.getInputStream();

            beginListenForData();
            print(transaksi, true);
            finish();
            Toast.makeText(context, "Device Bluetooth Printer tersambung", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {

            bluetoothDevice = null;
            Toast.makeText(context, "Device Bluetooth Printer gagal tersambung", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private void beginListenForData(){
        try {
            final Handler handler = new Handler();

            // this is the ASCII code for a newline character
            final byte delimiter = 10;

            stopWorker = false;
            readBufferPosition = 0;
            readBuffer = new byte[1024];

            workerThread = new Thread(new Runnable() {
                public void run() {

                    while (!Thread.currentThread().isInterrupted() && !stopWorker) {

                        try {

                            int bytesAvailable = inputStream.available();

                            if (bytesAvailable > 0) {

                                byte[] packetBytes = new byte[bytesAvailable];
                                inputStream.read(packetBytes);

                                for (int i = 0; i < bytesAvailable; i++) {

                                    byte b = packetBytes[i];
                                    if (b == delimiter) {

                                        byte[] encodedBytes = new byte[readBufferPosition];
                                        System.arraycopy(
                                                readBuffer, 0,
                                                encodedBytes, 0,
                                                encodedBytes.length
                                        );

                                        // specify US-ASCII encoding
                                        final String data = new String(encodedBytes, "US-ASCII");
                                        readBufferPosition = 0;

                                        // tell the user data were sent to bluetooth printer device
                                        handler.post(new Runnable() {
                                            public void run() {
                                                //myLabel.setText(data);
                                            }
                                        });

                                    } else {
                                        readBuffer[readBufferPosition++] = b;
                                    }
                                }
                            }

                        } catch (IOException ex) {
                            stopWorker = true;
                        }

                    }
                }
            });

            workerThread.start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // this will send text data to be printed by the bluetooth printer
//    public void print(String msg){
//        if(bluetoothDevice == null){
//            Toast.makeText(context, "Sambungkan ke device printer terlebih dahulu!", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        try {
//            msg += "\n";
//            outputStream.write(msg.getBytes());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    // close the connection to bluetooth printer.
    private void closeBT() throws IOException {
        try {
            stopWorker = true;
            outputStream.close();
            inputStream.close();
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public class DeviceClicked implements AdapterView.OnItemClickListener{
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            BluetoothDevice selected_device = listDevices.get(position);
            try {

                if(selected_device.getUuids()[0].getUuid().equals(BLUETOOTH_PRINTER_UUID)){
                    try{
                        bluetoothDevice = selected_device;
                        connectBluetooth();
                        dialogDevices.dismiss();
                    }
                    catch (IOException e){
                        e.printStackTrace();
                    }
                }
                else{
                    Toast.makeText(context, "Device bukan Device Printer, coba Bluetooth lain", Toast.LENGTH_SHORT).show();
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    private class DiscoveredClicked implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            //Pairing dengan Device
            bluetoothAdapter.cancelDiscovery();
            progressbar.setVisibility(View.GONE);
            btn_devices.setText(com.gmedia.modul.bluetoothprinter.R.string.cari_device);

            BluetoothDevice device = listDiscovered.get(position);

            try {
                createBond(device);
                dialogDevices.dismiss();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public boolean isPrinterReady(){

        boolean isSocketConncet = false;
        if(socket != null){

            isSocketConncet = socket.isConnected();
        }
        return bluetoothDevice != null && isSocketConncet;
    }


    // this will send text data to be printed by the bluetooth printer
    public void print(Transaksi transaksi, boolean isDeposit){
        final int NAMA_MAX = 15;
        final int JUMLAH_MAX = 5;
        final int HARGA_TOTAL_MAX = 10;

        if(bluetoothDevice == null){
            Toast.makeText(context, "Sambungkan ke device printer terlebih dahulu!", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            double jum = 0;
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

            //PROSES CETAK HEADER
            outputStream.write(PrintFormatter.DEFAULT_STYLE);
            outputStream.write(PrintFormatter.ALIGN_CENTER);
            Bitmap bmp = BitmapFactory.decodeResource(context.getResources(), com.gmedia.modul.bluetoothprinter.R.drawable.ic_yayasan);
            byte[] bmp_byte = PrintFormatter.decodeBitmap(bmp);
            if(bmp_byte != null){
                outputStream.write(bmp_byte);
            }
            outputStream.write("YAYASAN ISLAM AMANAH \n  @yay.amanah \n WA 0813-1162-6307".getBytes());

            outputStream.write(PrintFormatter.NEW_LINE);
            outputStream.write(PrintFormatter.ALIGN_RIGHT);
            SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault());
            String currentDateandTime = sdf.format(transaksi.getTglNota());
            outputStream.write(String.format("%s\n", currentDateandTime).getBytes());
            outputStream.write(PrintFormatter.NEW_LINE);

            outputStream.write(PrintFormatter.ALIGN_LEFT);
            outputStream.write(String.format("Nama    : %s\n", transaksi.getOutlet()).getBytes());
            outputStream.write(PrintFormatter.NEW_LINE);
            outputStream.write(String.format("Alamat  : %s\n", transaksi.getSales()).getBytes());
            outputStream.write(PrintFormatter.NEW_LINE);
            outputStream.write(String.format("Nominal : %s\n", RupiahFormatter.getRupiah(transaksi.getNo_nota())).getBytes());
            outputStream.write(PrintFormatter.NEW_LINE);
            outputStream.write(String.format("Petugas : %s\n", transaksi.getTglTransaksi()).getBytes());

            outputStream.write(PrintFormatter.NEW_LINE);

            //PROSES CETAK FOOTER
            outputStream.write(PrintFormatter.ALIGN_CENTER);
            outputStream.write("Jazakumulloh Khoiron Katsiron\n".getBytes());

            outputStream.write("==============================\n".getBytes());
            outputStream.write(PrintFormatter.DEFAULT_STYLE);
            outputStream.write(PrintFormatter.NEW_LINE);
            outputStream.write(PrintFormatter.NEW_LINE);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "Koneksi printer terputus, harap koneksi ulang bluetooth anda", Toast.LENGTH_LONG).show();
            stopService();
        }
    }

}
