package hr.algebra.insurancebackend.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "INFO_REPORT_DRIVER")
@Builder
public class InfoReportDriver {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private long id;

    @ManyToOne
    @JoinColumn(name = "vehicle_id", referencedColumnName = "ID")
    private Vehicle vehicle;

    @ManyToOne
    @JoinColumn(name = "driver_id", referencedColumnName = "ID")
    private Driver driver;

    @Column(name = "damages")
    private String damages;

    @Column(name = "accepted")
    private boolean accepted;
}

