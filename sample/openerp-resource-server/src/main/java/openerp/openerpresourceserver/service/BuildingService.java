package openerp.openerpresourceserver.service;

import openerp.openerpresourceserver.entity.Building;

import java.util.List;

public interface BuildingService {

    List<Building> getAllBuildings();

    Building getBuildingById(String id);

    void addBuilding(Building building);

    void deleteBuildingById(String id);

    void updateBuildingById(String id, Building updatedBuilding);
}
