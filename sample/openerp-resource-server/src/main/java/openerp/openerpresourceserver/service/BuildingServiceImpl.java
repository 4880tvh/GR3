//package openerp.openerpresourceserver.service;
//
//import lombok.AllArgsConstructor;
//import lombok.extern.log4j.Log4j2;
//import openerp.openerpresourceserver.entity.Building;
//import openerp.openerpresourceserver.repo.BuildingRepo;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//import java.util.NoSuchElementException;
//import java.util.Optional;
//
//@Log4j2
//@AllArgsConstructor(onConstructor_ = @Autowired)
//@Service
//public class BuildingServiceImpl implements BuildingService {
//
//    private BuildingRepo buildingRepository;
//
//    @Override
//    public List<Building> getAllBuildings() {
//        return buildingRepository.findAll();
//    }
//
//    @Override
//    public Building getBuildingById(Integer buildingId) {
//        Optional<Building> building = buildingRepository.findById(String.valueOf(buildingId));
//        if (building.isEmpty()) {
//            throw new NoSuchElementException("Building with ID " + buildingId + " does not exist");
//        }
//        return building.get();
//    }
//
//    @Override
//    public void addBuilding(Building building) {
//        buildingRepository.save(building);
//    }
//
//    @Override
//    public void deleteBuildingById(Integer buildingId) {
//        if (!buildingRepository.existsById(String.valueOf(buildingId))) {
//            throw new NoSuchElementException("Building with ID " + buildingId + " does not exist");
//        }
//        buildingRepository.deleteById(String.valueOf(buildingId));
//    }
//
//    @Override
//    public void updateBuildingById(Integer buildingId, Building updatedBuilding) {
//        Optional<Building> buildingOptional = buildingRepository.findById(String.valueOf(buildingId));
//        if (buildingOptional.isEmpty()) {
//            throw new NoSuchElementException("Building with ID " + buildingId + " does not exist");
//        }
//        Building existingBuilding = buildingOptional.get();
//        existingBuilding.setCode(updatedBuilding.getCode());
//        existingBuilding.setName(updatedBuilding.getName());
//        existingBuilding.setNumFloors(updatedBuilding.getNumFloors());
//        existingBuilding.setLocation(updatedBuilding.getLocation());
//        buildingRepository.save(existingBuilding);
//    }
//}