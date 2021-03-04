package com.dorotatomczak.warehouseapp.ui.products

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dorotatomczak.warehouseapp.R
import com.dorotatomczak.warehouseapp.data.model.Product
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_product.view.*

class ProductsAdapter(
    private val productClickCallback: ((Product) -> Unit),
    private val quantityChangeCallback: ((Product, Boolean) -> Unit)
) : RecyclerView.Adapter<ProductsAdapter.ProductsViewHolder>() {

    var products = ArrayList<Product>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductsViewHolder =
        ProductsViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_product, parent, false)
        )

    override fun onBindViewHolder(holder: ProductsViewHolder, position: Int) {
        val product = products[position]
        holder.bindProduct(product)
    }

    override fun getItemCount(): Int = products.size

    inner class ProductsViewHolder(override val containerView: View) :
        RecyclerView.ViewHolder(containerView), LayoutContainer, View.OnClickListener {

        init {
            containerView.setOnClickListener(this)
        }

        fun bindProduct(product: Product) {
            containerView.textViewName.text = product.name
            containerView.textViewManufacturer.text = product.manufacturer
            containerView.textViewPrice.text =
                containerView.context.getString(R.string.price, product.price)
            containerView.textViewQuantity.text = product.quantity.toString()

            containerView.buttonAddItems.setOnClickListener {
                quantityChangeCallback(products[adapterPosition], true)
            }

            containerView.buttonRemoveItems.setOnClickListener {
                quantityChangeCallback(products[adapterPosition], false)
            }
        }

        override fun onClick(view: View?) = productClickCallback(products[adapterPosition])
    }
}
