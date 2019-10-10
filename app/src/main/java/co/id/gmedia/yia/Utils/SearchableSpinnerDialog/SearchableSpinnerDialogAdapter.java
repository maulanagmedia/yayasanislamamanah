package co.id.gmedia.yia.Utils.SearchableSpinnerDialog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import co.id.gmedia.yia.R;

public class SearchableSpinnerDialogAdapter extends RecyclerView.Adapter<SearchableSpinnerDialogAdapter.SearchableSpinnerDialogViewHolder> {
    private Context context;
    private List<SimpleObjectModel> listKategori;
    private ChooserListener listener;

    public SearchableSpinnerDialogAdapter(Context context, List<SimpleObjectModel> listKategori, ChooserListener listener){
        this.context = context;
        this.listKategori = listKategori;
        this.listener = listener;
    }

    @NonNull
    @Override
    public SearchableSpinnerDialogViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SearchableSpinnerDialogViewHolder(LayoutInflater.from(context).inflate(R.layout.item_searchable_spinner, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull SearchableSpinnerDialogViewHolder holder, int position) {
        holder.bind(listKategori.get(position));
    }

    @Override
    public int getItemCount() {
        return listKategori.size();
    }

    class SearchableSpinnerDialogViewHolder extends RecyclerView.ViewHolder{

        TextView txt_item;

        SearchableSpinnerDialogViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_item = itemView.findViewById(R.id.txt_item);
        }

        void bind(final SimpleObjectModel c){
            txt_item.setText(c.getValue());
            txt_item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onSelected(c.getId(), c.getValue());
                }
            });
        }
    }

    public interface ChooserListener{
        void onSelected(String id, String value);
    }
}
