package lab.stringtemplateloader;

import org.joda.time.ReadableInstant;

public interface TemplateRepository {
     void putTemplate(String name, String content, ReadableInstant lastModified);
}
