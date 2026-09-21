package cosplayin.app.security.aspect;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import cosplayin.app.core.exception.model.UnauthorizedAccessExceptions;
import cosplayin.app.session.model.SessionDataModel;
import jakarta.servlet.http.HttpServletRequest;

@Aspect
@Component
public class RequireAuthAspect {

    @Before("(@annotation(cosplayin.app.security.anot.RequireAuth) ||" +
            "@within(cosplayin.app.security.anot.RequireAuth)) && @within(org.springframework.web.bind.annotation.RestController)")
    public void authenticated() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        if (attributes == null) {
            throw new IllegalStateException("No request context found");
        }

        HttpServletRequest request = attributes.getRequest();

        SessionDataModel id = (SessionDataModel) request.getAttribute("session");

        if (id == null) {
            throw new UnauthorizedAccessExceptions("you need to login to access this features");
        }

    }
}
