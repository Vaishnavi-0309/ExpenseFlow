package projects.springboot.expensetracker.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import projects.springboot.expensetracker.entity.Profile;

import java.util.Optional;

public interface ProfileRepository extends JpaRepository<Profile,Long> {

    Optional<Profile> findByEmail(String email);

    Optional<Profile> findByActivationToken(String activationToken);
}
