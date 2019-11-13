package co.id.gmedia.yia.ActSalesSosial.Adapter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.google.gson.Gson;

import java.util.List;

import co.id.gmedia.coremodul.CustomModel;
import co.id.gmedia.coremodul.ItemValidation;
import co.id.gmedia.yia.ActSalesBrosur.DetailCurrentPosActivity;
import co.id.gmedia.yia.ActSalesSosial.SalesSosialJadwalDetailActivity;
import co.id.gmedia.yia.R;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

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
        private ImageView ivAdd;
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
            holder.ivAdd = convertView.findViewById(R.id.iv_add);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        final CustomModel item = items.get(position);
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
                Gson gson = new Gson();
                Intent intent = new Intent(context, SalesSosialJadwalDetailActivity.class);
                intent.putExtra("donatur", gson.toJson(item));
                context.startActivity(intent);
            }
        });

        holder.ivAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    LayoutInflater inflater = (LayoutInflater) ((Activity)context).getSystemService(LAYOUT_INFLATER_SERVICE);
                    View viewDialog = inflater.inflate(R.layout.dialog_choser, null);
                    builder.setView(viewDialog);
                    //builder.setCancelable(true);

                    final Button btn1 = (Button) viewDialog.findViewById(R.id.btn_1);
                    final Button btn2 = (Button) viewDialog.findViewById(R.id.btn_2);
                    final ImageView ivClose = (ImageView) viewDialog.findViewById(R.id.iv_close);

                    AlertDialog alert = builder.create();
                    alert.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                    alert.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

                    alert.getWindow().setGravity(Gravity.BOTTOM);

                    final AlertDialog alertDialogs = alert;

                    btn1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view2) {

                            if(alertDialogs != null) {

                                try {
                                    alertDialogs.dismiss();
                                }catch (Exception e){
                                    e.printStackTrace();
                                }
                            }

                            Intent intent = new Intent(Intent.ACTION_DIAL);
                            intent.setData(Uri.parse("tel:" + item.getItem4()));
                            if (intent.resolveActivity(context.getPackageManager()) != null) {
                                context.startActivity(intent);
                            }
                        }
                    });

                    btn2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view2) {

                            if(alertDialogs != null) {

                                try {
                                    alertDialogs.dismiss();
                                }catch (Exception e){
                                    e.printStackTrace();
                                }
                            }

                            Intent intent = new Intent(context, DetailCurrentPosActivity.class);
                            intent.putExtra("nama", item.getItem2());
                            intent.putExtra("alamat", item.getItem3());
                            intent.putExtra("lat", item.getItem7());
                            intent.putExtra("long", item.getItem8());
                            context.startActivity(intent);
                        }
                    });

                    ivClose.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            if(alertDialogs != null) {

                                try {
                                    alertDialogs.dismiss();
                                }catch (Exception e){
                                    e.printStackTrace();
                                }
                            }
                        }
                    });

                    try {

                        alert.show();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
        });

        return convertView;

    }
}
