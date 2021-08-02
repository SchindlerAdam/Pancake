package com.example.pancake.model

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.RecyclerView
import com.example.pancake.Pancake
import com.example.pancake.R
import java.text.NumberFormat
import java.util.*


private const val PRICE_TYPE_NORMAL = 3.00
private const val PRICE_TYPE_AMERICAN = 4.50
private const val PRICE_FLAVOR_GENERAL = 1.00
private const val PRICE_FLAVOR_EXTRA = 1.50


class PancakeViewModel: ViewModel() {

    /**
     * Background properties
     */
    private val _type = MutableLiveData<String>()
    val type: LiveData<String> = _type

    private val _flavor = MutableLiveData<String>()
    val flavor: LiveData<String> = _flavor

    private val _quantity = MutableLiveData<Int>()
    val quantity: LiveData<Int> = _quantity

    private var _cart : MutableList<Pancake> = mutableListOf()
    val cart: MutableList<Pancake> = _cart

    private val _totalPrice = MutableLiveData<Double>()
    val totalPrice: LiveData<String> = Transformations.map(_totalPrice) {
        NumberFormat.getCurrencyInstance(Locale.US).format(it)
    }

    private val _date = MutableLiveData<String>()
    val date: LiveData<String> = _date


    /**
     * Methods to set liveData's value
     */
    fun setType(desiredType: String) {
        _type.value = desiredType
    }

    fun setFlavor(desiredFlavor: String) {
        _flavor.value = desiredFlavor
    }

    fun setQuantity(desiredQuantity: Int) {
        _quantity.value = desiredQuantity
    }

    fun setDate(desiredDate: String) {
        _date.value = desiredDate
    }


     private fun setPrice() {
         var tempTypePrice = 0.0
         var tempFlavorPrice = 0.0
         val tempTotalPrice = mutableListOf<Double>()
         for(item in 0 until cart.size){
             when(cart[item].type) {
                 "Normal" -> tempTypePrice = PRICE_TYPE_NORMAL
                 "American" -> tempTypePrice = PRICE_TYPE_AMERICAN
             }
             when(cart[item].flavor) {
                 "Cocoa" -> tempFlavorPrice = PRICE_FLAVOR_GENERAL
                 "Jam" -> tempFlavorPrice = PRICE_FLAVOR_GENERAL
                 "Cinnamon" -> tempFlavorPrice = PRICE_FLAVOR_GENERAL
                 "Nutella" -> tempFlavorPrice = PRICE_FLAVOR_EXTRA
                 "Maple Syrup" -> tempFlavorPrice = PRICE_FLAVOR_EXTRA
                 "Cottage Cheese" -> tempFlavorPrice = PRICE_FLAVOR_EXTRA
             }
         }

         tempTotalPrice.add((tempTypePrice + tempFlavorPrice) * _quantity.value!!)

         for(item in tempTotalPrice) {
             _totalPrice.value = _totalPrice.value?.plus(item)
         }
    }

    fun addCart() {
        _cart.add(Pancake(_type.value!!, _flavor.value!!, _quantity.value!!))
        setPrice()
    }

    fun checkAllDataExist(): Boolean {
        return _type.value != null && _flavor != null && _quantity.value!! > 0
    }

    fun resetOrder() {
        _type.value = ""
        _flavor.value = ""
        _quantity.value = 0
        _totalPrice.value = 0.0
        _date.value = ""
        cart.clear()
    }

    /**
     * Adapter for recyclerView
     */

    class PancakeAdapter(private val pancakeList: MutableList<Pancake>): RecyclerView.Adapter<PancakeAdapter.MyViewHolder>() {



        class MyViewHolder(viewItem: View): RecyclerView.ViewHolder(viewItem) {
            val pancakeOrderType: TextView = viewItem.findViewById(R.id.textview_order_type)
            val pancakeOrderFlavor: TextView = viewItem.findViewById(R.id.textview_order_flavor)
            val pancakeOrderQuantity: TextView = viewItem.findViewById(R.id.textview_order_quantity)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent, false)
            return MyViewHolder(view)
        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            holder.pancakeOrderType.text = pancakeList[position].type
            holder.pancakeOrderFlavor.text = pancakeList[position].flavor
            holder.pancakeOrderQuantity.text = pancakeList[position].quantity.toString()
        }

        override fun getItemCount(): Int {
            return pancakeList.size
        }
    }


    init {
        resetOrder()
    }

}