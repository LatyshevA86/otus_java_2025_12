package ru.otus.server;

import com.google.gson.Gson;
import java.util.Arrays;
import org.eclipse.jetty.ee10.servlet.FilterHolder;
import org.eclipse.jetty.ee10.servlet.ServletContextHandler;
import org.eclipse.jetty.ee10.servlet.ServletHolder;
import org.eclipse.jetty.server.Handler;
import ru.otus.crm.service.DBServiceClient;
import ru.otus.services.TemplateProcessor;
import ru.otus.services.UserAuthService;
import ru.otus.servlet.AuthorizationFilter;
import ru.otus.servlet.LoginAdminServlet;

public class AdminWebServerWithFilterBasedSecurity extends AdminWebServerSimple {

    private final UserAuthService authService;

    public AdminWebServerWithFilterBasedSecurity(
            int port,
            UserAuthService authService,
            DBServiceClient dbServiceClient,
            Gson gson,
            TemplateProcessor templateProcessor
    ) {
        super(port, dbServiceClient, gson, templateProcessor);
        this.authService = authService;
    }

    @Override
    protected Handler applySecurity(ServletContextHandler servletContextHandler, String... paths) {
        servletContextHandler.addServlet(
                new ServletHolder(new LoginAdminServlet(templateProcessor, authService)), "/login");

        AuthorizationFilter authorizationFilter = new AuthorizationFilter();

        Arrays.stream(paths)
                .forEachOrdered(
                        path -> servletContextHandler.addFilter(new FilterHolder(authorizationFilter), path, null));

        return servletContextHandler;
    }
}
