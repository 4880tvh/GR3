package openerp.openerpresourceserver.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import openerp.openerpresourceserver.service.ApartmentService;
import openerp.openerpresourceserver.entity.Apartment;


@RestController
@AllArgsConstructor(onConstructor_ = @Autowired)
@RequestMapping("/apartments")
public class ApartmentController {

    private ApartmentService apartmentService;

    @GetMapping("/{id}")
    public Apartment getApartment(@PathVariable Integer id) {
        return apartmentService.getApartmentById(id);
    }

    // Other REST endpoints for CRUD operations
}
