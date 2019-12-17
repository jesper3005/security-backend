package dto;

import entity.User;
import java.io.Serializable;

public class UserDTO implements Serializable {

    private String email, phone, firstName, lastName, gender;
    private int age;

    public UserDTO(User user) {
        this.email = user.getEmail();
        this.phone = user.getPhone();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.age = user.getAge();
        this.gender = user.getGender();
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public int getAge() {
        return age;
    }

    public String getGender() {
        return gender;
    }

}
