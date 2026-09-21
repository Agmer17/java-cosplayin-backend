package cosplayin.app.security.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import cosplayin.app.core.authorization.UserStatus;
import cosplayin.app.core.exception.model.ForbiddenAccessExceptions;
import cosplayin.app.core.exception.model.UnauthorizedAccessExceptions;
import cosplayin.app.security.anot.RequireUserStatus;
import cosplayin.app.security.context.UserCredentials;
import jakarta.servlet.http.HttpServletRequest;

@Aspect
@Component
public class RequireUserStatusAspect {

    @Before("(@annotation(cosplayin.app.security.anot.RequireUserStatus) || " +
            "@within(cosplayin.app.security.anot.RequireUserStatus)) && " +
            "@within(org.springframework.web.bind.annotation.RestController)")
    public void validateStatus(JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();

        RequireUserStatus allowedStatus = signature.getMethod().getAnnotation(RequireUserStatus.class);

        if (allowedStatus == null) {
            allowedStatus = joinPoint.getTarget()
                    .getClass()
                    .getAnnotation(RequireUserStatus.class);
        }

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

        for (UserStatus st : allowedStatus.value()) {
            if (currentUser.getStatus().equals(st)) {
                return;
            }
        }

        throw new ForbiddenAccessExceptions(
                "access to this feature is forbidden");
    }

}
