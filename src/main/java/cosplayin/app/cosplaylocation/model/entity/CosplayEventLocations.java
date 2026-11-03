package cosplayin.app.cosplaylocation.model.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.locationtech.jts.geom.Point;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "cosplay_event_locations")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CosplayEventLocations {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "osm_id")
    private Long osmId;

    @Column(name = "osm_type", length = 20)
    private String osmType;

    @Column(nullable = false, length = 255)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String address;


    private String locationType;

    @Column(
            name = "location",
            columnDefinition = "geometry(Point,4326)"
    )
    private Point location;


    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

}
