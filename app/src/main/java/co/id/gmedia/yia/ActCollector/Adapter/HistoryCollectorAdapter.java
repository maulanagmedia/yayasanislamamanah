package co.id.gmedia.yia.ActCollector.Adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.gmedia.modul.bluetoothprinter.Model.Item;
import com.gmedia.modul.bluetoothprinter.Model.Transaksi;
import com.gmedia.modul.bluetoothprinter.Printer;
import com.google.gson.Gson;
import com.rohimdev.sweetdialog.SweetDialog;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import co.id.gmedia.coremodul.ApiVolley;
import co.id.gmedia.coremodul.DialogBox;
import co.id.gmedia.coremodul.FormatItem;
import co.id.gmedia.coremodul.ItemValidation;
import co.id.gmedia.coremodul.SessionManager;
import co.id.gmedia.yia.ActAkun.RequestActivity;
import co.id.gmedia.yia.ActCollector.CollectorActivity;
import co.id.gmedia.yia.ActCollector.CollectorHistoryFragment;
import co.id.gmedia.yia.ActSalesBrosur.DetailCurrentPosActivity;
import co.id.gmedia.yia.Model.DonaturModel;
import co.id.gmedia.yia.Model.HistoryDonaturModel;
import co.id.gmedia.yia.R;
import co.id.gmedia.yia.Utils.AppRequestCallback;
import co.id.gmedia.yia.Utils.Converter;
import co.id.gmedia.yia.Utils.DialogFactory;
import co.id.gmedia.yia.Utils.JSONBuilder;
import co.id.gmedia.yia.Utils.ServerURL;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

