package hr.algebra.insurancebackend.repository;

import hr.algebra.insurancebackend.domain.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RolesRepository extends JpaRepository<UserRole, Long> {
    Optional<UserRole> findByName(String name);
}
