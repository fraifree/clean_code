
package com.example.clean_code.service;

import com.example.clean_code.model.Transaction;
import com.example.clean_code.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class TransactionService {
    @Value("${clean_code.transaction.batch}")
    private BigDecimal sum;

    private final TransactionRepository transactionRepository;

    @Transactional
    public void saveTransactionsBatch(List<Transaction> transactions) {
        if (transactions == null || transactions.isEmpty()) {
            log.warn("No transactions to process.");
            return;
        }

        transactions.stream()
                .filter(transaction -> transaction.getAmount().compareTo(sum) > 0)
                .forEach(this::processLargeTransaction);
        transactionRepository.saveTransactionsBatch(transactions);
    }

    @Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED)
    public List<Transaction> fetchTransactionsBatch(LocalDateTime startDate, LocalDateTime endDate) {
        return transactionRepository.fetchTransactions(startDate, endDate);
    }

    private void processLargeTransaction(Transaction transaction) {
        log.info("Processing large transaction: {}", transaction.getId());
    }
}
