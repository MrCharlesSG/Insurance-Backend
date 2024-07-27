package hr.algebra.insurancebackend.repository;

import hr.algebra.insurancebackend.domain.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {

    @Query("SELECT v FROM Vehicle v WHERE v.userInfo.id = :userInfoId")
    Optional<Vehicle> findByUserInfoId(@Param("userInfoId") long userInfoId);
    @Query("SELECT v FROM Vehicle v WHERE v.userInfo.username = :plate")
    Optional<Vehicle> findByPlate(@Param("plate") String plate);

}
