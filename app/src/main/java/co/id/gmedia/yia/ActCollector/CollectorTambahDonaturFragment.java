package co.id.gmedia.yia.ActCollector;


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
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.fxn.pix.Options;
import com.fxn.pix.Pix;
import com.fxn.utility.ImageQuality;
import com.gmedia.modul.bluetoothprinter.Model.Item;
import com.gmedia.modul.bluetoothprinter.Model.Transaksi;
import com.gmedia.modul.bluetoothprinter.PrintFormatter;
import com.gmedia.modul.bluetoothprinter.Printer;
import com.gmedia.modul.bluetoothprinter.RupiahFormatter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
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
import co.id.gmedia.coremodul.SessionManager;
import co.id.gmedia.yia.ActCollector.Adapter.FotoAdapter;
import co.id.gmedia.yia.R;
import co.id.gmedia.yia.Utils.AppRequestCallback;
import co.id.gmedia.yia.Utils.Converter;
import co.id.gmedia.yia.Utils.DialogFactory;
import co.id.gmedia.yia.Utils.GoogleLocationManager;
import co.id.gmedia.yia.Utils.JSONBuilder;
import co.id.gmedia.yia.Utils.SearchableSpinnerDialog.SearchableSpinnerDialogAdapter;
import co.id.gmedia.yia.Utils.SearchableSpinnerDialog.SimpleObjectModel;
import co.id.gmedia.yia.Utils.ServerURL;

/**
 * A simple {@link Fragment} subclass.
 */
public class CollectorTambahDonaturFragment extends Fragment {

    private Activity activity;
    private FotoAdapter adapter;
    private List<String> listGambar = new ArrayList<>();
    private List<SimpleObjectModel> listKota = new ArrayList<>(), listKecamatan = new ArrayList<>(), listKelurahan = new ArrayList<>();
    private String selectedKota = "", selectedKecamatan = "", selectedKelurahan = "";
    private DialogBox dialogBox;
    private SearchableSpinnerDialogAdapter dialogAdapter;
    private List<SimpleObjectModel> listChooser = new ArrayList<>();

    private TextView txt_kota, txt_kecamatan, txt_kelurahan;
    private EditText txt_nama, txt_alamat, txt_kontak, txt_jumlah_donasi,edtRt, edtRw;

    private String current = "";

    private GoogleLocationManager locationManager;
    private double lat = 0, lng = 0;
    private EditText edtKetarangan;
    SessionManager sessionManager;

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
    Printer printer;

    public CollectorTambahDonaturFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        activity = getActivity();
        View v = inflater.inflate(R.layout.fragment_collector_tambah_donatur, container, false);
        dialogBox = new DialogBox(activity);
        sessionManager = new SessionManager(activity);
        printer = new Printer(activity);
        printer.startService();

        txt_nama = v.findViewById(R.id.txt_nama);
        txt_alamat = v.findViewById(R.id.txt_alamat);
        txt_kontak = v.findViewById(R.id.txt_kontak);
        txt_kota = v.findViewById(R.id.txt_kota);
        txt_kecamatan = v.findViewById(R.id.txt_kecamatan);
        txt_kelurahan = v.findViewById(R.id.txt_kelurahan);
        edtRt = v.findViewById(R.id.edt_rt);
        edtRw = v.findViewById(R.id.edt_rw);
        edtKetarangan = (EditText) v.findViewById(R.id.edt_keterangan);

        RecyclerView rv_foto = v.findViewById(R.id.rv_foto);
        rv_foto.setItemAnimator(new DefaultItemAnimator());
        rv_foto.setLayoutManager(new LinearLayoutManager(activity, RecyclerView.HORIZONTAL, false));
        adapter = new FotoAdapter(activity, listGambar);
        rv_foto.setAdapter(adapter);

