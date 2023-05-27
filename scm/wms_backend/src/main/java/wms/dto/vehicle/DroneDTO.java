package wms.dto.vehicle;

import lombok.Data;
import org.springframework.security.core.parameters.P;

import javax.validation.constraints.Positive;
@Data
public class DroneDTO {
    @Positive(message = "Trong tai phai la so duong")
    double capacity;
    @Positive(message = "Toc do phai lon hon khong")
    double speed;
    @Positive(message = "Thoi gian hoat dong phai lon hon khong")
    double duration;
    @Positive(message = "Chi phi phai duong")
    double transportCostPerUnit;
    @Positive(message = "Phi cho doi phai duong")
    double waitingCost;
    String userManaged;
    String name;
}
