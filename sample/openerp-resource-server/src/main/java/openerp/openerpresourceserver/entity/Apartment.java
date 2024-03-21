package openerp.openerpresourceserver.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "moa_apartments")
public class Apartment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer apartmentId;

    private String code;
    private String name;
    private Boolean isRented;
    private Boolean isUsingService;
    private Integer ownerId;
    private String description;

    // Getters and setters
}