package co.id.gmedia.yia.ActCollector.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

import co.id.gmedia.yia.R;

public class FotoAdapter extends RecyclerView.Adapter
        <FotoAdapter.TambahanDonaturFotoViewHolder> {

    private Context context;
    private List<String> listGambar;

    private boolean isRemovable = true;

    public FotoAdapter(Context context, List<String> listGambar){
        this.context = context;
        this.listGambar = listGambar;
    }

    public FotoAdapter(Context context, List<String> listGambar, boolean isRemovable){
        this.context = context;
        this.listGambar = listGambar;
        this.isRemovable = isRemovable;
    }

    @NonNull
    @Override
    public TambahanDonaturFotoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new TambahanDonaturFotoViewHolder(LayoutInflater.from(context).
                inflate(R.layout.item_collector_tambahan_foto, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull TambahanDonaturFotoViewHolder holder, int position) {
        holder.bind(listGambar.get(position));
    }

    @Override
    public int getItemCount() {
        return listGambar.size();
    }

    class TambahanDonaturFotoViewHolder extends RecyclerView.ViewHolder{

        ImageView img_foto, img_close;

        TambahanDonaturFotoViewHolder(@NonNull View itemView) {
            super(itemView);
            img_foto = itemView.findViewById(R.id.img_foto);
            img_close = itemView.findViewById(R.id.img_close);
        }

        void bind(String url){
            Glide.with(context).load(url).transition(DrawableTransitionOptions.withCrossFade()).
                    apply(new RequestOptions().placeholder(new ColorDrawable(Color.WHITE)).
                            diskCacheStrategy(DiskCacheStrategy.NONE)).into(img_foto);

            if(isRemovable){
                img_close.setVisibility(View.VISIBLE);
                img_close.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        AlertDialog dialogHapus = new AlertDialog.Builder(context)
                                .setTitle("Konfirmasi")
                                .setMessage("Apakah anda yakin ingin menghapus foto?")
                                .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        listGambar.remove(getAdapterPosition());
                                        FotoAdapter.this.notifyItemRemoved(getAdapterPosition());
                                    }
                                })
                                .setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                })
                                .show();

                    }
                });
            }
            else{
                img_close.setVisibility(View.GONE);
            }
        }
    }
}
