package com.gmedia.modul.bluetoothprinter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.Toast;

import com.gmedia.modul.bluetoothprinter.Model.Item;
import com.gmedia.modul.bluetoothprinter.Model.Transaksi;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class Printer extends BluetoothPrinter {
    /*
        BLUETOOTH PRINTER
        Library untuk menggunakan bluetooth printer. Langkah menggunakan :
        1. Buat objek BluetoothPrinter dengan menggunakan keyword new dengan parameter input context
            (ex : btPrint = new BluetoothPrinter(this))
        2. panggil method startService untuk menginisialisasi object bluetooth printer
            (ex : btnPrint.startService())
        3. panggil method showDevices untuk melakukan koneksi dengan device bluetooth printer
            (ex : btPrint.showDevices())
        4. panggil method print dengan parameter transaksi untuk mencetak nota di device
            (ex :
            Calendar date = Calendar.getInstance();
            List<Item> listTransaksi = new ArrayList<>();
            listTransaksi.add(new Item("Denom 5k", 20, 5500));
            listTransaksi.add(new Item("Denom 25k", 10, 20500));
            listTransaksi.add(new Item("Denom 100k", 5, 97000));
            Transaksi t = new Transaksi("Yunma Jaya Cell", "Andi Kusworo", "PD001", date.getTime(), listTransaksi)
            btPrint.print(transaksi))
        5. panggil method stopService untuk mengakhiri koneksi, saran : gunakan di method onDestroy Activity
            (ex : btPrint.stopService())
    */

    public static String npwpToko = "";
    public Printer(Context context){
        super(context);
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
            Bitmap bmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.yia_header);
            byte[] bmp_byte = PrintFormatter.decodeBitmap(bmp);
            if(bmp_byte != null){
                outputStream.write(bmp_byte);
            }
            outputStream.write("YAYASAN ISLAM AMANAH \n  @yay.amanah \n WA 0813-1162-6307".getBytes());

            outputStream.write(PrintFormatter.NEW_LINE);
            outputStream.write(PrintFormatter.ALIGN_RIGHT);
            SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault());
            String currentDateandTime = sdf.format(transaksi.getTglPengambilan());
            outputStream.write(String.format("%s\n", currentDateandTime).getBytes());
            outputStream.write(PrintFormatter.NEW_LINE);

            outputStream.write(PrintFormatter.ALIGN_LEFT);
            outputStream.write(String.format("Nama    : %s\n", transaksi.getNama()).getBytes());
            outputStream.write(PrintFormatter.NEW_LINE);
            outputStream.write(String.format("Alamat  : %s\n", transaksi.getAlamat()).getBytes());
            outputStream.write(PrintFormatter.NEW_LINE);
            outputStream.write(String.format("Nominal : %s\n", RupiahFormatter.getRupiah(transaksi.getNominal())).getBytes());
            outputStream.write(PrintFormatter.NEW_LINE);
            outputStream.write(String.format("Petugas : %s\n", transaksi.getSales()).getBytes());

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

    private String[] leftAligned(String s, int max_length, int n){
        //Mencetak transaksi secara rata kiri
        String[] result = new String[n];
        int counter = 0;
        for(int i = 0; i < n; i++){
            StringBuilder builder = new StringBuilder();
            for(int j = 0; j < max_length; j++){
                if(counter < s.length()){
                    builder.append(s.charAt(counter));
                    counter++;
                }
                else{
                    builder.append(" ");
                }
            }
            result[i] = builder.toString();
            System.out.println(result[i]);
        }
        return result;
    }

    private String[] rightAligned(String s, int max_length, int n){
        //Mencekak transaksi secara rata kanan
        String[] result = new String[n];
        int counter = 0;
        for(int i = 0; i < n; i++) {
            StringBuilder builder = new StringBuilder();
            if (counter >= s.length()) {
                for (int j = 0; j < max_length; j++) {
                    builder.append(" ");
                }
            } else if (s.length() - i * max_length < max_length) {
                int pad = max_length - (s.length() - i * max_length);
                for (int j = 0; j < max_length; j++) {
                    if (j < pad) {
                        builder.append(" ");
                    } else {
                        builder.append(s.charAt(counter));
                        counter++;
                    }
                }
            } else {
                for (int j = 0; j < max_length; j++) {
                    builder.append(s.charAt(counter));
                    counter++;
                }
            }
            result[i] = builder.toString();
            System.out.println(result[i]);
        }
        return result;
    }
}
