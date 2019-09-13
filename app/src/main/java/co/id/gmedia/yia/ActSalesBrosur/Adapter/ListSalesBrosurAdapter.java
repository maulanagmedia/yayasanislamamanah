package co.id.gmedia.yia.ActSalesBrosur.Adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import co.id.gmedia.coremodul.CustomModel;
import co.id.gmedia.coremodul.FormatItem;
import co.id.gmedia.coremodul.ItemValidation;
import co.id.gmedia.yia.R;

public class ListSalesBrosurAdapter extends ArrayAdapter {

    private Activity context;
    private List<CustomModel> items;
    private ItemValidation iv = new ItemValidation();

    public ListSalesBrosurAdapter(Activity context, List<CustomModel> items) {
        super(context, R.layout.adapter_list_sales_brosur, items);
        this.context = context;
        this.items = items;
    }

    private static class ViewHolder {
        private TextView tvTanggal, tvNama, tvAlamat, tvKontak;
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
            convertView = inflater.inflate(R.layout.adapter_list_sales_brosur, null);
            holder.tvTanggal = convertView.findViewById(R.id.tv_tanggal);
            holder.tvNama = convertView.findViewById(R.id.tv_nama);
            holder.tvAlamat = convertView.findViewById(R.id.tv_alamat);
            holder.tvKontak= convertView.findViewById(R.id.tv_kontak);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        CustomModel item = items.get(position);
        holder.tvTanggal.setText(iv.ChangeFormatDateString(item.getItem5(), FormatItem.formatTimestamp, FormatItem.formatDateTimeDisplay1));
        holder.tvNama.setText(item.getItem2());
        holder.tvAlamat.setText(item.getItem3());
        holder.tvKontak.setText(item.getItem4());

        return convertView;

    }
}