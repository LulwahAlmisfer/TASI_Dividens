package org.example.tasi_dividens.models;

public class SubscriptionRequest {
    private String deviceToken;
    private String companySymbol;


    public String getDeviceToken() { return deviceToken; }
    public void setDeviceToken(String deviceToken) { this.deviceToken = deviceToken; }
    public String getCompanySymbol() { return companySymbol; }
    public void setCompanySymbol(String companySymbol) { this.companySymbol = companySymbol; }
}

