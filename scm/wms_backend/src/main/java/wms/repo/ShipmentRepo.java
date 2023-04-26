package wms.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import wms.entity.Shipment;

public interface ShipmentRepo extends JpaRepository<Shipment, Long> {
    Shipment getShipmentById(long id);
    Shipment getShipmentByCode(String code);
    @Query(value = "select * from shipment", nativeQuery = true)
    Page<Shipment> search(Pageable pageable);
}