        txt_jumlah_donasi = v.findViewById(R.id.txt_jumlah_donasi);
        txt_jumlah_donasi.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(!s.toString().equals(current)){
                    txt_jumlah_donasi.removeTextChangedListener(this);

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
                   txt_jumlah_donasi.setText(formatted);
                   txt_jumlah_donasi.setSelection(formatted.length());

                   txt_jumlah_donasi.addTextChangedListener(this);
                }
            }
        });

        v.findViewById(R.id.img_gambar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Options options = Options.init()
                        .setRequestCode(ServerURL.PIX_REQUEST_CODE)
                        .setCount(10)
                        .setFrontfacing(false)
                        .setImageQuality(ImageQuality.REGULAR)
                        .setScreenOrientation(Options.SCREEN_ORIENTATION_PORTRAIT);
                Pix.start((FragmentActivity) activity, options);
            }
        });

        v.findViewById(R.id.btn_simpan).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog dialog = new AlertDialog.Builder(activity)
                        .setTitle("Konfirmasi")
                        .setMessage("Apakah anda yakin ingin menyimpan data ?")
                        .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                saveDonatur();
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

        locationManager = new GoogleLocationManager((AppCompatActivity) activity, new GoogleLocationManager.LocationUpdateListener() {
            @Override
            public void onChange(Location location) {
                lat = location.getLatitude();
                lng = location.getLongitude();
            }
        });
        locationManager.startLocationUpdates();

        initData();


        //Inisialisasi UI bluetooth
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Bluetooth tidak menyala");
        builder.setMessage("Bluetooth anda tidak menyala. Nyalakan bluetooth sekarang?");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Meminta user menyalakan bluetooth
                Intent intentOpenBluetoothSettings = new Intent();
                intentOpenBluetoothSettings.setAction(android.provider.Settings.ACTION_BLUETOOTH_SETTINGS);
                activity.startActivity(intentOpenBluetoothSettings);
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
            Toast.makeText(activity, "Adapter Bluetooth tidak tersedia", Toast.LENGTH_SHORT).show();
        }
        /*if(!bluetoothAdapter.isEnabled()) {
            dialogBluetooth.show();
        }*/

        //init dialog UI
        dialogDevices = new Dialog(activity);
        dialogDevices.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogDevices.setContentView(com.gmedia.modul.bluetoothprinter.R.layout.popup_devices);
        list_devices = dialogDevices.findViewById(com.gmedia.modul.bluetoothprinter.R.id.list_devices);
        ListView list_discovered = dialogDevices.findViewById(com.gmedia.modul.bluetoothprinter.R.id.list_discovered);
        btn_devices = dialogDevices.findViewById(com.gmedia.modul.bluetoothprinter.R.id.btn_devices);
        progressbar = dialogDevices.findViewById(com.gmedia.modul.bluetoothprinter.R.id.progressbar);

        deviceAdapter = new ArrayAdapter<>(activity, com.gmedia.modul.bluetoothprinter.R.layout.item_devices, com.gmedia.modul.bluetoothprinter.R.id.txt_device, listDevicesData);
        list_devices.setAdapter(deviceAdapter);
        list_devices.setOnItemClickListener(new DeviceClicked());

        discoveredAdapter = new ArrayAdapter<>(activity, com.gmedia.modul.bluetoothprinter.R.layout.item_devices, com.gmedia.modul.bluetoothprinter.R.id.txt_device, listDiscoveredData);
        list_discovered.setAdapter(discoveredAdapter);
        list_discovered.setOnItemClickListener(new DiscoveredClicked());

        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        activity.registerReceiver(broadcastReceiver, filter);

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
                    activity.registerReceiver(broadcastReceiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));
                }
            }
        });

        return v;
    }

    private void saveDonatur(){
        final Calendar date = Calendar.getInstance();
        final List<Item> items = new ArrayList<>();

        if(!printer.bluetoothAdapter.isEnabled()){

            Toast.makeText(activity, "Mohon hidupkan bluetooth anda, kemudian klik cetak kembali", Toast.LENGTH_LONG).show();
            try{
                printer.dialogBluetooth.show();
            }catch (Exception e){
                e.printStackTrace();
            }
        }else{

            if(printer.isPrinterReady()){
                // TODO doing
                tambahDonatur();
                String nominal ="";
                if(!txt_jumlah_donasi.getText().toString().equals("0")){
                    nominal = txt_jumlah_donasi.getText().toString().replaceAll("[Rp,.\\s]", "");
                }else{
                    nominal = "0";
                }

                transaksi = new Transaksi(txt_nama.getText().toString(), txt_alamat.getText().toString(), Double.parseDouble(nominal), date.getTime(), items,sessionManager.getNama());

                printer.print(transaksi,true);

            }else{

                Toast.makeText(activity, "Harap pilih device printer telebih dahulu", Toast.LENGTH_LONG).show();
                printer.showDevices();
            }
        }

    }

    private void tambahDonatur(){
        dialogBox.showDialog(false);
        JSONBuilder body = new JSONBuilder();
        body.add("nama", txt_nama.getText().toString());
        body.add("alamat", txt_alamat.getText().toString());
        body.add("kontak", txt_kontak.getText().toString());
        body.add("keterangan", edtKetarangan.getText().toString());
        body.add("rt", edtRt.getText().toString());
        body.add("rw", edtRw.getText().toString());
        body.add("lat", lat);
        body.add("long", lng);
        body.add("kota", selectedKota);
        body.add("kecamatan", selectedKecamatan);
        body.add("kelurahan", selectedKelurahan);
        body.add("sales_collector", new SessionManager(activity).getId());
        body.add("nominal", txt_jumlah_donasi.getText().toString().replaceAll("[Rp,.\\s]", ""));
        ArrayList<String> listFoto = new ArrayList<>();

        for(String path : listGambar){
            File image = new File(path);
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            Bitmap bitmap = BitmapFactory.decodeFile(image.getAbsolutePath(), bmOptions);
            String bitmap_string = Converter.convertToBase64(bitmap);
            listFoto.add(bitmap_string);
        }

        body.add("foto", new JSONArray(listFoto));

        new ApiVolley(activity, body.create(), "POST",
                ServerURL.tambahDonaturLuarCollector, new AppRequestCallback
                (new AppRequestCallback.ResponseListener() {
                    @Override
                    public void onSuccess(String response, String message) {

                        dialogBox.dismissDialog();
                        if(activity instanceof CollectorActivity){
                            ((CollectorActivity) activity).switchTab(1);
                            ((CollectorActivity)activity).loadFragment(new CollectorHistoryFragment());
                        }
                        final Calendar date = Calendar.getInstance();
                        final List<Item> items = new ArrayList<>();

                        String nominal ="";
                        if(!txt_jumlah_donasi.getText().toString().equals("0")){
                            nominal = txt_jumlah_donasi.getText().toString().replaceAll("[Rp,.\\s]", "");
                        }else{
                            nominal = "0";
                        }
                        transaksi = new Transaksi(txt_nama.getText().toString(), txt_alamat.getText().toString(), Double.parseDouble(nominal), date.getTime(), items,sessionManager.getNama());

                        printer.print(transaksi,true);
//                        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
//                        builder.setTitle("Print nota infaq");
//                        builder.setMessage("Apakah anda ingin mengeprint nota infaq ?");
//                        builder.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                String nominal ="";
//                                if(!txt_jumlah_donasi.getText().toString().equals("0")){
//                                    nominal = txt_jumlah_donasi.getText().toString().replaceAll("[Rp,.\\s]", "");
//                                }else{
//                                    nominal = "0";
//                                }
//                                transaksi = new Transaksi(txt_nama.getText().toString(), txt_alamat.getText().toString(), Double.parseDouble(nominal), date.getTime(), items,sessionManager.getNama());
//                                if(!bluetoothAdapter.isEnabled()) {
//                                    dialogBluetooth.show();
//                                    Toast.makeText(activity, "Hidupkan bluetooth anda kemudian klik cetak kembali", Toast.LENGTH_LONG).show();
//                                }else{
//                                    if(isPrinterReady()){
//                                        print(transaksi, true);
//                                        if(activity instanceof CollectorActivity){
//                                            ((CollectorActivity) activity).switchTab(1);
//                                            ((CollectorActivity)activity).loadFragment(new CollectorHistoryFragment());
//                                        }
//                                    }else{
//                                        Toast.makeText(activity, "Harap pilih device printer telebih dahulu", Toast.LENGTH_LONG).show();
//                                        showDevices();
//                                    }
//                                }
//                            }
//                        });
//                        builder.setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                dialog.cancel();
//                                if(activity instanceof CollectorActivity){
//                                    ((CollectorActivity) activity).switchTab(1);
//                                    ((CollectorActivity)activity).loadFragment(new CollectorHistoryFragment());
//                                }
//                            }
//                        });
//                        dialogConfirm = builder.create();
//                        dialogConfirm.show();
                        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onEmpty(String message) {
                        dialogBox.dismissDialog();
                        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFail(String message) {
                        dialogBox.dismissDialog();
                        View.OnClickListener clickListener = new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                dialogBox.dismissDialog();
                                tambahDonatur();

                            }
                        };

                        dialogBox.showDialog(clickListener, "Ulangi Proses", "Terjadi kesalahan saat mengambil data");
                    }
                }));
    }

