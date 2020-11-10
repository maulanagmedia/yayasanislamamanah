package co.id.gmedia.yia.admin

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import co.id.gmedia.coremodul.*
import co.id.gmedia.coremodul.ApiVolley.VolleyCallback
import co.id.gmedia.yia.ActCollector.CollectorActivity
import co.id.gmedia.yia.R
import co.id.gmedia.yia.Utils.ServerURL
import com.bumptech.glide.Glide
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.CollapsingToolbarLayout
import kotlinx.android.synthetic.main.activity_admin.*
import org.json.JSONException
import org.json.JSONObject

class AdminActivity : AppCompatActivity() {

    lateinit var sessionManager : SessionManager
    lateinit var collapsingToolbarLayout: CollapsingToolbarLayout
    private var active_fragment: Fragment? = null
    private val iv = ItemValidation()
    lateinit var dialogBox: DialogBox

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin)
        sessionManager = SessionManager(this)
        dialogBox = DialogBox(this)

        loadFragment(DonaturFragment())
        getDashboard()
        initToolbar()
    }

    override fun onResume() {
        loadProfil()
        super.onResume()
    }

    private fun loadProfil() {
        txt_nama.text = sessionManager.nama
        val imageUtils = ImageUtils()
        //        imageUtils.LoadRealImage(session.getFoto(), img_foto);
        Glide.with(this@AdminActivity)
                .load(sessionManager.foto)
                .placeholder(R.drawable.ic_profile)
                .into(img_foto)
    }

    private fun initToolbar() {
        //Inisialisasi Toolbar
//        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        if (supportActionBar != null) {
            supportActionBar!!.title = ""
        }
        collapsingToolbarLayout = findViewById<CollapsingToolbarLayout>(R.id.collapsing)
        collapsingToolbarLayout.title = ""
        collapsingToolbarLayout.setCollapsedTitleTextColor(
                ContextCompat.getColor(this, R.color.colorWhite))
        collapsingToolbarLayout.setExpandedTitleColor(
                ContextCompat.getColor(this, R.color.colorPrimary))
        val appBarLayout = findViewById<AppBarLayout>(R.id.appbar)
        appBarLayout.setExpanded(true)
        appBarLayout.addOnOffsetChangedListener(object : AppBarLayout.OnOffsetChangedListener {
            var isShow = true
            var scrollRange = -1
            override fun onOffsetChanged(appBarLayout: AppBarLayout, verticalOffset: Int) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.totalScrollRange
                }
                if (scrollRange + verticalOffset == 0) {
                    collapsingToolbarLayout.title = "Home"
                    isShow = true
                } else if (isShow) {
                    collapsingToolbarLayout.title = ""
                    isShow = false
                }
            }
        })
    }

    fun loadFragment(fragment: Fragment) {
        active_fragment = fragment
        val trans = supportFragmentManager.beginTransaction()
        trans.replace(R.id.layout_container, fragment)
        trans.commit()
    }


    override fun onBackPressed() {
        val builder = AlertDialog.Builder(this@AdminActivity)
        val inflater = (this@AdminActivity as Activity).getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val viewDialog = inflater.inflate(R.layout.layout_exit_dialog, null)
        builder.setView(viewDialog)
        builder.setCancelable(false)
        val btnYa = viewDialog.findViewById<View>(R.id.btn_ya) as Button
        val btnTidak = viewDialog.findViewById<View>(R.id.btn_tidak) as Button
        val alert = builder.create()
        alert!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        btnYa.setOnClickListener {
            alert?.dismiss()
            val intent = Intent(this@AdminActivity, CollectorActivity::class.java)
            intent.putExtra("exit", true)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }
        btnTidak.setOnClickListener { alert?.dismiss() }
        alert.show()
    }

    private fun getDashboard() {
        dialogBox.showDialog(false)
        ApiVolley(this@AdminActivity, JSONObject(), "GET", ServerURL.getBackgroundDashboard,
                object : VolleyCallback {
                    override fun onSuccess(result: String) {
                        dialogBox.dismissDialog()
                        try {
                            val response = JSONObject(result)
                            val status = response.getJSONObject("metadata").getString("status")
                            val message = response.getJSONObject("metadata").getString("message")
                            if (iv.parseNullInteger(status) == 200) {
                                val gambarBg = response.getJSONObject("response").getString("gambar")
                                //                                imageUtils.LoadRealImage(gambarBg, ivBackground);
                                Glide.with(this@AdminActivity)
                                        .load(gambarBg)
                                        .placeholder(R.drawable.bg_home)
                                        .into(img_sampul)
                            }
                        } catch (e: JSONException) {
                            e.printStackTrace()
                            val clickListener = View.OnClickListener {
                                dialogBox.dismissDialog()
                                getDashboard()
                            }
                            dialogBox.showDialog(clickListener, "Ulangi Proses", "Terjadi kesalahan saat mengambil data")
                        }
                    }

                    override fun onError(result: String) {
                        val clickListener = View.OnClickListener {
                            dialogBox.dismissDialog()
                            getDashboard()
                        }
                        dialogBox.showDialog(clickListener, "Ulangi Proses", result)
                    }
                })
    }

}