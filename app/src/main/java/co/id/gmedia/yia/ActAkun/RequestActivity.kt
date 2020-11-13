package co.id.gmedia.yia.ActAkun

import android.os.Bundle
import android.os.Handler
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.NestedScrollView
import co.id.gmedia.coremodul.ApiVolley
import co.id.gmedia.coremodul.ApiVolley.VolleyCallback
import co.id.gmedia.coremodul.DialogBox
import co.id.gmedia.coremodul.ItemValidation
import co.id.gmedia.coremodul.SessionManager
import co.id.gmedia.yia.ActAkun.RequestActivity
import co.id.gmedia.yia.Model.DonaturModel
import co.id.gmedia.yia.R
import co.id.gmedia.yia.Utils.ServerURL
import com.google.gson.Gson
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_request.*
import org.json.JSONException
import org.json.JSONObject

class RequestActivity : AppCompatActivity() {
    private var edtSubjek: EditText? = null
    private var edtKet: EditText? = null
    private var tvDonatur: TextView? = null
    private var btnSimpan: Button? = null
    private var dialogBox: DialogBox? = null
    private var session: SessionManager? = null
    private val iv = ItemValidation()
    var donatur_item = ""
    var donaturModel: DonaturModel? = null
    private val gson = Gson()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_request)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        window.setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        )
        title = "Request donatur"
        initUi()
    }

    private fun initUi() {
        tvDonatur = findViewById(R.id.tv_donatur)
        edtSubjek = findViewById(R.id.edt_subjek)
        edtKet = findViewById(R.id.edt_keterangan)
        btnSimpan = findViewById(R.id.btn_simpan)
        dialogBox = DialogBox(this@RequestActivity)
        session = SessionManager(this@RequestActivity)
        donatur_item = intent.getStringExtra(DONATUR_ITEM)
        donaturModel = gson.fromJson(donatur_item, DonaturModel::class.java)
        tv_donatur.text =donaturModel?.nama
        btn_simpan.setOnClickListener(View.OnClickListener { sendRequest() })
    }

    private fun sendRequest() {
        dialogBox!!.showDialog(false)
        val obj = JSONObject()
        try {
            obj.put("id_sales", session!!.id)
            obj.put("id_donatur", donaturModel!!.id_donatur)
            obj.put("subjek", edtSubjek!!.text.toString())
            obj.put("ket", edtKet!!.text.toString())
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        ApiVolley(this@RequestActivity, obj, "POST", ServerURL.urlRequestDonatur, object : VolleyCallback {
            override fun onSuccess(result: String) {
                dialogBox!!.dismissDialog()
                try {
                    val response = JSONObject(result)
                    val status = response.getJSONObject("metadata").getString("status")
                    val message = response.getJSONObject("metadata").getString("message")
                    if (iv.parseNullInteger(status) == 200) {
                        Toasty.success(this@RequestActivity, message, Toast.LENGTH_SHORT, true).show()
                        Handler().postDelayed({ finish() }, 1000)
                    } else {
                        Toasty.error(this@RequestActivity, message, Toast.LENGTH_SHORT, true).show()
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                    val clickListener = View.OnClickListener { dialogBox!!.dismissDialog() }
                    dialogBox!!.showDialog(clickListener, "Ulangi Proses", "Terjadi kesalahan saat mengambil data")
                }
            }

            override fun onError(result: String) {
                dialogBox!!.dismissDialog()
                val clickListener = View.OnClickListener { dialogBox!!.dismissDialog() }
                dialogBox!!.showDialog(clickListener, "Ulangi Proses", "Terjadi kesalahan saat mengambil data")
            }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {
        const val DONATUR_ITEM = "donatur_item"
    }
}