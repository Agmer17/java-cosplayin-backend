package cosplayin.app.cosplayevent.model.entity;

import cosplayin.app.cosplaylocation.model.entity.CosplayEventLocations;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "cosplay_events")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CosplayEvents {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private String mediaUrl;

    @Column(columnDefinition = "TEXT")
    private String eventInformationUrl;

    @Column(nullable = false)
    private LocalDateTime startAt;

    @Column(nullable = false)
    private LocalDateTime endAt;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "event_location_id", nullable = false)
    private CosplayEventLocations location;

    @CreationTimestamp
    private LocalDateTime createdAt;
}
