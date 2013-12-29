package lab.stringtemplateloader.internal;


import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import lab.stringtemplateloader.TemplateConfigurationException;
import lab.stringtemplateloader.TemplateProcessor;
import lab.stringtemplateloader.TemplateRepository;
import org.joda.time.ReadableInstant;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;

public class FreemarkerTemplateProcessor implements TemplateRepository, TemplateProcessor {

    private Configuration configuration;
    private StringTemplateLoader stringTemplateLoader;

    public FreemarkerTemplateProcessor(Configuration configuration, StringTemplateLoader stringTemplateLoader) {
        this.configuration = configuration;
        this.stringTemplateLoader = stringTemplateLoader;
    }

    @Override
    public String process(String templateName, Map<String, Object> model) {
        Template template = getTemplate(templateName);

        return processTemplate(template, model);
    }

    private Template getTemplate(String templateName) {
        Template template;
        try {
            template = configuration.getTemplate(templateName);
        } catch (IOException e) {
            throw new TemplateConfigurationException("Unable to get template with name " + templateName, e);
        }
        return template;
    }

    private String processTemplate(Template template, Map<String, Object> model) {
        StringWriter stringWriter = new StringWriter();
        try {
            template.process(model, stringWriter);
        } catch (TemplateException e) {
            throw new TemplateConfigurationException("Unable to process template with name " + template.getName(), e);
        } catch (IOException e) {
            throw new TemplateConfigurationException("Unable to process template with name " + template.getName(), e);
        }
        return stringWriter.toString();
    }

    @Override
    public void putTemplate(String name, String content, ReadableInstant lastModified) {
        stringTemplateLoader.putTemplate(name, content, lastModified.getMillis());
        configuration.clearTemplateCache();
    }
}
