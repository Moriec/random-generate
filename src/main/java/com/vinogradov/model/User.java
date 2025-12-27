package com.vinogradov.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;

@JsonIgnoreProperties(ignoreUnknown = true)
public class User {
    @JsonProperty("gender")
    private String gender;
    
    private Name name;
    private Location location;
    private String email;
    private Login login;
    private Dob dob;
    private Registered registered;
    private String phone;
    private String cell;
    private Picture picture;
    
    @JsonProperty("nat")
    private String nationality;

    // Getters and Setters
    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Name getName() {
        return name;
    }

    public void setName(Name name) {
        this.name = name;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Login getLogin() {
        return login;
    }

    public void setLogin(Login login) {
        this.login = login;
    }

    public Dob getDob() {
        return dob;
    }

    public void setDob(Dob dob) {
        this.dob = dob;
    }

    public Registered getRegistered() {
        return registered;
    }

    public void setRegistered(Registered registered) {
        this.registered = registered;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCell() {
        return cell;
    }

    public void setCell(String cell) {
        this.cell = cell;
    }

    public Picture getPicture() {
        return picture;
    }

    public void setPicture(Picture picture) {
        this.picture = picture;
    }

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    // Helper methods
    public String getFullName() {
        if (name != null) {
            return name.getTitle() + " " + name.getFirst() + " " + name.getLast();
        }
        return "";
    }

    public String getAvatar() {
        if (picture != null) {
            return picture.getLarge();
        }
        return "";
    }

    public String getAddress() {
        if (location != null) {
            return location.getStreet().getNumber() + " " + location.getStreet().getName() + 
                   ", " + location.getCity() + ", " + location.getState() + ", " + location.getCountry() + 
                   " " + location.getPostcode();
        }
        return "";
    }

    public String getBirthDate() {
        if (dob != null && dob.getDate() != null) {
            try {
                LocalDate date = LocalDate.parse(dob.getDate().substring(0, 10));
                return date.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
            } catch (Exception e) {
                return dob.getDate();
            }
        }
        return "";
    }

    public int getAge() {
        if (dob != null && dob.getDate() != null) {
            try {
                LocalDate birthDate = LocalDate.parse(dob.getDate().substring(0, 10));
                return Period.between(birthDate, LocalDate.now()).getYears();
            } catch (Exception e) {
                return 0;
            }
        }
        return 0;
    }

    public String getCountry() {
        if (location != null) {
            return location.getCountry();
        }
        return "";
    }

    // Inner classes for JSON deserialization
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Name {
        private String title;
        private String first;
        private String last;

        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getFirst() { return first; }
        public void setFirst(String first) { this.first = first; }
        public String getLast() { return last; }
        public void setLast(String last) { this.last = last; }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Location {
        private Street street;
        private String city;
        private String state;
        private String country;
        private String postcode;

        public Street getStreet() { return street; }
        public void setStreet(Street street) { this.street = street; }
        public String getCity() { return city; }
        public void setCity(String city) { this.city = city; }
        public String getState() { return state; }
        public void setState(String state) { this.state = state; }
        public String getCountry() { return country; }
        public void setCountry(String country) { this.country = country; }
        public String getPostcode() { return postcode; }
        public void setPostcode(String postcode) { this.postcode = postcode; }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Street {
        private int number;
        private String name;

        public int getNumber() { return number; }
        public void setNumber(int number) { this.number = number; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Login {
        private String uuid;
        private String username;
        private String password;

        public String getUuid() { return uuid; }
        public void setUuid(String uuid) { this.uuid = uuid; }
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Dob {
        private String date;
        private int age;

        public String getDate() { return date; }
        public void setDate(String date) { this.date = date; }
        public int getAge() { return age; }
        public void setAge(int age) { this.age = age; }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Registered {
        private String date;
        private int age;

        public String getDate() { return date; }
        public void setDate(String date) { this.date = date; }
        public int getAge() { return age; }
        public void setAge(int age) { this.age = age; }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Picture {
        private String large;
        private String medium;
        private String thumbnail;

        public String getLarge() { return large; }
        public void setLarge(String large) { this.large = large; }
        public String getMedium() { return medium; }
        public void setMedium(String medium) { this.medium = medium; }
        public String getThumbnail() { return thumbnail; }
        public void setThumbnail(String thumbnail) { this.thumbnail = thumbnail; }
    }

}

