package com.example.myapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import androidx.appcompat.widget.AppCompatButton
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.adapters.CarAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONArray
import java.io.FileNotFoundException
import java.net.HttpURLConnection
import java.net.URL

class MainActivity : AppCompatActivity() {
    private var carRecycler: RecyclerView? = null
    lateinit var btnSearch: AppCompatButton
    lateinit var etSearch: EditText
    lateinit var btnCreate: AppCompatButton
    var isSearch: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        carRecycler = findViewById(R.id.mainRecycler)
        btnSearch = findViewById(R.id.main_btnSearch)
        etSearch = findViewById(R.id.main_searchEt)
        btnCreate = findViewById(R.id.main_createBtn)

        btnCreate.setOnClickListener {
            val intent = Intent(this, CreateCarActivity::class.java)
            startActivity(intent)
        }

        btnSearch.setOnClickListener {
            if (!etSearch.text.toString().isNullOrEmpty()) {
                isSearch = true
            } else {
                isSearch = false
            }
            getData()
        }

        getData()
    }

    fun getData() {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val url: URL
                if (isSearch) {
                    url =
                        URL("https://6597231a668d248edf22a0d3.mockapi.io/Cars?search=${etSearch.text}")
                } else {
                    url = URL("https://6597231a668d248edf22a0d3.mockapi.io/Cars")
                }

                val conn = url.openConnection() as HttpURLConnection
                conn.requestMethod = "GET"

                val result = conn.inputStream.bufferedReader().readText()

                val jsons = JSONArray(result)
                Log.d("cek_data", jsons.toString())

                runOnUiThread {
                    carRecycler?.adapter = CarAdapter(jsons)
                    carRecycler?.layoutManager = LinearLayoutManager(this@MainActivity)
                }
            } catch (ex: FileNotFoundException) {
                val data = JSONArray()
                runOnUiThread {
                    carRecycler?.adapter = CarAdapter(data)
                    carRecycler?.layoutManager = LinearLayoutManager(this@MainActivity)
                }
            }

        }
    }

    override fun onResume() {
        super.onResume()
        getData()
    }
}
