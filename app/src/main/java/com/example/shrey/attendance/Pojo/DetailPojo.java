package com.example.shrey.attendance.Pojo;
/**
 * Created by Shrey on 5/27/2018.
 */

public class DetailPojo {
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String username;
    public String password;
    public String confirmpasssword;
    public String designation;
    public String age;
    public String gender;
    public String currentUser;
    public String mobilenumber;
    public String date;
    public String key;
    public String value;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String time;

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String email;
    public String leaves;

    public DetailPojo() {

    }

    public DetailPojo(String name, String username, String password, String confirmpasssword, String designation, String age, String gender, String email, String mobilenumber) {
        this.name = name;
        this.username = username;
        this.password = password;
        this.confirmpasssword = confirmpasssword;
        this.age = age;
        this.gender = gender;
        this.mobilenumber = mobilenumber;
        this.designation = designation;
        this.email = email;
    }

    public DetailPojo(String name, String designation, String email, String mobilenumber, String age, String date, String time, String value) {
        this.name = name;
        this.age = age;
        this.mobilenumber = mobilenumber;
        this.designation = designation;
        this.email = email;
        this.date = date;
        this.time = time;
        this.value = value;

    }

    public DetailPojo(String date, String time, String value) {
        this.date = date;
        this.time = time;
        this.value = value;
    }
//    public DetailPojo(String key,String value)
//    {
//        this.key=key;
//        this.value=value;
//    }

    public String getLeaves() {
        return leaves;
    }

    public void setLeaves(String leaves) {
        this.leaves = leaves;
    }

    public String getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(String currentUser) {
        this.currentUser = currentUser;
    }


    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getMobilenumber() {
        return mobilenumber;
    }

    public void setMobilenumber(String mobilenumber) {
        this.mobilenumber = mobilenumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirmpasssword() {
        return confirmpasssword;
    }

    public void setConfirmpasssword(String confirmpasssword) {
        this.confirmpasssword = confirmpasssword;
    }

}