package com.manish.photoapp.photoappusersservice.model;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class CreateUserModel {

    @NotNull(message = "first name cannot be null")
    @Size(min = 3, message = "first name should have at least 3 characters")
    private String firstName;

    @NotNull(message = "first name cannot be null")
    @Size(min = 3, message = "last name should have at least 3 characters")
    private String lastName;

    @NotNull(message = "first name cannot be null")
    @Size(min = 8, max = 16, message = "password length must be min 8 and max 16 characters")
    private String password;

    @NotNull(message = "first name cannot be null")
    @Email
    private String email;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
