package org.example.tasi_dividens.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

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

    private String holdingTime;
    private String holdingSite;
    private String holdingType;

    private String annurl;

    public DividendEvent(String symbol, String type, LocalDate eventDate, String companyName, Double amount) {
        this.symbol = symbol;
        this.type = type;
        this.eventDate = eventDate;
        this.companyName = companyName;
        this.amount = amount;
    }

    public DividendEvent( String symbol, String type, LocalDate eventDate, String companyName, Double amount, String holdingTime, String holdingSite, String holdingType,String annUrl) {
        this.symbol = symbol;
        this.type = type;
        this.eventDate = eventDate;
        this.companyName = companyName;
        this.amount = amount;
        this.holdingTime = holdingTime;
        this.holdingSite = holdingSite;
        this.holdingType = holdingType;
        this.annurl = annUrl;
    }

    @JsonProperty("imageUrl")
    public String getImageUrl() {
        return "https://www.tadawulgroup.sa/Resources/SEMOBILELOGOS/" + symbol + ".png";
    }

    public String getTypeTitle() {
        String title = "إعلان ";

         switch (type) {
            case "dueDate" -> title += "إستحقاق توزيع";
            case "distributionDate" -> title += "توزيع ارباح";
            case "assembly" -> title += "جمعية";
            default -> title += "";
        }

         return title;
    }

    public String getTypeDescription() {
        String Description = "";

        switch (type) {
            case "dueDate" -> Description += ("الربح الموزع " + getAmount());
            case "distributionDate" -> Description += ("الربح الموزع " + getAmount());
            case "assembly" -> Description += Objects.equals(getHoldingType(), "natural") ? "جمعية عادية" : "جمعية غير عادية" ;
            default -> Description += "";
        }

        return Description;
    }
}

