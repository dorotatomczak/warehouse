package com.dorotatomczak.oauth.warehousebackend.service

import com.dorotatomczak.oauth.warehousebackend.dto.ProductDto
import com.dorotatomczak.oauth.warehousebackend.dto.ProductInWarehouseDto
import com.dorotatomczak.oauth.warehousebackend.repository.model.ProductInWarehouseEntity
import com.dorotatomczak.oauth.warehousebackend.repository.repository.ProductInWarehouseRepository
import com.dorotatomczak.oauth.warehousebackend.repository.repository.ProductRepository
import com.dorotatomczak.oauth.warehousebackend.repository.repository.WarehouseRepository
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import java.time.LocalDateTime


@Service
class ProductService(
    private val productRepository: ProductRepository,
    private val productInWarehouseRepository: ProductInWarehouseRepository,
    private val warehouseRepository: WarehouseRepository
) {

    fun getAllProducts(warehouseId: Int): List<ProductDto> =
        productInWarehouseRepository.findByWarehouseId(warehouseId).map { ProductDto.from(it) }

    fun getAllProductsInWarehouses(): Iterable<ProductInWarehouseDto> =
        productInWarehouseRepository.findAll().map { ProductInWarehouseDto.from(it) }

    fun addProduct(product: ProductDto): ProductDto {
        val productEntity = product.toProductEntity()
        val warehouses = warehouseRepository.findAll()

        val productInWarehouses = mutableListOf<ProductInWarehouseEntity>()
        for (warehouse in warehouses) {
            productInWarehouses.add(ProductInWarehouseEntity(product = productEntity, warehouse = warehouse))
        }
        productEntity.productInWarehouses = productInWarehouses
        val savedProductEntity = productRepository.save(productEntity)

        return ProductDto.from(savedProductEntity)
    }

    fun deleteProduct(id: Int, modified: LocalDateTime?) {
        val productFromDb = productRepository.findById(id)
            .orElseThrow {
                throw ResponseStatusException(
                    HttpStatus.CONFLICT, "Product with id $id was deleted by another user."
                )
            }

        if (productFromDb.modified!!.isAfter(modified)) {
            throw ResponseStatusException(
                HttpStatus.CONFLICT,
                "Product with id $id could not be deleted because it was modified by another user on ${productFromDb.modified}."
            )
        }

        return productRepository.deleteById(id)
    }

    fun modifyProduct(product: ProductDto): ProductDto {

        val productFromDb = productRepository.findById(product.id!!)
            .orElseThrow {
                throw ResponseStatusException(
                    HttpStatus.CONFLICT, "Product with id ${product.id} was deleted by another user."
                )
            }

        if (productFromDb.modified!!.isAfter(product.modified)) {
            throw ResponseStatusException(
                HttpStatus.CONFLICT,
                "Product with id ${product.id} could not be edited because it was modified by another user on ${productFromDb.modified}."
            )
        }

        productFromDb.with(product)
        val savedProductEntity = productRepository.save(productFromDb)

        return ProductDto.from(savedProductEntity)
    }

    fun changeProductQuantity(id: Int, modified: LocalDateTime?, delta: Int, warehouseId: Int): ProductDto {
        val productFromDb = productRepository.findById(id)
            .orElseThrow {
                throw ResponseStatusException(
                    HttpStatus.CONFLICT, "Product with id $id was deleted by another user."
                )
            }

        val productInWarehouse = productFromDb.productInWarehouses?.first { it.warehouse?.id == warehouseId }!!

        if (productInWarehouse.quantity + delta >= 0) {
            productInWarehouse.quantity += delta
            productRepository.save(productFromDb)
            return ProductDto.from(productInWarehouse)
        }

        throw ResponseStatusException(
            HttpStatus.CONFLICT,
            "Quantity of product with id ${productFromDb.id} cannot be below zero. Current quantity is ${productInWarehouse.quantity}."
        )
    }
}