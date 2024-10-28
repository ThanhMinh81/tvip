package com.example.iptvplayer.home

import android.content.Intent
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.iptvplayer.ChannelFilm
import com.example.iptvplayer.R
import com.example.iptvplayer.databinding.ActivityMainBinding
import com.example.iptvplayer.extensions.lightNavigationBar
import com.example.iptvplayer.extensions.lightStatusBar
import com.example.iptvplayer.extensions.setStatusNaviColor
import com.example.iptvplayer.home.adapter.CategoryAdapter
import com.example.iptvplayer.home.adapter.HomeAdapter
import com.example.iptvplayer.home.common.ApiHolder
import com.example.iptvplayer.home.common.DataHolder
import com.example.iptvplayer.model.Category
import com.example.iptvplayer.model.ItemChannel
import com.example.matheasyapp.base.BaseActivity
import com.google.android.material.navigation.NavigationView
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException

class MainActivity : BaseActivity<ActivityMainBinding>(ActivityMainBinding::inflate),
    NavigationView.OnNavigationItemSelectedListener {

    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var adapter: HomeAdapter
    private lateinit var adapterCategory: CategoryAdapter
    private lateinit var itemChannelList: ArrayList<ItemChannel>
    private lateinit var categoryList: ArrayList<Category>

    override fun viewCreated() {
        setStatusNaviColor()
        lightStatusBar()
        lightNavigationBar()

        setUpView()

        binding.apply {

            toggle = ActionBarDrawerToggle(
                this@MainActivity, drawerLayout,
                R.string.app_name,
                R.string.app_name
            )

            drawerLayout.addDrawerListener(toggle)
            toggle.syncState()
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            navigationView.setNavigationItemSelectedListener(this@MainActivity)

        }

        fetchM3UFromUrl("https://iptv-org.github.io/iptv/categories/animation.m3u") { value ->
            itemChannelList.clear()
            itemChannelList.addAll(value)
            adapter.setListBackUp(value)
            adapter.filterCategory()
            initTabbar(value)
        }

        onClick()

    }

    private fun setUpView() {
        itemChannelList = ArrayList()
        categoryList = ArrayList()
        adapter = HomeAdapter(itemChannelList, this@MainActivity) { item ->
            getListByCategory(item.category) { value ->
                val intent = Intent(this@MainActivity, PlayVideoActivity::class.java)
                intent.putExtra("object", item)
                DataHolder.itemList = value
                startActivity(intent)
            }
        }

        binding.rcvHome.layoutManager = LinearLayoutManager(this@MainActivity)
        binding.rcvHome.adapter = adapter

        adapterCategory = CategoryAdapter(categoryList, this@MainActivity) { category, id ->
            adapterCategory.updateItemChecked(id)
            adapterCategory.notifyDataSetChanged()
            if (id != 0) adapter.filterCategory(category) else adapter.filterCategory()
        }

        binding.rcvCategory.adapter = adapterCategory

        binding.rcvCategory.setLayoutManager(
            LinearLayoutManager(
                this@MainActivity,
                LinearLayoutManager.HORIZONTAL,
                false
            )
        )

    }

    private fun initTabbar(value: ArrayList<ItemChannel>) {
        val list: ArrayList<String> = arrayListOf()
        var id: Int = 1
        // init value all category
        val category = Category(0, resources.getString(R.string.category_all))
        categoryList.add(category)
        for (item in value) {
            if (!list.contains(item.category)) {
                list.add(item.category)
            }
        }

        for (item in list) {
            val category = Category(id, item)
            id += 1
            categoryList.add(category)
            adapterCategory.notifyDataSetChanged()
        }

    }

    private fun onClick() {
        binding.apply {
            icSetting.setOnClickListener {
                val intent : Intent = Intent(this@MainActivity , ChannelFilm::class.java)
                startActivity(intent)
            }
            btMenu.setOnClickListener {
                if (binding.drawerLayout.isDrawerOpen(androidx.core.view.GravityCompat.START)) {
                    drawerLayout.closeDrawer(androidx.core.view.GravityCompat.START)
                } else {
                    drawerLayout.openDrawer(androidx.core.view.GravityCompat.START)
                }
            }
            icSearch.setOnClickListener {
                val intent = Intent(this@MainActivity, SearchActivity::class.java)
                startActivity(intent)
            }
        }
    }

    private fun fetchM3UFromUrl(url: String, callback: (ArrayList<ItemChannel>) -> Unit) {

        val client = ApiHolder.HttpClient.instance
        val request = Request.Builder()
            .url(url)
            .build()

        client.newCall(request).enqueue(object : okhttp3.Callback {
            override fun onFailure(call: okhttp3.Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                response.body?.use { responseBody -> // Sử dụng use để tự động đóng responseBody
                    val bodyString = responseBody.string() // Đọc body string một lần
                    val playlist = parsePlaylist(bodyString)
                    // Nếu cần cập nhật UI hoặc trả về kết quả, hãy sử dụng runOnUiThread
                    runOnUiThread { callback(playlist) }
                }
            }
        })
    }


    private fun parsePlaylist(playlist: String?): ArrayList<ItemChannel> {
        val itemChannels = arrayListOf<ItemChannel>()
        playlist?.let {
            val lines = it.lines()
            var channelName = ""
            var logoUrl = ""
            var streamUrl = ""
            var group = ""
            var id: Int = 0
//            val itemGroups: ArrayList<String> = arrayListOf()
            for (line in lines) {
                if (line.startsWith("#EXTINF:")) {
                    channelName = line.substringAfter("tvg-id=\"").substringBefore("\"")
                    logoUrl = line.substringAfter("tvg-logo=\"").substringBefore("\"")
                    group = line.substringAfter("group-title=\"").substringBefore("\"")
                } else if (line.startsWith("http")) {

                    streamUrl = line

                    val itemChannel = ItemChannel(
                        id = id,
                        channelName = channelName,
                        streamUrl = streamUrl,
                        logoUrl = logoUrl,
                        category = group,
                        selected = 0
                    )
                    id += 1

                    itemChannels.add(itemChannel)
                }
            }
        }
        return itemChannels
    }


    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
//            R.id.nav_home -> {
//            }
//
//            R.id.nav_settings -> {
//            }
//
//            R.id.nav_about -> {
//            }
        }
        binding.drawerLayout.closeDrawers()
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }


    fun getListByCategory(value: String, callback: (ArrayList<ItemChannel>) -> Unit) {
        val list: ArrayList<ItemChannel> = ArrayList()

        itemChannelList.forEach { item ->
            if (item.category.equals(value)) {
                list.add(item)
            }
        }
        // Gọi callback với danh sách đã lọc
        callback(list)
    }


//    fun getListByCategory(value : String) : ArrayList<ItemChannel>{
//        val list : ArrayList<ItemChannel> = ArrayList()
//        itemChannelList.forEach { it -> if(it.category.equals(value)) {
//            list.add(it)
//        }
//        }
//
//        return list
//    }


}