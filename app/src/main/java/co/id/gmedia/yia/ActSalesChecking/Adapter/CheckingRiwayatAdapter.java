package co.id.gmedia.yia.ActSalesChecking.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import co.id.gmedia.coremodul.FormatItem;
import co.id.gmedia.coremodul.ItemValidation;
import co.id.gmedia.yia.Model.SurveyRiwayatModel;
import co.id.gmedia.yia.R;
import co.id.gmedia.yia.Utils.Converter;

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

        SurveyRiwayatViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_tanggal = itemView.findViewById(R.id.txt_tanggal);
            txt_nama_donatur = itemView.findViewById(R.id.txt_nama_donatur);
            txt_alamat_donatur = itemView.findViewById(R.id.txt_alamat_donatur);
            txt_kontak_donatur = itemView.findViewById(R.id.txt_kontak_donatur);
            txt_waktu_donasi = itemView.findViewById(R.id.txt_waktu_donasi);
            txt_lobi_kaleng = itemView.findViewById(R.id.txt_lobi_kaleng);
            txt_donasi = itemView.findViewById(R.id.txt_donasi);
        }

        void bind(SurveyRiwayatModel b){

            txt_nama_donatur.setText(b.getDonatur().getNama());
            txt_alamat_donatur.setText(b.getDonatur().getAlamat());
            txt_kontak_donatur.setText(b.getDonatur().getKontak());
            txt_tanggal.setText(iv.ChangeFormatDateString(b.getWaktu(), FormatItem.formatTimestamp, FormatItem.formatDateTimeDisplay1));
            txt_waktu_donasi.setText(b.getWaktu());
            txt_lobi_kaleng.setText(b.getLobi_kaleng());

            if(!b.getDonasi().toUpperCase().equals("YA")){

                txt_donasi.setTextColor(context.getResources().getColor(R.color.colorRed));
            }else{
                txt_donasi.setTextColor(context.getResources().getColor(R.color.colorGreen1));
            }

            txt_donasi.setText("Bersedia Donasi : " + b.getDonasi());
        }
    }
}
