package com.semicolon.tadlaly.Models;

import java.io.Serializable;

public class BankModel implements Serializable {
    private String account_id;
    private String account_name;
    private String account_IBAN;
    private String account_bank_name;
    private String account_number;
    private String account_image;

    public String getAccount_id() {
        return account_id;
    }

    public String getAccount_name() {
        return account_name;
    }

    public String getAccount_IBAN() {
        return account_IBAN;
    }

    public String getAccount_bank_name() {
        return account_bank_name;
    }

    public String getAccount_number() {
        return account_number;
    }

    public String getAccount_image() {
        return account_image;
    }
}
