package com.example.myapplication

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView

class ParteAdapter(
    private val context: Context,
    private val partes: List<Map<String, Any>>,
    private val onItemClick: (Map<String, Any>) -> Unit
) : BaseAdapter() {

    private class ViewHolder(
        val nombre: TextView,
        val detalle: TextView
    )

    override fun getCount(): Int {
        return partes.size
    }

    override fun getItem(position: Int): Any {
        return partes[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View
        val holder: ViewHolder

        if (convertView == null) {
            view = LayoutInflater.from(context).inflate(android.R.layout.simple_list_item_2, parent, false)
            holder = ViewHolder(
                view.findViewById(android.R.id.text1),
                view.findViewById(android.R.id.text2)
            )
            view.tag = holder
        } else {
            view = convertView
            holder = view.tag as ViewHolder
        }

        val parte = partes[position]
        holder.nombre.text = parte["nombre"] as String
        holder.detalle.text = "Precio: ${parte["precio"]}, Horas: ${parte["horas"]}"

        view.setOnClickListener {
            onItemClick(parte)
        }

        return view
    }
}