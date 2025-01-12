package org.example.tasi_dividens.repositories;

import org.example.tasi_dividens.models.DividendEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;

public interface DividendEventRepository extends JpaRepository<DividendEvent, Long> {
    boolean existsBySymbolAndTypeAndEventDateAndAmount(String symbol, String type, LocalDate eventDate, Double amount);
}
