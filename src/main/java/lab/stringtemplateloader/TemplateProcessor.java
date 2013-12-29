package lab.stringtemplateloader;

import java.util.Map;

public interface TemplateProcessor {
    String process(String templateName, Map<String, Object> model);
}
