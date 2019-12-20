package co.id.gmedia.yia.ActCollector;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import co.id.gmedia.coremodul.ApiVolley;
import co.id.gmedia.coremodul.CustomModel;
import co.id.gmedia.coremodul.DialogBox;
import co.id.gmedia.coremodul.ItemValidation;
import co.id.gmedia.coremodul.OptionItem;
import co.id.gmedia.coremodul.SessionManager;
import co.id.gmedia.yia.ActSalesBrosur.Adapter.SearchableSpinnerDialogOptionAdapter;
import co.id.gmedia.yia.Model.DonaturModel;
import co.id.gmedia.yia.R;
import co.id.gmedia.yia.Utils.DialogFactory;
import co.id.gmedia.yia.Utils.ServerURL;

public class EditDonaturCollectorActivity extends AppCompatActivity {

    private DonaturModel customModel;
    private DialogBox dialogBox;
    private Context context;
    private List<OptionItem> listKota = new ArrayList<>(), listKecamatan = new ArrayList<>(), listKeluarahan = new ArrayList<>();
    private String selectedKota = "", selectedKecamatan = "", selectedKelurahan = "";
    private ItemValidation iv = new ItemValidation();
    private SessionManager session;
    private EditText edtNama, edtAlamat, edtKontak,edtKeterangan;
    private boolean isEdit = false;
    private TextView tvKota, tvKecamatan, tvKelurahan;
    private ImageView ivKota, ivKecamatan, ivKelurahan;
    private Button btnSimpan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_donatur_collector);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );
        setTitle("Edit Donatur");
        context = this;

        edtNama = (EditText) findViewById(R.id.edt_nama);
        edtAlamat = (EditText) findViewById(R.id.edt_alamat);
        edtKontak = (EditText) findViewById(R.id.edt_kontak);
        edtKeterangan = (EditText) findViewById(R.id.edt_keterangan);


        ivKota = (ImageView) findViewById(R.id.iv_kota);
        tvKota = (TextView) findViewById(R.id.tv_kota);
        ivKecamatan = (ImageView) findViewById(R.id.iv_kecamatan);
        tvKecamatan = (TextView) findViewById(R.id.tv_kecamatan);
        ivKelurahan = (ImageView) findViewById(R.id.iv_kelurahan);
        tvKelurahan = (TextView) findViewById(R.id.tv_kelurahan);
        btnSimpan = (Button) findViewById(R.id.btn_simpan);
        dialogBox = new DialogBox(this);

        if(getIntent().hasExtra("donatur")){
            Gson gson = new Gson();
            customModel = gson.fromJson(getIntent().getStringExtra("donatur"), DonaturModel.class);
        }

        initDonatur();
        initEvent();
        getDataKota();
        getDataKecamatan(customModel.getIdKota());
        getDataKelurahan(customModel.getIdKecamatan());

        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            selectedKota = customModel.getIdKota();
            selectedKecamatan = customModel.getIdKecamatan();
            selectedKelurahan = customModel.getIdKelurahan();

            tvKota.setText(customModel.getKota());
            tvKecamatan.setText(customModel.getKecamatan());
            tvKelurahan.setText(customModel.getKelurahan());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        btnSimpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog dialog = new AlertDialog.Builder(context)
                        .setTitle("Konfirmasi")
                        .setMessage("Anda yakin ingin mengubah data donatur")
                        .setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                udpateData();
