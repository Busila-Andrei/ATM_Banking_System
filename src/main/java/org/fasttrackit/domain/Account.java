package org.fasttrackit.domain;

public class Account {
    private final String iban;
    private double balance;
    private final Card card;

    public Account(String iban,Card card){
        this.balance = 0;
        this.iban = iban;
        this.card = card;
    }

    public Account(String iban,double balance, Card card){
        this.iban = iban;
        this.balance = balance;
        this.card= card;
    }

    public Card getCard() {
        return card;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public String getIban() {
        return iban;
    }
}
