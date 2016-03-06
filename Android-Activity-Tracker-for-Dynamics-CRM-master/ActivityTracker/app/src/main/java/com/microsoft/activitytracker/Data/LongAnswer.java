package com.microsoft.activitytracker.Data;

/**
 * Created by Bob on 2016-03-05.
 */
public class LongAnswer {
    public String experiences;
    public String prevexp;
    public String littleSister;
    public String health;
    public String cultures;
    public String beliefsAboutChild;
    public String timeCommitment;
    public String changesInEdu;

    public LongAnswer (String experiences, String prevexp, String littleSister, String health,
                       String cultures, String beliefsAboutChild, String timeCommitment, String changesInEdu) {
        this.experiences = experiences;
        this.prevexp = prevexp;
        this.littleSister = littleSister;
        this.health = health;
        this.cultures = cultures;
        this.beliefsAboutChild = beliefsAboutChild;
        this.timeCommitment = timeCommitment;
        this.changesInEdu = changesInEdu;
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
}
