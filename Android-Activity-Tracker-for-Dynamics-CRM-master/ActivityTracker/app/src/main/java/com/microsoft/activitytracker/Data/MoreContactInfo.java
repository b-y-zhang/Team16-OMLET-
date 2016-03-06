package com.microsoft.activitytracker.Data;

/**
 * Created by Bob on 2016-03-05.
 */
public class MoreContactInfo {
    public String address;
    public String city;
    public String postalCode;
    public String dateOfBirth;
    public String age;
    public String bornInCanada;

    public String getAddress() {
        return address;
    }

    public String getCity() {
        return city;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public String getAge() {
        return age;
    }

    public String getBornInCanada() {
        return bornInCanada;
    }

    public MoreContactInfo (String address, String city, String postalCode, String dateOfBirth,
                            String age, String bornInCanada) {
        this.address = address;
        this.city = city;
        this.postalCode = postalCode;
        this.dateOfBirth = dateOfBirth;
        this.age = age;
        this.bornInCanada = bornInCanada;
    }
}
