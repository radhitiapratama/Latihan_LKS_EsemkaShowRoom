package com.example.myapplication.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.DetailCarActivity
import com.example.myapplication.R
import org.json.JSONArray

class CarAdapter(val cars: JSONArray) : RecyclerView.Adapter<CarAdapter.CarViewHolder>() {

    class CarViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameCar = itemView.findViewById<TextView>(R.id.main_nameCar)
        val brandCar = itemView.findViewById<TextView>(R.id.main_brandCar)
        val modelCar = itemView.findViewById<TextView>(R.id.main_modelCar)
        val yearCar = itemView.findViewById<TextView>(R.id.main_yearCar)
        val statusSaleCar = itemView.findViewById<AppCompatButton>(R.id.main_statusSaleCar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CarViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.card_car_layout, parent, false)
        return CarViewHolder(view)
    }

    override fun getItemCount(): Int {
        return cars.length()
    }

    override fun onBindViewHolder(holder: CarViewHolder, position: Int) {
        val car = cars.getJSONObject(position)

        holder.nameCar.text = car.getString("name")
        holder.brandCar.text = car.getString("brand")
        holder.modelCar.text = car.getString("model")
        holder.yearCar.text = car.getString("year")

        if (car.getBoolean("forSale")) {
            holder.statusSaleCar.setBackgroundResource(R.drawable.bg_for_sale)
            holder.statusSaleCar.text = "For Sale"
        } else {
            holder.statusSaleCar.setBackgroundResource(R.drawable.bg_not_for_sale)
            holder.statusSaleCar.text = "Not For Sale"
        }

        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, DetailCarActivity::class.java).apply {
                putExtra("id", car.getInt("id"))
            }

            holder.itemView.context.startActivity(intent)
        }
    }

}