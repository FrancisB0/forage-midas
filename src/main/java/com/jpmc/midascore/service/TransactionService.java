package com.jpmc.midascore.service;

import com.jpmc.midascore.foundation.Transaction;
import com.jpmc.midascore.repository.UserRepository;
import com.jpmc.midascore.repository.TransactionRepository;
import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.entity.TransactionRecord;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TransactionService {
    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;

    public TransactionService(UserRepository userRepository,
                              TransactionRepository transactionRepository) {
        this.userRepository = userRepository;
        this.transactionRepository = transactionRepository;
    }

    @Transactional
    public boolean process(Transaction t) {
        if (t == null) return false;
        float amount = t.getAmount();
        if (amount <= 0.0f) return false;
        long senderId = t.getSenderId();
        long recipientId = t.getRecipientId();
        if (senderId == recipientId) return false;

        UserRecord sender = userRepository.findById(senderId);
        if (sender == null) return false;

        UserRecord recipient = userRepository.findById(recipientId);
        if (recipient == null) return false;

        if (sender.getBalance() < amount) return false;

        sender.setBalance(sender.getBalance() - amount);
        recipient.setBalance(recipient.getBalance() + amount);

        TransactionRecord rec = new TransactionRecord(amount, sender, recipient);
        userRepository.save(sender);
        userRepository.save(recipient);
        transactionRepository.save(rec);
        return true;
    }
}
