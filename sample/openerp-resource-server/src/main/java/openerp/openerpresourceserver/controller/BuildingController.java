package openerp.openerpresourceserver.controller;
import lombok.AllArgsConstructor;
import openerp.openerpresourceserver.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import openerp.openerpresourceserver.entity.Building;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import openerp.openerpresourceserver.service.BuildingService;

@RestController
@AllArgsConstructor(onConstructor_ = @Autowired)
@RequestMapping("/buildings")
public class BuildingController {

    private BuildingService buildingService;

    @GetMapping("/get-all")
    public ResponseEntity<?> getAllBuildings() {
        List<Building> buildings = buildingService.getAllBuildings();
        return ResponseEntity.ok().body(buildings);

    }

    @GetMapping("/get-by-id/{id}")
    public ResponseEntity<Building> getBuildingById(@PathVariable String id) {
        Building building = buildingService.getBuildingById(id);
        return ResponseEntity.ok().body(building);
    }

    @PostMapping("/add")
    public ResponseEntity<String> addBuilding(@RequestBody Building building) {
        buildingService.addBuilding(building);
        return ResponseEntity.ok("Building added successfully");
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteBuilding(@PathVariable String id) {
        buildingService.deleteBuildingById(id);
        return ResponseEntity.ok("Building deleted successfully");
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<String> updateBuildingById(@PathVariable String id, @RequestBody Building updatedBuilding) {
        buildingService.updateBuildingById(id, updatedBuilding);
        return ResponseEntity.ok("Building updated successfully");
    }
}
