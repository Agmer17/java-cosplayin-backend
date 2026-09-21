package cosplayin.app.security.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import cosplayin.app.core.authorization.UserRoles;
import cosplayin.app.core.exception.model.ForbiddenAccessExceptions;
import cosplayin.app.core.exception.model.UnauthorizedAccessExceptions;
import cosplayin.app.security.anot.RequireRole;
import cosplayin.app.security.context.UserCredentials;
import jakarta.servlet.http.HttpServletRequest;

@Aspect
@Component
public class RequireRolesAspect {

    @Before("(@annotation(cosplayin.app.security.anot.RequireRole) || " +
            "@within(cosplayin.app.security.anot.RequireRole)) && " +
            "@within(org.springframework.web.bind.annotation.RestController)")
    public void checkRole(JoinPoint joinPoint) {

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();

        RequireRole requireRole = signature.getMethod().getAnnotation(RequireRole.class);

        if (requireRole == null) {
            requireRole = joinPoint.getTarget()
                    .getClass()
                    .getAnnotation(RequireRole.class);
        }

        validate(requireRole);
    }

    private void validate(RequireRole requireRole) {

        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        if (attributes == null) {
            throw new IllegalStateException("No request context found");
        }

        HttpServletRequest request = attributes.getRequest();

        UserCredentials currentUser = (UserCredentials) request.getAttribute("credentials");

        if (currentUser == null) {
            throw new UnauthorizedAccessExceptions(
                    "you need to login to access this feature");
        }

        for (UserRoles role : requireRole.value()) {
            if (currentUser.getRole().equals(role)) {
                return;
            }
        }

        throw new ForbiddenAccessExceptions(
                "access to this feature is forbidden");
    }
}