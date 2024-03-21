package openerp.openerpresourceserver.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import openerp.openerpresourceserver.entity.CitizenApartment;
import openerp.openerpresourceserver.service.CitizenApartmentService;
@RestController
@AllArgsConstructor(onConstructor_ = @Autowired)
@RequestMapping("/citizenapartments")
public class CitizenApartmentController {

    private CitizenApartmentService citizenApartmentService;

    @GetMapping("/{id}")
    public CitizenApartment getCitizenApartment(@PathVariable Integer id) {
        return citizenApartmentService.getCitizenApartmentById(id);
    }

    // Other REST endpoints for CRUD operations
}
