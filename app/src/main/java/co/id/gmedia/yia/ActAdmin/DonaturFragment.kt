package co.id.gmedia.yia.ActAdmin

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import co.id.gmedia.coremodul.ApiVolley
import co.id.gmedia.coremodul.DialogBox
import co.id.gmedia.coremodul.ItemValidation
import co.id.gmedia.coremodul.SessionManager
import co.id.gmedia.yia.ActAdmin.Adapter.DonaturAdapter
import co.id.gmedia.yia.Model.DonaturModel
import co.id.gmedia.yia.R
import co.id.gmedia.yia.Utils.ServerURL
import kotlinx.android.synthetic.main.fragment_donatur.*
import kotlinx.android.synthetic.main.fragment_donatur.view.*
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class DonaturFragment : Fragment() {

    lateinit var viewx : View
    private var keyword : String = ""
    private var start : Int = 0
    private var count : Int = 20
    lateinit var sessionManager : SessionManager

    var listDonatur: ArrayList<DonaturModel> = ArrayList()
    lateinit var adapter : DonaturAdapter

    private val iv = ItemValidation()
    private var dialogBox: DialogBox? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        viewx = inflater.inflate(R.layout.fragment_donatur, container, false)
        dialogBox = DialogBox(context)
        sessionManager = SessionManager(context)

//        setupListDonatur()
//        setupScrollDonatur()
        return viewx
    }

//    private fun setupListDonatur(){
//        adapter = context?.let { DonaturAdapter(it, listDonatur) }!!
//        viewx.rv_donatur.adapter = adapter
//        start =0
//        count = 20
//        listDonatur.clear()
//        loadDonatur(1)
//    }
//
//    private fun setupScrollDonatur() {
//        viewx.rv_donatur.addOnScrollListener(object : RecyclerView.OnScrollListener() {
//            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
//                super.onScrolled(recyclerView, dx, dy)
//                if (!recyclerView.canScrollVertically(1)) {
//                    dialogBox?.showDialog(false)
//                    start += count
//                    loadDonatur(2)
//                }
//            }
//        })
//    }
//
//    private fun loadDonatur(flag : Int){
//        // 1 = load
//        // 2 = scroll
//        if(start == 0){
//            listDonatur.clear()
//        }
//
//        var params = JSONObject()
//        params.put("id_sales",sessionManager.id)
//        params.put("keyword",keyword)
//        params.put("start",start)
//        params.put("count",count)
//        Log.d(">>>params",params.toString());
//
//        ApiVolley(context, params, "POST", ServerURL.getDonaturTetap,
//                object : ApiVolley.VolleyCallback {
//                    override fun onSuccess(result: String) {
//                        dialogBox?.dismissDialog()
//                        try {
//                            val response = JSONObject(result)
//                            val status = response.getJSONObject("metadata").getString("status")
//                            val message = response.getJSONObject("metadata").getString("message")
//                            if (iv.parseNullInteger(status) == 200) {
//                                val res = response.getJSONArray("response")
//                                for (i in 0 until res.length()) {
//                                    val obj: JSONObject = res.getJSONObject(i)
//                                    listDonatur.add(
//                                            DonaturModel(
//                                                    obj.getString("id")
//                                                    ,obj.getString("id_c_donatur")
//                                                    ,obj.getString("kdcus")
//                                                    ,obj.getString("nama")
//                                                    ,obj.getString("alamat")
//                                                    ,obj.getString("rt")
//                                                    ,obj.getString("rw")
//                                                    ,obj.getString("kontak")
//                                                    ,obj.getString("wa")
//                                                    ,obj.getString("kota")
//                                                    ,obj.getString("kecamatan")
//                                                    ,obj.getString("kelurahan")
//                                                    ,obj.getString("lat")
//                                                    ,obj.getString("long")
//                                                    ,obj.getInt("status_kontak")
//                                            )
//                                    )
//                                }
//                                adapter.notifyDataSetChanged()
//                            }
//                            else{
//                                Log.d(">>>>", message)
//                                if(flag == 1){
//                                    adapter.notifyDataSetChanged()
//                                }
//                            }
//                        } catch (e: JSONException) {
//                            e.printStackTrace()
//                            val clickListener = View.OnClickListener {
//                                dialogBox?.dismissDialog()
//                                start = 0
//                                count = 20
//                                keyword =""
//                                loadDonatur(1)
//                            }
//                            dialogBox?.showDialog(clickListener, "Ulangi Proses", "Terjadi kesalahan saat mengambil data")
//                        }
//                    }
//
//                    override fun onError(result: String) {
//                        val clickListener = View.OnClickListener {
//                            dialogBox?.dismissDialog()
//                            start = 0
//                            count = 20
//                            keyword =""
//                            loadDonatur(1)
//                        }
//                        dialogBox?.showDialog(clickListener, "Ulangi Proses", result)
//                    }
//                })
//    }

}