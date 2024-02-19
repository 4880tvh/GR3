package openerp.openerpresourceserver.service;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import openerp.openerpresourceserver.entity.Building;
import openerp.openerpresourceserver.repo.BuildingRepo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Log4j2
@AllArgsConstructor(onConstructor_ = @Autowired)
@Service
public class BuildingServiceImpl implements BuildingService {

    private BuildingRepo buildingRepo;
    @Override
    public List<Building> getAllBuildings() {
        return buildingRepo.findAll();
    }

    @Override
    public Building getBuildingById(String id) {
        Optional<Building> building = buildingRepo.findById(id);

        if (building.isEmpty()) {
            throw new NoSuchElementException("Not exist user with id " + id);
        }
        return building.get();
    }

    @Override
    public void addBuilding(Building building) {

        if (buildingRepo.existsById(building.getId())) {
            throw new IllegalArgumentException("Building with ID " + building.getId() + " already exists");
        }

        buildingRepo.save(building);
    }

    @Override
    public void deleteBuildingById(String id) {
        // Kiểm tra xem building có tồn tại hay không
        if (!buildingRepo.existsById(id)) {
            throw new NoSuchElementException("Building with ID " + id + " does not exist");
        }

        buildingRepo.deleteById(id);
    }

    @Override
    public void updateBuildingById(String id, Building updatedBuilding) {
        Optional<Building> buildingOptional = buildingRepo.findById(id);

        if (buildingOptional.isEmpty()) {
            throw new NoSuchElementException("Building with ID " + id + " does not exist");
        }

        Building existingBuilding = buildingOptional.get();
        existingBuilding.setName(updatedBuilding.getName());
        existingBuilding.setFloors(updatedBuilding.getFloors());
        existingBuilding.setLocation(updatedBuilding.getLocation());

        buildingRepo.save(existingBuilding);
    }
}