//                                    Toast.makeText(context,customModel.getItem2(),Toast.LENGTH_SHORT).show();
                            }
                        })
                        .show();

            }
        });
    }

    private void udpateData(){

        JSONObject object = new JSONObject();
        try {
            object.put("id",customModel.getId_donatur());
            object.put("nama",edtNama.getText().toString());
            object.put("alamat",edtAlamat.getText().toString());
            object.put("kontak",edtKontak.getText().toString());
            object.put("kota",selectedKota);
            object.put("kecamatan",selectedKecamatan);
            object.put("kelurahan",selectedKelurahan);
            object.put("note",edtKeterangan.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        new ApiVolley(context, object, "POST", ServerURL.updateDonatur, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {

                dialogBox.dismissDialog();
                try {

                    JSONObject response = new JSONObject(result);
                    String status = response.getJSONObject("metadata").getString("status");
                    String message = response.getJSONObject("metadata").getString("message");

                    Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                    if(iv.parseNullInteger(status) == 200){

                        Intent intent=new Intent();
                        intent.putExtra("isRiwayat","1");
                        setResult(1102,intent);
                        finish();//finishing activity
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    View.OnClickListener clickListener = new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialogBox.dismissDialog();
                            udpateData();
                        }
                    };

                    dialogBox.showDialog(clickListener, "Ulangi Proses", "Terjadi kesalahan saat mengambil data");
                }
            }

            @Override
            public void onError(String result) {
                dialogBox.dismissDialog();
                View.OnClickListener clickListener = new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialogBox.dismissDialog();
                        udpateData();
                    }
                };

                dialogBox.showDialog(clickListener, "Ulangi Proses", "Terjadi kesalahan saat mengambil data");
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void initDonatur(){
        edtNama.setText(customModel.getNama());
        edtKontak.setText(customModel.getKontak());
        edtAlamat.setText(customModel.getAlamat());
        edtKeterangan.setText(customModel.getKeterangan());
    }

    private void initEvent() {


        ivKota.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(listKota.size() > 0){

                    showDialog(listKota, 1);
                }
            }
        });

        ivKecamatan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(listKota.size() > 0){

                    showDialog(listKecamatan, 2);
                }
            }
        });

        ivKelurahan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(listKota.size() > 0){

                    showDialog(listKeluarahan, 3);
                }
            }
        });
    }

    private void showDialog(final List<OptionItem> listData, final int type){

        final Dialog dialogChooser = DialogFactory.getInstance().createDialog((Activity) context,
                R.layout.dialog_searchable_spinner, 90, 70);

        final EditText txt_search = dialogChooser.findViewById(R.id.txt_search);

        final SearchableSpinnerDialogOptionAdapter.ChooserListener listener = new SearchableSpinnerDialogOptionAdapter.ChooserListener() {
            @Override
            public void onSelected(String value, String text) {

                if(type == 1){

                    if(dialogChooser != null) {

                        try {
                            dialogChooser.dismiss();
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }

                    tvKota.setText(text);
                    selectedKota = value;
                    getDataKecamatan(value);

                }else if(type == 2){

                    if(dialogChooser != null) {

                        try {
                            dialogChooser.dismiss();
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }

                    tvKecamatan.setText(text);
                    selectedKecamatan = value;
                    getDataKelurahan(value);

                }else if (type == 3){

                    if(dialogChooser != null) {

                        try {
                            dialogChooser.dismiss();
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }

                    tvKelurahan.setText(text);
                    selectedKelurahan= value;
                }
            }
        };

        final RecyclerView rv_items = dialogChooser.findViewById(R.id.rv_items);
        final SearchableSpinnerDialogOptionAdapter[] dialogAdapter = {new SearchableSpinnerDialogOptionAdapter((Activity) context,
                listData, listener)};

        txt_search.setHint("Keyword");
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

                String keyword = txt_search.getText().toString().toLowerCase();

                List<OptionItem> newList = new ArrayList<>();

                for(OptionItem item : listData){

                    if(item.getText().toLowerCase().contains(keyword)){

                        newList.add(item);
                    }
                }

                dialogAdapter[0] = new SearchableSpinnerDialogOptionAdapter((Activity)context, newList, listener);
                rv_items.setItemAnimator(new DefaultItemAnimator());
                rv_items.setLayoutManager(new LinearLayoutManager((Activity) context));
                rv_items.setAdapter(dialogAdapter[0]);
                dialogAdapter[0].notifyDataSetChanged();
            }
        });

        rv_items.setItemAnimator(new DefaultItemAnimator());
        rv_items.setLayoutManager(new LinearLayoutManager((Activity) context));
        rv_items.setAdapter(dialogAdapter[0]);

        dialogChooser.show();
    }


    private void getDataKota() {

        dialogBox.showDialog(false);
        JSONObject jBody = new JSONObject();

        new ApiVolley(context, jBody, "GET", ServerURL.getKota, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {

                dialogBox.dismissDialog();
                try {

                    JSONObject response = new JSONObject(result);
                    String status = response.getJSONObject("metadata").getString("status");
                    String message = response.getJSONObject("metadata").getString("message");
                    listKota.clear();

                    if(iv.parseNullInteger(status) == 200){

                        JSONArray ja = response.getJSONArray("response");
                        for(int i = 0; i < ja.length(); i ++){

                            JSONObject jo = ja.getJSONObject(i);
                            listKota.add(
                                    new OptionItem(
                                            jo.getString("id")
                                            ,jo.getString("kota")
                                    )
                            );
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    View.OnClickListener clickListener = new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            dialogBox.dismissDialog();
                            getDataKota();

                        }
                    };

                    dialogBox.showDialog(clickListener, "Ulangi Proses", "Terjadi kesalahan saat mengambil data");
                }

                /*adapterKota.notifyDataSetChanged();

                if(listKota.size() > 0){

                    spKota.setSelection(0);
                }*/
            }

            @Override
            public void onError(String result) {
                dialogBox.dismissDialog();
                View.OnClickListener clickListener = new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        dialogBox.dismissDialog();
                        getDataKota();

                    }
                };

                dialogBox.showDialog(clickListener, "Ulangi Proses", "Terjadi kesalahan saat mengambil data");
            }
        });
    }

    private void getDataKecamatan(final String idKota) {

        dialogBox.showDialog(false);

        selectedKecamatan = "";
        tvKecamatan.setText("");
        selectedKelurahan = "";
        tvKelurahan.setText("");

        JSONObject jBody = new JSONObject();
        try {
            jBody.put("id_kota", idKota);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        new ApiVolley(context, jBody, "POST", ServerURL.getKecamatan, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {

                dialogBox.dismissDialog();
                try {

                    JSONObject response = new JSONObject(result);
                    String status = response.getJSONObject("metadata").getString("status");
                    String message = response.getJSONObject("metadata").getString("message");
                    listKecamatan.clear();

                    if(iv.parseNullInteger(status) == 200){

                        JSONArray ja = response.getJSONArray("response");
                        for(int i = 0; i < ja.length(); i ++){

                            JSONObject jo = ja.getJSONObject(i);
                            listKecamatan.add(
                                    new OptionItem(
                                            jo.getString("id")
                                            ,jo.getString("kecamatan")
                                    )
                            );
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    View.OnClickListener clickListener = new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            dialogBox.dismissDialog();
                            getDataKecamatan(idKota);

                        }
                    };

                    dialogBox.showDialog(clickListener, "Ulangi Proses", "Terjadi kesalahan saat mengambil data");
                }

                /*adapterKecamatan.notifyDataSetChanged();

                if(listKecamatan.size() > 0){

                    spKecamatan.setSelection(0);
                }*/
            }

            @Override
            public void onError(String result) {
                dialogBox.dismissDialog();
                View.OnClickListener clickListener = new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        dialogBox.dismissDialog();
                        getDataKecamatan(idKota);

                    }
                };

                dialogBox.showDialog(clickListener, "Ulangi Proses", "Terjadi kesalahan saat mengambil data");
            }
        });
    }

    private void getDataKelurahan(final String idKecamatan) {

        dialogBox.showDialog(false);

        selectedKelurahan = "";
        tvKelurahan.setText("");

        JSONObject jBody = new JSONObject();
        try {
            jBody.put("id_kecamatan", idKecamatan);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        new ApiVolley(context, jBody, "POST", ServerURL.getKelurahan, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {

                dialogBox.dismissDialog();
                try {

                    JSONObject response = new JSONObject(result);
                    String status = response.getJSONObject("metadata").getString("status");
                    String message = response.getJSONObject("metadata").getString("message");
                    listKeluarahan.clear();

                    if(iv.parseNullInteger(status) == 200){

                        JSONArray ja = response.getJSONArray("response");
                        for(int i = 0; i < ja.length(); i ++){

                            JSONObject jo = ja.getJSONObject(i);
                            listKeluarahan.add(
                                    new OptionItem(
                                            jo.getString("id")
                                            ,jo.getString("kelurahan")
                                    )
                            );
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    View.OnClickListener clickListener = new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            dialogBox.dismissDialog();
                            getDataKelurahan(idKecamatan);

                        }
                    };

                    dialogBox.showDialog(clickListener, "Ulangi Proses", "Terjadi kesalahan saat mengambil data");
                }

                /*adapterKelurahan.notifyDataSetChanged();

                if(listKeluarahan.size() > 0){

                    spKelurahan.setSelection(0);
                }*/
            }

            @Override
            public void onError(String result) {

                dialogBox.dismissDialog();
                View.OnClickListener clickListener = new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        dialogBox.dismissDialog();
                        getDataKelurahan(idKecamatan);

                    }
                };

                dialogBox.showDialog(clickListener, "Ulangi Proses", "Terjadi kesalahan saat mengambil data");
            }
        });
    }

}
