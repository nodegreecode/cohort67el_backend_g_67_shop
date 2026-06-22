package de.ait.g_67_shop.controller;

import de.ait.g_67_shop.dto.product.ProductDto;
import de.ait.g_67_shop.dto.product.ProductSaveDto;
import de.ait.g_67_shop.dto.product.ProductUpdateDto;
import de.ait.g_67_shop.service.interfaces.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/products")
@Tag(name = "products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Save product", description = "Save new Product to the Database")
    public ProductDto save(
            @RequestBody
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Body with new Product")
            ProductSaveDto saveDto) {
        return productService.save(saveDto);
    }

    @GetMapping
    public List<ProductDto> getAll() {
        return productService.getAllActiveProducts();
    }

    @GetMapping("/{id}")
    public ProductDto getById(@PathVariable Long id) {
        return productService.getActiveProductById(id);
    }

    @PutMapping("/{id}")
    public void update(@PathVariable Long id, @RequestBody ProductUpdateDto updateDto) {
        productService.update(id, updateDto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable Long id) {
        productService.deleteById(id);
    }

    @PutMapping("/{id}/restore")
    public void restoreById(@PathVariable Long id) {
        productService.restoreById(id);
    }

    @GetMapping("/count")
    public long getProductsQuantity() {
        return productService.getAllActiveProductsCount();
    }

    @GetMapping("/total-cost")
    public BigDecimal getProductsTotalCost() {
        return productService.getAllActiveProductsTotalCost();
    }

    @GetMapping("/avg-price")
    public BigDecimal getProductsAveragePrice() {
        return productService.getAllActiveProductsAveragePrice();
    }

    @PostMapping(value = "/{id}/image", consumes = "multipart/form-data")
    public void addImage(@PathVariable Long id, @RequestParam MultipartFile image) throws IOException {
        productService.addImage(id, image);
    }
}
