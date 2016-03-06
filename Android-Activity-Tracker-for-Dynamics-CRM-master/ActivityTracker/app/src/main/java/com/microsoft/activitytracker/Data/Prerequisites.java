package com.microsoft.activitytracker.Data;

/**
 * Created by Bob on 2016-03-05.
 */
public class Prerequisites {

    public Boolean age;
    public Boolean commitment;
    public Boolean adjectives;
    public Boolean legal;

    public Boolean getAge() {
        return age;
    }

    public Boolean getCommitment() {
        return commitment;
    }

    public Boolean getAdjectives() {
        return adjectives;
    }

    public Boolean getLegal() {
        return legal;
    }

    public Prerequisites (Boolean age, Boolean commitment, Boolean adjectives, Boolean legal) {
        this.age = age;
        this.commitment = commitment;
        this.adjectives = adjectives;
        this.legal = legal;

    }


}
