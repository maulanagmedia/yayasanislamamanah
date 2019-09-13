package co.id.gmedia.yia.ui.home;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import co.id.gmedia.yia.R;
import co.id.gmedia.yia.ui.ActTambahCalon.ListCalonActivity;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private View root;
    private Context context;
    private LinearLayout llTambahCalon, llTambahDonatur, llVerifikasiDonatur, llPenerimaanInfaq, llSetoranInfaq;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        root = inflater.inflate(R.layout.fragment_home, container, false);
        context = root.getContext();
        homeViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {

            }
        });

        initUI();
        initEvent();
        return root;
    }

    private void initUI() {

        llTambahCalon = (LinearLayout) root.findViewById(R.id.ll_tambah_calon);
        llTambahDonatur = (LinearLayout) root.findViewById(R.id.ll_tambah_donatur);
        llVerifikasiDonatur = (LinearLayout) root.findViewById(R.id.ll_verifikasi_donatur);
        llPenerimaanInfaq = (LinearLayout) root.findViewById(R.id.ll_tambah_calon);
        llSetoranInfaq = (LinearLayout) root.findViewById(R.id.ll_tambah_calon);
    }

    private void initEvent() {

        llTambahCalon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context, ListCalonActivity.class);
                startActivity(intent);
            }
        });

        llTambahDonatur.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                LayoutInflater inflater = (LayoutInflater) ((Activity)context).getSystemService(LAYOUT_INFLATER_SERVICE);
                View viewDialog = inflater.inflate(R.layout.dialog_pilih, null);
                builder.setView(viewDialog);
                builder.setCancelable(false);

                final Button btn1 = (Button) viewDialog.findViewById(R.id.btn_1);
                final Button btn2 = (Button) viewDialog.findViewById(R.id.btn_2);

                AlertDialog alert = builder.create();
                alert.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

                alert.getWindow().setGravity(Gravity.BOTTOM);

                final AlertDialog alertDialogs = alert;

                btn1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view2) {

                        if(alertDialogs != null) {

                            try {
                                alertDialogs.dismiss();
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                    }
                });

                try {

                    alert.show();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
    }
}