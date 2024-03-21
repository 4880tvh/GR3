package openerp.openerpresourceserver.service;

import openerp.openerpresourceserver.entity.CitizenApartment;
import java.util.List;
import org.springframework.stereotype.Service;
@Service
public interface CitizenApartmentService {
    CitizenApartment getCitizenApartmentById(Integer id);
    // Other methods for CRUD operations
}
