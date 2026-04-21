package com.talentstack.api.model;

/**
 * UserProfile maps user_profiles and holds person-name profile details.
 *
 * It shares the same primary key as User via @MapsId, forming a one-to-one extension of
 * account data for profile-specific fields.
 */

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Lob;

@Entity
@Table(name = "user_profiles")
public class UserProfile {

    @Id
    @Column(name = "user_id")
    private Long userId;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "first_name", nullable = false, length = 80)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 80)
    private String lastName;

    @Column(name = "age", nullable = false)
    private Integer age;

    @Column(name = "key_words", length = 45)
    private String keyWords;

    @Column(name = "location", length = 45)
    private String location;

    @Column(name = "distance")
    private Integer distance;

    @Column(name = "salary_min")
    private Integer salaryMin;

    @Column(name = "salary_max")
    private Integer salaryMax;

    @Column(name = "contract_type", length = 45)
    private String contractType;

    @Column(name = "max_days_old")
    private Integer maxDaysOld;

    @Column(name = "profile_strength")
    private Integer profileStrength;

    @Column(name = "about", length = 500)
    private String about;

    @Column(name = "skills", length = 500)
    private String skills;

    @Column(name = "tagline", length = 45)
    private String tagline;

    @Column(name = "applications_num")
    private Integer applicationsNum;

    @Column(name = "interviews_num")
    private Integer interviewsNum;

    @Lob
    @Column(name = "profile_photo", columnDefinition = "MEDIUMBLOB")
    private byte[] profilePhoto;

    public Long getUserId() {
        return userId;
    }

    public User getUser() {
        return user;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public Integer getAge() { return age;}

    public String getKeyWords() {
        return keyWords;
    }

    public String getLocation() {
        return location;
    }

    public Integer getDistance() {
        return distance;
    }

    public Integer getSalaryMin() {
        return salaryMin;
    }

    public Integer getSalaryMax() {
        return salaryMax;
    }

    public String getContractType() {
        return contractType;
    }

    public Integer getMaxDaysOld() {
        return maxDaysOld;
    }

    public Integer getProfileStrength() {return profileStrength;}

    public String getAbout() {return about;}

    public String getSkills() {return skills;}

    public String getTagline() {return tagline;}

    public Integer getApplicationsNum() {return applicationsNum;}

    public Integer getInterviewsNum() {return interviewsNum;}

    public byte[] getProfilePhoto() {return profilePhoto;}

    public void setUser(User user) {
        this.user = user;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setAge(Integer age) {this.age = age;}

    public void setKeyWords(String keyWords) {
        this.keyWords = keyWords;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setDistance(Integer distance) {
        this.distance = distance;
    }

    public void setSalaryMin(Integer salaryMin) {
        this.salaryMin = salaryMin;
    }

    public void setSalaryMax(Integer salaryMax) {
        this.salaryMax = salaryMax;
    }

    public void setContractType(String contractType) {
        this.contractType = contractType;
    }

    public void setMaxDaysOld(Integer maxDaysOld) {
        this.maxDaysOld = maxDaysOld;
    }

    public  void setProfileStrength(Integer profileStrength) {this.profileStrength = profileStrength;}

    public void setAbout(String about) {this.about = about;}

    public void setSkills(String skills) {this.skills = skills;}

    public void setTagline(String tagline) {this.tagline = tagline;}

    public void setApplicationsNum(Integer applicationsNum) {this.applicationsNum = applicationsNum;}

    public void setInterviewsNum(Integer interviewsNum) {this.interviewsNum = interviewsNum;}

    public void setProfilePhoto(byte[] profilePhoto) {this.profilePhoto = profilePhoto;}
}