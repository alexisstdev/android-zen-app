package com.example.zenapp.ui.blocklist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.zenapp.R

class AppAdapter(private val apps: List<AppItem>) : RecyclerView.Adapter<AppAdapter.AppViewHolder>() {

    class AppViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val iconApp: ImageView = itemView.findViewById(R.id.icon_app)
        val textAppName: TextView = itemView.findViewById(R.id.text_app_name)
        val checkboxApp: CheckBox = itemView.findViewById(R.id.checkbox_app)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_app, parent, false)
        return AppViewHolder(view)
    }

    override fun onBindViewHolder(holder: AppViewHolder, position: Int) {
        val app = apps[position]
        holder.textAppName.text = app.name

        if (app.iconDrawable != null) {
            holder.iconApp.setImageDrawable(app.iconDrawable)
        } else {
            holder.iconApp.setImageResource(R.mipmap.ic_launcher)
        }

        holder.checkboxApp.isChecked = app.isBlocked

        holder.checkboxApp.setOnCheckedChangeListener { _, isChecked ->
            app.isBlocked = isChecked
        }

        holder.itemView.setOnClickListener {
            holder.checkboxApp.isChecked = !holder.checkboxApp.isChecked
        }
    }

    override fun getItemCount(): Int = apps.size
}

