package com.example.clean_code.service;

import com.example.clean_code.model.Transaction;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionAsyncService {

    private final TransactionService transactionService;

    @Async
    public CompletableFuture<List<Transaction>> findTransactionsAsync(LocalDateTime from, LocalDateTime to) {
        try {
            return (CompletableFuture<List<Transaction>>) transactionService.fetchTransactionsBatch(from, to);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    @Async
    public void saveTransactionBatchAsync(List<Transaction> transactions) {
        transactionService.saveTransactionsBatch(transactions);
    }
}
