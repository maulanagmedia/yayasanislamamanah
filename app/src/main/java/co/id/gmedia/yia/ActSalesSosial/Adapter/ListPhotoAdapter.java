package co.id.gmedia.yia.ActSalesSosial.Adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.StrictMode;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import co.id.gmedia.coremodul.ImageUtils;
import co.id.gmedia.coremodul.ItemValidation;
import co.id.gmedia.coremodul.PhotoModel;
import co.id.gmedia.yia.R;

public class ListPhotoAdapter extends RecyclerView.Adapter<ListPhotoAdapter.MyViewHolder>{

    private Context context;
    private List<PhotoModel> masterList;
    private ItemValidation iv = new ItemValidation();

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public ImageView ivPhoto, ivClose;

        public MyViewHolder(View view) {

            super(view);

            ivPhoto = (ImageView) view.findViewById(R.id.iv_photo);
            ivClose = (ImageView) view.findViewById(R.id.iv_close);
        }
    }

    public ListPhotoAdapter(Context context, List masterlist){
        this.context = context;
        this.masterList = masterlist;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_photo, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        final PhotoModel image = masterList.get(position);

        //holder.ivItem1.setImageBitmap(image.getBitmap());
        ImageUtils iu = new ImageUtils();
        if(image.getWeb().isEmpty()){

            File file = new File(image.getUrl());

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(file.getAbsolutePath(), options);
            int imageHeight = options.outHeight;
            int imageWidth = options.outWidth;

            int newWidth = 0;
            int newHeight = 0;

            if(imageHeight > imageWidth){

                newWidth = 640;
                newHeight = newWidth * imageHeight / imageWidth;
            }else{

                newHeight = 640;
                newWidth = newHeight * imageWidth / imageHeight;
            }

            iu.LoadRealImage(file, holder.ivPhoto, newWidth, newHeight);
            //masterList.get(position).setKeterangan(ImageUtils.convert(BitmapFactory.decodeFile(file.getAbsolutePath())));
        }else{

            URL url = null;
            try {
                url = new URL(image.getWeb());
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            Bitmap bmp = null;
            try {
                if(url != null) bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }

            if(bmp != null){
                int imageHeight = bmp.getHeight();
                int imageWidth = bmp.getWidth();

                int newWidth = 0;
                int newHeight = 0;

                if(imageHeight > imageWidth){

                    newWidth = 640;
                    newHeight = newWidth * imageHeight / imageWidth;
                }else{

                    newHeight = 640;
                    newWidth = newHeight * imageWidth / imageHeight;
                }

                iu.LoadRealImage(image.getWeb(), holder.ivPhoto, newWidth, newHeight);
                //masterList.get(position).setKeterangan(ImageUtils.convert(bmp));
            }else{

                iu.LoadRealImage(image.getWeb(), holder.ivPhoto);
            }
        }

        holder.ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog builder = new AlertDialog.Builder(context)
                        .setTitle("Konfirmasi")
                        .setMessage("Apakah anda yakin ingin menghapus foto?")
                        .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                masterList.remove(position);
                                notifyDataSetChanged();
                            }
                        })
                        .setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        })
                        .show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return masterList.size();
    }
}
