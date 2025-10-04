package com.jpmc.midascore.service;

import com.jpmc.midascore.foundation.Transaction;
import com.jpmc.midascore.foundation.Incentive;
import com.jpmc.midascore.repository.UserRepository;
import com.jpmc.midascore.repository.TransactionRepository;
import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.entity.TransactionRecord;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

@Service
public class TransactionService {
    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;
    private final RestTemplate restTemplate;

    private static final String INCENTIVE_URL = "http://localhost:8080/incentive";

    public TransactionService(UserRepository userRepository,
                              TransactionRepository transactionRepository,
                              RestTemplate restTemplate) {
        this.userRepository = userRepository;
        this.transactionRepository = transactionRepository;
        this.restTemplate = restTemplate;
    }

    @Transactional
    public boolean process(Transaction t) {
        boolean ok = internalProcess(t); // extract existing logic into internalProcess(...) or inline below
        System.out.println("TX_PROCESS sender=" + (t==null?null:t.getSenderId()) +
                " recipient=" + (t==null?null:t.getRecipientId()) +
                " amount=" + (t==null?null:t.getAmount()) +
                " result=" + ok);
        return ok;
    }

    private boolean internalProcess(Transaction t) {
        if (t == null) return false;
        float amount = t.getAmount();
        if (amount <= 0) return false;
        long senderId = t.getSenderId();
        long recipientId = t.getRecipientId();
        if (senderId == recipientId) return false;

        UserRecord sender = userRepository.findById(senderId);
        if (sender == null) return false;
        UserRecord recipient = userRepository.findById(recipientId);
        if (recipient == null) return false;

        if (sender.getBalance() < amount) return false;


        sender.setBalance(sender.getBalance() - amount);


        float incentiveAmt = 0f;
        try {
            Incentive inc = restTemplate.postForObject(INCENTIVE_URL, t, Incentive.class);
            if (inc != null && inc.getAmount() >= 0) {
                incentiveAmt = inc.getAmount();
            }
        } catch (Exception e) {

        }


        recipient.setBalance(recipient.getBalance() + amount + incentiveAmt);

        TransactionRecord rec = new TransactionRecord(amount, incentiveAmt, sender, recipient);
        userRepository.save(sender);
        userRepository.save(recipient);
        transactionRepository.save(rec);
        return true;
    }
}
