package co.id.gmedia.yia.ActCollector;


import android.app.Activity;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.fxn.pix.Options;
import com.fxn.pix.Pix;
import com.fxn.utility.ImageQuality;

import java.util.ArrayList;
import java.util.List;

import co.id.gmedia.yia.ActCollector.Adapter.TambahanDonaturFotoAdapter;
import co.id.gmedia.yia.R;
import co.id.gmedia.yia.Utils.Converter;
import co.id.gmedia.yia.Utils.ServerURL;

/**
 * A simple {@link Fragment} subclass.
 */
public class CollectorTambahDonaturFragment extends Fragment {

    private Activity activity;
    private TambahanDonaturFotoAdapter adapter;
    private List<String> listGambar = new ArrayList<>();

    private EditText txt_jumlah_donasi;

    private String current = "";

    public CollectorTambahDonaturFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        activity = getActivity();
        View v =inflater.inflate(R.layout.fragment_collector_tambah_donatur, container, false);

        RecyclerView rv_foto = v.findViewById(R.id.rv_foto);
        rv_foto.setItemAnimator(new DefaultItemAnimator());
        rv_foto.setLayoutManager(new LinearLayoutManager(activity, RecyclerView.HORIZONTAL, false));
        adapter = new TambahanDonaturFotoAdapter(activity, listGambar);
        rv_foto.setAdapter(adapter);

        txt_jumlah_donasi = v.findViewById(R.id.txt_jumlah_donasi);
        txt_jumlah_donasi.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(!s.toString().equals(current)){
                    txt_jumlah_donasi.removeTextChangedListener(this);

                    String cleanString = s.toString().replaceAll("[Rp,.\\s]", "");
                    String formatted;
                    if(!cleanString.isEmpty()){
                        double parsed = Double.parseDouble(cleanString);
                        formatted = Converter.doubleToRupiah(parsed);
                    }
                    else{
                        formatted = "";
                    }

                   current = formatted;
                   txt_jumlah_donasi.setText(formatted);
                   txt_jumlah_donasi.setSelection(formatted.length());

                   txt_jumlah_donasi.addTextChangedListener(this);
                }
            }
        });

        v.findViewById(R.id.img_gambar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Options options = Options.init()
                        .setRequestCode(ServerURL.PIX_REQUEST_CODE)
                        .setCount(10)
                        .setFrontfacing(false)
                        .setImageQuality(ImageQuality.REGULAR)
                        .setScreenOrientation(Options.SCREEN_ORIENTATION_PORTRAIT);
                Pix.start((FragmentActivity) activity, options);
            }
        });

        return v;
    }

    void updateGambar(ArrayList<String> listGambar){
        this.listGambar.addAll(listGambar);
        adapter.notifyDataSetChanged();
    }
}
