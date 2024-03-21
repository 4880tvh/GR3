package openerp.openerpresourceserver.service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import openerp.openerpresourceserver.entity.Apartment;
import openerp.openerpresourceserver.repo.ApartmentRepository;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
@Log4j2
@AllArgsConstructor(onConstructor_ = @Autowired)
@Service
public class ApartmentServiceImpl implements ApartmentService {
    @Autowired
    private ApartmentRepository apartmentRepository;

    @Override
    public Apartment getApartmentById(Integer id) {
        return apartmentRepository.findById(id).orElse(null);
    }

    // Implement other methods for CRUD operations
}
