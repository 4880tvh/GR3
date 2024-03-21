//package openerp.openerpresourceserver.entity;
//
//import jakarta.persistence.Column;
//import jakarta.persistence.Entity;
//import jakarta.persistence.Id;
//import jakarta.persistence.Table;
//import lombok.AllArgsConstructor;
//import lombok.Builder;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//import org.springframework.data.annotation.CreatedDate;
//import org.springframework.data.annotation.LastModifiedDate;
//import javax.persistence.*;
//import java.util.Date;
//import java.util.List;
//@Data
//@Builder
//@NoArgsConstructor
//@AllArgsConstructor
//@Entity
//@Table(name = "service_type")
//public class ServiceType {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @Column(name = "service_type_id")
//    private Integer serviceTypeId;
//
//    @Column(name = "description")
//    private String description;
//
//    @Column(name = "is_available")
//    private Boolean isAvailable;
//
//    // getters and setters
//}