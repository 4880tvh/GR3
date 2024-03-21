//package openerp.openerpresourceserver.controller;
//import lombok.AllArgsConstructor;
//import openerp.openerpresourceserver.entity.User;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//import openerp.openerpresourceserver.entity.Building;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Optional;
//import openerp.openerpresourceserver.service.BuildingService;
//
//@RestController
//@AllArgsConstructor(onConstructor_ = @Autowired)
//@RequestMapping("/buildings")
//public class BuildingController {
//
//    private BuildingService buildingService;
//
//    @GetMapping("/get-all")
//    public ResponseEntity<?> getAllBuildings() {
//        List<Building> buildings = buildingService.getAllBuildings();
//        return ResponseEntity.ok().body(buildings);
//    }
//
//    @GetMapping("/get-by-id/{id}")
//    public ResponseEntity<Building> getBuildingById(@PathVariable String id) {
//        // Chuyển đổi id từ String sang Integer
//        Integer buildingId = Integer.parseInt(id);
//        Building building = buildingService.getBuildingById(buildingId);
//        return ResponseEntity.ok().body(building);
//    }
//
//    @PostMapping("/add")
//    public ResponseEntity<String> addBuilding(@RequestBody Building building) {
//        buildingService.addBuilding(building);
//        return ResponseEntity.ok("Building added successfully");
//    }
//
//    @DeleteMapping("/delete/{id}")
//    public ResponseEntity<String> deleteBuilding(@PathVariable String id) {
//        // Chuyển đổi id từ String sang Integer
//        Integer buildingId = Integer.parseInt(id);
//        buildingService.deleteBuildingById(buildingId);
//        return ResponseEntity.ok("Building deleted successfully");
//    }
//
//    @PutMapping("/update/{id}")
//    public ResponseEntity<String> updateBuildingById(@PathVariable String id, @RequestBody Building updatedBuilding) {
//        // Chuyển đổi id từ String sang Integer
//        Integer buildingId = Integer.parseInt(id);
//        buildingService.updateBuildingById(buildingId, updatedBuilding);
//        return ResponseEntity.ok("Building updated successfully");
//    }
//}
