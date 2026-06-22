package de.ait.g_67_shop.controller;

import de.ait.g_67_shop.domain.Customer;
import de.ait.g_67_shop.domain.Product;
import de.ait.g_67_shop.domain.User;
import de.ait.g_67_shop.domain.enums.Role;
import de.ait.g_67_shop.dto.customer.CustomerDto;
import de.ait.g_67_shop.dto.customer.CustomerSaveDto;
import de.ait.g_67_shop.dto.customer.CustomerUpdateDto;
import de.ait.g_67_shop.dto.position.PositionUpdateDto;
import de.ait.g_67_shop.repository.CustomerRepository;
import de.ait.g_67_shop.repository.ProductRepository;
import de.ait.g_67_shop.repository.UserRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.client.RestTestClient;

import javax.crypto.SecretKey;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CustomerControllerTestIT {

    @LocalServerPort
    private int port;

    private RestTestClient httpClient;

    @Autowired
    private CustomerRepository repository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private ProductRepository productRepository;

    @Value("${KEY_PHRASE_ACCESS}")
    private String accessPhrase;

    private String adminAccessToken;

    private static final String CUSTOMERS_RESOURCE = "/customers";

    @BeforeEach
    void setUp() {
        httpClient = RestTestClient.bindToServer().baseUrl("http://localhost:" + port).build();
        addUsersToDb();
        createAdminAccessToken();
    }

    @AfterEach
    void tearDown() {
        repository.deleteAll();
        productRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void shouldSaveCustomer() {
        var saveDto = new CustomerSaveDto();
        saveDto.setName("Tester");

        String tokenCookie = "Access-Token=" + adminAccessToken;

        httpClient.post()
                .uri(CUSTOMERS_RESOURCE)
                .cookie("Access-Token", adminAccessToken)
                .body(saveDto)
                .exchange()
                .expectStatus()
                .isCreated()
                .expectBody(CustomerDto.class)
                .value(dto -> {
                    assertNotNull(dto, "Response doesn't have a body");
                    assertNotNull(dto.getId(), "Returned customer doesn't have an ID");
                    assertEquals(saveDto.getName(), dto.getName(), "Returned customer has incorrect name");

                    var savedCustomer = repository.findByIdAndActiveTrue(dto.getId()).orElse(null);
                    assertNotNull(savedCustomer, "Customer wasn't properly saved to the database");
                    assertEquals(saveDto.getName(), savedCustomer.getName(), "Saved customer has incorrect name");
                });
    }

    @Test
    void shouldReturn404WhenCustomerInactive() {
        var savedCustomer = createCustomer("Peter", false);

        String tokenCookie = "Access-Token=" + adminAccessToken;

        httpClient.get()
                .uri(CUSTOMERS_RESOURCE + "/" + savedCustomer.getId())
                .header("Cookie", tokenCookie)
                .exchange()
                .expectStatus()
                .isNotFound()
                .expectBody(String.class)
                .value(body -> {
                    assertNotNull(body, "Response body shouldn't be null");
                    assertTrue(body.contains(savedCustomer.getId().toString()), "Response body doesn't contain expected message");
                });
    }

    @Test
    void shouldUpdateCustomerName() {
        var savedCustomer = createCustomer("Max", true);
        var updateDto = new CustomerUpdateDto();
        updateDto.setName("Rene");

        String tokenCookie = "Access-Token=" + adminAccessToken;

        httpClient.put()
                .uri(CUSTOMERS_RESOURCE + "/" + savedCustomer.getId())
                .cookie("Access-Token", adminAccessToken)
                .body(updateDto)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .isEmpty();

        var updatedCustomer = repository.findByIdAndActiveTrue(savedCustomer.getId()).orElse(null);
        assertEquals(updateDto.getName(), updatedCustomer.getName(), "Customer's name wasn't properly updated");
    }

    @Test
    void shouldReturn404WhenRemovingCustomerNotExist() {
        Long customerId = 12345L;
        httpClient.delete()
                .uri(CUSTOMERS_RESOURCE + "/" + customerId)
                .exchange()
                .expectStatus()
                .isNotFound()
                .expectBody(String.class)
                .value(body -> {
                    assertNotNull(body);
                    assertTrue(body.contains(customerId.toString()), "Response body doesn't contain expected message");
                });
    }

    @Test
    void shouldAddProductToCart() {
        var savedCustomer = createCustomer("Monika", true);
        var savedProduct = createProduct("Apple", 2.49, true);
        var positionUpdateDto = new PositionUpdateDto();
        positionUpdateDto.setQuantity(5);

        httpClient.post().uri(CUSTOMERS_RESOURCE + "/" + savedCustomer.getId() + "/positions/" + savedProduct.getId())
                .body(positionUpdateDto)
                .exchange()
                .expectStatus()
                .isCreated()
                .expectBody()
                .isEmpty();

        var updatedCustomer = repository.findByIdAndActiveTrue(savedCustomer.getId()).orElse(null);
        assertNotNull(updatedCustomer, "Customer wasn't properly saved to database");

        var positions = updatedCustomer.getCart().getPositions();
        assertNotNull(positions, "Cart positions should not be null");
        assertEquals(1, positions.size(), "Expected exactly one cart position");

        var position = positions.iterator().next();
        assertEquals(savedProduct.getId(), position.getProduct().getId(), "Updated customer has incorrect product in the cart");
        assertEquals(positionUpdateDto.getQuantity(), position.getQuantity(), "Quantity of the product in the cart is incorrect");
    }

    private Product createProduct(String title, double price, boolean isActive) {
        Product product = new Product();
        product.setTitle(title);
        product.setPrice(BigDecimal.valueOf(price));
        product.setActive(isActive);
        return productRepository.save(product);
    }


    private Customer createCustomer(String name, boolean isActive) {
        Customer customer = new Customer();
        customer.setName(name);
        customer.setActive(isActive);
        return repository.save(customer);
    }

    private void createAdminAccessToken() {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + 60000);

        SecretKey secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(accessPhrase));

        adminAccessToken = Jwts
                .builder()
                .subject("admin@test.com")
                .expiration(expiration)
                .signWith(secretKey)
                .compact();
    }

    private void addUsersToDb() {
        User admin = new User();
        admin.setEmail("admin@test.com");
        admin.setPassword(passwordEncoder.encode("adminPass"));
        admin.setName("Admin");
        admin.setRole(Role.ROLE_ADMIN);
        admin.setConfirmed(true);
        userRepository.save(admin);
    }


}
