//package openerp.openerpresourceserver.controller;
//
//import lombok.AllArgsConstructor;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//import openerp.openerpresourceserver.entity.Services;
//import openerp.openerpresourceserver.service.ServicesService;
//import java.util.List;
//
//@RestController
//@AllArgsConstructor(onConstructor_ = @Autowired)
//@RequestMapping("/services")
//public class ServicesController {
//
//    private ServicesService serviceService;
//
//    @GetMapping("/get-all")
//    public ResponseEntity<?> getAllServices() {
//        List<Services> services = serviceService.getAllServices();
//        return ResponseEntity.ok().body(services);
//    }
//
//    @GetMapping("/get-by-id/{id}")
//    public ResponseEntity<Services> getServiceById(@PathVariable Integer id) {
//        Services service = serviceService.getServiceById(id);
//        return ResponseEntity.ok().body(service);
//    }
//
//    @PostMapping("/add")
//    public ResponseEntity<String> addService(@RequestBody Services service) {
//        serviceService.addService(service);
//        return ResponseEntity.ok("Service added successfully");
//    }
//
//    @DeleteMapping("/delete/{id}")
//    public ResponseEntity<String> deleteService(@PathVariable Integer id) {
//        serviceService.deleteServiceById(id);
//        return ResponseEntity.ok("Service deleted successfully");
//    }
//
//    @PutMapping("/update/{id}")
//    public ResponseEntity<String> updateServiceById(@PathVariable Integer id, @RequestBody Services updatedService) {
//        serviceService.updateServiceById(id, updatedService);
//        return ResponseEntity.ok("Service updated successfully");
//    }
//}
