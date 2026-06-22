package de.ait.g_67_shop.controller;

import de.ait.g_67_shop.domain.Product;
import de.ait.g_67_shop.dto.product.ProductDto;
import de.ait.g_67_shop.dto.product.ProductSaveDto;
import de.ait.g_67_shop.repository.ProductRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;


import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import org.springframework.test.web.servlet.client.RestTestClient;


import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ProductControllerTestIT {

    @LocalServerPort
    private int port;

    private RestTestClient restTestClient;

    @Autowired
    private ProductRepository repository;

    private static final String PRODUCTS_RESOURCE = "/products";

    @BeforeEach
    void startUp() {
        restTestClient = RestTestClient.bindToServer().baseUrl("http://localhost:" + port).build();

        Product activeProduct = new Product();
        activeProduct.setTitle("Test active product");
        activeProduct.setPrice(new BigDecimal("111.00"));
        activeProduct.setActive(true);

        Product inactiveProduct = new Product();
        inactiveProduct.setTitle("Test inactive product");
        inactiveProduct.setPrice(new BigDecimal("222.00"));
        inactiveProduct.setActive(false);

        repository.saveAll(List.of(activeProduct, inactiveProduct));
    }

    @AfterEach
    void cleanDatabase() {
        repository.deleteAll();
    }

    @Test
    void shouldSaveProduct() {
        ProductSaveDto saveDto = new ProductSaveDto();
        saveDto.setTitle("Test product");
        saveDto.setPrice(new BigDecimal("777.00"));

        restTestClient.post().uri(PRODUCTS_RESOURCE)
                .body(saveDto)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(ProductDto.class)
                .value(dto -> {
                    assertNotNull(dto, "Response doesn't have a body");
                    assertNotNull(dto.getId(), "Returned product doesn't have an ID");
                    assertEquals(saveDto.getTitle(), dto.getTitle(), "Returned product has incorrect title");
                    assertEquals(saveDto.getPrice(), dto.getPrice(), "Returned product has incorrect price");

                    Product savedProduct = repository.findByIdAndActiveTrue(dto.getId()).orElse(null);
                    assertNotNull(savedProduct, "Product wasn't properly saved to the database");
                    assertEquals(saveDto.getTitle(), savedProduct.getTitle(), "Saved product has incorrect title");
                    assertEquals(saveDto.getPrice(), savedProduct.getPrice(), "Saved product has incorrect price");
                });
    }

    @Test
    void shouldReturn400WhenTitleIsEmpty() {
        ProductSaveDto saveDto = new ProductSaveDto();
        saveDto.setTitle("");
        saveDto.setPrice(new BigDecimal("777.00"));

        restTestClient.post().uri(PRODUCTS_RESOURCE)
                .body(saveDto)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(String.class)
                .value(body -> {
                    assertNotNull(body, "Response body shouldn't be null");
                    assertTrue(body.contains("title"), "Response doesn't contain an expected message");
                });
    }


}