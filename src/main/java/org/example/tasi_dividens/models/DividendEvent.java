package org.example.tasi_dividens.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
@Data
@Entity
@NoArgsConstructor
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

    public DividendEvent(String symbol, String type, LocalDate eventDate, String companyName, Double amount) {
        this.symbol = symbol;
        this.type = type;
        this.eventDate = eventDate;
        this.companyName = companyName;
        this.amount = amount;
    }

    @JsonProperty("imageUrl")
    public String getImageUrl() {
        return "https://web.alrajhi-capital.sa/stock-images/" + symbol + ".webp";
    }
}

