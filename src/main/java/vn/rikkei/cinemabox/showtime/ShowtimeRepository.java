package vn.rikkei.cinemabox.showtime;

import java.util.Optional;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import jakarta.persistence.LockModeType;

public interface ShowtimeRepository extends JpaRepository<Showtime, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select s from Showtime s where s.id = :id")
    Optional<Showtime> findByIdForUpdate(@Param("id") Long id);
}
