package com.dorotatomczak.warehouseapp.data.repository

import com.dorotatomczak.warehouseapp.data.local.dao.ProductDao
import com.dorotatomczak.warehouseapp.data.local.dao.WarehouseDao
import com.dorotatomczak.warehouseapp.data.local.entity.Operation
import com.dorotatomczak.warehouseapp.data.local.entity.ProductEntity
import com.dorotatomczak.warehouseapp.data.model.Product
import com.dorotatomczak.warehouseapp.data.queue.OfflineRequestQueue
import com.dorotatomczak.warehouseapp.data.remote.api.ApiCaller.safeApiCall
import com.dorotatomczak.warehouseapp.data.remote.api.Result
import com.dorotatomczak.warehouseapp.data.remote.api.service.ProductsService
import kotlinx.coroutines.Dispatchers
import java.time.LocalDateTime
import javax.inject.Inject


class ProductsRepository @Inject constructor(
    private val productsService: ProductsService,
    private val productDao: ProductDao,
    private val warehouseDao: WarehouseDao,
    private val queue: OfflineRequestQueue
) {

    suspend fun getProducts(warehouseId: Int, sync: Boolean = false): Result<List<Product>> {
        return try {
            if (sync) {
                safeApiCall(Dispatchers.IO) {
                    val products = productsService.getProducts(warehouseId).map { it.toProduct() }
                    val productsInWarehouses = productsService.getProductsInWarehouses()
                    productDao.insertProductsWithWarehouse(products, productsInWarehouses)
                    products
                }
            } else {
                Result.Success(productDao.getAllProductsFromWarehouse(warehouseId))
            }
        } catch (e: Exception) {
            Result.GenericError(e)
        }
    }

    suspend fun addProduct(product: Product, sync: Boolean = false): Result<Product> {
        return try {
            if (sync) {
                safeApiCall(Dispatchers.IO) {
                    productsService.addProduct(product).toProduct()
                }
            } else {
                product.modified = LocalDateTime.now()
                val warehouses = warehouseDao.getAll()
                val addedProduct = productDao.insertProductWithWarehouses(product, warehouses)
                queue.enqueue(Operation.ADD, addedProduct)
                Result.Success(addedProduct)
            }
        } catch (e: Exception) {
            Result.GenericError(e)
        }
    }

    suspend fun editProduct(product: Product, sync: Boolean = false): Result<Product> {
        return try {
            if (sync) {
                safeApiCall(Dispatchers.IO) {
                    productsService.editProduct(product).toProduct()
                }
            } else {
                product.modified = LocalDateTime.now()
                productDao.update(ProductEntity.from(product))
                queue.enqueue(Operation.UPDATE, product)
                Result.Success(productDao.findById(product.id!!).toProduct(product.quantity))
            }
        } catch (e: Exception) {
            Result.GenericError(e)
        }
    }

    suspend fun changeProductQuantity(
        product: Product,
        delta: Int,
        warehouseId: Int,
        sync: Boolean = false
    ): Result<Product> {
        return try {
            if (sync) {
                safeApiCall(Dispatchers.IO) {
                    productsService.changeProductQuantity(
                        product.id!!,
                        LocalDateTime.now(),
                        delta,
                        warehouseId
                    ).toProduct()
                }
            } else {
                product.quantity += delta
                queue.enqueue(Operation.CHANGE_QUANTITY, product, delta)
                Result.Success(productDao.changeProductQuantity(product, warehouseId))
            }
        } catch (e: Exception) {
            Result.GenericError(e)
        }
    }

    suspend fun deleteProduct(product: Product, sync: Boolean = false): Result<Unit> {
        return try {
            if (sync) {
                safeApiCall(Dispatchers.IO) {
                    productsService.deleteProduct(product.id!!, product.modified!!)
                }
            } else {
                product.modified = LocalDateTime.now()
                queue.enqueue(Operation.DELETE, product)
                Result.Success(productDao.delete(ProductEntity.from(product)))
            }
        } catch (e: Exception) {
            Result.GenericError(e)
        }
    }
}
