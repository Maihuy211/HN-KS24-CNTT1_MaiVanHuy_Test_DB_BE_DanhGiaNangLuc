package vn.rikkei.cinemabox.customer;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.rikkei.cinemabox.common.DuplicateException;
import vn.rikkei.cinemabox.common.PageResponse;
import vn.rikkei.cinemabox.common.ResourceNotFoundException;

@Service
@Transactional(readOnly = true)
public class CustomerService {

    private final CustomerRepository repository;

    public CustomerService(CustomerRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public CustomerResponse create(CreateCustomerRequest request) {
        if (repository.existsByEmail(request.email())) {
            throw new DuplicateException("Email đã tồn tại");
        }

        Customer c = new Customer();
        c.setFullName(request.fullName());
        c.setEmail(request.email());
        c.setPhone(request.phone());
        c.setMemberLevel(request.memberLevel());

        return CustomerResponse.from(repository.save(c));
    }

    public CustomerResponse getById(Long id) {
        Customer c = repository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Không tìm thấy khách hàng id=" + id));
        return CustomerResponse.from(c);
    }

    public PageResponse<CustomerResponse> list(int page, int size) {
        if (page < 0 || size < 1 || size > 50) {
            throw new IllegalArgumentException("page hoặc size không hợp lệ");
        }

        Page<Customer> result = repository.findAll(
                PageRequest.of(page, size, Sort.by("id").ascending()));

        return PageResponse.from(result.map(CustomerResponse::from));
    }
}
