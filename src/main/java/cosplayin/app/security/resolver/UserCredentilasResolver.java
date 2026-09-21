package cosplayin.app.security.resolver;

import org.jspecify.annotations.Nullable;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import cosplayin.app.core.exception.model.UnauthorizedAccessExceptions;
import cosplayin.app.security.anot.CurrentUser;
import cosplayin.app.security.context.UserCredentials;
import jakarta.servlet.http.HttpServletRequest;

@Component
public class UserCredentilasResolver implements HandlerMethodArgumentResolver {

    @Override
    public @Nullable Object resolveArgument(MethodParameter parameter, @Nullable ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest, @Nullable WebDataBinderFactory binderFactory) throws Exception {

        HttpServletRequest request = (HttpServletRequest) webRequest.getNativeRequest();

        Object credObj = request.getAttribute("credentials");

        if (credObj == null) {
            throw new UnauthorizedAccessExceptions("please login before accessing this feature");
        }

        UserCredentials cred = (UserCredentials) credObj;
        System.out.println("is there any cred object " + cred.getId());

        System.out.println("CUURENT USER CREDENTIAL IN RESOLVER " + cred.getId());

        return cred;

    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(CurrentUser.class)
                && parameter.getParameterType().equals(UserCredentials.class);
    }

}
