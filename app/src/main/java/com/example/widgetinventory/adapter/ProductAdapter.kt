package com.example.widgetinventory.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.widgetinventory.databinding.ItemProductBinding
import com.example.widgetinventory.model.Product
import java.text.NumberFormat
import java.util.Locale

class ProductAdapter(
    private var products: List<Product>,
    private val onItemClick: (Product) -> Unit
) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    inner class ProductViewHolder(private val binding: ItemProductBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(product: Product) {
            binding.tvProductName.text = product.name
            binding.tvProductId.text = "ID: ${product.id}"
            
            val formattedPrice = formatPrice(product.price)
            binding.tvProductPrice.text = "$$formattedPrice"

            binding.root.setOnClickListener {
                onItemClick(product)
            }
        }

        private fun formatPrice(price: Double): String {
            val format = NumberFormat.getInstance(Locale("es", "CO"))
            format.minimumFractionDigits = 2
            format.maximumFractionDigits = 2
            return format.format(price)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding = ItemProductBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.bind(products[position])
    }

    override fun getItemCount(): Int = products.size

    fun updateProducts(newProducts: List<Product>) {
        products = newProducts
        notifyDataSetChanged()
    }
}
