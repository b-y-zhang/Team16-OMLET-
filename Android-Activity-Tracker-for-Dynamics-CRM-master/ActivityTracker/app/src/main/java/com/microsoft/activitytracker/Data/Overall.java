package com.microsoft.activitytracker.Data;

import com.microsoft.activitytracker.Models.Contact;

/**
 * Created by Bob on 2016-03-06.
 */
public class Overall {
    public String firstName;
    public String lastName;
    public String homePhone;
    public String cellPhone;
    public String email;
    public String altEmail;
    public String preferred;
    public String experiences;
    public String prevexp;
    public String littleSister;
    public String health;
    public String cultures;
    public String beliefsAboutChild;
    public String timeCommitment;
    public String changesInEdu;
    public String address;
    public String city;
    public String postalCode;
    public String dateOfBirth;
    public String age;
    public String bornInCanada;


    public Overall(String firstName, String lastName, String homePhone, String cellPhone,
                   String email, String altEmail, String preferred, String experiences,
                   String prevexp, String littleSister, String health, String cultures,
                   String beliefsAboutChild, String timeCommitment, String changesInEdu,
                   String address, String city, String postalCode, String dateOfBirth, String age,
                   String bornInCanada) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.homePhone = homePhone;
        this.cellPhone = cellPhone;
        this.email = email;
        this.altEmail = altEmail;
        this.preferred = preferred;
        this.experiences = experiences;
        this.prevexp = prevexp;
        this.littleSister = littleSister;
        this.health = health;
        this.cultures = cultures;
        this.beliefsAboutChild = beliefsAboutChild;
        this.timeCommitment = timeCommitment;
        this.changesInEdu = changesInEdu;
        this.address = address;
        this.city = city;
        this.postalCode = postalCode;
        this.dateOfBirth = dateOfBirth;
        this.age = age;
        this.bornInCanada = bornInCanada;

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

    public String getExperiences() {
        return experiences;
    }

    public String getPrevexp() {
        return prevexp;
    }

    public String getLittleSister() {
        return littleSister;
    }

    public String getHealth() {
        return health;
    }

    public String getCultures() {
        return cultures;
    }

    public String getBeliefsAboutChild() {
        return beliefsAboutChild;
    }

    public String getTimeCommitment() {
        return timeCommitment;
    }

    public String getChangesInEdu() {
        return changesInEdu;
    }

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
}
