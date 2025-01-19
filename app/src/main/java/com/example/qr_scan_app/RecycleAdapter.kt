package com.example.qr_scan_app

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class RecycleAdapter(private val userList: List<Map<String, Any>>?) : RecyclerView.Adapter<RecycleAdapter.UserViewHolder>() {
    class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvName: TextView = itemView.findViewById(R.id.idParce)
        val tvAge: TextView = itemView.findViewById(R.id.status)
        val row:LinearLayout = itemView.findViewById(R.id.rzad)
        val view:View = itemView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.table_components, parent, false)
        return UserViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = userList?.get(position)
        holder.tvName.text = user?.getValue("Id_Paczki").toString()
        holder.tvAge.text = user?.getValue("Status").toString()
        if(user?.getValue("czyZniszczona").toString()=="true")
            holder.row.setBackgroundResource(R.drawable.rounded_bad)
    }

    override fun getItemCount(): Int {
        return userList?.size ?: 0
    }
}