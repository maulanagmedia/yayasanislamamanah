package co.id.gmedia.yia.ActCollector.Adapter;

import android.content.Context;
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

    public FotoAdapter(Context context, List<String> listGambar){
        this.context = context;
        this.listGambar = listGambar;
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

        ImageView img_foto;

        TambahanDonaturFotoViewHolder(@NonNull View itemView) {
            super(itemView);
            img_foto = itemView.findViewById(R.id.img_foto);
        }

        void bind(String url){
            Glide.with(context).load(url).transition(DrawableTransitionOptions.withCrossFade()).
                    apply(new RequestOptions().placeholder(new ColorDrawable(Color.WHITE)).
                            diskCacheStrategy(DiskCacheStrategy.NONE)).into(img_foto);
        }
    }
}
