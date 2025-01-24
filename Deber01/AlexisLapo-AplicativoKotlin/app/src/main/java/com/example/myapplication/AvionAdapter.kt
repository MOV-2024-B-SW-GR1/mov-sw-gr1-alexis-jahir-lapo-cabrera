package com.example.myapplication

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageButton
import android.widget.TextView

class AvionAdapter(
    private val context: Context,
    private val aviones: List<Map<String, Any>>,
    private val showDeleteButton: Boolean,
    private val onItemClick: (Map<String, Any>) -> Unit,
    private val onDeleteClick: (Map<String, Any>) -> Unit
) : BaseAdapter() {

    override fun getCount(): Int = aviones.size
    override fun getItem(position: Int): Any = aviones[position]
    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.item_avion, parent, false)

        val avion = aviones[position]

        val tvNombreAvion = view.findViewById<TextView>(R.id.tvNombreAvion)
        val btnDelete = view.findViewById<ImageButton>(R.id.btnDelete)

        tvNombreAvion.text = avion["nombre"] as String

        btnDelete.visibility = if (showDeleteButton) View.VISIBLE else View.GONE

        view.setOnClickListener { onItemClick(avion) }
        btnDelete.setOnClickListener { onDeleteClick(avion) }

        return view
    }
}
