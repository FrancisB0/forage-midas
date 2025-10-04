package com.jpmc.midascore.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "transaction_record")
public class TransactionRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private float amount;

    @Column(nullable = false)
    private float incentive; 

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id")
    private UserRecord sender;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "recipient_id")
    private UserRecord recipient;

    protected TransactionRecord() {}

    public TransactionRecord(float amount, float incentive, UserRecord sender, UserRecord recipient) {
        this.amount = amount;
        this.incentive = incentive;
        this.sender = sender;
        this.recipient = recipient;
    }

    public Long getId() { return id; }
    public float getAmount() { return amount; }
    public float getIncentive() { return incentive; }
    public UserRecord getSender() { return sender; }
    public UserRecord getRecipient() { return recipient; }
}
