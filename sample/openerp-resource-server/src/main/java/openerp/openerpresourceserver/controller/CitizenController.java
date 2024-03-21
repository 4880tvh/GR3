package openerp.openerpresourceserver.controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import openerp.openerpresourceserver.service.CitizenService;
import openerp.openerpresourceserver.entity.Citizen;
@RestController
@AllArgsConstructor(onConstructor_ = @Autowired)
@RequestMapping("/citizens")
public class CitizenController {

    private CitizenService citizenService;

    @GetMapping("/{id}")
    public Citizen getCitizen(@PathVariable Integer id) {
        return citizenService.getCitizenById(id);
    }

    // Other REST endpoints for CRUD operations
}
