package com.dorotatomczak.warehouseapp.data.queue

import com.dorotatomczak.warehouseapp.data.local.dao.RequestDao
import com.dorotatomczak.warehouseapp.data.local.entity.Operation
import com.dorotatomczak.warehouseapp.data.local.entity.RequestEntity
import com.dorotatomczak.warehouseapp.data.model.Product
import javax.inject.Inject

class OfflineRequestQueue @Inject constructor(private val requestDao: RequestDao) {

    suspend fun enqueue(
        operation: Operation,
        product: Product,
        delta: Int? = null,
        warehouseId: Int? = 1
    ) {
        val requestEntity = RequestEntity(
            operation = operation,
            product = product,
            delta = delta,
            warehouseId = warehouseId
        )
        when {
            product.isOnline || (operation == Operation.ADD) || (operation == Operation.CHANGE_QUANTITY) -> enqueue(
                requestEntity
            )

            operation == Operation.DELETE -> requestDao.deleteByProductId(product.id!!)

            else -> requestDao.updateByProductId(
                product.id!!,
                product.name,
                product.manufacturer,
                product.price,
                product.quantity
            )
        }
    }

    suspend fun dequeue(request: OfflineRequest) = requestDao.deleteByRequestId(request.id!!)

    suspend fun peekAll(): List<OfflineRequest> = requestDao.getAll().map { it.toOfflineRequest() }

    suspend fun clearAll() = requestDao.deleteAll()

    private suspend fun enqueue(requestEntity: RequestEntity) = requestDao.insert(requestEntity)
}
