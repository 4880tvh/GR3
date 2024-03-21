package openerp.openerpresourceserver.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import openerp.openerpresourceserver.entity.CitizenApartment;
import org.springframework.stereotype.Repository;

@Repository
public interface CitizenApartmentRepository extends JpaRepository<CitizenApartment, Integer> {
}