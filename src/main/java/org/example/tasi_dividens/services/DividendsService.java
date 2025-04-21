package org.example.tasi_dividens.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.tasi_dividens.models.DividendDto;
import org.example.tasi_dividens.models.DividendEvent;
import org.example.tasi_dividens.repositories.DividendEventRepository;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.http.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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

//    // TASI website uses AJAX so this is not useful
//    public void scrape() throws IOException {
//        String url = "https://www.saudiexchange.sa/wps/portal/saudiexchange/newsandreports/issuer-financial-calendars/dividends";
//
//        Document doc = Jsoup.connect(url)
//                .userAgent("PostmanRuntime/7.43.2")
//                .header("Media-Type","text/plain")
//                .referrer("https://www.google.com")
//                .header("Accept","*/*")
//                .header("Accept-Encoding","gzip, deflate, br")
//                .header("Connection","keep-alive")
//                .cookie("h","d")
//                .timeout(1000)
//                .get();
//
//        Elements rows = doc.select("table tr");
//        System.out.println(rows);
//        for (Element row : rows) {
//            Elements cols = row.select("td");
//            System.out.println(cols);
//        }
//    }

//    public void saveEventIfNotExists(String symbol, String type, LocalDate eventDate, String companyName, Double amount) {
//        boolean exists = repository.existsBySymbolAndTypeAndEventDateAndAmount(symbol, type, eventDate,amount);
//        if (!exists) {
//            DividendEvent event = new DividendEvent();
//            event.setSymbol(symbol);
//            event.setType(type);
//            event.setEventDate(eventDate);
//            event.setCompanyName(companyName);
//            event.setAmount(amount);
//
//            repository.save(event);
//        }
//    }

    public void bulkInsertEvents(List<DividendEvent> events) {
        String sql = "INSERT INTO dividend_events (symbol, type, event_date, company_name, amount) VALUES (?, ?, ?, ?, ?) " +
                "ON CONFLICT (symbol, type, event_date) DO NOTHING";

        List<Object[]> batch = events.stream()
                .map(e -> new Object[]{e.getSymbol(), e.getType(), e.getEventDate(), e.getCompanyName(), e.getAmount()})
                .toList();

        jdbcTemplate.batchUpdate(sql, batch);
    }


    @Scheduled(cron = "${dividends.job.cron}")
    public void fetchAndStoreDividendEvents() {
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



    public List<DividendEvent> getAllEvents() {
        return repository.findAll();
    }

}
