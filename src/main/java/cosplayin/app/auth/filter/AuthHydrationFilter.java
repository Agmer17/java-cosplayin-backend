package cosplayin.app.auth.filter;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import cosplayin.app.session.model.SessionAuthContext;
import cosplayin.app.session.service.SessionService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AuthHydrationFilter implements HandlerInterceptor {

    private final SessionService sessionService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {

        String accessToken = null;

        Cookie[] cookies = request.getCookies();

        if (cookies != null) {
            System.out.println("ngecek cookie : ");
            for (Cookie c : cookies) {
                if ("access_token".equals(c.getName())) {
                    accessToken = c.getValue();
                    System.out.println("dapet cookie : " + accessToken);

                }
            }
        }

        if (accessToken != null && !accessToken.trim().isEmpty()) {
            SessionAuthContext cred = sessionService.findSessionAndCreds(accessToken);

            if (cred != null) {
                request.setAttribute("session", cred.sessionData());
                request.setAttribute("credentials", cred.credentials());
            }

        }
        return true;
    }
}
