package co.id.gmedia.yia.ActCollector;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.fxn.pix.Options;
import com.fxn.pix.Pix;
import com.fxn.utility.ImageQuality;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import co.id.gmedia.coremodul.ApiVolley;
import co.id.gmedia.coremodul.DialogBox;
import co.id.gmedia.coremodul.SessionManager;
import co.id.gmedia.yia.ActCollector.Adapter.FotoAdapter;
import co.id.gmedia.yia.R;
import co.id.gmedia.yia.Utils.AppRequestCallback;
import co.id.gmedia.yia.Utils.Converter;
import co.id.gmedia.yia.Utils.DialogFactory;
import co.id.gmedia.yia.Utils.GoogleLocationManager;
import co.id.gmedia.yia.Utils.JSONBuilder;
import co.id.gmedia.yia.Utils.SearchableSpinnerDialog.SearchableSpinnerDialogAdapter;
import co.id.gmedia.yia.Utils.SearchableSpinnerDialog.SimpleObjectModel;
import co.id.gmedia.yia.Utils.ServerURL;

/**
 * A simple {@link Fragment} subclass.
 */
public class CollectorTambahDonaturFragment extends Fragment {

    private Activity activity;
    private FotoAdapter adapter;
    private List<String> listGambar = new ArrayList<>();
    private List<SimpleObjectModel> listKota = new ArrayList<>(), listKecamatan = new ArrayList<>(), listKelurahan = new ArrayList<>();
    private String selectedKota = "", selectedKecamatan = "", selectedKelurahan = "";
    private DialogBox dialogBox;
    private SearchableSpinnerDialogAdapter dialogAdapter;
    private List<SimpleObjectModel> listChooser = new ArrayList<>();

    private TextView txt_kota, txt_kecamatan, txt_kelurahan;
    private EditText txt_nama, txt_alamat, txt_kontak, txt_jumlah_donasi;

    private String current = "";

    private GoogleLocationManager locationManager;
    private double lat = 0, lng = 0;
    private EditText edtKetarangan;

    public CollectorTambahDonaturFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        activity = getActivity();
        View v = inflater.inflate(R.layout.fragment_collector_tambah_donatur, container, false);
        dialogBox = new DialogBox(activity);

        txt_nama = v.findViewById(R.id.txt_nama);
        txt_alamat = v.findViewById(R.id.txt_alamat);
        txt_kontak = v.findViewById(R.id.txt_kontak);
        txt_kota = v.findViewById(R.id.txt_kota);
        txt_kecamatan = v.findViewById(R.id.txt_kecamatan);
        txt_kelurahan = v.findViewById(R.id.txt_kelurahan);
        edtKetarangan = (EditText) v.findViewById(R.id.edt_keterangan);

        RecyclerView rv_foto = v.findViewById(R.id.rv_foto);
        rv_foto.setItemAnimator(new DefaultItemAnimator());
        rv_foto.setLayoutManager(new LinearLayoutManager(activity, RecyclerView.HORIZONTAL, false));
        adapter = new FotoAdapter(activity, listGambar);
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

        v.findViewById(R.id.btn_simpan).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog dialog = new AlertDialog.Builder(activity)
                        .setTitle("Konfirmasi")
                        .setMessage("Apakah anda yakin ingin menyimpan data ?")
                        .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                tambahDonatur();
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

        locationManager = new GoogleLocationManager((AppCompatActivity) activity, new GoogleLocationManager.LocationUpdateListener() {
            @Override
            public void onChange(Location location) {
                lat = location.getLatitude();
                lng = location.getLongitude();
            }
        });
        locationManager.startLocationUpdates();

        initData();

