package org.example.tasi_dividens.models;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

@Data
public class DividendDto {
    private String company;
    private String companyShotName;
    private String symbol;
    private String announcedDate; // الاعلان
    private String dueDate; //الاستحقاق
    private String distributionWay;
    private String distributionDate; // توزيع
    private Double amount;
    private String amountValue;
    @JsonIgnore
    private String companyUrl;

}