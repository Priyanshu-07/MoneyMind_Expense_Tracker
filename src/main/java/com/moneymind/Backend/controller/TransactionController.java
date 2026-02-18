package com.moneymind.Backend.controller;

import com.moneymind.Backend.dto.TransactionRequest;
import com.moneymind.Backend.entity.Transaction;
import com.moneymind.Backend.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @GetMapping
    public ResponseEntity<List<Transaction>> getAll(@AuthenticationPrincipal UserDetails user) {
        return ResponseEntity.ok(transactionService.getAllTransactions(user.getUsername()));
    }

    @PostMapping
    public ResponseEntity<Transaction> create(@AuthenticationPrincipal UserDetails user,
                                              @Valid @RequestBody TransactionRequest req) {
        return ResponseEntity.ok(transactionService.createTransaction(user.getUsername(), req));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Transaction> update(@AuthenticationPrincipal UserDetails user,
                                              @PathVariable Long id,
                                              @Valid @RequestBody TransactionRequest req) {
        return ResponseEntity.ok(transactionService.updateTransaction(user.getUsername(), id, req));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@AuthenticationPrincipal UserDetails user,
                                       @PathVariable Long id) {
        transactionService.deleteTransaction(user.getUsername(), id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/export/csv")
    public ResponseEntity<byte[]> exportToCsv(@AuthenticationPrincipal UserDetails user) {
        List<Transaction> transactions = transactionService.getAllTransactions(user.getUsername());

        StringBuilder csv = new StringBuilder();
        csv.append("Date,Type,Category,Description,Tags,Amount\n");

        for (Transaction tx : transactions) {
            csv.append(tx.getTransactionDate()).append(",");
            csv.append(tx.getType()).append(",");
            csv.append(tx.getCategory() != null ? tx.getCategory().getName() : "").append(",");
            csv.append(tx.getDescription() != null ? "\"" + tx.getDescription() + "\"" : "").append(",");
            csv.append(tx.getTags() != null ? "\"" + tx.getTags() + "\"" : "").append(",");
            csv.append(tx.getAmount()).append("\n");
        }

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=transactions.csv");
        headers.add("Content-Type", "text/csv");

        return ResponseEntity.ok()
                .headers(headers)
                .body(csv.toString().getBytes());
    }

    @GetMapping("/filter")
    public ResponseEntity<List<Transaction>> filter(
            @AuthenticationPrincipal UserDetails user,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) String search) {

        List<Transaction> all = transactionService.getAllTransactions(user.getUsername());

        return ResponseEntity.ok(all.stream()
                .filter(tx -> type == null || tx.getType().equals(type))
                .filter(tx -> categoryId == null || (tx.getCategory() != null && tx.getCategory().getId().equals(categoryId)))
                .filter(tx -> startDate == null || !tx.getTransactionDate().isBefore(LocalDate.parse(startDate)))
                .filter(tx -> endDate == null || !tx.getTransactionDate().isAfter(LocalDate.parse(endDate)))
                .filter(tx -> search == null || search.isEmpty() ||
                        (tx.getDescription() != null && tx.getDescription().toLowerCase().contains(search.toLowerCase())) ||
                        (tx.getTags() != null && tx.getTags().toLowerCase().contains(search.toLowerCase())))
                .toList());
    }
}