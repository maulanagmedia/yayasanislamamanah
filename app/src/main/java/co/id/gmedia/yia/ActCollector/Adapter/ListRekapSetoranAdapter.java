package co.id.gmedia.yia.ActCollector.Adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import co.id.gmedia.coremodul.CustomModel;
import co.id.gmedia.coremodul.ItemValidation;
import co.id.gmedia.yia.R;

public class ListRekapSetoranAdapter extends ArrayAdapter {

    private Activity context;
    private List<CustomModel> items;
    private ItemValidation iv = new ItemValidation();

    public ListRekapSetoranAdapter(Activity context, List<CustomModel> items) {
        super(context, R.layout.adapter_rekap_setoran, items);
        this.context = context;
        this.items = items;
    }

    private static class ViewHolder {
        private TextView tvItem1, tvItem2, tvItem3, tvItem4, tvItem5, tvItem6, tvItem7, tvBtn1, tvBtn2, tvBtn3;
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
            convertView = inflater.inflate(R.layout.adapter_rekap_setoran, null);
            holder.tvBtn1 = convertView.findViewById(R.id.tv_btn1);
            holder.tvBtn2 = convertView.findViewById(R.id.tv_btn2);
            holder.tvBtn3 = convertView.findViewById(R.id.tv_btn3);
            holder.tvItem1 = convertView.findViewById(R.id.tv_item1);
            holder.tvItem2 = convertView.findViewById(R.id.tv_item2);
            holder.tvItem3 = convertView.findViewById(R.id.tv_item3);
            holder.tvItem4 = convertView.findViewById(R.id.tv_item4);
            holder.tvItem5 = convertView.findViewById(R.id.tv_item5);
            holder.tvItem6 = convertView.findViewById(R.id.tv_item6);
            holder.tvItem7 = convertView.findViewById(R.id.tv_item7);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        CustomModel item = items.get(position);
        //holder.tvTanggal.setText(iv.ChangeFormatDateString(item.getItem5(), FormatItem.formatTimestamp, FormatItem.formatDateTimeDisplay1));
        //holder.tvKontak.setText(item.getItem4());

        holder.tvItem1.setText(item.getItem1());
        holder.tvItem2.setText(iv.ChangeToCurrencyFormat(item.getItem2()));
        holder.tvItem3.setText(iv.ChangeToCurrencyFormat(item.getItem3()));
        holder.tvItem4.setText(iv.ChangeToCurrencyFormat(item.getItem4()));
        holder.tvItem5.setText(iv.ChangeToCurrencyFormat(item.getItem5()));
        holder.tvItem6.setText(iv.ChangeToCurrencyFormat(item.getItem6()));
        holder.tvItem7.setText(iv.ChangeToCurrencyFormat(item.getItem7()));

        return convertView;

    }
}
