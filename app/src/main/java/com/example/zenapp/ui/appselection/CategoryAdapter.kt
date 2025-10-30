package com.example.zenapp.ui.appselection

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.zenapp.R
import com.example.zenapp.ui.blocklist.AppItem

class CategoryAdapter(
    private var categories: Map<AppCategory, List<AppItem>>,
    private val onSelectionChanged: () -> Unit
) : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val categoryName: TextView = itemView.findViewById(R.id.text_category_name)
        val recyclerCategoryApps: RecyclerView = itemView.findViewById(R.id.recycler_category_apps)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_category, parent, false)
        return CategoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val category = categories.keys.toList()[position]
        val apps = categories[category] ?: emptyList()

        holder.categoryName.text = category.displayName

        val appAdapter = AppSelectionAdapter(apps, onSelectionChanged)
        holder.recyclerCategoryApps.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = appAdapter
        }
    }

    override fun getItemCount(): Int = categories.size

    fun updateCategories(newCategories: Map<AppCategory, List<AppItem>>) {
        categories = newCategories
        notifyDataSetChanged()
    }
}

