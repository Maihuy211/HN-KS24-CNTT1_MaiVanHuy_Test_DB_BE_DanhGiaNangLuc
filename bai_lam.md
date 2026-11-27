# Bài làm CinemaBox – Database + Backend

Họ tên:
Mã sinh viên:
Lớp:

## Pha 1 (không dùng AI)

Tự đánh dấu phần đã xong trước mốc 1 (chỉ để GV tham khảo, không thay cho kết quả chấm):

- [ ] I1  - [ ] I2  - [ ] I3  - [ ] Endpoint GET /api/showtimes/{id}
- [ ] F1  - [ ] F2  - [ ] F3  - [ ] API khách hàng (tạo, xem, danh sách, validate, lỗi)

Ghi chú (nếu có):

## Pha 2 (được dùng AI)

### Cách bạn chống bán vượt số ghế khi nhiều người đặt cùng lúc

Trong `BookingService`, tôi dùng transaction kết hợp với khóa bi quan `PESSIMISTIC_WRITE` trên dòng `showtimes` đang được đặt. Request phải lấy khóa trước khi đọc và cập nhật `available_seats`, nên các request đồng thời vào cùng một suất chiếu sẽ được xử lý lần lượt và luôn đọc số ghế mới nhất. Sau khi lấy khóa, nếu số ghế còn lại không đủ thì ném `NOT_ENOUGH_SEATS`; nếu đủ mới trừ ghế và lưu vé `CONFIRMED`. Cách kiểm chứng là chạy API đặt vé với nhiều request vào cùng suất chiếu và kiểm tra số ghế không âm, tổng số vé bán không vượt số ghế ban đầu.

### Cách bạn hoàn tác khi thanh toán lỗi

Lệnh gọi `PaymentGateway.charge(...)` được thực hiện bên trong phương thức `@Transactional`, trước khi trừ ghế và lưu booking. Khi `FakePaymentGateway` ném `PaymentFailedException`, exception được giữ nguyên để thoát khỏi transaction; Spring sẽ rollback toàn bộ thay đổi cơ sở dữ liệu. Vì vậy suất chiếu giữ nguyên số ghế, không phát sinh vé `CONFIRMED` và API trả `402 PAYMENT_FAILED`. Cách kiểm chứng là gửi request có tổng tiền vượt hạn mức thanh toán và chạy test `thanhToanBiTuChoiTraPaymentFailed`.

### Test bạn đã viết (tên lớp, tên test, mỗi test kiểm tra điều gì)

### Nhật ký dùng AI

| # | Bạn hỏi AI điều gì                             | AI gợi ý gì | Bạn giữ / sửa / bỏ, vì sao |
|---|------------------------------------------------|---|---|
| 1 | Kiểm tra Mục 4.3 đã hoàn thành chưa            | Gợi ý đối chiếu API đặt vé, xử lý lỗi, test rollback và nội dung bắt buộc trong `bai_lam.md`. | Giữ phần đối chiếu; bổ sung nội dung Pha 2 còn thiếu và kiểm tra lại các test. |
| 2 | Kiểm tra các phần còn thiếu của Mục 4.1 và 4.2 | Đối chiếu tên cột SQL, DTO, controller, service và các loại lỗi API cần trả về. | Giữ các kiểm tra phù hợp với đề; sửa phần xử lý validation, JSON sai, tham số sai và email trùng trong `GlobalExceptionHandler`. |

