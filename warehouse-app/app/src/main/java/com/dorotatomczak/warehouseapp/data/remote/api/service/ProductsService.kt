package com.dorotatomczak.warehouseapp.data.remote.api.service

import com.dorotatomczak.warehouseapp.data.model.Product
import com.dorotatomczak.warehouseapp.data.model.ProductInWarehouse
import com.dorotatomczak.warehouseapp.data.model.ProductResponse
import retrofit2.http.*
import java.time.LocalDateTime

interface ProductsService {

    companion object {
        const val API_URL = "http://10.0.2.2:9090/"
    }

    @GET("products")
    suspend fun getProducts(@Query("warehouseId") warehouseId: Int): List<ProductResponse>

    @GET("productsInWarehouses")
    suspend fun getProductsInWarehouses(): List<ProductInWarehouse>

    @POST("product")
    suspend fun addProduct(@Body product: Product): ProductResponse

    @PUT("product")
    suspend fun editProduct(@Body product: Product): ProductResponse

    @FormUrlEncoded
    @PUT("product/{id}")
    suspend fun changeProductQuantity(
        @Path("id") id: Int,
        @Field("modified") modified: LocalDateTime,
        @Field("delta") delta: Int,
        @Field("warehouseId") warehouseId: Int
    ): ProductResponse

    @FormUrlEncoded
    @HTTP(method = "DELETE", path = "product/{id}", hasBody = true)
    suspend fun deleteProduct(@Path("id") id: Int, @Field("modified") modified: LocalDateTime)
}
