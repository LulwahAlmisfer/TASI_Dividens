package org.example.tasi_dividens.services;

import org.example.tasi_dividens.models.DeviceSubscription;
import org.example.tasi_dividens.models.DividendEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SubscriptionService {

    private static final Logger log = LoggerFactory.getLogger(SubscriptionService.class);
    private final JdbcTemplate jdbcTemplate;
    private final PushNotificationService pushService;

    public SubscriptionService(JdbcTemplate jdbcTemplate, PushNotificationService pushService) {
        this.jdbcTemplate = jdbcTemplate;
        this.pushService = pushService;
    }

    public void saveSubscription(String token, String companySymbol) {
        String sql = "INSERT INTO device_subscription (device_token, company_symbol) VALUES (?, ?) " +
                "ON CONFLICT (device_token, company_symbol) DO NOTHING";
        jdbcTemplate.update(sql, token, companySymbol);
    }

    public  void  unsubscribe(String token, String companySymbol) {
        log.info("Unsubscribing from device subscription");
        String sql = "DELETE FROM device_subscription WHERE device_token = ? AND company_symbol = ?";
        jdbcTemplate.update(sql, token, companySymbol);
    }

    @Scheduled(cron = "${notifications.job.cron}")
    public void notifyForTodayEvents() {
        try {
        log.info("start notifyForTodayEvents: ");
        String sqlEvents = """
        SELECT id, symbol, company_name, type, amount, event_date
        FROM dividend_events
        WHERE DATE(event_date) = CURRENT_DATE
        """;

        List<DividendEvent> events = jdbcTemplate.query(sqlEvents, (rs, rowNum) ->
                new DividendEvent(
                        rs.getString("symbol"),
                        rs.getString("type"),
                        rs.getDate("event_date").toLocalDate(),
                        rs.getString("company_name"),
                        rs.getDouble("amount")
                )
        );


        log.info("events count: {}", events.size());


        for (DividendEvent event : events) {
            String sqlSubs = "SELECT device_token, company_symbol FROM device_subscription WHERE company_symbol = ?";
            List<DeviceSubscription> subs = jdbcTemplate.query(sqlSubs, new Object[]{event.getSymbol()},
                    (rs, rowNum) -> new DeviceSubscription(rs.getString("device_token"), rs.getString("company_symbol"))
            );



            log.info("company {} subscribed count {}",event.getCompanyName() + " - " + event.getSymbol(),subs.size());


            for (DeviceSubscription sub : subs) {
                log.info("to be notify token : {} for company: {}", sub.getDeviceToken(), sub.getCompanySymbol());

                try {
                    pushService.sendPush(
                            sub.getDeviceToken(),
                            event.getTypeTitle() + " - " + event.getCompanyName(),
                            event.getTypeDescription(),
                            "",
                            "com.LulwahAlmisfer.Compounded"
                    );

                    Thread.sleep(100);
                } catch (Exception e) {
                    log.error("Failed to send push to {}: {}", sub.getDeviceToken(), e.getMessage());
                }
            }

        }

        } catch (Exception e) {
            log.error("Push notification failed: {}", e.getMessage(), e);
            throw e;
        }
    }
    }
