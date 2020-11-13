package co.id.gmedia.yia.ActAdmin

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.Contacts
import android.provider.ContactsContract
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.*
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import co.id.gmedia.coremodul.*
import co.id.gmedia.coremodul.ApiVolley.VolleyCallback
import co.id.gmedia.yia.ActAdmin.Adapter.DonaturAdapter
import co.id.gmedia.yia.ActAkun.DetailAkunActivity
import co.id.gmedia.yia.ActNotifikasi.ListNotificationActivity
import co.id.gmedia.yia.Model.DonaturModel
import co.id.gmedia.yia.R
import co.id.gmedia.yia.Utils.ServerURL
import com.bumptech.glide.Glide
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.CollapsingToolbarLayout
import kotlinx.android.synthetic.main.activity_admin.*
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class AdminActivity : AppCompatActivity(), DonaturAdapter.DonaturAdapterCallback {

    lateinit var sessionManager: SessionManager
    lateinit var collapsingToolbarLayout: CollapsingToolbarLayout
    private val iv = ItemValidation()
    lateinit var dialogBox: DialogBox

    private var keyword: String = ""
    private var start: Int = 0
    private var count: Int = 10

    //permission
    private val appPermission = arrayOf(
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.WRITE_CONTACTS
    )
    private val PERMIOSSION_REQUEST_CODE = 1240

    var listDonatur: ArrayList<DonaturModel> = ArrayList()
    lateinit var adapter: DonaturAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin)
        sessionManager = SessionManager(this)
        dialogBox = DialogBox(this)

        getDashboard()
        initToolbar()
        dialogBox.showDialog(false)
        setupListDonatur()
        setupScrollDonatur()

        val bundle = intent.extras
        if (bundle != null) {
            if (bundle.getBoolean("exit", false)) {
                finish()
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            }
        }

        edt_search.setOnEditorActionListener(object : TextView.OnEditorActionListener {
            override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    dialogBox.showDialog(false)
                    val `in`: InputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    `in`.hideSoftInputFromWindow(edt_search.windowToken, 0)
                    start = 0
                    count = 10
                    keyword = edt_search.text.toString()
                    loadDonatur(1)
                    return true
                }
                return false
            }
        })

        edt_search.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun afterTextChanged(s: Editable) {
                if (edt_search.text.toString().isEmpty()) {
                    dialogBox.showDialog(false)
                    keyword = ""
                    start = 0
                    count = 10
                    loadDonatur(1)
                }
            }
        })

    }

    override fun onResume() {
        loadProfil()

        if (!checkPermission()) {
            checkPermission()
        }
        super.onResume()
    }

    private fun loadProfil() {
        txt_nama.text = sessionManager.nama
        val imageUtils = ImageUtils()
        Glide.with(this@AdminActivity)
                .load(sessionManager.foto)
                .placeholder(R.drawable.ic_profile)
                .into(img_foto)
    }

    private fun initToolbar() {
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
//        active_fragment = fragment
//        val trans = supportFragmentManager.beginTransaction()
//        trans.replace(R.id.layout_container, fragment)
//        trans.commit()
    }

    override fun onBackPressed() {
        val builder = AlertDialog.Builder(this@AdminActivity)
        val inflater = getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val viewDialog = inflater.inflate(R.layout.layout_exit_dialog, null)
        builder.setView(viewDialog)
        builder.setCancelable(false)
        val btnYa = viewDialog.findViewById<Button>(R.id.btn_ya)
        val btnTidak = viewDialog.findViewById<Button>(R.id.btn_tidak)
        val alert = builder.create()
        alert!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        btnYa.setOnClickListener {
            alert?.dismiss()
            val intent = Intent(this@AdminActivity, AdminActivity::class.java)
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

    private fun setupListDonatur() {
        adapter = DonaturAdapter(this@AdminActivity, listDonatur, this)
        rv_donatur.adapter = adapter
        start = 0
        count = 10
        listDonatur.clear()
        loadDonatur(1)
    }

    private fun setupScrollDonatur() {
        rv_donatur.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (!recyclerView.canScrollVertically(1)) {
                    dialogBox?.showDialog(false)
                    start += count
                    loadDonatur(2)
                }
            }
        })
    }

    private fun loadDonatur(flag: Int) {
        // 1 = load
        // 2 = scroll
        if (start == 0) {
            listDonatur.clear()
        }

        var params = JSONObject()
        params.put("id_sales", sessionManager.id)
        params.put("keyword", keyword)
        params.put("start", start)
        params.put("count", count)
        Log.d(">>>params", params.toString());

        ApiVolley(this, params, "POST", ServerURL.getDonaturTetap,
                object : ApiVolley.VolleyCallback {
                    override fun onSuccess(result: String) {
                        dialogBox?.dismissDialog()
                        try {
                            val response = JSONObject(result)
                            val status = response.getJSONObject("metadata").getString("status")
                            val message = response.getJSONObject("metadata").getString("message")
                            if (iv.parseNullInteger(status) == 200) {
                                val res = response.getJSONArray("response")
                                for (i in 0 until res.length()) {
                                    val obj: JSONObject = res.getJSONObject(i)
                                    listDonatur.add(
                                            DonaturModel(
                                                    obj.getString("id"), obj.getString("id_c_donatur"), obj.getString("kdcus"), obj.getString("nama"), obj.getString("alamat"), obj.getString("rt"), obj.getString("rw"), obj.getString("kontak"), obj.getString("wa"), obj.getString("kota"), obj.getString("kecamatan"), obj.getString("kelurahan"), obj.getString("lat"), obj.getString("long"), obj.getInt("status_kontak")
                                            )
                                    )
                                }
                                adapter.notifyDataSetChanged()
                            } else {
                                Log.d(">>>>", message)
                                if (flag == 1) {
                                    adapter.notifyDataSetChanged()
                                }
                            }
                        } catch (e: JSONException) {
                            e.printStackTrace()
                            val clickListener = View.OnClickListener {
                                dialogBox?.dismissDialog()
                                start = 0
                                count = 10
                                keyword = ""
                                loadDonatur(1)
                            }
                            dialogBox?.showDialog(clickListener, "Ulangi Proses", "Terjadi kesalahan saat mengambil data")
                        }
                    }

                    override fun onError(result: String) {
                        val clickListener = View.OnClickListener {
                            dialogBox?.dismissDialog()
                            start = 0
                            count = 10
                            keyword = ""
                            loadDonatur(1)
                        }
                        dialogBox?.showDialog(clickListener, "Ulangi Proses", result)
                    }
                })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_collector, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        val id = item.itemId
        return if (id == R.id.option_profile) {
            val intent = Intent(this, DetailAkunActivity::class.java)
            startActivity(intent)
            true
        } else if (id == R.id.option_notif) {
            val intent = Intent(this, ListNotificationActivity::class.java)
            //            Intent intent =new Intent(context, AdminActivity.class);
            startActivity(intent)
            true
        } else {
            super.onOptionsItemSelected(item)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1001 ) {
            Log.d(">>>id", "id " + sessionManager.idDonatur)
            Log.d(">>>start", "start $start")
            if (resultCode == Activity. RESULT_OK ) {
                Toast. makeText(this, "Added Contact", Toast.LENGTH_SHORT).show()
            }
            if (resultCode == Activity. RESULT_CANCELED ) {
                Toast. makeText(this, "Cancelled Added Contact",
                        Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        for (i in grantResults) {
            if (i == -1) {
                val dialog = androidx.appcompat.app.AlertDialog.Builder(this)
                        .setTitle("Informasi")
                        .setMessage("Mohon ijinkan semua akses untuk menunjang fitur aplikasi")
                        .setNeutralButton("Ok") { dialog, which -> onBackPressed() }
                        .show()
            }
        }
    }

    private fun checkPermission(): Boolean {
        val permissionList: MutableList<String> = ArrayList()
        for (perm in appPermission) {
            if (ContextCompat.checkSelfPermission(this, perm) != PackageManager.PERMISSION_GRANTED) {
                permissionList.add(perm)
            }
        }
        if (permissionList.isNotEmpty()) {
            ActivityCompat.requestPermissions((this as Activity?)!!, permissionList.toTypedArray(), PERMIOSSION_REQUEST_CODE)
            return false
        }
        return true
    }

    override fun onSaveContact(data: DonaturModel) {
        dialogBox.showDialog(false)
        var params = JSONObject()
        params.put("id_sales", sessionManager.id)
        params.put("id_donatur", data.id)
        Log.d(">>>params kontak", params.toString());

        ApiVolley(this, params, "POST", ServerURL.saveKontakDonatur,
                object : ApiVolley.VolleyCallback {
                    override fun onSuccess(result: String) {
                        dialogBox.dismissDialog()
                        try {
                            val response = JSONObject(result)
                            val status = response.getJSONObject("metadata").getString("status")
                            val message = response.getJSONObject("metadata").getString("message")
                            if (iv.parseNullInteger(status) == 200) {
                                keyword = edt_search.text.toString()
                                start = 0
                                count = 10
                                loadDonatur(1)
                                addContact(data.nama, data.kontak)
                            } else {
                                Log.d(">>>>", message)
                            }
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                    }

                    override fun onError(result: String) {
                        Log.d(">>err", result)
                        dialogBox.dismissDialog()
                    }
                }
        )
    }

    private fun addContact(name: String, phone: String) {
        val values = ContentValues()
        values.put(Contacts.People.NUMBER, phone)
        values.put(Contacts.People.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)
        values.put(Contacts.People.LABEL, name)
        values.put(Contacts.People.NAME, name)
        val dataUri: Uri? = contentResolver.insert(Contacts.People.CONTENT_URI, values)
        var updateUri: Uri = Uri.withAppendedPath(dataUri, Contacts.People.Phones.CONTENT_DIRECTORY)
        values.clear()
        values.put(Contacts.People.Phones.TYPE, Contacts.People.TYPE_MOBILE)
        values.put(Contacts.People.NUMBER, phone)
        updateUri = contentResolver.insert(updateUri, values)!!
        Log.d(">>>updateuri", updateUri.toString())
    }

}