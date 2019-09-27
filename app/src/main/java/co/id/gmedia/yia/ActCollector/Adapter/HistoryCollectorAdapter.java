package co.id.gmedia.yia.ActCollector.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import co.id.gmedia.yia.Model.HistoryDonaturModel;
import co.id.gmedia.yia.R;
import co.id.gmedia.yia.Utils.Converter;

public class HistoryCollectorAdapter extends RecyclerView.Adapter
        <HistoryCollectorAdapter.HistoryCollectorViewHolder> {

    private Context context;
    private List<HistoryDonaturModel> listDonatur;

    public HistoryCollectorAdapter(Context context, List<HistoryDonaturModel> listDonatur){
        this.context = context;
        this.listDonatur = listDonatur;
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
                txt_jumlah_donasi, txt_nama_donatur, txt_alamat_donatur, txt_kontak_donatur;

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
        }

        void bind(HistoryDonaturModel b){
            txt_nama_donatur.setText(b.getDonatur().getNama());
            txt_alamat_donatur.setText(b.getDonatur().getAlamat());
            txt_kontak_donatur.setText(b.getDonatur().getKontak());
            txt_tanggal.setText(Converter.DToString(b.getTanggal()));
            txt_jenis_donasi.setText(b.getJenis());
            txt_waktu_donasi.setText(b.getWaktu());
            txt_jumlah_donasi.setText(Converter.doubleToRupiah(b.getJumlah()));

            if(b.isTambahan()){
                txt_tambahan.setVisibility(View.VISIBLE);
            }
            else{
                txt_tambahan.setVisibility(View.GONE);
            }
        }
    }
}