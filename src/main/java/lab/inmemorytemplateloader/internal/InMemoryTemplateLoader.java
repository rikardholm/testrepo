package lab.inmemorytemplateloader.internal;

import freemarker.cache.TemplateLoader;
import lab.inmemorytemplateloader.FreemarkerStringTemplateRepository;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

public class InMemoryTemplateLoader implements TemplateLoader, FreemarkerStringTemplateRepository {
    private Map<String, String> templates = new HashMap<String, String>();

    @Override
    public Object findTemplateSource(String name) throws IOException {
        return templates.get(name);
    }

    @Override
    public long getLastModified(Object templateSource) {
        return -1;
    }

    @Override
    public Reader getReader(Object templateSource, String encoding) throws IOException {
        return new StringReader((String) templateSource);
    }

    @Override
    public void closeTemplateSource(Object templateSource) throws IOException {
    }

    @Override
    public void updateTemplate(String name, String content) {
        templates.put(name, content);
    }
}
