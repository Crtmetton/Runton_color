package com.colorrun.listener;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import com.colorrun.config.ThymeleafConfig;

@WebListener
public class ThymeleafInitializer implements ServletContextListener {
    
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ThymeleafConfig.init(sce.getServletContext());
    }
    
    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        // Nothing to do
    }
} 