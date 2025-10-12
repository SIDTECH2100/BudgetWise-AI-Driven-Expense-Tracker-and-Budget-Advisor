package in.siddharthsingh.moneymanager.controller;
import in.siddharthsingh.moneymanager.dto.ExpenseDTO;
import in.siddharthsingh.moneymanager.dto.FilterDTO;
import in.siddharthsingh.moneymanager.dto.IncomeDTO;
import in.siddharthsingh.moneymanager.entity.ProfileEntity;
import in.siddharthsingh.moneymanager.service.*;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/email")
@RequiredArgsConstructor
public class EmailController {

    private final ExcelService excelService;
    private final IncomeService incomeService;
    private final ExpenseService expenseService;
    private final EmailService emailService;
    private final ProfileService profileService;

    @GetMapping("/income-excel")
    public ResponseEntity<Void> emailIncomeExcel() throws IOException, MessagingException {
        ProfileEntity profile = profileService.getCurrentProfile();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        excelService.writeIncomesToExcel(baos, incomeService.getCurrentMonthIncomesForCurrentUser());
        emailService.sendEmailWithAttachment(profile.getEmail(),
                "Your Income Excel Report",
                "Please find attached your income report",
                baos.toByteArray(),
                "income.xlsx");
        return ResponseEntity.ok(null);
    }

    @GetMapping("/expense-excel")
    public ResponseEntity<Void> emailExpenseExcel() throws IOException, MessagingException {
        ProfileEntity profile = profileService.getCurrentProfile();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        excelService.writeExpensesToExcel(baos, expenseService.getCurrentMonthExpensesForCurrentUser());
        emailService.sendEmailWithAttachment(
                profile.getEmail(),
                "Your Expense Excel Report",
                "Please find attached your expense report.",
                baos.toByteArray(),
                "expenses.xlsx");
        return ResponseEntity.ok(null);
    }
    // added code  for filter data only to downalod
    @PostMapping("/income/download")
    public ResponseEntity<byte[]> downloadIncomeExcel(@RequestBody FilterDTO filter) throws IOException {
        List<IncomeDTO> incomes = incomeService.filterIncomes(
                filter.getStartDate() != null ? filter.getStartDate() : LocalDate.MIN,
                filter.getEndDate() != null ? filter.getEndDate() : LocalDate.now(),
                filter.getKeyword() != null ? filter.getKeyword() : "",
                Sort.by("asc".equalsIgnoreCase(filter.getSortOrder()) ? Sort.Direction.ASC : Sort.Direction.DESC,
                        filter.getSortField() != null ? filter.getSortField() : "date")
        );
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        excelService.writeIncomesToExcel(baos, incomes);
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=filtered_income.xlsx")
                .body(baos.toByteArray());
    }

    @PostMapping("/expense/download")
    public ResponseEntity<byte[]> downloadExpenseExcel(@RequestBody FilterDTO filter) throws IOException {
        List<ExpenseDTO> expenses = expenseService.filterExpenses(
                filter.getStartDate() != null ? filter.getStartDate() : LocalDate.MIN,
                filter.getEndDate() != null ? filter.getEndDate() : LocalDate.now(),
                filter.getKeyword() != null ? filter.getKeyword() : "",
                Sort.by("asc".equalsIgnoreCase(filter.getSortOrder()) ? Sort.Direction.ASC : Sort.Direction.DESC,
                        filter.getSortField() != null ? filter.getSortField() : "date")
        );
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        excelService.writeExpensesToExcel(baos, expenses);
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=filtered_expenses.xlsx")
                .body(baos.toByteArray());
    }

}

