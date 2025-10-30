package com.example.zenapp.ui.appselection

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.zenapp.R
import com.example.zenapp.databinding.ActivityAppSelectionBinding
import com.example.zenapp.ui.blocklist.AppItem
import android.graphics.drawable.Drawable
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager

class AppSelectionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAppSelectionBinding
    private lateinit var categoryAdapter: CategoryAdapter
    private val allApps = mutableListOf<AppItem>()
    private var filteredApps = mutableListOf<AppItem>()

    companion object {
        const val EXTRA_SELECTED_APPS = "selected_apps"
        const val EXTRA_RESULT_APPS = "result_apps"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAppSelectionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupBackButton()
        loadApps()
        setupRecyclerView()
        setupSearchBar()
        setupButtons()
        updateSelectedCount()
    }

    private fun setupBackButton() {
        binding.buttonBack.setOnClickListener {
            finish()
        }
    }

    private fun loadApps() {
        val pm = packageManager

        // Obtener apps instaladas
        val installedApps: List<ApplicationInfo> = pm.getInstalledApplications(PackageManager.GET_META_DATA)

        allApps.clear()

        installedApps.forEach { appInfo: ApplicationInfo ->
            val launchIntent = pm.getLaunchIntentForPackage(appInfo.packageName)
            if (launchIntent != null) {
                val appName = pm.getApplicationLabel(appInfo).toString()
                val appIcon: Drawable = pm.getApplicationIcon(appInfo)

                val appItem = AppItem(
                    name = appName,
                    packageName = appInfo.packageName,
                    category = AppCategory.OTHER,
                    isBlocked = false
                )
                appItem.iconDrawable = appIcon
                allApps.add(appItem)
            }
        }

        filteredApps.clear()
        filteredApps.addAll(allApps)
    }


    private fun setupRecyclerView() {
        categoryAdapter = CategoryAdapter(groupAppsByCategory(filteredApps)) {
            updateSelectedCount()
        }

        binding.recyclerAppsCategories.apply {
            layoutManager = LinearLayoutManager(this@AppSelectionActivity)
            adapter = categoryAdapter
        }
    }

    private fun groupAppsByCategory(apps: List<AppItem>): Map<AppCategory, List<AppItem>> {
        return apps.groupBy { it.category }
            .toSortedMap(compareBy { it.displayName })
    }

    private fun setupSearchBar() {
        binding.searchBar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                filterApps(s.toString())
            }
        })
    }

    private fun filterApps(query: String) {
        filteredApps.clear()

        if (query.isEmpty()) {
            filteredApps.addAll(allApps)
        } else {
            filteredApps.addAll(
                allApps.filter {
                    it.name.contains(query, ignoreCase = true)
                }
            )
        }

        categoryAdapter.updateCategories(groupAppsByCategory(filteredApps))
    }

    private fun setupButtons() {
        binding.buttonConfirm.setOnClickListener {
            val selectedApps = allApps.filter { it.isBlocked }
            val resultIntent = Intent().apply {
                putExtra(EXTRA_RESULT_APPS, ArrayList(selectedApps))
            }
            setResult(RESULT_OK, resultIntent)
            finish()
        }
    }

    private fun updateSelectedCount() {
        val count = allApps.count { it.isBlocked }
        binding.textSelectedCount.text = "$count apps seleccionadas"
    }
}

