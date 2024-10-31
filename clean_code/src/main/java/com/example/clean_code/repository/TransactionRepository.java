
package com.example.clean_code.repository;

import com.example.clean_code.enums.Status;
import com.example.clean_code.model.Transaction;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Repository
@Slf4j
public class TransactionRepository {

    @Value("${spring.datasource.url}")
    private String jdbcUrl;
    @Value("${spring.datasource.username}")
    private String username;
    @Value("${spring.datasource.password}")
    private String password;
    @Value("${clean_code.transaction.batch}")
    private int batchSize;

    public void saveTransactionsBatch(List<Transaction> transactions) {
        String sql = "INSERT INTO transaction (amount, date, status) VALUES (?, ?, ?)";

        try (Connection connection = DriverManager.getConnection(jdbcUrl, username, password);
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            connection.setAutoCommit(false);

            int count = 0;

            for (Transaction transaction : transactions) {
                preparedStatement.setBigDecimal(1, transaction.getAmount());
                preparedStatement.setTimestamp(2, Timestamp.valueOf(transaction.getDate()));
                preparedStatement.setString(3, transaction.getStatus().name());
                preparedStatement.addBatch();
                count++;

                if (count % batchSize == 0) {
                    preparedStatement.executeBatch();
                    connection.commit();
                    log.info("Committed {} transactions.", count);
                }
            }

            preparedStatement.executeBatch();
            connection.commit();
            log.info("Committed remaining {} transactions.", count);

        } catch (SQLException e) {
            log.error("Error saving transactions batch: {}", e.getMessage(), e);

        }
    }

    public List<Transaction> fetchTransactions(LocalDateTime startDate, LocalDateTime endDate) {
        List<Transaction> transactions = new ArrayList<>();
        String sql = "SELECT * FROM transaction WHERE date >= ? AND date <= ? LIMIT 100";

        log.info("Fetching transactions from {} to {}", startDate, endDate);

        try (Connection connection = DriverManager.getConnection(jdbcUrl, username, password);
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setTimestamp(1, java.sql.Timestamp.valueOf(startDate));
            preparedStatement.setTimestamp(2, java.sql.Timestamp.valueOf(endDate));

            int batchCount = 0;
            while (true) {
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (!resultSet.next()) {
                        log.info("No more transactions found.");
                        break;
                    }

                    do {
                        Transaction transaction = mapRowToTransaction(resultSet);
                        transactions.add(transaction);
                        batchCount++;
                    } while (resultSet.next() && batchCount < batchSize);
                }

                log.info("Fetched {} transactions in this batch.", batchCount);

                if (batchCount < 100) {
                    log.info("All transactions fetched.");
                    break;
                }

                batchCount = 0;
            }
        } catch (SQLException e) {
            log.error("Error fetching transactions: {}", e.getMessage(), e);
        }

        return transactions;
    }

    private Transaction mapRowToTransaction(ResultSet resultSet) throws SQLException {
        Transaction transaction = new Transaction();
        transaction.setId(resultSet.getLong("id"));
        transaction.setAmount(resultSet.getBigDecimal("amount"));
        transaction.setDate(resultSet.getTimestamp("date").toLocalDateTime());
        transaction.setStatus(Status.valueOf(resultSet.getString("status")));
        transaction.setVersion(resultSet.getLong("version"));
        return transaction;
    }
}
