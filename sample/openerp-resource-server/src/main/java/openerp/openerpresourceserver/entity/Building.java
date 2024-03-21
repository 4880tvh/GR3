//package openerp.openerpresourceserver.entity;
//
//import javax.persistence.Column;
//import javax.persistence.Entity;
//import javax.persistence.GeneratedValue;
//import javax.persistence.GenerationType;
//import javax.persistence.Id;
//import javax.persistence.OneToMany;
//import javax.persistence.Table;
//import java.util.List;
//
//@Entity
//@Table(name = "building")
//public class Building {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @Column(name = "building_id")
//    private Integer buildingId;
//
//    @Column(name = "code")
//    private String code;
//
//    @Column(name = "name")
//    private String name;
//
//    @Column(name = "num_floors")
//    private Integer numFloors;
//
//    @Column(name = "location")
//    private String location;
//
//    @OneToMany(mappedBy = "building")
//    private List<Apartment> apartments;
//
//    // Constructors, getters, and setters
//    public Integer getBuildingId() {
//        return buildingId;
//    }
//
//    public void setBuildingId(Integer buildingId) {
//        this.buildingId = buildingId;
//    }
//
//    public String getCode() {
//        return code;
//    }
//
//    public void setCode(String code) {
//        this.code = code;
//    }
//
//    public String getName() {
//        return name;
//    }
//
//    public void setName(String name) {
//        this.name = name;
//    }
//
//    public Integer getNumFloors() {
//        return numFloors;
//    }
//
//    public void setNumFloors(Integer numFloors) {
//        this.numFloors = numFloors;
//    }
//
//    public String getLocation() {
//        return location;
//    }
//
//    public void setLocation(String location) {
//        this.location = location;
//    }
//}
