package openerp.openerpresourceserver.service;

import openerp.openerpresourceserver.entity.Apartment;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public interface ApartmentService {

    Apartment getApartmentById(Integer id);
    // Other methods for CRUD operations
}

