package fr.ub.m2gl;

import javax.jws.soap.SOAPBinding;
import java.rmi.server.UID;
import java.util.UUID;

public class User {

    private String ID;
    private String firstname;
    private String lastname;

    public String getFirstname() {
        return firstname;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getLastname() {
        return lastname;
    }

    public User(String ID, String firstname, String lastname) {
        this.ID = ID;
        this.firstname = firstname;
        this.lastname = lastname;
    }

    public User(String firstname, String lastname) {
        this.ID = UUID.randomUUID().toString();
        this.firstname = firstname;
        this.lastname = lastname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public User(){

    }
}
