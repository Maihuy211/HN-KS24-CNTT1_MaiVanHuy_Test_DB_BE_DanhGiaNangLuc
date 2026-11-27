-- F1. Lệnh CREATE TABLE reviews (xem đề). Chỉ tạo bảng reviews, không sửa bảng có sẵn.
CREATE TABLE reviews (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    customer_id BIGINT       NOT NULL,
    movie_id    BIGINT       NOT NULL,
    rating      INT          NOT NULL,
    content     VARCHAR(500),
    created_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);