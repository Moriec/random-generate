package com.vinogradov.config;

import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapperBuilder;
import freemarker.template.TemplateExceptionHandler;

import javax.servlet.ServletContext;

public class FreeMarkerConfig {
    private static Configuration cfg;

    public static Configuration getInstance(ServletContext servletContext) {
        if (cfg == null) {
            cfg = new Configuration(Configuration.VERSION_2_3_32);
            cfg.setServletContextForTemplateLoading(servletContext, "/WEB-INF/templates");
            cfg.setDefaultEncoding("UTF-8");
            cfg.setTemplateExceptionHandler(TemplateExceptionHandler.HTML_DEBUG_HANDLER);
            cfg.setLogTemplateExceptions(false);
            cfg.setWrapUncheckedExceptions(true);
            cfg.setFallbackOnNullLoopVariable(false);
            DefaultObjectWrapperBuilder builder = new DefaultObjectWrapperBuilder(Configuration.VERSION_2_3_32);
            builder.setExposeFields(true);
            cfg.setObjectWrapper(builder.build());
        }
        return cfg;
    }
}


