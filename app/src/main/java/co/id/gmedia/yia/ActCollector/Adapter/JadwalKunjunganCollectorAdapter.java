package co.id.gmedia.yia.ActCollector.Adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import java.util.Date;
import java.util.List;

import co.id.gmedia.coremodul.ApiVolley;
import co.id.gmedia.coremodul.DialogBox;
import co.id.gmedia.coremodul.SessionManager;
import co.id.gmedia.yia.ActCollector.CollectorDonaturDetailActivity;
import co.id.gmedia.yia.ActSalesBrosur.DetailCurrentPosActivity;
import co.id.gmedia.yia.Model.DonaturModel;
import co.id.gmedia.yia.R;
import co.id.gmedia.yia.Utils.AppRequestCallback;
import co.id.gmedia.yia.Utils.Converter;
import co.id.gmedia.yia.Utils.DialogFactory;
import co.id.gmedia.yia.Utils.JSONBuilder;
import co.id.gmedia.yia.Utils.ServerURL;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

public class JadwalKunjunganCollectorAdapter extends RecyclerView.Adapter<JadwalKunjunganCollectorAdapter.JadwalKunjunganViewHolder> {

    private Context context;
    private List<DonaturModel> listDonatur;

    public JadwalKunjunganCollectorAdapter(Context context, List<DonaturModel> listDonatur){
        this.context = context;
        this.listDonatur = listDonatur;
    }

    @NonNull
    @Override
    public JadwalKunjunganViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new JadwalKunjunganViewHolder(LayoutInflater.from(context).
                inflate(R.layout.item_collector_jadwal, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull JadwalKunjunganViewHolder holder, int position) {
        holder.bind(listDonatur.get(position));
    }

    @Override
    public int getItemCount() {
        return listDonatur.size();
    }

    class JadwalKunjunganViewHolder extends RecyclerView.ViewHolder{

        TextView txt_nama, txt_alamat, txt_kontak, txt_rk;
        ImageView img_plus, img_cek;
        RelativeLayout rlInput;

        JadwalKunjunganViewHolder(@NonNull View itemView) {
            super(itemView);

            txt_nama = itemView.findViewById(R.id.txt_nama);
            txt_alamat = itemView.findViewById(R.id.txt_alamat);
            txt_kontak = itemView.findViewById(R.id.txt_kontak);
            txt_rk = itemView.findViewById(R.id.txt_rk);
            img_cek = itemView.findViewById(R.id.img_cek);
            img_plus = itemView.findViewById(R.id.img_plus);
            rlInput = itemView.findViewById(R.id.rl_input);
        }

        void bind(final DonaturModel b){

            txt_nama.setText(b.getNama());
            txt_kontak.setText(b.getKontak());
            txt_alamat.setText(b.getAlamat());
            txt_rk.setText(b.getRk().toUpperCase());

            if(b.isDikunjungi()){
                img_cek.setVisibility(View.VISIBLE);
                rlInput.setVisibility(View.GONE);
            }else{
                img_cek.setVisibility(View.GONE);
                rlInput.setVisibility(View.VISIBLE);
            }

            rlInput.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Gson gson = new Gson();
                    Intent intent = new Intent(context, CollectorDonaturDetailActivity.class);
                    intent.putExtra("donatur", gson.toJson(b));
                    context.startActivity(intent);
                }
            });

            img_plus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    LayoutInflater inflater = (LayoutInflater) ((Activity)context).getSystemService(LAYOUT_INFLATER_SERVICE);
                    View viewDialog = inflater.inflate(R.layout.dialog_choser_collection, null);
                    builder.setView(viewDialog);
                    //builder.setCancelable(true);

                    final Button btn0 = (Button) viewDialog.findViewById(R.id.btn_0);
                    final Button btn1 = (Button) viewDialog.findViewById(R.id.btn_1);
                    final Button btn2 = (Button) viewDialog.findViewById(R.id.btn_2);
                    final ImageView ivClose = (ImageView) viewDialog.findViewById(R.id.iv_close);

                    final AlertDialog alert = builder.create();
                    alert.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                    alert.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

                    alert.getWindow().setGravity(Gravity.BOTTOM);

                    final AlertDialog alertDialogs = alert;

                    viewDialog.findViewById(R.id.btn_berhenti).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            alert.dismiss();
                            showBerhentiDialog(b.getId_donatur(), b.getKaleng());
                        }
                    });

                    btn0.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            Gson gson = new Gson();
                            Intent intent = new Intent(context, CollectorDonaturDetailActivity.class);
                            intent.putExtra("donatur", gson.toJson(b));
                            intent.putExtra("edit", true);
                            context.startActivity(intent);
                        }
                    });

                    btn1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view2) {

                            if(alertDialogs != null) {

                                try {
                                    alertDialogs.dismiss();
                                }catch (Exception e){
                                    e.printStackTrace();
                                }
                            }

                            Intent intent = new Intent(Intent.ACTION_DIAL);
                            intent.setData(Uri.parse("tel:" + b.getKontak()));
                            if (intent.resolveActivity(context.getPackageManager()) != null) {
                                context.startActivity(intent);
                            }
                        }
                    });

                    btn2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view2) {

                            if(alertDialogs != null) {

                                try {
                                    alertDialogs.dismiss();
                                }catch (Exception e){
                                    e.printStackTrace();
                                }
                            }

                            Intent intent = new Intent(context, DetailCurrentPosActivity.class);
                            intent.putExtra("nama", b.getNama());
                            intent.putExtra("lat", b.getLatitude());
                            intent.putExtra("long", b.getLognitude());
                            context.startActivity(intent);
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
                    berhentiDonasi(id_donatur, Integer.parseInt(txt_kaleng_kembali.getText().toString()), txt_keterangan.getText().toString());
                    dialog_berhenti.dismiss();
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
}
