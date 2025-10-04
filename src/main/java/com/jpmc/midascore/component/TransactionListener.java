package com.jpmc.midascore.component;

import com.jpmc.midascore.foundation.Transaction;
import com.jpmc.midascore.service.TransactionService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class TransactionListener {

    private final TransactionService transactionService;

    public TransactionListener(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    // Primary JSON listener (matches KafkaProducer sending Transaction objects)
    @KafkaListener(topics = "${general.kafka-topic}", groupId = "midas-core-group", containerFactory = "kafkaListenerContainerFactory")
    public void consume(Transaction t) {
        if (t != null) {
            boolean ok = transactionService.process(t);
            System.out.println("JSON_LISTENER sender=" + t.getSenderId() + " recipient=" + t.getRecipientId() + " amount=" + t.getAmount() + " applied=" + ok);
        }
    }

    // Fallback: if anything comes through as raw text (defensive)
    @KafkaListener(topics = "${general.kafka-topic}", groupId = "midas-core-group-raw")
    public void consumeRaw(String line) {
        if (line == null || line.isBlank()) return;
        String[] parts = line.trim().split(",\\s*");
        if (parts.length >= 3) {
            try {
                Transaction t = new Transaction(Long.parseLong(parts[0]), Long.parseLong(parts[1]), Float.parseFloat(parts[2]));
                boolean ok = transactionService.process(t);
                System.out.println("RAW_LISTENER line=\"" + line + "\" applied=" + ok);
            } catch (Exception e) {
                System.out.println("RAW_PARSE_FAIL line=\"" + line + "\"");
            }
        }
    }
}