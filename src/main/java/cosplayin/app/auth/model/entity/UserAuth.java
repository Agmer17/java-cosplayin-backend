package cosplayin.app.auth.model.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import cosplayin.app.auth.model.type.AuthProvider;
import cosplayin.app.user.model.entity.Users;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Table(name = "user_auths", uniqueConstraints = {
        @UniqueConstraint(name = "users_auth_provider_openid_key", columnNames = { "provider", "provider_openid" }),
        @UniqueConstraint(name = "users_email_provider", columnNames = { "email", "provider" })
})
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class UserAuth {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Users user;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private AuthProvider provider;

    @Column(nullable = false)
    private String providerOpenId;

    @Column(nullable = false)
    private String email;

    @CreationTimestamp
    private LocalDateTime createdAt;
}
