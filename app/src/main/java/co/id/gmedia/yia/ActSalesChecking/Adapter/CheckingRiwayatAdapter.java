package co.id.gmedia.yia.ActSalesChecking.Adapter;

import android.app.Activity;
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
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import java.util.List;

import co.id.gmedia.coremodul.FormatItem;
import co.id.gmedia.coremodul.ItemValidation;
import co.id.gmedia.yia.ActAkun.RequestActivity;
import co.id.gmedia.yia.ActSalesBrosur.DetailCurrentPosActivity;
import co.id.gmedia.yia.Model.SurveyRiwayatModel;
import co.id.gmedia.yia.R;
import co.id.gmedia.yia.Utils.Converter;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

public class CheckingRiwayatAdapter extends RecyclerView.Adapter<CheckingRiwayatAdapter.SurveyRiwayatViewHolder> {

    private Context context;
    private List<SurveyRiwayatModel> listDonatur;
    private ItemValidation iv = new ItemValidation();

    public CheckingRiwayatAdapter(Context context, List<SurveyRiwayatModel> listDonatur){
        this.context = context;
        this.listDonatur = listDonatur;
    }

    @NonNull
    @Override
    public SurveyRiwayatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SurveyRiwayatViewHolder(LayoutInflater.from(context).
                inflate(R.layout.item_survey_riwayat, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull SurveyRiwayatViewHolder holder, int position) {
        holder.bind(listDonatur.get(position));
    }

    @Override
    public int getItemCount() {
        return listDonatur.size();
    }

    class SurveyRiwayatViewHolder extends RecyclerView.ViewHolder{

        TextView txt_tanggal, txt_waktu_donasi, txt_lobi_kaleng,
                txt_nama_donatur, txt_alamat_donatur, txt_kontak_donatur, txt_donasi;
        ImageView ivMenu;

        SurveyRiwayatViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_tanggal = itemView.findViewById(R.id.txt_tanggal);
            txt_nama_donatur = itemView.findViewById(R.id.txt_nama_donatur);
            txt_alamat_donatur = itemView.findViewById(R.id.txt_alamat_donatur);
            txt_kontak_donatur = itemView.findViewById(R.id.txt_kontak_donatur);
            txt_waktu_donasi = itemView.findViewById(R.id.txt_waktu_donasi);
            txt_lobi_kaleng = itemView.findViewById(R.id.txt_lobi_kaleng);
            txt_donasi = itemView.findViewById(R.id.txt_donasi);
            ivMenu = itemView.findViewById(R.id.img_menu);
        }

        void bind(SurveyRiwayatModel b){

            txt_nama_donatur.setText(b.getDonatur().getNama());
            txt_alamat_donatur.setText(b.getDonatur().getAlamat());
            txt_kontak_donatur.setText(b.getDonatur().getKontak()+" / "+b.getDonatur().getWa());
            txt_tanggal.setText(iv.ChangeFormatDateString(b.getWaktu(), FormatItem.formatTimestamp, FormatItem.formatDateTimeDisplay1));
            txt_waktu_donasi.setText(b.getWaktu());
            txt_lobi_kaleng.setText(b.getLobi_kaleng());

            if(!b.getDonasi().toUpperCase().equals("YA")){

                txt_donasi.setTextColor(context.getResources().getColor(R.color.colorRed));
            }else{
                txt_donasi.setTextColor(context.getResources().getColor(R.color.colorGreen1));
            }

            txt_donasi.setText("Bersedia Donasi : " + b.getDonasi());

            ivMenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    LayoutInflater inflater = (LayoutInflater) ((Activity)context).getSystemService(LAYOUT_INFLATER_SERVICE);
                    View viewDialog = inflater.inflate(R.layout.dialog_choser_checking, null);
                    builder.setView(viewDialog);
                    //builder.setCancelable(true);

                    final Button btn1 = (Button) viewDialog.findViewById(R.id.btn_1);
                    final Button btn2 = (Button) viewDialog.findViewById(R.id.btn_2);
                    final ImageView ivClose = (ImageView) viewDialog.findViewById(R.id.iv_close);

                    AlertDialog alert = builder.create();
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
                            intent.setData(Uri.parse("tel:" + b.getDonatur().getKontak()));
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
                            intent.putExtra("nama", b.getDonatur().getNama());
                            intent.putExtra("alamat", b.getDonatur().getAlamat());
                            intent.putExtra("lat", b.getDonatur().getLatitude());
                            intent.putExtra("long", b.getDonatur().getLognitude());
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
}
