package cosplayin.app.auth.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import cosplayin.app.auth.model.entity.UserAuth;
import cosplayin.app.auth.model.projection.AuthCredentialsProjections;
import cosplayin.app.auth.model.type.AuthProvider;

public interface AuthRepository extends JpaRepository<UserAuth, UUID> {

    Optional<UserAuth> findByProviderAndProviderOpenId(
            AuthProvider provider,
            String providerOpenId);

    @Query("""
                select
                    u.id as userId,
                    ua.provider as provider,
                    u.status as status,
                    u.role as userRoles
                from UserAuth ua
                join ua.user u
                where ua.provider = :provider
                  and ua.providerOpenId = :providerOpenId
            """)
    Optional<AuthCredentialsProjections> findCredentialsByProviderAndProviderOpenId(
            @Param("provider") AuthProvider provider,
            @Param("providerOpenId") String providerOpenId);
}