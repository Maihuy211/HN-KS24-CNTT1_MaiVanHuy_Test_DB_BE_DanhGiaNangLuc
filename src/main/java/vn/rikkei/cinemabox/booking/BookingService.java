package vn.rikkei.cinemabox.booking;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.rikkei.cinemabox.common.ResourceNotFoundException;
import vn.rikkei.cinemabox.customer.Customer;
import vn.rikkei.cinemabox.customer.CustomerRepository;
import vn.rikkei.cinemabox.payment.PaymentGateway;
import vn.rikkei.cinemabox.showtime.Showtime;
import vn.rikkei.cinemabox.showtime.ShowtimeRepository;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final CustomerRepository customerRepository;
    private final ShowtimeRepository showtimeRepository;
    private final PaymentGateway paymentGateway;

    public BookingService(BookingRepository bookingRepository,
                          CustomerRepository customerRepository,
                          ShowtimeRepository showtimeRepository,
                          PaymentGateway paymentGateway) {
        this.bookingRepository = bookingRepository;
        this.customerRepository = customerRepository;
        this.showtimeRepository = showtimeRepository;
        this.paymentGateway = paymentGateway;
    }

    @Transactional
    public BookingResponse create(CreateBookingRequest request) {
        Customer customer = customerRepository.findById(request.customerId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Không tìm thấy khách hàng id=" + request.customerId()));

        // Khóa đúng suất chiếu trong suốt transaction để các request đồng thời
        // không cùng đọc rồi cùng trừ một số ghế.
        Showtime showtime = showtimeRepository.findByIdForUpdate(request.showtimeId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Không tìm thấy suất chiếu id=" + request.showtimeId()));

        if (showtime.getAvailableSeats() < request.quantity()) {
            throw new NotEnoughSeatsException("Suất chiếu không đủ ghế");
        }

        long amount = showtime.getPrice() * request.quantity();
        if ("GOLD".equals(customer.getMemberLevel())) {
            amount = amount * 90 / 100;
        }

        // PaymentFailedException là RuntimeException nên transaction tự rollback:
        // ghế chưa bị trừ và booking chưa được lưu.
        String paymentRef = paymentGateway.charge(customer.getId(), amount);

        showtime.setAvailableSeats(showtime.getAvailableSeats() - request.quantity());

        Booking booking = new Booking();
        booking.setCustomer(customer);
        booking.setShowtime(showtime);
        booking.setQuantity(request.quantity());
        booking.setTotalAmount(amount);
        booking.setStatus("CONFIRMED");
        booking.setPaymentRef(paymentRef);

        return BookingResponse.from(bookingRepository.save(booking));
    }
}
