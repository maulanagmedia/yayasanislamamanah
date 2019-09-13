package co.id.gmedia.yia.ActSalesBrosur;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import co.id.gmedia.yia.R;

public class SalesBrosurDetailFragment extends Fragment {

    private View root;
    private Context context;

    public SalesBrosurDetailFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_sales_brosur_detail, container, false);
        context = root.getContext();
        initUI();
        return root;
    }

    private void initUI() {


    }


}
