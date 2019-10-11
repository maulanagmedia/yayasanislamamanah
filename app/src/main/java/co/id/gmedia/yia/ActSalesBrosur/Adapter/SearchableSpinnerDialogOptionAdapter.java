package co.id.gmedia.yia.ActSalesBrosur.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import co.id.gmedia.coremodul.OptionItem;
import co.id.gmedia.yia.R;

public class SearchableSpinnerDialogOptionAdapter extends RecyclerView.Adapter<SearchableSpinnerDialogOptionAdapter.SearchableSpinnerDialogViewHolder> {
    private Context context;
    private List<OptionItem> listKategori;
    private ChooserListener listener;

    public SearchableSpinnerDialogOptionAdapter(Context context, List<OptionItem> listKategori, ChooserListener listener){
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

        void bind(final OptionItem c){
            txt_item.setText(c.getText());
            txt_item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onSelected(c.getValue(), c.getText());
                }
            });
        }
    }

    public interface ChooserListener{
        void onSelected(String value, String text);
    }
}
