package at.qe.skeleton;

import at.qe.skeleton.configs.CustomServletContextInitializer;
import at.qe.skeleton.configs.WebSecurityConfig;
import javax.faces.webapp.FacesServlet;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;

/**
 * Spring boot application. Execute maven with <code>mvn spring-boot:run</code>
 * to start this web application.
 *
 * This class is part of the skeleton project provided for students of the
 * course "Software Architecture" offered by Innsbruck University.
*/
@SpringBootApplication
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class Main extends SpringBootServletInitializer {

    public static void main(String[] args) {
            SpringApplication.run(Main.class, args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(new Class[]{Main.class, CustomServletContextInitializer.class, WebSecurityConfig.class});
    }

    @Bean
    public ServletRegistrationBean<FacesServlet> servletRegistrationBean() {
            FacesServlet servlet = new FacesServlet();
            ServletRegistrationBean<FacesServlet> servletRegistrationBean = new ServletRegistrationBean<>(servlet,
                            "*.xhtml");
            servletRegistrationBean.setName("Faces Servlet");
            servletRegistrationBean.setAsyncSupported(true);
            servletRegistrationBean.setLoadOnStartup(1);
            return servletRegistrationBean;
    }
    
}
