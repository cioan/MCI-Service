package org.sharedhealth.mci.web.launch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.embedded.EmbeddedServletContainerFactory;
import org.springframework.boot.context.embedded.ServletContextInitializer;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.servlet.DispatcherServlet;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;
import java.util.Map;

import static java.lang.Integer.valueOf;
import static java.lang.System.getenv;
import static org.sharedhealth.mci.web.config.MCIConfig.getSupportedServletMappings;

@Configuration
@Import(WebMvcConfig.class)
public class Main {

    private static final String API_VERSION = "API_VERSION";
    private static final String IS_LATEST_API_VERSION = "IS_LATEST_API_VERSION";
    public static final String MCI_PORT = "MCI_PORT";

    @Bean
    public EmbeddedServletContainerFactory getFactory() {
        final Map<String, String> env = getenv();
        TomcatEmbeddedServletContainerFactory factory = new TomcatEmbeddedServletContainerFactory();
        factory.addInitializers(new ServletContextInitializer() {
            @Override
            public void onStartup(ServletContext servletContext) throws ServletException {

                ServletRegistration.Dynamic mci = servletContext.addServlet("mci", DispatcherServlet.class);

                mci.addMapping(getServletMappings(env));

                mci.setInitParameter("contextClass", "org.springframework.web.context.support" +
                        ".AnnotationConfigWebApplicationContext");
                mci.setInitParameter("contextConfigLocation", "org.sharedhealth.mci.web.launch.WebMvcConfig");
                mci.setAsyncSupported(true);

            }
        });

        String mci_port = env.get(MCI_PORT);
        factory.setPort(valueOf(mci_port));
        return factory;
    }

    private String[] getServletMappings(Map<String, String> env) {
        return getSupportedServletMappings(env.get(API_VERSION), Boolean.valueOf(env.get(IS_LATEST_API_VERSION)));
    }

    public static void main(String[] args) throws Exception {
        SpringApplication.run(Main.class, args);
    }
}
