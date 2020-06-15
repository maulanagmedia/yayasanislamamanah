package co.id.gmedia.yia.ActCollector;

import android.Manifest;
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
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.gmedia.modul.bluetoothprinter.BluetoothPrinter;
import com.gmedia.modul.bluetoothprinter.Model.Item;
import com.gmedia.modul.bluetoothprinter.Model.Transaksi;
import com.gmedia.modul.bluetoothprinter.PrintFormatter;
import com.gmedia.modul.bluetoothprinter.PspPrinter;
import com.gmedia.modul.bluetoothprinter.RupiahFormatter;
import com.zj.btsdk.BluetoothService;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

import co.id.gmedia.coremodul.SessionManager;
import co.id.gmedia.yia.R;
import co.id.gmedia.yia.Utils.BluetoothHandler;
import co.id.gmedia.yia.Utils.PrinterCommands;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class SettingsActivity extends AppCompatActivity {

    RelativeLayout rlPrinter;
    TextView tvStatus;
    private Context context;

    private final String TAG = SettingsActivity.class.getSimpleName();
    public static final int RC_BLUETOOTH = 0;
    public static final int RC_CONNECT_DEVICE = 1;
    public static final int RC_ENABLE_BLUETOOTH = 2;

    private BluetoothService mService = null;
    private boolean isPrinterReady = false;
    private PspPrinter printer;
    SessionManager sessionManager;
    String message = "<h3>Yayasan Islam Amanah<br> @yay.amanah / WA 0813-1162-6307</h3>";

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
        setContentView(R.layout.settings_activity);
        sessionManager = new SessionManager(SettingsActivity.this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );

        context = this;
//        printer = new PspPrinter(context);
//        printer.startService();
        initUi();
    }

    private void initUi(){

        //Inisialisasi UI
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

        Calendar date = Calendar.getInstance();
        List<Item> items = new ArrayList<>();
        items.add(new Item("Teknolgi", "-", 20000));
        transaksi = new Transaksi("Rohim", "Tegalarum RT 05/04", Double.parseDouble("20000"), date.getTime(), items,sessionManager.getNama());
        rlPrinter = findViewById(R.id.rl_printer);
        tvStatus = findViewById(R.id.tv_status);
        rlPrinter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!bluetoothAdapter.isEnabled()) {

                    dialogBluetooth.show();
                    Toast.makeText(context, "Hidupkan bluetooth anda kemudian klik cetak kembali", Toast.LENGTH_LONG).show();
                }else{

                    if(isPrinterReady()){

                        print(transaksi, true);

                    }else{

                        Toast.makeText(context, "Harap pilih device printer telebih dahulu", Toast.LENGTH_LONG).show();
                        showDevices();
                    }
                }
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        printer.stopService();
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