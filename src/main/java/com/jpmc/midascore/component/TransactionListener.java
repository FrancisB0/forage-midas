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

    @KafkaListener(
        topics = "${general.kafka-topic}",
        groupId = "midas-core-group",
        containerFactory = "kafkaListenerContainerFactory"
    )
    public void consume(Transaction transaction) {
        boolean ok = transactionService.process(transaction);
        System.out.println("Received transaction: " + transaction + " persisted=" + ok);
    }
}