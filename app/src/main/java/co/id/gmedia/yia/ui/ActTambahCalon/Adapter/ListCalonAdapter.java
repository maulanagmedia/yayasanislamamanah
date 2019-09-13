package co.id.gmedia.yia.ui.ActTambahCalon.Adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import co.id.gmedia.coremodul.CustomModel;
import co.id.gmedia.coremodul.ItemValidation;
import co.id.gmedia.yia.R;

public class ListCalonAdapter extends ArrayAdapter {

    private Activity context;
    private List<CustomModel> items;
    private ItemValidation iv = new ItemValidation();

    public ListCalonAdapter(Activity context, List<CustomModel> items) {
        super(context, R.layout.adapter_list_calon, items);
        this.context = context;
        this.items = items;
    }

    private static class ViewHolder {
        private TextView tvItem1, tvItem2, tvItem3;
        private RelativeLayout rlDelete;
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
            /*convertView = inflater.inflate(R.layout.adapter_pengeluaran, null);
            holder.tvItem1 = convertView.findViewById(R.id.tv_item1);
            holder.tvItem2 = convertView.findViewById(R.id.tv_item2);
            holder.tvItem3 = convertView.findViewById(R.id.tv_item3);
            holder.rlDelete = convertView.findViewById(R.id.rl_delete);*/
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        return convertView;

    }
}
