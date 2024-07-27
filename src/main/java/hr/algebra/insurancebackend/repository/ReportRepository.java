package hr.algebra.insurancebackend.repository;

import hr.algebra.insurancebackend.domain.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReportRepository extends JpaRepository<Report, Long> {
    @Query("SELECT r FROM Report r " +
            "JOIN r.infoReportDriverA a " +
            "JOIN r.infoReportDriverB b " +
            "WHERE (a.vehicle.id = :vehicleId OR b.vehicle.id = :vehicleId) " +
            "AND (a.status = REJECTED OR b.status = REJECTED)")
    List<Report> findAllByVehicleIdAndRejected(@Param("vehicleId") Long vehicleId);

    @Query("SELECT r FROM Report r " +
            "JOIN r.infoReportDriverA a " +
            "JOIN r.infoReportDriverB b " +
            "WHERE (a.vehicle.id = :vehicleId OR b.vehicle.id = :vehicleId) " +
            "AND (a.status = ACCEPTED AND b.status = ACCEPTED)")
    List<Report> findAllByVehicleIdAndAccepted(@Param("vehicleId") Long vehicleId);
    @Query("SELECT r FROM Report r " +
            "JOIN r.infoReportDriverA a " +
            "JOIN r.infoReportDriverB b " +
            "WHERE (a.vehicle.id = :vehicleId OR b.vehicle.id = :vehicleId) " +
            "AND (a.status = WAITING OR b.status = WAITING)")
    List<Report> findAllByVehicleIdAndWaiting(@Param("vehicleId") Long vehicleId);

    @Query("SELECT r FROM Report r " +
            "JOIN r.infoReportDriverA a " +
            "JOIN r.infoReportDriverB b " +
            "WHERE a.vehicle.id = :vehicleId OR b.vehicle.id = :vehicleId")
    List<Report> findAllByVehicleId(@Param("vehicleId") Long vehicleId);

    @Query("SELECT COUNT(r) FROM Report r " +
            "JOIN r.infoReportDriverA a " +
            "JOIN r.infoReportDriverB b " +
            "WHERE a.status = REJECTED OR b.status = REJECTED")
    long countRejectedReports();

    @Query("SELECT COUNT(r) FROM Report r " +
            "JOIN r.infoReportDriverA a " +
            "JOIN r.infoReportDriverB b " +
            "WHERE a.status = ACCEPTED AND b.status = ACCEPTED")
    long countAcceptedReports();

    @Query("SELECT COUNT(r) FROM Report r " +
            "JOIN r.infoReportDriverA a " +
            "JOIN r.infoReportDriverB b " +
            "WHERE a.status = WAITING OR b.status = WAITING")
    long countWaitingReports();
}
