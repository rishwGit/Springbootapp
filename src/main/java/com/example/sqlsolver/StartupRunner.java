package com.example.sqlsolver;

import com.example.sqlsolver.dto.WebhookResponse;
import com.example.sqlsolver.service.WebhookService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class StartupRunner implements CommandLineRunner {

    private final WebhookService webhookService;

    @Value("${user.name}")
    private String name;

    @Value("${user.regNo}")
    private String regNo;

    @Value("${user.email}")
    private String email;

    public StartupRunner(WebhookService webhookService) {
        this.webhookService = webhookService;
    }

    @Override
    public void run(String... args) {
        System.out.println("Generating webhook for: " + name + " / " + regNo);

        WebhookResponse resp = webhookService.generateWebhook(name, regNo, email);
        if (resp == null || resp.getWebhook() == null || resp.getAccessToken() == null) {
            throw new RuntimeException("Invalid response from generateWebhook API");
        }

        String webhookUrl = resp.getWebhook();
        String accessToken = resp.getAccessToken();

        System.out.println("Webhook URL: " + webhookUrl);
        System.out.println("Token received.");

        // Final SQL Query for Question 1 (Odd regNo)
        String finalQuery = """
            SELECT p.amount AS SALARY,
                   CONCAT(e.first_name, ' ', e.last_name) AS NAME,
                   TIMESTAMPDIFF(YEAR, e.dob, CURDATE()) AS AGE,
                   d.department_name AS DEPARTMENT_NAME
            FROM payments p
            JOIN employee e ON p.emp_id = e.emp_id
            JOIN department d ON e.department = d.department_id
            WHERE DAY(p.payment_time) <> 1
              AND p.amount = (
                  SELECT MAX(amount) FROM payments WHERE DAY(payment_time) <> 1
              );
            """;

        webhookService.sendFinalQuery(webhookUrl, accessToken, finalQuery);
        System.out.println("Final SQL query submitted successfully!");
    }
}
