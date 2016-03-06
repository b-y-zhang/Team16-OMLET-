package com.microsoft.activitytracker.Data;

/**
 * Created by Bob on 2016-03-05.
 */
public class ContactInformation {
    public String firstName;
    public String lastName;
    public String homePhone;
    public String cellPhone;
    public String email;
    public String altEmail;
    public String preferred;

    public ContactInformation (String firstName, String lastName, String homePhone, String cellPhone,
                               String email, String altEmail, String preferred) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.homePhone = homePhone;
        this.cellPhone = cellPhone;
        this.email = email;
        this.altEmail = altEmail;
        this.preferred = preferred;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getHomePhone() {
        return homePhone;
    }

    public String getCellPhone() {
        return cellPhone;
    }

    public String getEmail() {
        return email;
    }

    public String getAltEmail() {
        return altEmail;
    }

    public String getPreferred() {
        return preferred;
    }
}
