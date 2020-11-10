package co.id.gmedia.yia.ActSalesBrosur.Adapter;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.List;

import co.id.gmedia.coremodul.CustomModel;
import co.id.gmedia.coremodul.FormatItem;
import co.id.gmedia.coremodul.ItemValidation;
import co.id.gmedia.yia.ActCollector.CollectorDonaturDetailActivity;
import co.id.gmedia.yia.ActSalesBrosur.EditDonaturActivity;
import co.id.gmedia.yia.R;

public class ListSalesBrosurAdapter extends ArrayAdapter {

    private Activity context;
    private List<CustomModel> items;
    private ItemValidation iv = new ItemValidation();
//    private ListSalesBrosurAdapterCallback mAdapterCallback;

    public ListSalesBrosurAdapter(Activity context, List<CustomModel> items) {
        super(context, R.layout.adapter_list_sales_brosur, items);
        this.context = context;
        this.items = items;
//        this.mAdapterCallback = listSalesBrosurAdapter;
    }

    private static class ViewHolder {
        private TextView tvTanggal, tvNama, tvAlamat, tvKontak;
        private RelativeLayout rlEdit;
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
//            holder.rlEdit = convertView.findViewById(R.id.rl_edit);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        final CustomModel item = items.get(position);
        //holder.tvTanggal.setText(iv.ChangeFormatDateString(item.getItem5(), FormatItem.formatTimestamp, FormatItem.formatDateTimeDisplay1));
        holder.tvTanggal.setText(item.getItem5());
        holder.tvNama.setText(item.getItem2());
        holder.tvAlamat.setText(item.getItem3());
        holder.tvKontak.setText(item.getItem4()+" / "+item.getItem13());
//        holder.rlEdit.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Gson gson = new Gson();
//                Intent intent = new Intent(context, EditDonaturActivity.class);
//                intent.putExtra("donatur", gson.toJson(item));
//                intent.putExtra("edit", true);
//                context.startActivityForResult(intent, 1102);
//            }
//        });

        return convertView;

    }

//    public interface ListSalesBrosurAdapterCallback {
//        void editDonatur(int position);
//    }
}
