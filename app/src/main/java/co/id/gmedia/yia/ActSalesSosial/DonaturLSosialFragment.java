package co.id.gmedia.yia.ActSalesSosial;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.Uri;
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
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
import co.id.gmedia.yia.ActSalesBrosur.DetailCurrentPosActivity;
import co.id.gmedia.yia.HomeSocialSalesActivity;
import co.id.gmedia.yia.R;
import co.id.gmedia.yia.Utils.AppRequestCallback;
import co.id.gmedia.yia.Utils.Converter;
import co.id.gmedia.yia.Utils.DialogFactory;
import co.id.gmedia.yia.Utils.GoogleLocationManager;
import co.id.gmedia.yia.Utils.JSONBuilder;
import co.id.gmedia.yia.Utils.SearchableSpinnerDialog.SearchableSpinnerDialogAdapter;
import co.id.gmedia.yia.Utils.SearchableSpinnerDialog.SimpleObjectModel;
import co.id.gmedia.yia.Utils.ServerURL;

public class DonaturLSosialFragment extends Fragment {

    private View root;
    private Activity activity;
    private FotoAdapter adapter;
    private List<String> listGambar = new ArrayList<>();
    private List<SimpleObjectModel> listKota = new ArrayList<>(), listKecamatan = new ArrayList<>(), listKelurahan = new ArrayList<>();
    private String selectedKota = "", selectedKecamatan = "", selectedKelurahan = "";
    private DialogBox dialogBox;
    private SearchableSpinnerDialogAdapter dialogAdapter;
    private List<SimpleObjectModel> listChooser = new ArrayList<>();

    private TextView txt_kota, txt_kecamatan, txt_kelurahan;
    private EditText txt_nama, txt_alamat, txt_kontak, edtRT, edtRW, txt_wa;
    private TextView tv_latitude, tv_longitude;

    private String current = "";

    private GoogleLocationManager locationManager;
    private double lat = 0, lng = 0;
    private EditText edtKetarangan;
    private RecyclerView rv_foto;
    private LinearLayout llBukaMap;
    private RadioButton rbKalengYa, rbKalengTidak;
    private RadioGroup rgKaleng;
    private LinearLayout llIsiKaleng;
    private EditText edtJumlahKaleng;

    public DonaturLSosialFragment() {
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
        root = inflater.inflate(R.layout.fragment_donatur_lsosial, container, false);
        activity = getActivity();
        initUI();
        return root;
    }

    private void initUI() {

        dialogBox = new DialogBox(activity);

        txt_nama = root.findViewById(R.id.txt_nama);
        txt_alamat = root.findViewById(R.id.txt_alamat);
        txt_kontak = root.findViewById(R.id.txt_kontak);
        txt_kota = root.findViewById(R.id.txt_kota);
        txt_wa = root.findViewById(R.id.txt_wa);
        edtRT = root.findViewById(R.id.edt_rt);
        edtRW = root.findViewById(R.id.edt_rw);
        txt_kecamatan = root.findViewById(R.id.txt_kecamatan);
        txt_kelurahan = root.findViewById(R.id.txt_kelurahan);
        edtKetarangan = (EditText) root.findViewById(R.id.edt_keterangan);
        tv_latitude = (TextView) root.findViewById(R.id.tv_latitude);
        tv_longitude = (TextView) root.findViewById(R.id.tv_longitude);
        llBukaMap = (LinearLayout) root.findViewById(R.id.ll_buka_map);
        rbKalengYa = (RadioButton) root.findViewById(R.id.rb_kaleng_ya);
        rbKalengTidak = (RadioButton) root.findViewById(R.id.rb_kaleng_tidak);
        rgKaleng = (RadioGroup) root.findViewById(R.id.rg_kaleng);
        llIsiKaleng = (LinearLayout) root.findViewById(R.id.layout_kaleng);
        edtJumlahKaleng = (EditText) root.findViewById(R.id.txt_jumlah_kaleng);

        rv_foto = (RecyclerView) root.findViewById(R.id.rv_foto);
        rv_foto.setItemAnimator(new DefaultItemAnimator());
        rv_foto.setLayoutManager(new LinearLayoutManager(activity, RecyclerView.HORIZONTAL, false));
        adapter = new FotoAdapter(activity, listGambar);
        rv_foto.setAdapter(adapter);

        rbKalengYa.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if(isChecked){
                    root.findViewById(R.id.layout_kaleng).setVisibility(View.VISIBLE);
                }
                else{
                    root.findViewById(R.id.layout_kaleng).setVisibility(View.GONE);
                }
            }
        });

        root.findViewById(R.id.img_gambar).setOnClickListener(new View.OnClickListener() {
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

        root.findViewById(R.id.btn_simpan).setOnClickListener(new View.OnClickListener() {
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

                tv_latitude.setText(String.valueOf(lat));
                tv_longitude.setText(String.valueOf(lng));
            }
        });

        locationManager.startLocationUpdates();

        llBukaMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(activity, DetailCurrentPosActivity.class);
                startActivity(intent);
            }
        });

        initData();
    }

    private void tambahDonatur(){
        dialogBox.showDialog(false);
        JSONBuilder body = new JSONBuilder();

        body.add("nama", txt_nama.getText().toString());
        body.add("alamat", txt_alamat.getText().toString());
        body.add("rt",edtRT.getText().toString());
        body.add("rw",edtRW.getText().toString());
        body.add("kontak", txt_kontak.getText().toString());
        body.add("wa", txt_wa.getText().toString());
        body.add("keterangan", edtKetarangan.getText().toString());
        body.add("lat", lat);
        body.add("long", lng);
        body.add("kota", selectedKota);
        body.add("kecamatan", selectedKecamatan);
        body.add("kelurahan", selectedKelurahan);
        body.add("id_sales", new SessionManager(activity).getId());
        body.add("note", edtKetarangan.getText().toString());
        body.add("donasi", "1");
        body.add("lobi_kaleng", rbKalengYa.isChecked()?"ya":"tidak");
        body.add("total_kaleng", rbKalengYa.isChecked() ? edtJumlahKaleng.getText().toString() : "");
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
                ServerURL.saveDataLuarSosial, new AppRequestCallback
                (new AppRequestCallback.ResponseListener() {
                    @Override
                    public void onSuccess(String response, String message) {

                        dialogBox.dismissDialog();
                        if(activity instanceof HomeSocialSalesActivity){
                            ((HomeSocialSalesActivity)activity).changeState(2);
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

                        dialogBox.showDialog(clickListener, "Ulangi Proses", message);
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

    public void updateGambar(ArrayList<String> listGambar){
        this.listGambar.addAll(listGambar);
        adapter.notifyDataSetChanged();
    }

    public void retryLocation(){
        locationManager.startLocationUpdates();
    }
}
