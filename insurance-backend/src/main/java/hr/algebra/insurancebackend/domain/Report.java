package hr.algebra.insurancebackend.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "REPORT")
@Builder
public class Report {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private long id;

    @ManyToOne
    @JoinColumn(name = "info_report_driver_a", referencedColumnName = "ID")
    private InfoReportDriver infoReportDriverA;

    @ManyToOne
    @JoinColumn(name = "info_report_driver_b", referencedColumnName = "ID")
    private InfoReportDriver infoReportDriverB;

    @Column(name = "date")
    private Date date;

    @Column(name = "place")
    private String place;

    @Column(name = "details")
    private String details;
}

