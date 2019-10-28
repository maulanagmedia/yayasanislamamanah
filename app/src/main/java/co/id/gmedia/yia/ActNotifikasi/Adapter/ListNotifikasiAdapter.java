package co.id.gmedia.yia.ActNotifikasi.Adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import co.id.gmedia.coremodul.CustomModel;
import co.id.gmedia.coremodul.FormatItem;
import co.id.gmedia.coremodul.ItemValidation;
import co.id.gmedia.yia.R;

public class ListNotifikasiAdapter extends ArrayAdapter {

    private Activity context;
    private List<CustomModel> items;
    private ItemValidation iv = new ItemValidation();


    public ListNotifikasiAdapter(Activity context, List<CustomModel> items) {
        super(context, R.layout.adapter_list_notif, items);
        this.context = context;
        this.items = items;
    }

    private static class ViewHolder {
        private TextView tvItem1, tvItem2;
        private ImageView ivDown;
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
            convertView = inflater.inflate(R.layout.adapter_list_notif, null);
            holder.tvItem1 = (TextView) convertView.findViewById(R.id.tv_item1);
            holder.tvItem2 = (TextView) convertView.findViewById(R.id.tv_item2);
            holder.ivDown = (ImageView) convertView.findViewById(R.id.iv_down);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        CustomModel item = items.get(position);
        holder.tvItem1.setText(iv.ChangeFormatDateString(item.getItem3(), FormatItem.formatTimestamp, FormatItem.formatDateTimeDisplay1));
        holder.tvItem2.setText(item.getItem2());
        final ViewHolder finalHolder = holder;
        holder.ivDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(finalHolder.tvItem2.getMaxLines() == 1){

                    finalHolder.tvItem2.setMaxLines(1000);
                    finalHolder.ivDown.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_up_arrow));
                }else{

                    finalHolder.tvItem2.setMaxLines(1);
                    finalHolder.ivDown.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_down_arrow));
                }
            }
        });

        return convertView;

    }
}