//    private void tambahDonatur(){
//        final Calendar date = Calendar.getInstance();
//        final List<Item> items = new ArrayList<>();
//
//        dialogBox.showDialog(false);
//        JSONBuilder body = new JSONBuilder();
//        body.add("nama", txt_nama.getText().toString());
//        body.add("alamat", txt_alamat.getText().toString());
//        body.add("kontak", txt_kontak.getText().toString());
//        body.add("keterangan", edtKetarangan.getText().toString());
//        body.add("rt", edtRt.getText().toString());
//        body.add("rw", edtRw.getText().toString());
//        body.add("lat", lat);
//        body.add("long", lng);
//        body.add("kota", selectedKota);
//        body.add("kecamatan", selectedKecamatan);
//        body.add("kelurahan", selectedKelurahan);
//        body.add("sales_collector", new SessionManager(activity).getId());
//        body.add("nominal", txt_jumlah_donasi.getText().toString().replaceAll("[Rp,.\\s]", ""));
//        ArrayList<String> listFoto = new ArrayList<>();
//
//        for(String path : listGambar){
//            File image = new File(path);
//            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
//            Bitmap bitmap = BitmapFactory.decodeFile(image.getAbsolutePath(), bmOptions);
//            String bitmap_string = Converter.convertToBase64(bitmap);
//            listFoto.add(bitmap_string);
//        }
//
//        body.add("foto", new JSONArray(listFoto));
//
//        new ApiVolley(activity, body.create(), "POST",
//                ServerURL.tambahDonaturLuarCollector, new AppRequestCallback
//                (new AppRequestCallback.ResponseListener() {
//            @Override
//            public void onSuccess(String response, String message) {
//
//                dialogBox.dismissDialog();
//                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
//                builder.setTitle("Print nota infaq");
//                builder.setMessage("Apakah anda ingin mengeprint nota infaq ?");
//                builder.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        String nominal ="";
//                        if(!txt_jumlah_donasi.getText().toString().equals("0")){
//                            nominal = txt_jumlah_donasi.getText().toString().replaceAll("[Rp,.\\s]", "");
//                        }else{
//                            nominal = "0";
//                        }
//                        transaksi = new Transaksi(txt_nama.getText().toString(), txt_alamat.getText().toString(), Double.parseDouble(nominal), date.getTime(), items,sessionManager.getNama());
//                        if(!bluetoothAdapter.isEnabled()) {
//                            dialogBluetooth.show();
//                            Toast.makeText(activity, "Hidupkan bluetooth anda kemudian klik cetak kembali", Toast.LENGTH_LONG).show();
//                        }else{
//                            if(isPrinterReady()){
//                                print(transaksi, true);
//                                if(activity instanceof CollectorActivity){
//                                    ((CollectorActivity) activity).switchTab(1);
//                                    ((CollectorActivity)activity).loadFragment(new CollectorHistoryFragment());
//                                }
//                            }else{
//                                Toast.makeText(activity, "Harap pilih device printer telebih dahulu", Toast.LENGTH_LONG).show();
//                                showDevices();
//                            }
//                        }
//                    }
//                });
//                builder.setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.cancel();
//                        if(activity instanceof CollectorActivity){
//                            ((CollectorActivity) activity).switchTab(1);
//                            ((CollectorActivity)activity).loadFragment(new CollectorHistoryFragment());
//                        }
//                    }
//                });
//                dialogConfirm = builder.create();
//                dialogConfirm.show();
//                Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
//            }
//
//            @Override
//            public void onEmpty(String message) {
//                dialogBox.dismissDialog();
//                Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
//            }
//
//            @Override
//            public void onFail(String message) {
//                dialogBox.dismissDialog();
//                View.OnClickListener clickListener = new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//
//                        dialogBox.dismissDialog();
//                        tambahDonatur();
//
//                    }
//                };
//
//                dialogBox.showDialog(clickListener, "Ulangi Proses", "Terjadi kesalahan saat mengambil data");
//            }
//        }));
//    }

    private void showKotaDialog(){
        final Dialog dialogChooser = DialogFactory.getInstance().createDialog(activity,
                R.layout.dialog_searchable_spinner, 90, 70);

        listChooser = new ArrayList<>();
        listChooser.addAll(listKota);

        EditText txt_search = dialogChooser.findViewById(R.id.txt_search);
        txt_search.setHint("Cari Kota");
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
                listChooser.clear();
                for(SimpleObjectModel o : listKota){
                    if(o.getValue().toLowerCase().contains(search.toLowerCase())){
                        listChooser.add(o);
                    }
                }

                dialogAdapter.notifyDataSetChanged();
            }
        });

        RecyclerView rv_items = dialogChooser.findViewById(R.id.rv_items);
        rv_items.setItemAnimator(new DefaultItemAnimator());
        rv_items.setLayoutManager(new LinearLayoutManager(activity));
        dialogAdapter = new SearchableSpinnerDialogAdapter(activity,
                listChooser, new SearchableSpinnerDialogAdapter.ChooserListener() {
            @Override
            public void onSelected(String id, String value) {
                txt_kota.setText(value);
                selectedKota = id;

                selectedKecamatan = "";
                txt_kecamatan.setText("");
                listKecamatan.clear();
                selectedKelurahan = "";
                txt_kelurahan.setText("");
                listKelurahan.clear();

                loadKecamatan(id);
                dialogChooser.dismiss();
            }
        });

        rv_items.setAdapter(dialogAdapter);
        dialogChooser.show();
    }

    private void showKecamatanDialog(){
        final Dialog dialogChooser = DialogFactory.getInstance().createDialog(activity,
                R.layout.dialog_searchable_spinner, 90, 70);

        listChooser = new ArrayList<>();
        listChooser.addAll(listKecamatan);

        EditText txt_search = dialogChooser.findViewById(R.id.txt_search);
        txt_search.setHint("Cari Kecamatan");
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
                listChooser.clear();
                for(SimpleObjectModel o : listKecamatan){
                    if(o.getValue().toLowerCase().contains(search.toLowerCase())){
                        listChooser.add(o);
                    }
                }

                dialogAdapter.notifyDataSetChanged();
            }
        });

        RecyclerView rv_items = dialogChooser.findViewById(R.id.rv_items);
        rv_items.setItemAnimator(new DefaultItemAnimator());
        rv_items.setLayoutManager(new LinearLayoutManager(activity));
        dialogAdapter = new SearchableSpinnerDialogAdapter(activity,
                listChooser, new SearchableSpinnerDialogAdapter.ChooserListener() {
            @Override
            public void onSelected(String id, String value) {
                txt_kecamatan.setText(value);
                selectedKecamatan = id;

                selectedKelurahan = "";
                txt_kelurahan.setText("");
                listKelurahan.clear();

                loadKelurahan(id);
                dialogChooser.dismiss();
            }
        });

        rv_items.setAdapter(dialogAdapter);
        dialogChooser.show();
    }

    private void showKelurahanDialog(){
        final Dialog dialogChooser = DialogFactory.getInstance().createDialog(activity,
                R.layout.dialog_searchable_spinner, 90, 70);

        listChooser = new ArrayList<>();
        listChooser.addAll(listKelurahan);

        EditText txt_search = dialogChooser.findViewById(R.id.txt_search);
        txt_search.setHint("Cari Kelurahan");
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
                listChooser.clear();
                for(SimpleObjectModel o : listKelurahan){
                    if(o.getValue().toLowerCase().contains(search.toLowerCase())){
                        listChooser.add(o);
                    }
                }

                dialogAdapter.notifyDataSetChanged();
            }
        });

        RecyclerView rv_items = dialogChooser.findViewById(R.id.rv_items);
        rv_items.setItemAnimator(new DefaultItemAnimator());
        rv_items.setLayoutManager(new LinearLayoutManager(activity));
        dialogAdapter = new SearchableSpinnerDialogAdapter(activity,
                listChooser, new SearchableSpinnerDialogAdapter.ChooserListener() {
            @Override
            public void onSelected(String id, String value) {
                txt_kelurahan.setText(value);
                selectedKelurahan = id;
                dialogChooser.dismiss();
            }
        });

        rv_items.setAdapter(dialogAdapter);
        dialogChooser.show();
    }

    private void initData() {
        txt_kota.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showKotaDialog();
            }
        });

        txt_kecamatan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showKecamatanDialog();
            }
        });

        txt_kelurahan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showKelurahanDialog();
            }
        });

        loadKota();
    }

    private void loadKota(){
        dialogBox.showDialog(false);

        new ApiVolley(activity, new JSONObject(), "GET",
                ServerURL.getKota, new AppRequestCallback(new AppRequestCallback.ResponseListener() {
            @Override
            public void onSuccess(String response, String message) {
                dialogBox.dismissDialog();
                try {
                    JSONArray ja = new JSONArray(response);
                    for(int i = 0; i < ja.length(); i ++){
                        JSONObject jo = ja.getJSONObject(i);
                        listKota.add(
                                new SimpleObjectModel(
                                        jo.getString("id")
                                        ,jo.getString("kota")
                                )
                        );
                    }
                } catch (JSONException e) {
                    Log.e("json_log", e.getMessage());
                    View.OnClickListener clickListener = new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            dialogBox.dismissDialog();
                            loadKota();

                        }
                    };

                    dialogBox.showDialog(clickListener, "Ulangi Proses", "Terjadi kesalahan saat mengambil data");
                }

                if(listKota.size() > 0){
                    txt_kota.setText(listKota.get(0).getValue());
                    selectedKota = listKota.get(0).getId();
                    loadKecamatan(listKota.get(0).getId());
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
                        loadKota();

                    }
                };

                dialogBox.showDialog(clickListener, "Ulangi Proses", "Terjadi kesalahan saat mengambil data");
            }
        }));
    }

    private void loadKecamatan(final String idKota){
        dialogBox.showDialog(false);

        JSONBuilder body = new JSONBuilder();
        body.add("id_kota", idKota);

        new ApiVolley(activity, body.create(), "POST",
                ServerURL.getKecamatan, new AppRequestCallback(new AppRequestCallback.ResponseListener() {
            @Override
            public void onSuccess(String response, String message) {
                try {
                    JSONArray ja = new JSONArray(response);
                    for(int i = 0; i < ja.length(); i++){

                        JSONObject jo = ja.getJSONObject(i);
                        listKecamatan.add(
                                new SimpleObjectModel(
                                        jo.getString("id")
                                        ,jo.getString("kecamatan")
                                )
                        );
                    }
                } catch (JSONException e) {
                    dialogBox.dismissDialog();
                    e.printStackTrace();
                    View.OnClickListener clickListener = new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            dialogBox.dismissDialog();
                            loadKecamatan(idKota);

                        }
                    };

                    dialogBox.showDialog(clickListener, "Ulangi Proses", "Terjadi kesalahan saat mengambil data");
                }

                if(listKecamatan.size() > 0){
                    txt_kecamatan.setText(listKecamatan.get(0).getValue());
                    selectedKecamatan = listKecamatan.get(0).getId();
                    loadKelurahan(listKecamatan.get(0).getId());
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
                        loadKecamatan(idKota);

                    }
                };

                dialogBox.showDialog(clickListener, "Ulangi Proses", "Terjadi kesalahan saat mengambil data");
            }
        }));
    }

    private void loadKelurahan(final String idKecamatan){
        dialogBox.showDialog(false);

        JSONBuilder body = new JSONBuilder();
        body.add("id_kecamatan", idKecamatan);

        new ApiVolley(activity, body.create(), "POST",
                ServerURL.getKelurahan, new AppRequestCallback(new AppRequestCallback.ResponseListener() {
            @Override
            public void onSuccess(String response, String message) {
                dialogBox.dismissDialog();
                try {
                    JSONArray ja = new JSONArray(response);
                    for(int i = 0; i < ja.length(); i ++){

                        JSONObject jo = ja.getJSONObject(i);
                        listKelurahan.add(
                                new SimpleObjectModel(
                                        jo.getString("id")
                                        ,jo.getString("kelurahan")
                                )
                        );
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    View.OnClickListener clickListener = new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            dialogBox.dismissDialog();
                            loadKelurahan(idKecamatan);

                        }
                    };

                    dialogBox.showDialog(clickListener, "Ulangi Proses", "Terjadi kesalahan saat mengambil data");
                }

                if(listKelurahan.size() > 0){
                    txt_kelurahan.setText(listKelurahan.get(0).getValue());
                    selectedKelurahan = listKelurahan.get(0).getId();
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
                        loadKelurahan(idKecamatan);

                    }
                };

                dialogBox.showDialog(clickListener, "Ulangi Proses", "Terjadi kesalahan saat mengambil data");
            }
        }));
    }

    void updateGambar(ArrayList<String> listGambar){
        this.listGambar.addAll(listGambar);
        adapter.notifyDataSetChanged();
    }

    void retryLocation(){
        locationManager.startLocationUpdates();
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
            activity.unregisterReceiver(broadcastReceiver);
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
            Toast.makeText(activity, "Pairing device gagal!", Toast.LENGTH_SHORT).show();
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
//            activity.finish();
            if(activity instanceof CollectorActivity){
                ((CollectorActivity) activity).switchTab(1);
                ((CollectorActivity)activity).loadFragment(new CollectorHistoryFragment());
            }
            Toast.makeText(activity, "Device Bluetooth Printer tersambung", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
//            if(activity instanceof CollectorActivity){
//                ((CollectorActivity) activity).switchTab(1);
//                ((CollectorActivity)activity).loadFragment(new CollectorHistoryFragment());
//            }
            bluetoothDevice = null;
            Toast.makeText(activity, "Device Bluetooth Printer gagal tersambung \n Silahkan cetak ulang nota di history", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(activity, "Device bukan Device Printer, coba Bluetooth lain", Toast.LENGTH_SHORT).show();
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
    public void print(Transaksi transaksi, boolean isKet){
        final int NAMA_MAX = 15;
        final int JUMLAH_MAX = 5;
        final int HARGA_TOTAL_MAX = 10;

        if(bluetoothDevice == null){
            Toast.makeText(activity, "Sambungkan ke device printer terlebih dahulu!", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            double jum = 0;
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

            //PROSES CETAK HEADER
            outputStream.write(PrintFormatter.DEFAULT_STYLE);
            outputStream.write(PrintFormatter.ALIGN_CENTER);
            Bitmap bmp = BitmapFactory.decodeResource(activity.getResources(), com.gmedia.modul.bluetoothprinter.R.drawable.ic_yayasan);
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
            Toast.makeText(activity, "Koneksi printer terputus, harap koneksi ulang bluetooth anda", Toast.LENGTH_LONG).show();
            stopService();
        }
    }

    @Override
    public void onDestroy() {

        printer.stopService();
        super.onDestroy();
    }
}
