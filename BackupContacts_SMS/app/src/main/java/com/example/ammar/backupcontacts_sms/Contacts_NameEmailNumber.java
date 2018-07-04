package com.example.ammar.backupcontacts_sms;

/**
 * Created by ammar on 3/8/16.
 */
public class Contacts_NameEmailNumber {

    String name, email, number;

    public Contacts_NameEmailNumber(String name, String email, String number) {
        // TODO Auto-generated constructor stub
        this.name = name;
        this.email = email;
        this.number = number;
    }

    public Contacts_NameEmailNumber(){

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }
}
