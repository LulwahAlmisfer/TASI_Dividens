package org.example.tasi_dividens.models;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
@Data
@Entity
@Table(name = "dividend_events", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"symbol", "type", "eventDate"})
})
public class DividendEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String symbol;

    private String type; // "dueDate" or "distributionDate"

    private LocalDate eventDate;

    private LocalDateTime createdAt = LocalDateTime.now();

    private String companyName;

    private Double amount;

}

