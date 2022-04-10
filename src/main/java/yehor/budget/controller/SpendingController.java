package yehor.budget.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import yehor.budget.service.SpendingService;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/spending")
@RequiredArgsConstructor
public class SpendingController {

    private final SpendingService spendingService;

    @GetMapping
    public int getSpending(@RequestParam("date") String date) {
        LocalDate localDate = LocalDate.parse(date);
        return spendingService.findByDate(localDate);
    }

}
