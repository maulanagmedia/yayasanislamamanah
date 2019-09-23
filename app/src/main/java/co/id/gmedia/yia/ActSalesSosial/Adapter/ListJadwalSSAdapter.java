package co.id.gmedia.yia.ActSalesSosial.Adapter;

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

public class ListJadwalSSAdapter extends ArrayAdapter {

    private Activity context;
    private List<CustomModel> items;
    private ItemValidation iv = new ItemValidation();

    public ListJadwalSSAdapter(Activity context, List<CustomModel> items) {
        super(context, R.layout.adapter_jadwal_sales_sosial, items);
        this.context = context;
        this.items = items;
    }

    private static class ViewHolder {
        private TextView tvItem1, tvItem2, tvItem3;
        private RelativeLayout rlCheck, rlInput;
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
            convertView = inflater.inflate(R.layout.adapter_jadwal_sales_sosial, null);
            holder.tvItem1 = convertView.findViewById(R.id.tv_item1);
            holder.tvItem2 = convertView.findViewById(R.id.tv_item2);
            holder.tvItem3 = convertView.findViewById(R.id.tv_item3);
            holder.rlCheck = convertView.findViewById(R.id.rl_check);
            holder.rlInput = convertView.findViewById(R.id.rl_input);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        CustomModel item = items.get(position);
        holder.tvItem1.setText(item.getItem2());
        holder.tvItem2.setText(item.getItem3());
        holder.tvItem3.setText(item.getItem4());

        if(item.getItem6().equals("0")){

            holder.rlCheck.setVisibility(View.GONE);
            holder.rlInput.setVisibility(View.VISIBLE);
        }else{

            holder.rlCheck.setVisibility(View.VISIBLE);
            holder.rlInput.setVisibility(View.GONE);
        }

        holder.rlInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        return convertView;

    }
}
