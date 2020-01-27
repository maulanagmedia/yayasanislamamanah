package co.id.gmedia.yia.ActSalesSosial.Adapter;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.List;

import co.id.gmedia.coremodul.CustomModel;
import co.id.gmedia.coremodul.FormatItem;
import co.id.gmedia.coremodul.ItemValidation;
import co.id.gmedia.yia.ActSalesBrosur.EditDonaturActivity;
import co.id.gmedia.yia.ActSalesSosial.EditDonaturSosialActivity;
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
        private TextView tvTanggal, tvNama, tvAlamat, tvKontak, tvStatus, tvStatusChecking, tvChecker;
//        private RelativeLayout rlEdit;
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
            holder.tvStatusChecking = convertView.findViewById(R.id.tv_status_checking);
            holder.tvChecker = convertView.findViewById(R.id.tv_checker);
//            holder.rlEdit = convertView.findViewById(R.id.rl_edit);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        final CustomModel item = items.get(position);
        holder.tvTanggal.setText(item.getItem6());
        holder.tvNama.setText(item.getItem3());
        holder.tvAlamat.setText(item.getItem4());
        holder.tvKontak.setText(item.getItem5());

        if(item.getItem7().equals("0")){

            holder.tvStatus.setTextColor(context.getResources().getColor(R.color.colorRed));
        }else{
            holder.tvStatus.setTextColor(context.getResources().getColor(R.color.colorGreen1));
        }

        if(item.getItem12().equals("")){

            holder.tvStatusChecking.setTextColor(context.getResources().getColor(R.color.colorRed));
        }else{
            holder.tvStatusChecking.setTextColor(context.getResources().getColor(R.color.colorGreen1));
        }

        holder.tvStatus.setText(item.getItem9());
        holder.tvStatusChecking.setText(item.getItem11());
        holder.tvChecker.setText(item.getItem12());
//        holder.rlEdit.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Gson gson = new Gson();
//                Intent intent = new Intent(context, EditDonaturSosialActivity.class);
//                intent.putExtra("donatur", gson.toJson(item));
//                intent.putExtra("edit", true);
//                context.startActivityForResult(intent, 1102);
//            }
//        });

        return convertView;

    }
}
