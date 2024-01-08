package com.example.myapplication

import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings.Global
import android.util.Log
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
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.URL
import kotlin.math.log

class CreateCarActivity : AppCompatActivity() {
    lateinit var carName: EditText
    lateinit var brandName: EditText
    lateinit var modelName: EditText
    lateinit var year: EditText
    lateinit var btnSubmit: AppCompatButton
    lateinit var btnBack: AppCompatButton
    lateinit var rbSale: RadioButton
    lateinit var rbNotForSale: RadioButton
    lateinit var rgStatusSale: RadioGroup


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_car)

        carName = findViewById(R.id.create_car_carNameEt)
        brandName = findViewById(R.id.create_car_carBrandEt)
        modelName = findViewById(R.id.create_car_carModelEt)
        year = findViewById(R.id.create_car_carYearEt)
        btnSubmit = findViewById(R.id.create_car_btnSubmit)
        btnBack = findViewById(R.id.create_car_btnBack)
        rbSale = findViewById(R.id.create_car_rbSale)

        btnBack.setOnClickListener { finish() }
        btnSubmit.setOnClickListener {
            if (!validate()) {
                AlertDialog.Builder(this)
                    .setTitle("Failed!")
                    .setMessage("All field must be filled!")
                    .setCancelable(false)
                    .setPositiveButton(
                        "OK",
                        DialogInterface.OnClickListener { dialog, which -> dialog.dismiss() })
                    .show()
                return@setOnClickListener
            }

            createCar()
        }
    }

    fun createCar() {
        GlobalScope.launch(Dispatchers.IO) {
            val url = URL("https://6597231a668d248edf22a0d3.mockapi.io/Cars")
            val conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "POST"
            conn.doOutput = true
            conn.setRequestProperty("Content-Type", "application/json")
            conn.setRequestProperty("Accept", "application/json")

            val status: Boolean = rbSale.isChecked

            val json = JSONObject().apply {
                put("name", carName.text.toString())
                put("model", modelName.text.toString())
                put("brand", brandName.text.toString())
                put("year", year.text.toString().toIntOrNull())
                put("forSale", status)
            }

            Log.d("data_send", json.toString())

            val outputStream = DataOutputStream(conn.outputStream)
            outputStream.write(json.toString().toByteArray())
            outputStream.flush()
            outputStream.close()

            val statusCode = conn.responseCode

            Log.d("cek_data", statusCode.toString())

            runOnUiThread {
                if (statusCode in 200..209) {
                    AlertDialog.Builder(this@CreateCarActivity)
                        .setCancelable(false)
                        .setTitle("Success")
                        .setMessage("Car created successfully!")
                        .setNeutralButton(
                            "OK",
                            DialogInterface.OnClickListener { dialog, which -> finish() })
                        .show()
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