public class HistoryCollectorAdapter extends RecyclerView.Adapter
        <HistoryCollectorAdapter.HistoryCollectorViewHolder> {

    private Context context;
    private ItemValidation iv = new ItemValidation();
    private List<DonaturModel> listDonatur;
    Transaksi transaksi;
    SessionManager sessionManager;
    private HistoryCollectorAdapterCalback collectorAdapterCalback;

    public HistoryCollectorAdapter(Context context, List<DonaturModel> listDonatur, HistoryCollectorAdapterCalback calback){
        this.context = context;
        this.listDonatur = listDonatur;
        this.collectorAdapterCalback = calback;
        sessionManager = new SessionManager(context);
    }

    @NonNull
    @Override
    public HistoryCollectorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new HistoryCollectorViewHolder(LayoutInflater.from(context).
                inflate(R.layout.item_collector_history, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryCollectorViewHolder holder, int position) {
        holder.bind(listDonatur.get(position));
    }

    @Override
    public int getItemCount() {
        return listDonatur.size();
    }

    class HistoryCollectorViewHolder extends RecyclerView.ViewHolder{

        TextView txt_tambahan, txt_tanggal, txt_waktu_donasi, txt_jenis_donasi,
                txt_jumlah_donasi, txt_nama_donatur, txt_alamat_donatur, txt_kontak_donatur, txt_nominal, txt_jenis, tvTanggal;
        ImageView imgMenu;

        HistoryCollectorViewHolder(@NonNull View itemView) {
            super(itemView);

            txt_tambahan = itemView.findViewById(R.id.txt_tambahan);
            txt_tanggal = itemView.findViewById(R.id.txt_tanggal);
            txt_nama_donatur = itemView.findViewById(R.id.txt_nama_donatur);
            txt_alamat_donatur = itemView.findViewById(R.id.txt_alamat_donatur);
            txt_kontak_donatur = itemView.findViewById(R.id.txt_kontak_donatur);
            txt_jenis_donasi = itemView.findViewById(R.id.txt_jenis_donasi);
            txt_waktu_donasi = itemView.findViewById(R.id.txt_waktu_donasi);
            txt_jumlah_donasi = itemView.findViewById(R.id.txt_jumlah_donasi);
            txt_nominal= itemView.findViewById(R.id.txt_nominal);
            txt_jenis= itemView.findViewById(R.id.txt_jenis);
            tvTanggal= itemView.findViewById(R.id.tv_tanggal);
            imgMenu = itemView.findViewById(R.id.img_menu);
        }

        void bind(final DonaturModel b){
            txt_nama_donatur.setText(b.getNama());
            txt_alamat_donatur.setText(b.getAlamat());
            txt_kontak_donatur.setText(b.getKontak());
            txt_nominal.setText(iv.ChangeToRupiahFormat(b.getNominal()));
            txt_jenis.setText(b.getJenisDonatur());
            tvTanggal.setText(iv.ChangeFormatDateString(b.getTanggal(), FormatItem.formatTimestamp, FormatItem.formatDateTimeStamp));

            final Calendar date = Calendar.getInstance();
            final List<Item> items = new ArrayList<>();
            String nominal ="";
            if(!b.getNominal().equals("0")){
                nominal = b.getNominal();
            }else{
                nominal = "0";
            }

            final String finalNominal = nominal;
            imgMenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    LayoutInflater inflater = (LayoutInflater) ((Activity)context).getSystemService(LAYOUT_INFLATER_SERVICE);
                    View viewDialog = inflater.inflate(R.layout.dialog_choser_history_collector, null);
                    builder.setView(viewDialog);

                    final ImageView ivClose = (ImageView) viewDialog.findViewById(R.id.iv_close);

                    final AlertDialog alert = builder.create();
                    alert.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                    alert.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

                    alert.getWindow().setGravity(Gravity.BOTTOM);

                    final AlertDialog alertDialogs = alert;

                    viewDialog.findViewById(R.id.btn_request).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(context, RequestActivity.class);
                            intent.putExtra(RequestActivity.DONATUR_ITEM, new Gson().toJson(b));
                            context.startActivity(intent);
                        }
                    });

                    viewDialog.findViewById(R.id.btn_print).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            Date date = null;
                            try {
                                date = format.parse(b.getTanggal());
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            transaksi = new Transaksi(b.getNama(), b.getAlamat(), Double.parseDouble(finalNominal), date, sessionManager.getNama());
                            collectorAdapterCalback.onRowPrintNota(transaksi);
                        }
                    });

                    viewDialog.findViewById(R.id.btn_berhenti).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            alert.dismiss();
                            showBerhentiDialog(b.getId_donatur(), b.getKaleng());
                        }
                    });

                    ivClose.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

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
            });
        }
    }

    private void showBerhentiDialog(final String id_donatur, final String kaleng){
        final Dialog dialog_berhenti = DialogFactory.getInstance().createDialog((Activity) context,
                R.layout.dialog_berhenti_donasi, 90);

        final EditText txt_kaleng_kembali = dialog_berhenti.findViewById(R.id.txt_kaleng_kembali);
        txt_kaleng_kembali.setText(kaleng);
        final EditText txt_keterangan = dialog_berhenti.findViewById(R.id.txt_keterangan);

        dialog_berhenti.findViewById(R.id.btn_dialog_berhenti).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!txt_kaleng_kembali.getText().toString().equals("")){

                    AlertDialog dialog = new AlertDialog.Builder(context)
                            .setTitle("Konfirmasi")
                            .setMessage("Apakah anda yakin ingin menyimpan data?")
                            .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    berhentiDonasi(id_donatur, Integer.parseInt(txt_kaleng_kembali.getText().toString()), txt_keterangan.getText().toString());
                                    dialog_berhenti.dismiss();
                                }
                            })
                            .setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            })
                            .show();
                }
                else{
                    Toast.makeText(context, "Jumlah kaleng kembali tidak boleh kosong", Toast.LENGTH_SHORT).show();
                }
            }
        });

        dialog_berhenti.show();
    }


    private void berhentiDonasi(String id_donatur, int kaleng_kembali, String keterangan){
        final DialogBox dialogBox = new DialogBox(context);
        dialogBox.showDialog(false);

        JSONBuilder body = new JSONBuilder();
        body.add("id_donatur", id_donatur);
        body.add("id_user", new SessionManager(context).getId());
        body.add("tgl_berhenti", Converter.DToString(new Date()));
        body.add("kaleng_kembali", kaleng_kembali);
        body.add("ket_berhenti", keterangan);

        new ApiVolley(context, body.create(), "POST", ServerURL.berhentiDonasi,
                new AppRequestCallback(new AppRequestCallback.ResponseListener() {
                    @Override
                    public void onSuccess(String response, String message) {

                        dialogBox.dismissDialog();
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onEmpty(String message) {

                        dialogBox.dismissDialog();
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFail(String message) {

                        dialogBox.dismissDialog();
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                    }
                }));
    }

    public interface HistoryCollectorAdapterCalback {
        void onRowPrintNota(Transaksi transaksi);
    }
}
