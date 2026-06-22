package de.ait.g_67_shop.service.interfaces;

import de.ait.g_67_shop.domain.Customer;
import de.ait.g_67_shop.dto.customer.CustomerDto;
import de.ait.g_67_shop.dto.customer.CustomerSaveDto;
import de.ait.g_67_shop.dto.customer.CustomerUpdateDto;
import de.ait.g_67_shop.dto.position.PositionUpdateDto;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

public interface CustomerService {

    CustomerDto save(CustomerSaveDto saveDto);

    List<CustomerDto> getAllActiveCustomers();

    CustomerDto getActiveCustomerById(Long id);

    Customer getActiveEntityById(Long id);

    void update(Long id, CustomerUpdateDto updateDto);

    void deleteById(Long id);

    void restoreById(Long id);

    long getAllActiveCustomersCount();

    BigDecimal getActiveCustomerCartTotalPrice(Long id);

    BigDecimal getActiveCustomerCartProductAveragePrice(Long id);

    void addActiveProductToActiveCustomersCartById(Long customerId, Long productId, PositionUpdateDto positionUpdateDto);

    void deleteProductFromCustomersCartById(Long customerId, Long productId, PositionUpdateDto positionUpdateDto);

    void emptyActiveCustomersCartById(Long id);

    void addAvatar(Long id, MultipartFile avatar) throws IOException;

}
