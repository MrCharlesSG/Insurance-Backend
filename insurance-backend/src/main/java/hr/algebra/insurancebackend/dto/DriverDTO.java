package hr.algebra.insurancebackend.dto;

import hr.algebra.insurancebackend.domain.Driver;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DriverDTO {

    private long id;
    private String name;

    private String surnames;

    private String passport;

    private String email;

    private Date birthday;

    public DriverDTO(Driver driver) {
        this.birthday = driver.getBirthday();
        this.passport = driver.getPassport();
        this.name = driver.getName();
        this.email = driver.getEmail();
        this.surnames = driver.getSurnames();
        this.id = driver.getId();
    }
}
