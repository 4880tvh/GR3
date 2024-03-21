package openerp.openerpresourceserver.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import openerp.openerpresourceserver.entity.Citizen;
import org.springframework.stereotype.Repository;

@Repository
public interface CitizenRepository extends JpaRepository<Citizen, Integer> {
}
