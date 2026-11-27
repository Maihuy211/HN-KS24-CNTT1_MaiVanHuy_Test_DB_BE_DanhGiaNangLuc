package vn.rikkei.cinemabox;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class FresherApiTest {

    @Autowired
    private MockMvc mvc;

    @Test
    void showtimeKhongTonTaiTraNotFound() throws Exception {
        mvc.perform(get("/api/showtimes/9999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("NOT_FOUND"));
    }

    @Test
    void taoKhachHangHopLeTraCreatedVaMacDinhStandard() throws Exception {
        mvc.perform(post("/api/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "fullName": "Khach Test",
                                  "email": "khach-test@example.com",
                                  "phone": "0912345678"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.fullName").value("Khach Test"))
                .andExpect(jsonPath("$.memberLevel").value("STANDARD"));
    }

    @Test
    void duLieuSaiTraValidationFailed() throws Exception {
        mvc.perform(post("/api/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "fullName": " ",
                                  "email": "sai",
                                  "phone": "123"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("VALIDATION_FAILED"))
                .andExpect(jsonPath("$.details").isArray());
    }

    @Test
    void pageSaiTraBadRequest() throws Exception {
        mvc.perform(get("/api/customers?page=-1&size=10"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("BAD_REQUEST"));
    }

    @Test
    void thanhToanBiTuChoiTraPaymentFailed() throws Exception {
        mvc.perform(post("/api/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "customerId": 2,
                                  "showtimeId": 11,
                                  "quantity": 10
                                }
                                """))
                .andExpect(status().is(402))
                .andExpect(jsonPath("$.error").value("PAYMENT_FAILED"));
    }
}
