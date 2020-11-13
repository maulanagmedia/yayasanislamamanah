package co.id.gmedia.yia.ActAdmin.Adapter

import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.provider.Contacts.People
import android.provider.ContactsContract.CommonDataKinds.Phone
import android.view.*
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import co.id.gmedia.coremodul.SessionManager
import co.id.gmedia.yia.ActSalesBrosur.DetailCurrentPosActivity
import co.id.gmedia.yia.Model.DonaturModel
import co.id.gmedia.yia.R
import es.dmoral.toasty.Toasty


class DonaturAdapter(private val context: Context,
                     private val data: ArrayList<DonaturModel>?,
                     private val mAdapterCallback: DonaturAdapterCallback?) :
        RecyclerView.Adapter<DonaturAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DonaturAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_donatur, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: DonaturAdapter.ViewHolder, position: Int) {
        val item = data?.get(position)
        if(position % 2 == 0){
            holder.rlDonatur.setBackgroundColor(Color.parseColor("#F4F4F4"))
        }else {
            holder.rlDonatur.setBackgroundColor(Color.parseColor("#FFFFFF"))
        }
        holder.tvNama.text = item?.nama
        var alamat = ""
        if(item?.rt.equals("") && item?.rw.equals("")){
            alamat = item?.alamat.toString()+", RT "+item?.rt+"/"+item?.rw+", Kelurahan "+item?.kelurahan+",Kecamatan "+item?.kecamatan+","+item?.kota
        }else{
            alamat = item?.alamat.toString()+", Kelurahan "+item?.kelurahan+",Kecamatan "+item?.kecamatan+","+item?.kota
        }
        holder.tvAlamat.text = alamat
        var kontak : String = ""
        if(!item?.wa.equals("")){
            kontak = item?.kontak+" / "+item?.wa
        }else{
            kontak = item?.kontak.toString()
        }
        holder.tvKontak.text = kontak
        if(item?.status_kontak == 0){
            holder.tvStatus.setTextColor(ContextCompat.getColor(context, R.color.colorRed2))
            holder.tvStatus.text = "Kontak belum disimpan"
        }else{
            holder.tvStatus.setTextColor(ContextCompat.getColor(context, R.color.colorGreen1))
            holder.tvStatus.text = "Kontak sudah disimpan"
        }

        holder.imgMenu.setOnClickListener {
            val builder = AlertDialog.Builder(context)
            val inflater = (context as Activity).getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val viewDialog = inflater.inflate(R.layout.dialog_choser_admin, null)
            builder.setView(viewDialog)
            //builder.setCancelable(true);

            //builder.setCancelable(true);
            val btn_save_kontak = viewDialog.findViewById<Button>(R.id.btn_save_kontak)
            val btn_map = viewDialog.findViewById<Button>(R.id.btn_map)
            val ivClose = viewDialog.findViewById<ImageView>(R.id.iv_close)

            val alert = builder.create()
            alert.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            alert.window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)

            alert.window!!.setGravity(Gravity.BOTTOM)

            btn_save_kontak.setOnClickListener {
                if (alert != null) {
                    try {
                        alert.dismiss()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                if(!item?.kontak.equals("")){
                    alert.dismiss()
                    mAdapterCallback?.onSaveContact(item!!)
//                    item?.kontak?.let { it1 -> addContact(item?.nama, it1) }
//                    var sessionManager = SessionManager(context)
//                    sessionManager.saveIdDonatur(item?.id)
//                    val contactIntent = Intent(ContactsContract.Intents.Insert.ACTION)
//                    contactIntent.type = ContactsContract.RawContacts.CONTENT_TYPE
//                    contactIntent
//                            .putExtra(ContactsContract.Intents.Insert.NAME, item?.nama)
//                            .putExtra(ContactsContract.Intents.Insert.PHONE, item?.kontak)
//                            .putExtra(
//                                    ContactsContract.Intents.Insert.PHONE_TYPE,
//                                    ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE
//                            )
//                    if(!item?.wa.equals("")){
//                        contactIntent.putExtra(ContactsContract.Intents.Insert.PHONE, item?.wa)
//                                .putExtra(
//                                        ContactsContract.Intents.Insert.PHONE_TYPE,
//                                        ContactsContract.CommonDataKinds.Phone.TYPE_CUSTOM
//                                )
//                    }

//                    (context as Activity).startActivityForResult(contactIntent, 1001)
                }else{
                    Toasty.error(context, "Kontak masih kosong", Toast.LENGTH_SHORT).show()
                }
            }

            btn_map.setOnClickListener {
                if (alert != null) {
                    try {
                        alert.dismiss()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                if(!item?.latitude.equals("") && !item?.lognitude.equals("")){
                    val intent = Intent(context, DetailCurrentPosActivity::class.java)
                    intent.putExtra("nama", item?.nama)
                    intent.putExtra("alamat", item?.alamat)
                    intent.putExtra("lat", item?.latitude)
                    intent.putExtra("long", item?.lognitude)
                    context.startActivity(intent)
                }
                else{
                    Toasty.error(context, "Lokasi tidak ditemukan", Toast.LENGTH_SHORT).show()
                }
            }

            ivClose.setOnClickListener {
                if (alert != null) {
                    try {
                        alert.dismiss()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }

            try {
                alert.show()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

    }

    override fun getItemCount(): Int {
        return data?.size ?: 0
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val tvNama = view.findViewById<TextView>(R.id.txt_nama)
        val tvAlamat = view.findViewById<TextView>(R.id.txt_alamat)
        val tvKontak = view.findViewById<TextView>(R.id.txt_kontak)
        val tvStatus = view.findViewById<TextView>(R.id.txt_status)
        val rlDonatur = view.findViewById<RelativeLayout>(R.id.rl_donatur)
        val imgMenu =view.findViewById<ImageView>(R.id.img_plus)
    }

    interface DonaturAdapterCallback {
        fun onSaveContact(data : DonaturModel)
    }

}