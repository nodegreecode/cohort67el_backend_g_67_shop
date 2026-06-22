package de.ait.g_67_shop.service.interfaces;

import de.ait.g_67_shop.domain.Product;
import de.ait.g_67_shop.dto.product.ProductDto;
import de.ait.g_67_shop.dto.product.ProductSaveDto;
import de.ait.g_67_shop.dto.product.ProductUpdateDto;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

public interface ProductService {

    ProductDto save(ProductSaveDto saveDto);

    List<ProductDto> getAllActiveProducts();

    ProductDto getActiveProductById(Long id);

    Product getActiveEntityById(Long id);

    void update(Long id, ProductUpdateDto product);

    void deleteById(Long id);

    void restoreById(Long id);

    long getAllActiveProductsCount();

    BigDecimal getAllActiveProductsTotalCost();

    BigDecimal getAllActiveProductsAveragePrice();

    void addImage(Long id, MultipartFile image) throws IOException;

}
