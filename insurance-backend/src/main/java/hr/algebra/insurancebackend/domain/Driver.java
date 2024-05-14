package hr.algebra.insurancebackend.domain;

import hr.algebra.insurancebackend.dto.DriverDTO;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.Set;

@Entity
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "DRIVER")
@Builder
public class Driver {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private long id;
    @Column(name = "name")
    private String name;

    @Column(name = "surnames")
    private String surnames;

    @Column(name = "passport")
    private String passport;

    @Column(name = "email")
    private String email;

    @Column(name = "birthday")
    private Date birthday;

    @ManyToMany(mappedBy = "drivers")
    private Set<Vehicle> vehicles;

    public Driver(DriverDTO driverDTO) {
        this.birthday = driverDTO.getBirthday();
        this.passport = driverDTO.getPassport();
        this.name = driverDTO.getName();
        this.email = driverDTO.getEmail();
        this.surnames = driverDTO.getSurnames();
        this.id = driverDTO.getId();
    }


}
