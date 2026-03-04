package net.bjmsw.uvm.util;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;

import java.io.StringWriter;
import java.util.Map;

/**
 * Utility class providing methods for rendering templates using the FreeMarker library.
 * This class is configured to use FreeMarker version 2.3.31 with specific settings,
 * such as UTF-8 encoding and handling of template exceptions.
 */
public class TemplateUtil {
    private static Configuration cfg;

    static {
        // Initialize FreeMarker once
        cfg = new Configuration(Configuration.VERSION_2_3_31);
        cfg.setClassForTemplateLoading(TemplateUtil.class, "/templates"); // Or your specific path
        cfg.setDefaultEncoding("UTF-8");
        cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        cfg.setLogTemplateExceptions(false);
        cfg.setWrapUncheckedExceptions(true);
        cfg.setFallbackOnNullLoopVariable(false);
    }

    public static String render(String templatePath, Map<String, Object> model) throws Exception {
        Template temp = cfg.getTemplate(templatePath);
        try (StringWriter out = new StringWriter()) {
            temp.process(model, out);
            return out.toString();
        }
    }
}
