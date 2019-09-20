package co.id.gmedia.yia.ActSalesSosial.Adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import co.id.gmedia.coremodul.CustomModel;
import co.id.gmedia.coremodul.FormatItem;
import co.id.gmedia.coremodul.ItemValidation;
import co.id.gmedia.yia.R;

public class ListRiwayatSSAdapter extends ArrayAdapter {

    private Activity context;
    private List<CustomModel> items;
    private ItemValidation iv = new ItemValidation();

    public ListRiwayatSSAdapter(Activity context, List<CustomModel> items) {
        super(context, R.layout.adapter_riwayat_ss, items);
        this.context = context;
        this.items = items;
    }

    private static class ViewHolder {
        private TextView tvTanggal, tvNama, tvAlamat, tvKontak, tvStatus;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        ViewHolder holder = new ViewHolder();

        if(convertView == null){
            LayoutInflater inflater = context.getLayoutInflater();
            convertView = inflater.inflate(R.layout.adapter_riwayat_ss, null);
            holder.tvTanggal = convertView.findViewById(R.id.tv_tanggal);
            holder.tvNama = convertView.findViewById(R.id.tv_nama);
            holder.tvAlamat = convertView.findViewById(R.id.tv_alamat);
            holder.tvKontak = convertView.findViewById(R.id.tv_kontak);
            holder.tvStatus = convertView.findViewById(R.id.tv_status);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        CustomModel item = items.get(position);
        holder.tvTanggal.setText(iv.ChangeFormatDateString(item.getItem5(), FormatItem.formatTimestamp, FormatItem.formatDateTimeDisplay1));
        holder.tvNama.setText(item.getItem2());
        holder.tvAlamat.setText(item.getItem3());
        holder.tvKontak.setText(item.getItem4());

        if(item.getItem6().equals("0")){

            holder.tvStatus.setTextColor(context.getResources().getColor(R.color.colorRed));
        }else{
            holder.tvStatus.setTextColor(context.getResources().getColor(R.color.colorGreen1));
        }

        holder.tvStatus.setText(item.getItem7());




        return convertView;

    }
}
