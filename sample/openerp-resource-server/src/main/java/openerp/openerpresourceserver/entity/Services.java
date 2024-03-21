//package openerp.openerpresourceserver.entity;
//
//import javax.persistence.Column;
//import javax.persistence.Entity;
//import javax.persistence.GeneratedValue;
//import javax.persistence.GenerationType;
//import javax.persistence.Id;
//import javax.persistence.OneToOne;
//import javax.persistence.Table;
//
//@Entity
//@Table(name = "services")
//public class Services {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @Column(name = "service_id")
//    private Integer serviceId;
//
//    @Column(name = "service_name")
//    private String serviceName;
//
//    @Column(name = "service_type")
//    private Integer serviceType;
//
//    @OneToOne(mappedBy = "service")
//    private Apartment apartment;
//
//    @Column(name = "schedules")
//    private String schedules;
//
//    @Column(name = "user_id")
//    private Integer userId;
//
//    @Column(name = "apartment_id")
//    private Integer apartmentId;
//    // Constructors, getters, and setters
//    public Integer getServiceId() {
//        return serviceId;
//    }
//
//    public void setServiceId(Integer serviceId) {
//        this.serviceId = serviceId;
//    }
//
//    public String getServiceName() {
//        return serviceName;
//    }
//
//    public void setServiceName(String serviceName) {
//        this.serviceName = serviceName;
//    }
//
//    public Integer getServiceType() {
//        return serviceType;
//    }
//
//    public void setServiceType(Integer serviceType) {
//        this.serviceType = serviceType;
//    }
//
//    public Integer getUserId() {
//        return userId;
//    }
//
//    public void setUserId(Integer userId) {
//        this.userId = userId;
//    }
//
//    public Integer getApartmentId() {
//        return apartmentId;
//    }
//
//    public void setApartmentId(Integer apartmentId) {
//        this.apartmentId = apartmentId;
//    }
//
//    public String getSchedules() {
//        return schedules;
//    }
//
//    public void setSchedules(String schedules) {
//        this.schedules = schedules;
//    }
//}
