package org.example.tasi_dividens.models;

import jakarta.persistence.*;

@Entity
@Table(name = "device_subscriptions")
public class DeviceSubscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String deviceToken;
    private String companySymbol;

    // Constructors
    public DeviceSubscription() {}

    public DeviceSubscription(String deviceToken, String companySymbol) {
        this.deviceToken = deviceToken;
        this.companySymbol = companySymbol;
    }


    public Long getId() { return id; }
    public String getDeviceToken() { return deviceToken; }
    public void setDeviceToken(String deviceToken) { this.deviceToken = deviceToken; }
    public String getCompanySymbol() { return companySymbol; }
    public void setCompanySymbol(String companySymbol) { this.companySymbol = companySymbol; }
}
