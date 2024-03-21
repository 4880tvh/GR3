package openerp.openerpresourceserver.service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import openerp.openerpresourceserver.entity.CitizenApartment;
import openerp.openerpresourceserver.service.CitizenApartmentService;
import openerp.openerpresourceserver.repo.CitizenApartmentRepository;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
@Log4j2
@AllArgsConstructor(onConstructor_ = @Autowired)
@Service
public class CitizenApartmentServiceImpl implements CitizenApartmentService {
    @Autowired
    private CitizenApartmentRepository citizenApartmentRepository;

    @Override
    public CitizenApartment getCitizenApartmentById(Integer id) {
        return citizenApartmentRepository.findById(id).orElse(null);
    }

    // Implement other methods for CRUD operations
}
