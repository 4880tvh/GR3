package openerp.openerpresourceserver.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.util.List;
import java.util.Date;
@Entity
@Table(name = "moa_citizens")
public class Citizen {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer userId;

    private String name;
    private Integer age;
    private Date dob;
    private String address;
    private String email;
    private String phone;
    private String NIN;
    private Integer apartmentCode;

    // Getters and setters
}