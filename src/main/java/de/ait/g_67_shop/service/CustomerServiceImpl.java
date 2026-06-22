package de.ait.g_67_shop.service;

import de.ait.g_67_shop.domain.Cart;
import de.ait.g_67_shop.domain.Customer;
import de.ait.g_67_shop.domain.Position;
import de.ait.g_67_shop.domain.Product;
import de.ait.g_67_shop.dto.customer.CustomerDto;
import de.ait.g_67_shop.dto.customer.CustomerSaveDto;
import de.ait.g_67_shop.dto.customer.CustomerUpdateDto;
import de.ait.g_67_shop.dto.mapping.CustomerMapper;
import de.ait.g_67_shop.dto.position.PositionUpdateDto;
import de.ait.g_67_shop.exceptions.types.EntityNotFoundException;
import de.ait.g_67_shop.exceptions.types.EntityUpdateException;
import de.ait.g_67_shop.repository.CustomerRepository;
import de.ait.g_67_shop.service.interfaces.CustomerService;
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
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Service
public class CustomerServiceImpl implements CustomerService {

    private final Logger logger = LoggerFactory.getLogger(CustomerServiceImpl.class);

    private final CustomerRepository customerRepository;

    private final ProductService productService;

    private final CustomerMapper customerMapper;

    private final FileService fileService;

    public CustomerServiceImpl(CustomerRepository customerRepository, ProductService productService, CustomerMapper customerMapper, FileService fileService) {
        this.customerRepository = customerRepository;
        this.productService = productService;
        this.customerMapper = customerMapper;
        this.fileService = fileService;
    }

    @Override
    public CustomerDto save(CustomerSaveDto saveDto) {
        Objects.requireNonNull(saveDto, "CustomerSaveDto cannot be null");
        Customer entity = customerMapper.mapDtoToEntity(saveDto);
        entity.setActive(true);
        customerRepository.save(entity);
        logger.info("Customer saved to the database: {}", entity);
        return customerMapper.mapEntityToDto(entity);
    }

    @Override
    public List<CustomerDto> getAllActiveCustomers() {
        List<Customer> entities = customerRepository.findAllByActiveTrue();
        return customerMapper.mapEntityToDto(entities);
    }

    @Override
    public CustomerDto getActiveCustomerById(Long id) {
        Objects.requireNonNull(id, "Customer id cannot be null");
        Customer customer = customerRepository.findByIdAndActiveTrue(id).orElseThrow(() -> new EntityNotFoundException(Customer.class, id));
        return customerMapper.mapEntityToDto(customer);
    }

    @Override
    public Customer getActiveEntityById(Long id) {
        Objects.requireNonNull(id, "Customer id cannot be null");
        return customerRepository.findByIdAndActiveTrue(id).orElseThrow(() -> new EntityNotFoundException(Customer.class, id));
    }

    @Override
    @Transactional
    public void update(Long id, CustomerUpdateDto updateDto) {
        Objects.requireNonNull(id, "Customer id cannot be null");
        Objects.requireNonNull(updateDto, "CustomerUpdateDto cannot be null");
        String newName = updateDto.getName();
        Customer customer = customerRepository.findById(id).orElseThrow(() -> {
            logger.warn("Failed to update customer name. Customer id {} not found", id);
            return new EntityUpdateException(String.format("Failed to update customer name: customer id %d was not found", id));
        });
        customer.setName(newName);
        logger.info("Customer id {} updated, new name: {}", id, newName);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        Objects.requireNonNull(id, "Customer id cannot be null");
        Customer customer = customerRepository.findByIdAndActiveTrue(id).orElseThrow(() -> {
            logger.warn("Failed to delete customer. Customer id {} not found", id);
            //return new EntityUpdateException(String.format("Failed to delete customer: customer id %d was not found", id));
            return new EntityNotFoundException(Customer.class, id);
        });
        customer.setActive(false);
        logger.info("Customer id {} marked as inactive", id);
    }

    @Override
    @Transactional
    public void restoreById(Long id) {
        Objects.requireNonNull(id, "Customer id cannot be null");
        Customer customer = customerRepository.findById(id).orElseThrow(() -> {
            logger.warn("Failed to restore customer. Customer id {} not found", id);
            return new EntityUpdateException(String.format("Failed to restore customer: customer id %d was not found", id));
        });
        customer.setActive(true);
        logger.info("Customer id {} marked as active", id);
    }

    @Override
    public long getAllActiveCustomersCount() {
        return customerRepository.countByActiveTrue();
    }

    @Override
    public BigDecimal getActiveCustomerCartTotalPrice(Long id) {
        Objects.requireNonNull(id, "Customer id cannot be null");
        Customer customer = customerRepository.findByIdAndActiveTrue(id).orElseThrow(() -> new EntityNotFoundException(Customer.class, id));
        Cart cart = customer.getCart();

        if (cart == null) {
            logger.warn("No cart exists for customer id {}", id);
            throw new EntityNotFoundException(Cart.class, null);
        }

        BigDecimal totalCartPrice = BigDecimal.ZERO;

        for (Position p : cart.getPositions()) {
            BigDecimal price = p.getProduct().getPrice();
            int quantity = p.getQuantity();
            BigDecimal totalProductPrice = price.multiply(BigDecimal.valueOf(quantity));
            totalCartPrice = totalCartPrice.add(totalProductPrice);
        }

        return totalCartPrice;
    }

