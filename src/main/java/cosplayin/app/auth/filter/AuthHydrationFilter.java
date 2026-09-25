package cosplayin.app.auth.filter;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import cosplayin.app.security.context.UserCredentials;
import cosplayin.app.session.model.SessionAuthContext;
import cosplayin.app.session.service.SessionService;
import cosplayin.app.user.model.entity.Users;
import cosplayin.app.user.service.UsersService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AuthHydrationFilter implements HandlerInterceptor {

    private final SessionService sessionService;

    private final UsersService userService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {

        String accessToken = null;

        Cookie[] cookies = request.getCookies();

        if (cookies != null) {
            for (Cookie c : cookies) {
                if ("access_token".equals(c.getName())) {
                    accessToken = c.getValue();

                }
            }
        }

        if (accessToken != null && !accessToken.trim().isEmpty()) {
            SessionAuthContext ctx = sessionService.findSessionAndCreds(accessToken);

            if (ctx != null) {
                if (ctx.credentials() != null) {
                    request.setAttribute("session", ctx.sessionData());
                    request.setAttribute("credentials", ctx.credentials());
                } else {
                    Users user = userService.getUser(ctx.sessionData().getId());
                    if (user != null) {
                        UserCredentials credentials = new UserCredentials(user.getId(), user.getStatus(),
                                user.getRole());
                        request.setAttribute("credentials", credentials); // need to fix this later
                        request.setAttribute("session", ctx.sessionData());
                    }
                }
            }

        }
        return true;
    }
}
