package org.example.tasi_dividens.controllers;

import lombok.RequiredArgsConstructor;
import org.example.tasi_dividens.services.AnnouncementScraperService;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/announcements")
@RequiredArgsConstructor
public class AnnouncementController {

    private final AnnouncementScraperService scraperService;

    @GetMapping("/scrape")
    public Map<String, Object> scrape(@RequestParam String url) throws IOException {
        return scraperService.scrapeAnnouncement(url);
    }
}
