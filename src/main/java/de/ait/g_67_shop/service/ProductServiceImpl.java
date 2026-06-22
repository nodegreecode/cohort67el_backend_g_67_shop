package de.ait.g_67_shop.service;

import de.ait.g_67_shop.domain.Product;
import de.ait.g_67_shop.dto.mapping.ProductMapper;
import de.ait.g_67_shop.dto.product.ProductDto;
import de.ait.g_67_shop.dto.product.ProductSaveDto;
import de.ait.g_67_shop.dto.product.ProductUpdateDto;
import de.ait.g_67_shop.exceptions.types.EntityNotFoundException;
import de.ait.g_67_shop.repository.ProductRepository;
import de.ait.g_67_shop.service.interfaces.FileService;
import de.ait.g_67_shop.service.interfaces.ProductService;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Objects;

@Service
public class ProductServiceImpl implements ProductService {

    private final Logger logger = LoggerFactory.getLogger(ProductServiceImpl.class);

    private final ProductRepository productRepository;

    private final ProductMapper productMapper;

    private final FileService fileService;

    public ProductServiceImpl(ProductRepository productRepository, ProductMapper productMapper, FileService fileService) {
        this.productRepository = productRepository;
        this.productMapper = productMapper;
        this.fileService = fileService;
    }

    @Override
    public ProductDto save(ProductSaveDto saveDto) {
        Objects.requireNonNull(saveDto, "ProductSaveDto cannot be null");
        Product entity = productMapper.mapDtoToEntity(saveDto);
        entity.setActive(true);
        productRepository.save(entity);
        logger.info("Product saved to the database: {}", entity);
        return productMapper.mapEntityToDto(entity);
    }

    @Override
    public List<ProductDto> getAllActiveProducts() {
        List<Product> entities = productRepository.findAllByActiveTrue();
        return productMapper.mapEntityToDto(entities);
    }

    @Override
    public ProductDto getActiveProductById(Long id) {
        Product product = productRepository.findByIdAndActiveTrue(id).orElseThrow(() -> new EntityNotFoundException(Product.class, id));
        return productMapper.mapEntityToDto(product);
    }

    @Override
    public Product getActiveEntityById(Long id) {
        return productRepository.findByIdAndActiveTrue(id).orElseThrow(() -> new EntityNotFoundException(Product.class, id));
    }

    @Override
    @Transactional
    public void update(Long id, ProductUpdateDto updateDto) {
        BigDecimal newPrice = updateDto.getNewPrice();
        productRepository.findById(id).ifPresent(p -> {
            p.setPrice(newPrice);
            logger.info("Product id {} updated, new price: {}", id, newPrice);
        });
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        productRepository.findByIdAndActiveTrue(id).ifPresent(p -> {
            p.setActive(false);
            logger.info("Product id {} marked as inactive", id);
        });
    }

    @Override
    @Transactional
    public void restoreById(Long id) {
        productRepository.findById(id).ifPresent(p -> {
            p.setActive(true);
            logger.info("Product id {} marked as active", id);
        });
    }

    @Override
    public long getAllActiveProductsCount() {
        return productRepository.countByActiveTrue();
    }

    @Override
    public BigDecimal getAllActiveProductsTotalCost() {
        return productRepository.findAllByActiveTrue()
                .stream()
                .map(Product::getPrice)
                .reduce(BigDecimal::add)
                .orElse(BigDecimal.ZERO);
    }

    @Override
    public BigDecimal getAllActiveProductsAveragePrice() {
        long productsCount = getAllActiveProductsCount();

        if (productsCount == 0) {
            return BigDecimal.ZERO;
        }
        return getAllActiveProductsTotalCost().divide(BigDecimal.valueOf(productsCount), 2, RoundingMode.HALF_UP);
    }

    @Override
    @Transactional
    public void addImage(Long id, MultipartFile image) throws IOException {
        Objects.requireNonNull(id, "Product id cannot be null");

        Product product = getActiveEntityById(id);
        String imageUrl = fileService.uploadAndGetUrl(image);
        product.setImageUrl(imageUrl);
    }
}
