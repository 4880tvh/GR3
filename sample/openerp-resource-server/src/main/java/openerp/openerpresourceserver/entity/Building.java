package openerp.openerpresourceserver.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "building")


public class Building {
    @Id
    @Column(name = "id", updatable = false, nullable = false)
    private String id;
    private String name;
    private int floors;
    private String location;

    // constructors, getters, and setters

    // You may want to add appropriate constructors, getters, and setters here.
}