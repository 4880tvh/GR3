package wms.algorithms.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TruckRouteElement {
    private String locationID;
    private String action; //
    private Node node;
}
