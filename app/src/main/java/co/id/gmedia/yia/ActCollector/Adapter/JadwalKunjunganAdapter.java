package co.id.gmedia.yia.ActCollector.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import co.id.gmedia.yia.Model.DonaturModel;
import co.id.gmedia.yia.R;

public class JadwalKunjunganAdapter extends RecyclerView.Adapter<JadwalKunjunganAdapter.JadwalKunjunganViewHolder> {

    private Context context;
    private List<DonaturModel> listDonatur;

    public JadwalKunjunganAdapter(Context context, List<DonaturModel> listDonatur){
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

        TextView txt_nama, txt_alamat, txt_kontak;
        ImageView img_plus, img_cek;

        JadwalKunjunganViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_nama = itemView.findViewById(R.id.txt_nama);
            txt_alamat = itemView.findViewById(R.id.txt_alamat);
            txt_kontak = itemView.findViewById(R.id.txt_kontak);
            img_cek = itemView.findViewById(R.id.img_cek);
            img_plus = itemView.findViewById(R.id.img_plus);
        }

        void bind(DonaturModel b){
            txt_nama.setText(b.getNama());
            txt_kontak.setText(b.getKontak());
            txt_alamat.setText(b.getAlamat());
        }
    }
}
