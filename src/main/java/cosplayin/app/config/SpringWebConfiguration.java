package cosplayin.app.config;

import java.util.List;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import cosplayin.app.auth.filter.AuthHydrationFilter;
import cosplayin.app.security.resolver.UserCredentilasResolver;
import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class SpringWebConfiguration implements WebMvcConfigurer {
    private final AuthHydrationFilter hydrationFilter;
    private final UserCredentilasResolver credentilasResolver;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(hydrationFilter)
                .addPathPatterns("/**")
                .excludePathPatterns(
                        "/api/uploads/public/**",
                        "/css/**",
                        "/js/**",
                        "/images/**",
                        "/favicon.ico");
        ;
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(credentilasResolver);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/api/uploads/public/**")
                .addResourceLocations("file:uploads/public/");
    }
}
