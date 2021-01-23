package org.fasttrackit.domain;

public class Card {
    private final String number;
    private String pin;

    public Card(String number, String pin){
        this.number = number;
        this.pin = pin;
    }

    public String getNumber() {
        return number;
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }
}
