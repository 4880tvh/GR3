package openerp.openerpresourceserver.service;

import openerp.openerpresourceserver.entity.Citizen;
import java.util.List;
import org.springframework.stereotype.Service;
@Service
public interface CitizenService {
    Citizen getCitizenById(Integer id);
    // Other methods for CRUD operations
}
