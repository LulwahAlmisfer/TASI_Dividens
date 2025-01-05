package org.example.tasi_dividens.controllers;

import org.example.tasi_dividens.models.DividendDto;
import org.example.tasi_dividens.services.DividendsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/dividends")
public class DividendsController {

    private final DividendsService service = new DividendsService();

    @GetMapping("all")
    public List<DividendDto> getDividends() {
        return service.fetchDividendDetails();
    }

}
