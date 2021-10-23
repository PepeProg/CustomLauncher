package com.example.customlauncher

import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class CustomLauncherActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_custom_launcher)
        recyclerView = findViewById(R.id.recycler_list)
        recyclerView.apply {
            layoutManager = LinearLayoutManager(this@CustomLauncherActivity)
        }
        updateAdapter()
    }

    private inner class ItemHolder(itemView: View): RecyclerView.ViewHolder(itemView), View.OnClickListener {
        private val itemTextView = itemView as TextView
        private lateinit var resolveInfo: ResolveInfo
        init {
            itemTextView.setOnClickListener(this)
        }

        fun bind(resolveInfo: ResolveInfo) {
            this.resolveInfo = resolveInfo
            val packageManager = itemView.context.packageManager
            val appName = resolveInfo.loadLabel(packageManager)
            itemTextView.text = appName
        }

        override fun onClick(p0: View) {
            val activityInfo = resolveInfo.activityInfo
            val intent = Intent(Intent.ACTION_MAIN).apply {
                setClassName(activityInfo.applicationInfo.packageName,
                activityInfo.name)  //this function is using name of application package and name of activity,
                                    //while constructor Intent(Context, Class<?>) finding it on itself
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            val context = itemView.context
            context.startActivity(intent)
        }
    }

    private inner class ItemAdapter(val appList: List<ResolveInfo>)
        :RecyclerView.Adapter<ItemHolder>(){
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
            val holderView = LayoutInflater.from(parent.context)
                .inflate(android.R.layout.simple_list_item_1, parent, false)    //using android standard layout
            return ItemHolder(holderView)
        }

        override fun onBindViewHolder(holder: ItemHolder, position: Int) {
            holder.bind(appList[position])
        }

        override fun getItemCount(): Int {
            return appList.size
        }
    }

    private fun updateAdapter() {
        val setupIntent = Intent(Intent.ACTION_MAIN).apply {    //creating intent of all launched apps
            addCategory(Intent.CATEGORY_LAUNCHER)
        }
        val activities = packageManager.queryIntentActivities(setupIntent, 0)   //returns list of ResolveInfo, containing info about this intent
        activities.sortBy {
            it.loadLabel(packageManager).toString()
        }
        val adapter = ItemAdapter(activities)
        recyclerView.adapter = adapter
    }
}