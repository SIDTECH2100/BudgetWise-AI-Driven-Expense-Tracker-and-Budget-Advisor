package in.siddharthsingh.moneymanager.service;

import in.siddharthsingh.moneymanager.dto.ExpenseDTO;
import in.siddharthsingh.moneymanager.repository.ExpenseRepository;
import in.siddharthsingh.moneymanager.repository.IncomeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AIService {

    private final ChatClient chatClient;          // From AIConfig
    private final ExpenseRepository expenseRepository;
    private final IncomeRepository incomeRepository;

    /**
     * Basic Q/A with AI model
     */
    public String ask(String prompt) {
        return chatClient.prompt()
                .user(prompt)
                .call()
                .content();
    }


    /**
     * Helper to format amounts in Indian Rupees
     */
    private String formatINR(BigDecimal amount) {
        if (amount == null) return "₹0.00";
        return "₹" + String.format("%,.2f", amount);
    }
    /**
     * Generate 2-3 personalized financial suggestions for a user
     */
    public List<String> generateSuggestions(Long id) {
        LocalDate today = LocalDate.now();
        LocalDate startOfMonth = today.withDayOfMonth(1);

        // Fetch this month's expenses and map to DTO
        List<ExpenseDTO> monthlyExpenses = expenseRepository
                .findByProfileIdAndDateBetween(id, startOfMonth, today)
                .stream()
                .map(e -> ExpenseDTO.builder()
                        .amount(e.getAmount())          // BigDecimal, no conversion
                        .categoryName(e.getName())      // Expense name as category
                        .date(e.getDate())
                        .build())
                .collect(Collectors.toList());

        // Total monthly spend
        BigDecimal totalSpent = monthlyExpenses.stream()
                .map(ExpenseDTO::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Top 5 spending categories
        String categorySummary = monthlyExpenses.stream()
                .collect(Collectors.groupingBy(ExpenseDTO::getCategoryName,
                        Collectors.reducing(BigDecimal.ZERO, ExpenseDTO::getAmount, BigDecimal::add)))
                .entrySet()
                .stream()
                .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
                .limit(5)
                .map(e -> String.format("%s (%.2f)", e.getKey(), e.getValue()))
                .collect(Collectors.joining(", "));

        String financialContext = String.format(
                "Total spent this month: %.2f\nTop 5 spending categories: %s",
                totalSpent, categorySummary
        );

        String prompt = """
                You are BudgetWise, a friendly financial advisor.
                Based on the user's spending data below, give 2-3 short, actionable, and encouraging tips.
                Do not be judgmental. Start each tip with a bullet point (*).

                User's spending data:
                %s
                """.formatted(financialContext);

        String llmResponse = chatClient.prompt()
                .user(prompt)
                .call()
                .content();

        assert llmResponse != null;
        return Arrays.stream(llmResponse.split("\\*"))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
    }

    /**
     * Full chatbot response with financial summary context
     */
//    public String getChatResponse(Long id, String userPrompt) {
//        LocalDate today = LocalDate.now();
//        LocalDate startOfMonth = today.withDayOfMonth(1);
//
//        // Total expenses this month
//        BigDecimal totalExpenses = expenseRepository.findByProfileIdAndDateBetween(id, startOfMonth, today)
//                .stream()
//                .map(e -> e.getAmount())
//                .reduce(BigDecimal.ZERO, BigDecimal::add);
//
//        // Total income this month
//        BigDecimal totalIncome = incomeRepository.findByProfileIdAndDateBetween(id, startOfMonth, today)
//                .stream()
//                .map(i -> i.getAmount())
//                .reduce(BigDecimal.ZERO, BigDecimal::add);
//
//        BigDecimal totalSaved = totalIncome.subtract(totalExpenses);
//
//        // Top 5 expense categories
//        String topCategories = expenseRepository.findByProfileIdAndDateBetween(id, startOfMonth, today)
//                .stream()
//                .collect(Collectors.groupingBy(e -> e.getName(),
//                        Collectors.reducing(BigDecimal.ZERO, e -> e.getAmount(), BigDecimal::add)))
//                .entrySet()
//                .stream()
//                .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
//                .limit(5)
//                .map(e -> String.format("%s (%s)", e.getKey(), formatINR(e.getValue())))
//                .collect(Collectors.joining(", "));
//
//        String financialContext = String.format(
//                """
//                Here is a summary of the user's financial data for the current month:
//                - Total Income: %.2f
//                - Total Expenses: %.2f
//                - Total Saved: %.2f
//                - Top 5 Spending Categories: %s
//                """,
//                totalIncome, totalExpenses, totalSaved, topCategories
//        );
////        String finalPrompt = String.format(
////                """
////                You are BudgetWise, a friendly, approachable, and insightful financial AI assistant.
////                Your goal is to provide **clear, concise, and encouraging advice** based on the user's financial data.
////
////                **Instructions for the Response:**
////                - Use **Markdown** formatting for readability.
////                - Use bullet points (`*`) for key insights.
////                - Highlight numbers, amounts, and categories in **bold**.
////                - Keep sentences **short, simple, and easy to understand**.
////                - Include relevant emojis like 💰, 📈, 💡, 🏦 to make advice engaging.
////
////
////                **User's Financial Data:**
////                %s
////
////                **User's Question / Prompt:**
////                "%s"
////                """,
////                financialContext,
////                userPrompt
////        );
//        String finalPrompt;
//
//        if(userPrompt.toLowerCase().matches(".*\\b(hi|hello|hey|how are you|greetings)\\b.*")) {
//            // Friendly casual response
//            finalPrompt = """
//        You are BudgetWise, a friendly and approachable financial AI assistant.
//        Respond to the user in a short, casual, and engaging way.
//        User's message: "%s"
//        """.formatted(userPrompt);
//        } else {
//            // Finance-related suggestions in concise, point form
//            finalPrompt = String.format(
//                    """
//                    You are BudgetWise, a friendly financial AI assistant.
//                    Provide **3 short, actionable suggestions** based on the user's financial data.
//                    Use bullet points (*), highlight numbers/categories in **bold**, and include emojis like 💰, 📈, 💡.
//
//                    User's Financial Data:
//                    %s
//
//                    User's Question:
//                    "%s"
//                    """,
//                    financialContext,
//                    userPrompt
//            );
//        }
//
//        return chatClient.prompt()
//                .user(finalPrompt)
//                .call()
//                .content();
//    }

    public String getChatResponse(Long id, String userPrompt) {
        LocalDate today = LocalDate.now();
        LocalDate startOfMonth = today.withDayOfMonth(1);

        // Total expenses this month
        BigDecimal totalExpenses = expenseRepository.findByProfileIdAndDateBetween(id, startOfMonth, today)
                .stream()
                .map(e -> e.getAmount())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Total income this month
        BigDecimal totalIncome = incomeRepository.findByProfileIdAndDateBetween(id, startOfMonth, today)
                .stream()
                .map(i -> i.getAmount())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalSaved = totalIncome.subtract(totalExpenses);

        // Top 5 expense categories
        String topCategories = expenseRepository.findByProfileIdAndDateBetween(id, startOfMonth, today)
                .stream()
                .collect(Collectors.groupingBy(e -> e.getName(),
                        Collectors.reducing(BigDecimal.ZERO, e -> e.getAmount(), BigDecimal::add)))
                .entrySet()
                .stream()
                .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
                .limit(5)
                .map(e -> String.format("%s (%s)", e.getKey(), formatINR(e.getValue())))
                .collect(Collectors.joining(", "));

        // Build financial context with INR formatting
        String financialContext = String.format(
                """
                - Total Income: %s
                - Total Expenses: %s
                - Total Saved: %s
                - Top 5 Spending Categories: %s
                """,
                formatINR(totalIncome),
                formatINR(totalExpenses),
                formatINR(totalSaved),
                topCategories
        );

        String finalPrompt;

        // Check for casual messages
        if(userPrompt.toLowerCase().matches(".*\\b(hi|hello|hey|how are you|greetings)\\b.*")) {
            finalPrompt = """
            You are BudgetWise, a friendly and approachable financial AI assistant.
            Respond in a short, casual, and engaging way.
            User's message: "%s"
            """.formatted(userPrompt);
        } else {
            // Finance-related messages: short, bullet-point suggestions
            finalPrompt = String.format(
                    """
                    You are BudgetWise, a friendly financial AI assistant.
                    Provide **3 short, actionable suggestions** based on the user's financial data.
                    - Use bullet points (*)
                    - Highlight numbers/categories in **bold**
                    - Include emojis like 💰, 📈, 💡
                    - Keep suggestions short (1-2 sentences max)
    
                    User's Financial Data:
                    %s
    
                    User's Question:
                    "%s"
                    """,
                    financialContext,
                    userPrompt
            );
        }

        return chatClient.prompt()
                .user(finalPrompt)
                .call()
                .content();
    }

}



