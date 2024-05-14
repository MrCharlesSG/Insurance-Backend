package hr.algebra.insurancebackend.repository;

import hr.algebra.insurancebackend.domain.UserInfo;
import hr.algebra.insurancebackend.domain.Vehicle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {

    Optional<Vehicle> findByUserInfo(UserInfo userInfo);

}
