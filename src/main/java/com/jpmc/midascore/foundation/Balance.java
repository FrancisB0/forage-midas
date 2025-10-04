package com.jpmc.midascore.foundation;

public class Balance {
    private long userId;
    private float balance;

    public Balance() {}

    public Balance(long userId, float balance) {
        this.userId = userId;
        this.balance = balance;
    }

    public long getUserId() { return userId; }
    public float getBalance() { return balance; }

    public void setUserId(long userId) { this.userId = userId; }
    public void setBalance(float balance) { this.balance = balance; }

    @Override
    public String toString() {
        return "Balance{userId=" + userId + ", balance=" + balance + "}";
    }
}