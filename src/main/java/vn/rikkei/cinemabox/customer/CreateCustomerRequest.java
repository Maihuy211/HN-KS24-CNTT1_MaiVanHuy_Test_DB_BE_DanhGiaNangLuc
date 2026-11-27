package vn.rikkei.cinemabox.customer;

import jakarta.validation.constraints.*;

public record CreateCustomerRequest(
        @NotBlank
        @Size(max = 100)
        String fullName,

        @NotBlank
        @Email
        @Size(max = 150)
        String email,

        @Pattern(regexp = "^0[0-9]{9}$")
        String phone,

        @Pattern(regexp = "STANDARD|GOLD")
        String memberLevel) {
}