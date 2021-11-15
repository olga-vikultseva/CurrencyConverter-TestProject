package com.example.currencyconverter.ui.currencypicker

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.currencyconverter.R
import com.example.currencyconverter.ui.currencypicker.model.CurrencyPickerItem

class CurrencyAdapter(
    private val itemClickListener: (String) -> Unit
) : RecyclerView.Adapter<CurrencyAdapter.CurrencyViewHolder>() {

    var currencyList: List<CurrencyPickerItem> = emptyList()
        //In general it's better to use DiffUtil, but in this particular case it's fine
        //to use just NotifyDataSetChanged. We don't have many items, they don't shuffle in runtime.
        @SuppressLint("NotifyDataSetChanged")
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CurrencyViewHolder =
        CurrencyViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.picker_item_currency, parent, false)
        )

    override fun onBindViewHolder(holder: CurrencyViewHolder, position: Int) {
        holder.bind(currencyList[position])
    }

    override fun getItemCount(): Int = currencyList.size

    inner class CurrencyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val currencyName = itemView.findViewById<TextView>(R.id.currency_name)
        private val currencyCode = itemView.findViewById<TextView>(R.id.currency_code)
        private val isSelected = itemView.findViewById<ImageView>(R.id.is_selected)

        init {
            itemView.setOnClickListener {
                itemClickListener(currencyList[adapterPosition].currency.code)
            }
        }

        fun bind(item: CurrencyPickerItem) {
            currencyName.text = item.currency.name
            currencyCode.text = item.currency.code
            isSelected.isVisible = item.isSelected
        }
    }
}