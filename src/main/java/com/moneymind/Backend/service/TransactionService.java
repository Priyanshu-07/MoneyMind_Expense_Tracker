package com.moneymind.Backend.service;

import com.moneymind.Backend.dto.TransactionRequest;
import com.moneymind.Backend.entity.*;
import com.moneymind.Backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    private User getUser(String email) {
        return userRepository.findByEmail(email).orElseThrow();
    }

    public Transaction createTransaction(String email, TransactionRequest req) {
        User user = getUser(email);
        Category category = req.getCategoryId() != null
                ? categoryRepository.findById(req.getCategoryId()).orElse(null)
                : null;

        Transaction tx = Transaction.builder()
                .amount(req.getAmount())
                .type(req.getType())
                .description(req.getDescription())
                .tags(req.getTags())
                .transactionDate(req.getTransactionDate())
                .user(user)
                .category(category)
                .build();

        return transactionRepository.save(tx);
    }

    public List<Transaction> getAllTransactions(String email) {
        User user = getUser(email);
        return transactionRepository.findByUserIdOrderByTransactionDateDesc(user.getId());
    }

    public Transaction updateTransaction(String email, Long id, TransactionRequest req) {
        Transaction tx = transactionRepository.findById(id).orElseThrow();
        if (!tx.getUser().getEmail().equals(email)) throw new RuntimeException("Unauthorized");

        Category category = req.getCategoryId() != null
                ? categoryRepository.findById(req.getCategoryId()).orElse(null)
                : null;

        tx.setAmount(req.getAmount());
        tx.setType(req.getType());
        tx.setDescription(req.getDescription());
        tx.setTags(req.getTags());
        tx.setTransactionDate(req.getTransactionDate());
        tx.setCategory(category);

        return transactionRepository.save(tx);
    }

    public void deleteTransaction(String email, Long id) {
        Transaction tx = transactionRepository.findById(id).orElseThrow();
        if (!tx.getUser().getEmail().equals(email)) throw new RuntimeException("Unauthorized");
        transactionRepository.delete(tx);
    }
}