        return v;
    }

    private void tambahDonatur(){

        dialogBox.showDialog(false);
        JSONBuilder body = new JSONBuilder();
        body.add("nama", txt_nama.getText().toString());
        body.add("alamat", txt_alamat.getText().toString());
        body.add("kontak", txt_kontak.getText().toString());
        body.add("keterangan", edtKetarangan.getText().toString());
        body.add("lat", lat);
        body.add("long", lng);
        body.add("kota", selectedKota);
        body.add("kecamatan", selectedKecamatan);
        body.add("kelurahan", selectedKelurahan);
        body.add("sales_collector", new SessionManager(activity).getId());
        body.add("nominal", txt_jumlah_donasi.getText().toString().replaceAll("[Rp,.\\s]", ""));
        ArrayList<String> listFoto = new ArrayList<>();

        for(String path : listGambar){
            File image = new File(path);
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            Bitmap bitmap = BitmapFactory.decodeFile(image.getAbsolutePath(), bmOptions);
            String bitmap_string = Converter.convertToBase64(bitmap);
            listFoto.add(bitmap_string);
        }

        body.add("foto", new JSONArray(listFoto));

        new ApiVolley(activity, body.create(), "POST",
                ServerURL.tambahDonaturLuarCollector, new AppRequestCallback
                (new AppRequestCallback.ResponseListener() {
            @Override
            public void onSuccess(String response, String message) {

                dialogBox.dismissDialog();
                if(activity instanceof CollectorActivity){
                    ((CollectorActivity)activity).loadFragment(new CollectorHistoryFragment());
                }
                Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onEmpty(String message) {
                dialogBox.dismissDialog();
                Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFail(String message) {
                dialogBox.dismissDialog();
                View.OnClickListener clickListener = new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        dialogBox.dismissDialog();
                        tambahDonatur();

                    }
                };

                dialogBox.showDialog(clickListener, "Ulangi Proses", "Terjadi kesalahan saat mengambil data");
            }
        }));
    }

    private void showKotaDialog(){
        final Dialog dialogChooser = DialogFactory.getInstance().createDialog(activity,
                R.layout.dialog_searchable_spinner, 90, 70);

        listChooser = new ArrayList<>();
        listChooser.addAll(listKota);

        EditText txt_search = dialogChooser.findViewById(R.id.txt_search);
        txt_search.setHint("Cari Kota");
        txt_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String search = s.toString();
                listChooser.clear();
                for(SimpleObjectModel o : listKota){
                    if(o.getValue().toLowerCase().contains(search.toLowerCase())){
                        listChooser.add(o);
                    }
                }

                dialogAdapter.notifyDataSetChanged();
            }
        });

        RecyclerView rv_items = dialogChooser.findViewById(R.id.rv_items);
        rv_items.setItemAnimator(new DefaultItemAnimator());
        rv_items.setLayoutManager(new LinearLayoutManager(activity));
        dialogAdapter = new SearchableSpinnerDialogAdapter(activity,
                listChooser, new SearchableSpinnerDialogAdapter.ChooserListener() {
            @Override
            public void onSelected(String id, String value) {
                txt_kota.setText(value);
                selectedKota = id;

                selectedKecamatan = "";
                txt_kecamatan.setText("");
                listKecamatan.clear();
                selectedKelurahan = "";
                txt_kelurahan.setText("");
                listKelurahan.clear();

                loadKecamatan(id);
                dialogChooser.dismiss();
            }
        });

        rv_items.setAdapter(dialogAdapter);
        dialogChooser.show();
    }

    private void showKecamatanDialog(){
        final Dialog dialogChooser = DialogFactory.getInstance().createDialog(activity,
                R.layout.dialog_searchable_spinner, 90, 70);

        listChooser = new ArrayList<>();
        listChooser.addAll(listKecamatan);

        EditText txt_search = dialogChooser.findViewById(R.id.txt_search);
        txt_search.setHint("Cari Kecamatan");
        txt_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String search = s.toString();
                listChooser.clear();
                for(SimpleObjectModel o : listKecamatan){
                    if(o.getValue().toLowerCase().contains(search.toLowerCase())){
                        listChooser.add(o);
                    }
                }

                dialogAdapter.notifyDataSetChanged();
            }
        });

        RecyclerView rv_items = dialogChooser.findViewById(R.id.rv_items);
        rv_items.setItemAnimator(new DefaultItemAnimator());
        rv_items.setLayoutManager(new LinearLayoutManager(activity));
        dialogAdapter = new SearchableSpinnerDialogAdapter(activity,
                listChooser, new SearchableSpinnerDialogAdapter.ChooserListener() {
            @Override
            public void onSelected(String id, String value) {
                txt_kecamatan.setText(value);
                selectedKecamatan = id;

                selectedKelurahan = "";
                txt_kelurahan.setText("");
                listKelurahan.clear();

                loadKelurahan(id);
                dialogChooser.dismiss();
            }
        });

        rv_items.setAdapter(dialogAdapter);
        dialogChooser.show();
    }

    private void showKelurahanDialog(){
        final Dialog dialogChooser = DialogFactory.getInstance().createDialog(activity,
                R.layout.dialog_searchable_spinner, 90, 70);

        listChooser = new ArrayList<>();
        listChooser.addAll(listKelurahan);

        EditText txt_search = dialogChooser.findViewById(R.id.txt_search);
        txt_search.setHint("Cari Kelurahan");
        txt_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String search = s.toString();
                listChooser.clear();
                for(SimpleObjectModel o : listKelurahan){
                    if(o.getValue().toLowerCase().contains(search.toLowerCase())){
                        listChooser.add(o);
                    }
                }

                dialogAdapter.notifyDataSetChanged();
            }
        });

        RecyclerView rv_items = dialogChooser.findViewById(R.id.rv_items);
        rv_items.setItemAnimator(new DefaultItemAnimator());
        rv_items.setLayoutManager(new LinearLayoutManager(activity));
        dialogAdapter = new SearchableSpinnerDialogAdapter(activity,
                listChooser, new SearchableSpinnerDialogAdapter.ChooserListener() {
            @Override
            public void onSelected(String id, String value) {
                txt_kelurahan.setText(value);
                selectedKelurahan = id;
                dialogChooser.dismiss();
            }
        });

        rv_items.setAdapter(dialogAdapter);
        dialogChooser.show();
    }

    private void initData() {
        txt_kota.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showKotaDialog();
            }
        });

        txt_kecamatan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showKecamatanDialog();
            }
        });

        txt_kelurahan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showKelurahanDialog();
            }
        });

        loadKota();
    }

    private void loadKota(){
        dialogBox.showDialog(false);

        new ApiVolley(activity, new JSONObject(), "GET",
                ServerURL.getKota, new AppRequestCallback(new AppRequestCallback.ResponseListener() {
            @Override
            public void onSuccess(String response, String message) {
                dialogBox.dismissDialog();
                try {
                    JSONArray ja = new JSONArray(response);
                    for(int i = 0; i < ja.length(); i ++){
                        JSONObject jo = ja.getJSONObject(i);
                        listKota.add(
                                new SimpleObjectModel(
                                        jo.getString("id")
                                        ,jo.getString("kota")
                                )
                        );
                    }
                } catch (JSONException e) {
                    Log.e("json_log", e.getMessage());
                    View.OnClickListener clickListener = new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            dialogBox.dismissDialog();
                            loadKota();

                        }
                    };

                    dialogBox.showDialog(clickListener, "Ulangi Proses", "Terjadi kesalahan saat mengambil data");
                }

                if(listKota.size() > 0){
                    txt_kota.setText(listKota.get(0).getValue());
                    selectedKota = listKota.get(0).getId();
                    loadKecamatan(listKota.get(0).getId());
                }
            }

            @Override
            public void onEmpty(String message) {
                dialogBox.dismissDialog();
            }

            @Override
            public void onFail(String message) {
                dialogBox.dismissDialog();
                View.OnClickListener clickListener = new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        dialogBox.dismissDialog();
                        loadKota();

                    }
                };

                dialogBox.showDialog(clickListener, "Ulangi Proses", "Terjadi kesalahan saat mengambil data");
            }
        }));
    }

    private void loadKecamatan(final String idKota){
        dialogBox.showDialog(false);

        JSONBuilder body = new JSONBuilder();
        body.add("id_kota", idKota);

        new ApiVolley(activity, body.create(), "POST",
                ServerURL.getKecamatan, new AppRequestCallback(new AppRequestCallback.ResponseListener() {
            @Override
            public void onSuccess(String response, String message) {
                try {
                    JSONArray ja = new JSONArray(response);
                    for(int i = 0; i < ja.length(); i++){

                        JSONObject jo = ja.getJSONObject(i);
                        listKecamatan.add(
                                new SimpleObjectModel(
                                        jo.getString("id")
                                        ,jo.getString("kecamatan")
                                )
                        );
                    }
                } catch (JSONException e) {
                    dialogBox.dismissDialog();
                    e.printStackTrace();
                    View.OnClickListener clickListener = new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            dialogBox.dismissDialog();
                            loadKecamatan(idKota);

                        }
                    };

                    dialogBox.showDialog(clickListener, "Ulangi Proses", "Terjadi kesalahan saat mengambil data");
                }

                if(listKecamatan.size() > 0){
                    txt_kecamatan.setText(listKecamatan.get(0).getValue());
                    selectedKecamatan = listKecamatan.get(0).getId();
                    loadKelurahan(listKecamatan.get(0).getId());
                }
            }

            @Override
            public void onEmpty(String message) {
                dialogBox.dismissDialog();
            }

            @Override
            public void onFail(String message) {
                dialogBox.dismissDialog();
                View.OnClickListener clickListener = new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        dialogBox.dismissDialog();
                        loadKecamatan(idKota);

                    }
                };

                dialogBox.showDialog(clickListener, "Ulangi Proses", "Terjadi kesalahan saat mengambil data");
            }
        }));
    }

    private void loadKelurahan(final String idKecamatan){
        dialogBox.showDialog(false);

        JSONBuilder body = new JSONBuilder();
        body.add("id_kecamatan", idKecamatan);

        new ApiVolley(activity, body.create(), "POST",
                ServerURL.getKelurahan, new AppRequestCallback(new AppRequestCallback.ResponseListener() {
            @Override
            public void onSuccess(String response, String message) {
                dialogBox.dismissDialog();
                try {
                    JSONArray ja = new JSONArray(response);
                    for(int i = 0; i < ja.length(); i ++){

                        JSONObject jo = ja.getJSONObject(i);
                        listKelurahan.add(
                                new SimpleObjectModel(
                                        jo.getString("id")
                                        ,jo.getString("kelurahan")
                                )
                        );
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    View.OnClickListener clickListener = new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            dialogBox.dismissDialog();
                            loadKelurahan(idKecamatan);

                        }
                    };

                    dialogBox.showDialog(clickListener, "Ulangi Proses", "Terjadi kesalahan saat mengambil data");
                }

                if(listKelurahan.size() > 0){
                    txt_kelurahan.setText(listKelurahan.get(0).getValue());
                    selectedKelurahan = listKelurahan.get(0).getId();
                }
            }

            @Override
            public void onEmpty(String message) {
                dialogBox.dismissDialog();
            }

            @Override
            public void onFail(String message) {
                dialogBox.dismissDialog();
                View.OnClickListener clickListener = new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        dialogBox.dismissDialog();
                        loadKelurahan(idKecamatan);

                    }
                };

                dialogBox.showDialog(clickListener, "Ulangi Proses", "Terjadi kesalahan saat mengambil data");
            }
        }));
    }

    void updateGambar(ArrayList<String> listGambar){
        this.listGambar.addAll(listGambar);
        adapter.notifyDataSetChanged();
    }

    void retryLocation(){
        locationManager.startLocationUpdates();
    }
}
