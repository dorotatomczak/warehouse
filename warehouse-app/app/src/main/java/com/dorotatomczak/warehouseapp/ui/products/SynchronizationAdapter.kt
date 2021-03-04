package com.dorotatomczak.warehouseapp.ui.products

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.LayoutRes
import com.dorotatomczak.warehouseapp.R
import com.dorotatomczak.warehouseapp.data.local.entity.Operation
import com.dorotatomczak.warehouseapp.data.model.SyncResult

class SynchronizationAdapter(
    context: Context,
    @LayoutRes private val layoutResource: Int,
    private val syncResults: ArrayList<SyncResult>
) : ArrayAdapter<SyncResult>(context, layoutResource, syncResults) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var listItem: View? = convertView

        if (listItem == null) {
            listItem = LayoutInflater.from(context).inflate(R.layout.item_request, parent, false)!!
        }

        val syncResult = syncResults[position]

        val imageViewRequestOutcome =
            listItem.findViewById(R.id.imageViewRequestOutcome) as ImageView
        val textViewRequest = listItem.findViewById(R.id.textViewRequest) as TextView
        val textViewResponse = listItem.findViewById(R.id.textViewResponse) as TextView

        if (syncResult.wasSuccessful) {
            imageViewRequestOutcome.setImageResource(R.drawable.ic_successful_request)
        } else {
            imageViewRequestOutcome.setImageResource(R.drawable.ic_failed_request)
        }

        val requestText = if (syncResult.request.operation != Operation.CHANGE_QUANTITY) {
            context.getString(
                R.string.request,
                syncResult.request.operation,
                syncResult.request.product.id,
                syncResult.request.product.name,
                syncResult.request.product.manufacturer,
                syncResult.request.product.price,
                syncResult.request.product.quantity
            )
        } else {
            context.getString(
                R.string.request_with_delta,
                syncResult.request.operation,
                syncResult.request.product.id,
                syncResult.request.product.name,
                syncResult.request.product.manufacturer,
                syncResult.request.product.price,
                syncResult.request.product.quantity,
                syncResult.request.delta
            )
        }

        textViewRequest.text = requestText
        textViewResponse.text = syncResult.responseMessage

        return listItem
    }
}
