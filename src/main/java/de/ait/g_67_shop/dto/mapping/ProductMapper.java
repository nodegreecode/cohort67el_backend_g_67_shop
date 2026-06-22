package de.ait.g_67_shop.dto.mapping;

import de.ait.g_67_shop.domain.Product;
import de.ait.g_67_shop.dto.product.ProductDto;
import de.ait.g_67_shop.dto.product.ProductSaveDto;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    ProductDto mapEntityToDto(Product entity);

    Product mapDtoToEntity(ProductSaveDto dto);

    List<ProductDto> mapEntityToDto(List<Product> entities);

}
