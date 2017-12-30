package com.example.user.cs496_002;


public class ListViewItem {
    private String name ;
    private String number ;
    private String email;
    private String link;

    public void setName(String name) {
        this.name = name ;
    }
    public void setNumber(String number) {
        this.number = number ;
    }
    public void setEmail(String email){this.email = email;}
    public void setLink(String link){this.link = link;}

    public String getName() {
        return this.name ;
    }
    public String getNumber() {
        return this.number ;
    }
    public String getEmail() { return this.email;}
    public String getLink() {return this.link;}
}


