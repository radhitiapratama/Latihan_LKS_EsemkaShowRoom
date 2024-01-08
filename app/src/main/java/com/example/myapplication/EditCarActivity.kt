package com.example.myapplication

import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.io.DataOutputStream
import java.net.HttpURLConnection
import java.net.URL

class EditCarActivity : AppCompatActivity() {
    lateinit var carName: EditText
    lateinit var brandName: EditText
    lateinit var modelName: EditText
    lateinit var year: EditText
    lateinit var btnUpdate: AppCompatButton
    lateinit var btnBack: AppCompatButton
    lateinit var rbSale: RadioButton
    lateinit var rbNotForSale: RadioButton


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_car)

        carName = findViewById(R.id.edit_car_carNameEt)
        brandName = findViewById(R.id.edit_car_carBrandEt)
        modelName = findViewById(R.id.edit_car_carModelEt)
        year = findViewById(R.id.edit_car_carYearEt)
        btnUpdate = findViewById(R.id.edit_car_btnSubmit)
        btnBack = findViewById(R.id.edit_car_btnBack)
        rbSale = findViewById(R.id.edit_car_rbforSale)
        rbNotForSale = findViewById(R.id.edit_car_rbNotForSale)

        getData()

        btnBack.setOnClickListener { finish() }

        btnUpdate.setOnClickListener {
            if (!validate()) {
                AlertDialog.Builder(this)
                    .setCancelable(false)
                    .setTitle("Failed")
                    .setMessage("All field must be filled")
                    .setPositiveButton(
                        "OK",
                        DialogInterface.OnClickListener { dialog, which -> dialog.dismiss() })
                    .show()

                return@setOnClickListener
            }

            updateData()
        }
    }

    fun getData() {
        GlobalScope.launch(Dispatchers.IO) {
            val id = intent.getIntExtra("id", 1)
            val url = URL("https://6597231a668d248edf22a0d3.mockapi.io/Cars/$id")
            val conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "GET"

            val result = conn.inputStream.bufferedReader().readText()
            val json = JSONObject(result)
            val responseCode = conn.responseCode

            runOnUiThread {
                if (responseCode in 200..299) {
                    carName.setText(json.getString("name"))
                    brandName.setText(json.getString("brand"))
                    modelName.setText(json.getString("model"))
                    year.setText(json.getString("year"))

                    if (json.getBoolean("forSale")) {
                        rbSale.isChecked = true
                    } else {
                        rbNotForSale.isChecked = true
                    }
                }
            }
        }
    }

    fun updateData() {
        GlobalScope.launch(Dispatchers.IO) {
            val id = intent.getIntExtra("id", 1)
            val url = URL("https://6597231a668d248edf22a0d3.mockapi.io/Cars/$id")
            val conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "PUT"
            conn.setRequestProperty("Content-Type", "application/json")
            conn.setRequestProperty("Accept", "application/json")

            var status: Boolean
            if (rbSale.isChecked) {
                status = true
            } else {
                status = false
            }

            val json = JSONObject().apply {
                put("name", carName.text)
                put("model", modelName.text)
                put("brand", brandName.text)
                put("year", year.text)
                put("forSale", status)
            }

            val outpuStream = DataOutputStream(conn.outputStream)
            outpuStream.write(json.toString().toByteArray())
            outpuStream.close()

            val responseCode = conn.responseCode
            runOnUiThread {
                if (responseCode in 200..299) {
                    AlertDialog.Builder(this@EditCarActivity)
                        .setCancelable(false)
                        .setTitle("Success")
                        .setMessage("Car updated successfully")
                        .setPositiveButton("OK", DialogInterface.OnClickListener { dialog, which ->
                            finish()
                        }).show()
                } else {
                    AlertDialog.Builder(this@EditCarActivity)
                        .setCancelable(false)
                        .setTitle("Failed")
                        .setMessage("Failed to update car")
                        .setPositiveButton("OK", DialogInterface.OnClickListener { dialog, which ->
                            dialog.dismiss()
                        }).show()
                }
            }
        }
    }

    fun validate(): Boolean {
        return !(carName.text.toString().isNullOrEmpty() || brandName.text.toString()
            .isNullOrEmpty() || modelName.text.toString()
            .isNullOrEmpty() || year.text.toString().isNullOrEmpty())
    }
}