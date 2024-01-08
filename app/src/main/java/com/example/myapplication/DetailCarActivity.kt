package com.example.myapplication

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

class DetailCarActivity : AppCompatActivity() {
    lateinit var carName: TextView
    lateinit var carBrand: TextView
    lateinit var carModel: TextView
    lateinit var carYear: TextView
    lateinit var carStatus: AppCompatButton
    lateinit var btnEdit: AppCompatButton
    lateinit var btnDelete: AppCompatButton
    lateinit var btnBack: AppCompatButton


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_car)

        carName = findViewById(R.id.detail_car_carName)
        carBrand = findViewById(R.id.detail_car_carBrand)
        carModel = findViewById(R.id.detail_car_carModel)
        carYear = findViewById(R.id.detail_car_carYear)
        carStatus = findViewById(R.id.detail_car_statusSaleCar)
        btnEdit = findViewById(R.id.detail_car_btnEdit)
        btnDelete = findViewById(R.id.detail_car_btnDelete)
        btnBack = findViewById(R.id.detail_car_btnBack)

        getData()

        btnBack.setOnClickListener { finish() }
        btnEdit.setOnClickListener {
            val intent = Intent(this, EditCarActivity::class.java).apply {
                putExtra("id", intent.getIntExtra("id", 1))
            }
            startActivity(intent)
        }

        btnDelete.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Warning")
                .setMessage("Sure delete data?")
                .setCancelable(false)
                .setPositiveButton("Yes", DialogInterface.OnClickListener { dialog, which ->
                    delete()
                })
                .setNegativeButton(
                    "No",
                    DialogInterface.OnClickListener { dialog, which -> dialog.dismiss() })
                .show()
        }
    }

    fun delete() {
        GlobalScope.launch(Dispatchers.IO) {
            val id = intent.getIntExtra("id", 1)
            val url = URL("https://6597231a668d248edf22a0d3.mockapi.io/Cars/$id")
            val conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "DELETE"
            conn.doOutput = true

            val code = conn.responseCode

            runOnUiThread {
                if (code in 200..299) {
                    AlertDialog.Builder(this@DetailCarActivity)
                        .setCancelable(false)
                        .setTitle("Success")
                        .setMessage("Car deleted successfully")
                        .setPositiveButton("Ok", DialogInterface.OnClickListener { dialog, which ->
                            finish()
                        })
                        .show()
                }
            }
        }
    }

    fun getData() {
        GlobalScope.launch(Dispatchers.IO) {
            val id = intent.getIntExtra("id", 1)
            val url = URL("https://6597231a668d248edf22a0d3.mockapi.io/Cars/$id")
            val conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "GET"

            val json = JSONObject(conn.inputStream.bufferedReader().readText())

            runOnUiThread {
                carName.text = json.getString("name")
                carBrand.text = json.getString("brand")
                carModel.text = json.getString("model")
                carYear.text = json.getString("year")

                if (json.getBoolean("forSale")) {
                    carStatus.setBackgroundResource(R.drawable.bg_for_sale)
                    carStatus.text = "For Sale"
                } else {
                    carStatus.setBackgroundResource(R.drawable.bg_not_for_sale)
                    carStatus.text = "Not For Sale"
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        getData()
    }
}