package com.appsinventiv.yoolah.Models;

public class PhoneContactModel {
    String name,number;

    public PhoneContactModel(String name, String number) {
        this.name = name;
        this.number = number;
    }

    public PhoneContactModel() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }
}
