package org.example.tasi_dividens.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j;
import lombok.extern.slf4j.Slf4j;
import org.example.tasi_dividens.models.AssemblyDto;
import org.example.tasi_dividens.models.DividendDto;
import org.example.tasi_dividens.models.DividendEvent;
import org.example.tasi_dividens.repositories.DividendEventRepository;
import org.springframework.http.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.xml.crypto.Data;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class DividendsService {


    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final DividendEventRepository repository;
    private final JdbcTemplate jdbcTemplate;

    public DividendsService(RestTemplate restTemplate, ObjectMapper objectMapper, DividendEventRepository repository,JdbcTemplate jdbcTemplate) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
        this.repository = repository;
        this.jdbcTemplate = jdbcTemplate;
    }


    public List<DividendDto> fetchDividendDetails() {
        String url = "https://www.saudiexchange.sa/wps/portal/saudiexchange/newsandreports/issuer-financial-calendars/dividends/!ut/p/z1/04_Sj9CPykssy0xPLMnMz0vMAfIjo8ziTR3NDIw8LAz8LTw8zA0C3bw9LTyDvAwMAoz1w9EU-LqbGQT6OQb6G5mbGriHGehHkaTfIDjAFKggwNfYxyDIwN3AjDj9BjiAIxH2R-FV4mOGoQDTi6gKsPgBrACPI4MTi_QLckNDIwwyPXUdFRUBgmVDWw!!/p0/IZ7_5A602H80OOMQC0604RU6VD10J3=CZ6_5A602H80O8HH70QFKI8IRJ00P3=NJgetDividendsDetails=/";

        // Body
        MultiValueMap<String, String> formData = getDividendDetailsBody();
        // Headers
        HttpHeaders headers = getHttpHeaders();

        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(formData, headers);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);

        try {
            JsonNode root = objectMapper.readTree(response.getBody());
            JsonNode data = root.path("data");

            return objectMapper.readerForListOf(DividendDto.class).readValue(data);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return List.of();
        }

    }

    public List<AssemblyDto> fetchAssemblyDetails() {
        String url = "https://www.saudiexchange.sa/wps/portal/saudiexchange/newsandreports/issuer-financial-calendars/general-assembly-meetings/!ut/p/z1/04_Sj9CPykssy0xPLMnMz0vMAfIjo8ziTR3NDIw8LAz8LTw8zA0C3bw9LTyDvAwMQgz0w9EU-LqbGQT6OQb6G5mbGriHGehHkaTfIDjAFKggwNfYxyDIwN3AjDj9BjiAIxH2R-FV4mOGoQDTi6gKsPgBrACPI4MTi_QLckNDIwwyPXUdFRUBAn6bmA!!/p0/IZ7_5A602H80OOMQC0604RU6VD10J2=CZ6_5A602H80O8HH70QFKI8IRJ00T0=NJgetGenAssemblyDetails=/";

        // Request Body todo clean  clients should be in separate class
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("searchType", "searchData");
        formData.add("marketsListId", "M");
        LocalDate yesterday = LocalDate.now().minusDays(1);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        formData.add("fromDate", yesterday.format(formatter));
        formData.add("toDate", "");
        formData.add("companySymbol", "");
        formData.add("sector", "");
        formData.add("type", "");
        formData.add("status", "");
        formData.add("bySymbol", "");
        formData.add("market", "M");

        // Request Headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.set("accept", "application/json, text/javascript, */*; q=0.01");
        headers.set("accept-language", "en-US,en;q=0.9");
        headers.set("dnt", "1");
        headers.set("origin", "https://www.saudiexchange.sa");
        headers.set("priority", "u=1, i");
        headers.set("referer", "https://www.saudiexchange.sa/wps/portal/saudiexchange/newsandreports/issuer-financial-calendars/general-assembly-meetings");
        headers.set("sec-ch-ua", "\"Chromium\";v=\"135\", \"Not-A.Brand\";v=\"8\"");
        headers.set("sec-ch-ua-mobile", "?0");
        headers.set("sec-ch-ua-platform", "\"macOS\"");
        headers.set("sec-fetch-dest", "empty");
        headers.set("sec-fetch-mode", "cors");
        headers.set("sec-fetch-site", "same-origin");
        headers.set("user-agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/135.0.0.0 Safari/537.36");
        headers.set("x-requested-with", "XMLHttpRequest");
        headers.set("cookie", "asdfghj");

        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(formData, headers);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);

        try {
            JsonNode root = objectMapper.readTree(response.getBody());
            JsonNode data = root.path("data");

            return objectMapper.readerForListOf(AssemblyDto.class).readValue(data);
        } catch (Exception e) {
            System.out.println("Error parsing assembly data: " + e.getMessage());
            return List.of();
        }
    }


    private static MultiValueMap<String, String> getDividendDetailsBody() {
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("symbolorcompany", "");
        formData.add("start", "05-03-2020");
        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        formData.add("end", today);
        formData.add("marketsListId", "M");
        formData.add("sector", "");
        formData.add("period", "CUSTOM");
        formData.add("bySymbol", "");
        formData.add("market", "");
        return formData;
    }

    private static HttpHeaders getHttpHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.set("accept", "application/json, text/javascript, */*; q=0.01");
        headers.set("accept-language", "en-US,en;q=0.9");
        headers.set("dnt", "1");
        headers.set("origin", "https://www.saudiexchange.sa");
        headers.set("priority", "u=1, i");
        headers.set("referer", "https://www.saudiexchange.sa/wps/portal/saudiexchange/newsandreports/issuer-financial-calendars/dividends");
        headers.set("sec-ch-ua", "\"Not:A-Brand\";v=\"24\", \"Chromium\";v=\"134\"");
        headers.set("sec-ch-ua-mobile", "?0");
        headers.set("sec-ch-ua-platform", "\"macOS\"");
        headers.set("sec-fetch-dest", "empty");
        headers.set("sec-fetch-mode", "cors");
        headers.set("sec-fetch-site", "same-origin");
        headers.set("user-agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/134.0.0.0 Safari/537.36");
        headers.set("x-requested-with", "XMLHttpRequest");
        headers.set("cookie", "asdfghj");
        return headers;
    }


    public void bulkInsertEvents(List<DividendEvent> events) {
        String sql = "INSERT INTO dividend_events (symbol, type, event_date, company_name, amount, holding_time, holding_site, holding_type) VALUES (?, ?, ?, ?, ?, ?, ?, ?) " +
                "ON CONFLICT (symbol, type, event_date) DO NOTHING";

        List<Object[]> batch = events.stream()
                .map(e -> new Object[]{e.getSymbol(), e.getType(), e.getEventDate(), e.getCompanyName(), e.getAmount(),e.getHoldingTime(),e.getHoldingSite(),e.getHoldingType()})
                .toList();

        jdbcTemplate.batchUpdate(sql, batch);
    }


    @Scheduled(cron = "${dividends.job.cron}")
    public void fetchAndStoreDividendEvents() {
        log.info("start fetchAndStoreDividendEvents");

        List<DividendDto> data = fetchDividendDetails();

        LocalDate oneYearAgo = LocalDate.now().minusYears(1);

        List<DividendDto> filteredData = data.stream()
                .filter(dto -> {
                    try {
                        if (dto.getDueDate() != null && !dto.getDueDate().isBlank()) {
                            LocalDate due = LocalDate.parse(dto.getDueDate());
                            if (!due.isBefore(oneYearAgo)) return true;
                        }

                        if (dto.getDistributionDate() != null && !dto.getDistributionDate().isBlank()) {
                            LocalDate dist = LocalDate.parse(dto.getDistributionDate());
                            if (!dist.isBefore(oneYearAgo)) return true;
                        }
                    } catch (Exception ignored) {}

                    return false;
                })
                .toList();

        List<DividendEvent> toInsert = new ArrayList<>();

        for (DividendDto dto : filteredData) {
            String symbol = dto.getSymbol();
            String companyName = dto.getCompanyShotName();
            Double amount = dto.getAmount();

            if (dto.getDueDate() != null && !dto.getDueDate().isBlank()) {
                try {
                    LocalDate due = LocalDate.parse(dto.getDueDate());
                    toInsert.add(new DividendEvent(symbol, "dueDate", due, companyName, amount));
                } catch (Exception ignored) {}
            }

            if (dto.getDistributionDate() != null && !dto.getDistributionDate().isBlank()) {
                try {
                    LocalDate dist = LocalDate.parse(dto.getDistributionDate());
                    toInsert.add(new DividendEvent(symbol, "distributionDate", dist, companyName, amount));
                } catch (Exception ignored) {}
            }
        }

        bulkInsertEvents(toInsert);
        System.out.println("✅ Fast insert finished: " + LocalDate.now());
    }


    @Scheduled(cron = "${dividends.job.cron}")
    public void fetchAndStoreAssemblyEvents() {
        log.info("start fetchAndStoreAssemblyEvents");

        List<AssemblyDto> data = fetchAssemblyDetails();

        LocalDate oneYearAgo = LocalDate.now().minusYears(1);

        List<AssemblyDto> filteredData = data.stream()
                .filter(dto -> {
                    try {
                        if (dto.getHoldingDt() != null && !dto.getHoldingDt().isBlank()) {
                            LocalDate due = LocalDate.parse(dto.getHoldingDt());
                            if (!due.isBefore(oneYearAgo)) return true;
                        }

                    } catch (Exception ignored) {}

                    return false;
                })
                .toList();

        List<DividendEvent> toInsert = new ArrayList<>();

        for (AssemblyDto dto : filteredData) {
            String symbol = dto.getCompSymbolCode();
            String companyName = dto.getCompanyName();
            String holdingTime = dto.getHoldingDt();
            String holdingSite = dto.getHoldingSite();
            String holdingType = dto.getNatureOfGenMetng();
            if (holdingType.contains("غير")) {
                holdingType = "UnNatural";
            } else {
                holdingType = "natural";
            }

            if (dto.getHoldingDt() != null && !dto.getHoldingDt().isBlank()) {
                try {
                    LocalDate holdingDt = LocalDate.parse(dto.getHoldingDt());
                    toInsert.add(new DividendEvent(symbol, "assembly", holdingDt, companyName, 0.0,holdingTime,holdingSite,holdingType));
                } catch (Exception ignored) {}
            }

        }

        bulkInsertEvents(toInsert);
        log.info("✅ Fast insert finished: {}", LocalDate.now());
    }

    public List<DividendEvent> getAllEvents() {
        LocalDate sixMonthsAgo = LocalDate.now().minusMonths(6);
        return repository.findAll()
                .stream()
                .filter(event -> event.getEventDate() != null && !event.getEventDate().isBefore(sixMonthsAgo))
                .sorted(Comparator.comparing(DividendEvent::getEventDate).reversed())
                .collect(Collectors.toList());
    }

}
