package integration;

import freemarker.cache.NullCacheStorage;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

public class TemplateTest {
    public static final String TEMPLATE_NAME = "asdf";
    private static Logger logger = LoggerFactory.getLogger(TemplateTest.class);
    private final Configuration configuration = new Configuration();
    private Template template;
    private String processed;
    private final Map<String,Object> model = new HashMap<String, Object>();

    @Before
    public void setUp() throws Exception {
        configuration.clearEncodingMap();
        configuration.clearSharedVariables();
        configuration.setDefaultEncoding("UTF-8");
        configuration.setCacheStorage(new NullCacheStorage());
    }

    @Test
    public void template_should_be_processed_by_freemarker() throws Exception {
        String templateText = "a template åäö";

        template = new Template(TEMPLATE_NAME, templateText, configuration);

        processed = process(template, model);

        assertThat(processed, is(equalTo(templateText)));
    }

    @Test
    public void template_should_be_usable_by_freemarker() throws Exception {
        String templateText = "a template ${replaceMe} weeee";

        template = new Template(TEMPLATE_NAME, templateText, configuration);

        model.put("replaceMe", "replaced");

        processed = process(template, model);

        assertThat(processed, is(equalTo("a template replaced weeee")));
    }

    private String process(Template template, Object dataModel) throws IOException, TemplateException {
        StringWriter stringWriter = new StringWriter();

        template.process(dataModel, stringWriter);

        String processed = stringWriter.toString();

        logger.info("Output: " + processed);

        return processed;
    }
}
