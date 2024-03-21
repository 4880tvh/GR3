package openerp.openerpresourceserver.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import openerp.openerpresourceserver.entity.Citizen;
import openerp.openerpresourceserver.repo.CitizenRepository;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
@Log4j2
@AllArgsConstructor(onConstructor_ = @Autowired)
@Service
public class CitizenServiceImpl implements CitizenService {
    @Autowired
    private CitizenRepository citizenRepository;

    @Override
    public Citizen getCitizenById(Integer id) {
        return citizenRepository.findById(id).orElse(null);
    }

    // Implement other methods for CRUD operations
}
