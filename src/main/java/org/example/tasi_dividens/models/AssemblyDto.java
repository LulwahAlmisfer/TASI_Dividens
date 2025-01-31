package org.example.tasi_dividens.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class AssemblyDto {

    private String compSymbolCode;
    private String modifiedDt;
    private String companyName;
    private String natureOfGenMetng;
    private String status;
    private String holdingDt;
    private String holdingTime;
    private String holdingSite;

}

