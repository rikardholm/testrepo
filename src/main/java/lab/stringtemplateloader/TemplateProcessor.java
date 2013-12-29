package lab.stringtemplateloader;

import java.util.Map;

public interface TemplateProcessor {
    /**
     *
     * @param templateName
     * @param model
     * @return
     * @throws TemplateConfigurationException if unable to process due to missing templates or unparseable content.
     */
    String process(String templateName, Map<String, Object> model);
}
