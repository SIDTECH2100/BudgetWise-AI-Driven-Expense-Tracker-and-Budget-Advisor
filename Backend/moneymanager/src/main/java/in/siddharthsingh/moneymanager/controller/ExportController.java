package in.siddharthsingh.moneymanager.controller;

import in.siddharthsingh.moneymanager.dto.ExpenseDTO;
import in.siddharthsingh.moneymanager.dto.FilterDTO;
import in.siddharthsingh.moneymanager.dto.IncomeDTO;
import in.siddharthsingh.moneymanager.entity.ProfileEntity;
import in.siddharthsingh.moneymanager.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.MessagingException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/export")
@RequiredArgsConstructor
public class ExportController {

    private final ExcelService excelService;
    private final IncomeService incomeService;
    private final ExpenseService expenseService;
    private final EmailService emailService;
    private final ProfileService profileService;

    @PostMapping("/income")
    public ResponseEntity<Void> exportIncomeExcel(@RequestBody FilterDTO filter) throws IOException, MessagingException, jakarta.mail.MessagingException {
        ProfileEntity profile = profileService.getCurrentProfile();

        // Get filtered incomes
        List<IncomeDTO> incomes = incomeService.filterIncomes(
                filter.getStartDate() != null ? filter.getStartDate() : LocalDate.MIN,
                filter.getEndDate() != null ? filter.getEndDate() : LocalDate.now(),
                filter.getKeyword() != null ? filter.getKeyword() : "",
                Sort.by("asc".equalsIgnoreCase(filter.getSortOrder()) ? Sort.Direction.ASC : Sort.Direction.DESC,
                        filter.getSortField() != null ? filter.getSortField() : "date")
        );

        // Create Excel
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        excelService.writeIncomesToExcel(baos, incomes);

        // Send email
        emailService.sendEmailWithAttachment(profile.getEmail(),
                "Your Filtered Income Report",
                "Please find attached your filtered income report",
                baos.toByteArray(),
                "filtered_income.xlsx");

        return ResponseEntity.ok().build();
    }

    @PostMapping("/expense")
    public ResponseEntity<Void> exportExpenseExcel(@RequestBody FilterDTO filter) throws IOException, MessagingException, jakarta.mail.MessagingException {
        ProfileEntity profile = profileService.getCurrentProfile();

        List<ExpenseDTO> expenses = expenseService.filterExpenses(
                filter.getStartDate() != null ? filter.getStartDate() : LocalDate.MIN,
                filter.getEndDate() != null ? filter.getEndDate() : LocalDate.now(),
                filter.getKeyword() != null ? filter.getKeyword() : "",
                Sort.by("asc".equalsIgnoreCase(filter.getSortOrder()) ? Sort.Direction.ASC : Sort.Direction.DESC,
                        filter.getSortField() != null ? filter.getSortField() : "date")
        );

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        excelService.writeExpensesToExcel(baos, expenses);

        emailService.sendEmailWithAttachment(profile.getEmail(),
                "Your Filtered Expense Report",
                "Please find attached your filtered expense report",
                baos.toByteArray(),
                "filtered_expenses.xlsx");

        return ResponseEntity.ok().build();
    }
}

