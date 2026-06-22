package de.ait.g_67_shop.controller;

import de.ait.g_67_shop.dto.customer.CustomerDto;
import de.ait.g_67_shop.dto.customer.CustomerSaveDto;
import de.ait.g_67_shop.dto.customer.CustomerUpdateDto;
import de.ait.g_67_shop.dto.position.PositionUpdateDto;
import de.ait.g_67_shop.service.interfaces.CustomerService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/customers")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CustomerDto save(@RequestBody CustomerSaveDto saveDto) {
        return customerService.save(saveDto);
    }

    @GetMapping
    public List<CustomerDto> getAll() {
        return customerService.getAllActiveCustomers();
    }

    @GetMapping("/{customerId}")
    public CustomerDto getById(@PathVariable Long customerId) {
        return customerService.getActiveCustomerById(customerId);
    }

    @PutMapping("/{customerId}")
    public void update(@PathVariable Long customerId, @RequestBody CustomerUpdateDto updateDto) {
        customerService.update(customerId, updateDto);
    }

    @DeleteMapping("/{customerId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable Long customerId) {
        customerService.deleteById(customerId);
    }

    @PutMapping("/{customerId}/restore")
    public void restoreById(@PathVariable Long customerId) {
        customerService.restoreById(customerId);
    }

    @GetMapping("/count")
    public long getCustomersQuantity() {
        return customerService.getAllActiveCustomersCount();
    }

    @GetMapping("/{customerId}/cart/total-price")
    public BigDecimal getCustomersCartTotalPriceById(@PathVariable Long customerId) {
        return customerService.getActiveCustomerCartTotalPrice(customerId);
    }

    @GetMapping("/{customerId}/cart/avg-price")
    public BigDecimal getCustomersCartProductAveragePriceById(@PathVariable Long customerId) {
        return customerService.getActiveCustomerCartProductAveragePrice(customerId);
    }

    @PostMapping("/{customerId}/positions/{productId}")
    @ResponseStatus(HttpStatus.CREATED)
    public void addProductToCustomersCartById(@PathVariable Long customerId, @PathVariable Long productId, @RequestBody PositionUpdateDto positionUpdateDto) {
        customerService.addActiveProductToActiveCustomersCartById(customerId, productId, positionUpdateDto);
    }

    @PutMapping("/{customerId}/positions/{productId}")
    public void deleteProductFromCustomersCartById(@PathVariable Long customerId, @PathVariable Long productId, @RequestBody PositionUpdateDto positionUpdateDto) {
        customerService.deleteProductFromCustomersCartById(customerId, productId, positionUpdateDto);
    }

    @DeleteMapping("/{customerId}/positions")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void emptyCustomersCartById(@PathVariable Long customerId) {
        customerService.emptyActiveCustomersCartById(customerId);
    }

    @PostMapping(value = "/{id}/avatar", consumes = "multipart/form-data")
    public void addAvatar(@PathVariable Long id, @RequestParam MultipartFile avatar) throws IOException {
        customerService.addAvatar(id, avatar);
    }

}
