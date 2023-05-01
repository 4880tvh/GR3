package wms.service.vehicle;

import com.fasterxml.jackson.core.JsonProcessingException;
import wms.dto.ReturnPaginationDTO;
import wms.dto.product.ProductDTO;
import wms.dto.vehicle.DroneDTO;
import wms.dto.vehicle.TruckDTO;
import wms.entity.DroneEntity;
import wms.entity.ProductEntity;
import wms.entity.TruckEntity;
import wms.exception.CustomException;

public interface IVehicleService {
    TruckEntity createTruck(TruckDTO truckDTO) throws CustomException;
    ReturnPaginationDTO<TruckEntity> getAllTrucks(int page, int pageSize, String sortField, boolean isSortAsc) throws JsonProcessingException;
    TruckEntity getTruckById(long id);
    TruckEntity getTruckByCode(String code);
    TruckEntity updateTruck(TruckDTO truckDTO, long id) throws CustomException;
    void deleteTruckById(long id);
    DroneEntity createDrone(DroneDTO droneDTO) throws CustomException;
    ReturnPaginationDTO<DroneEntity> getAllDrones(int page, int pageSize, String sortField, boolean isSortAsc) throws JsonProcessingException;
    DroneEntity getDroneById(long id);
    DroneEntity getDroneByCode(String code);
    DroneEntity updateDrone(DroneDTO droneDTO, long id) throws CustomException;
    void deleteDroneById(long id);
}