    @Override
    public BigDecimal getActiveCustomerCartProductAveragePrice(Long id) {
        Objects.requireNonNull(id, "Customer id cannot be null");
        Customer customer = getActiveEntityById(id);
        Cart cart = customer.getCart();

        if (cart == null) {
            logger.warn("No cart exists for customer id {}", id);
            throw new EntityNotFoundException(Cart.class, null);
        }

        int totalQuantity = cart.getPositions().stream().mapToInt(Position::getQuantity).sum();

        if (totalQuantity == 0) {
            return BigDecimal.ZERO;
        }

        BigDecimal cartTotalPrice = getActiveCustomerCartTotalPrice(id);
        return cartTotalPrice.divide(BigDecimal.valueOf(totalQuantity), 2, RoundingMode.HALF_UP);

    }

    @Override
    @Transactional
    public void addActiveProductToActiveCustomersCartById(Long customerId, Long productId, PositionUpdateDto positionUpdateDto) {
        Objects.requireNonNull(customerId, "Customer id cannot be null");
        Objects.requireNonNull(productId, "Product id cannot be null");
        Objects.requireNonNull(positionUpdateDto, "Product quantity cannot be null");
        int quantity = positionUpdateDto.getQuantity();
        if (quantity <= 0) {
            logger.warn("Quantity must be greater than zero");
            throw new EntityUpdateException("Quantity must be greater than zero");
        }

        Customer customer = customerRepository.findByIdAndActiveTrue(customerId).orElseThrow(() -> new EntityNotFoundException(Customer.class, customerId));
        Cart cart = customer.getCart();

        if (cart == null) {
            cart = new Cart();
            cart.setCustomer(customer);
            customer.setCart(cart);
        }

        Product product = productService.getActiveEntityById(productId);

        Set<Position> positions = cart.getPositions();
        Position position = positions.stream()
                .filter(p -> p.getProduct().getId().equals(productId))
                .findFirst()
                .orElse(null);

        if (position == null) {
            position = new Position();
            position.setProduct(product);
            position.setQuantity(quantity);
            position.setCart(cart);
            positions.add(position);
        } else {
            position.setQuantity(position.getQuantity() + quantity);
        }

        try {
            customerRepository.save(customer);
            logger.info("Product id {} added to cart of customer id {}. Quantity: {}", productId, customerId, quantity);
        } catch (Exception e) {
            logger.warn("Failed to add product {} to customer {} cart",
                    productId, customerId, e);
            throw new EntityUpdateException(String.format(
                    "Failed to add product with id %d to cart of customer id %d",
                    productId,
                    customerId
            ));
        }

    }

    @Override
    @Transactional
    public void deleteProductFromCustomersCartById(Long customerId, Long productId, PositionUpdateDto positionUpdateDto) {
        Objects.requireNonNull(customerId, "Customer id cannot be null");
        Objects.requireNonNull(productId, "Product id cannot be null");
        Objects.requireNonNull(positionUpdateDto, "Product quantity cannot be null");
        int quantity = positionUpdateDto.getQuantity();
        if (quantity <= 0) {
            logger.warn("Quantity must be greater than zero");
            throw new EntityUpdateException("Quantity must be greater than zero");
        }

        Customer customer = getActiveEntityById(customerId);
        Cart cart = customer.getCart();

        if (cart == null) {
            logger.warn("No cart exists for customer id {}", customerId);
            throw new EntityNotFoundException(Cart.class, null);
        }

        boolean productFound = false;
        Iterator<Position> iterator = cart.getPositions().iterator();
        while (iterator.hasNext()) {
            Position position = iterator.next();
            if (position.getProduct().getId().equals(productId)) {
                productFound = true;
                if (position.getQuantity() > quantity) {
                    position.setQuantity(position.getQuantity() - quantity);
                    logger.info("Quantity of product id {} in cart of customer id {} decreased by {}", productId, customerId, quantity);
                } else {
                    iterator.remove();
                    position.setCart(null);
                    logger.info("Product id {} deleted from cart of customer id {}", productId, customerId);
                }
            }
        }
        if (!productFound) {
            logger.warn("Failed to delete product id {}: product not found in cart", productId);
            throw new EntityUpdateException(String.format("Failed to delete product id %d: product not found in cart", productId));
        }
    }

    @Override
    @Transactional
    public void emptyActiveCustomersCartById(Long id) {
        Objects.requireNonNull(id, "Customer id cannot be null");
        Customer customer = getActiveEntityById(id);
        Cart cart = customer.getCart();
        if (cart == null) {
            logger.warn("Failed to clear cart: no cart exists for customer id {}", id);
            throw new EntityUpdateException(String.format("Failed to clear cart: no cart exists for customer id %d", id));
        }
        cart.getPositions().clear();
        logger.info("Cart of customer id {} cleared", id);
    }

    @Override
    public void addAvatar(Long id, MultipartFile avatar) throws IOException {
        Objects.requireNonNull(id, "Customer id cannot be null");

        Customer customer = getActiveEntityById(id);
        String imageUrl = fileService.uploadAndGetUrl(avatar);
        customer.setAvatarUrl(imageUrl);
    }
}
