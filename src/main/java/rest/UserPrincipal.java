package rest;

import entity.User;

import java.security.Principal;

public class UserPrincipal implements Principal {

    private String name;

    public UserPrincipal(User user) {
        String firstNameLetter = user.getFirstName().substring(0, 1);
        String lastNameLetter = user.getLastName().substring(0, 1);
        int ageTimesID = user.getAge() * user.getId();
        String name = firstNameLetter + lastNameLetter + ageTimesID;
        this.name = name;
    }

    public UserPrincipal(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

}
