package integration;

import freemarker.cache.NullCacheStorage;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import lab.inmemorytemplateloader.internal.InMemoryTemplateLoader;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class InMemoryTemplateLoaderTest {
    public static final String TEMPLATE_NAME = "asdf";
    private static Logger logger = LoggerFactory.getLogger(InMemoryTemplateLoaderTest.class);
    private final Configuration configuration = new Configuration();
    private final InMemoryTemplateLoader sut = new InMemoryTemplateLoader();
    private String processed;
    private final Map<String,Object> model = new HashMap<String, Object>();

    @Before
    public void setUp() throws Exception {
        configuration.setTemplateLoader(sut);
        configuration.setCacheStorage(new NullCacheStorage());
    }

    @Test(expected = FileNotFoundException.class)
    public void should_cause_freemarker_to_throw_a_FileNotFoundException_if_template_does_not_exist() throws Exception {
        configuration.getTemplate("DOES/NOT/EXIST");
    }

    @Test
    public void should_not_cause_freemarker_to_throw_a_FileNotFoundException_if_template_exists() throws Exception {
        sut.updateTemplate(TEMPLATE_NAME, "qwerqwrwer");
        configuration.getTemplate(TEMPLATE_NAME);
    }

    @Test
    public void template_should_be_processed_by_freemarker() throws Exception {
        String templateText = "a template åäö";
        sut.updateTemplate(TEMPLATE_NAME, templateText);

        processed = process(model);

        assertThat(processed, is(equalTo(templateText)));
    }

    @Test
    public void template_should_be_usable_by_freemarker() throws Exception {
        String templateText = "a template ${replaceMe} weeee";
        sut.updateTemplate(TEMPLATE_NAME, templateText);

        model.put("replaceMe", "replaced");

        processed = process(model);

        assertThat(processed, is(equalTo("a template replaced weeee")));
    }

    @Test
    public void should_return_unknown_lastModified_value() throws Exception {
        sut.updateTemplate(TEMPLATE_NAME, "asdf");

        Object templateSource = sut.findTemplateSource(TEMPLATE_NAME);

        long actual = sut.getLastModified(templateSource);

        assertThat(actual, is(equalTo(-1L)));
    }

    private String process(Object dataModel) throws TemplateException, IOException {
        Template template = configuration.getTemplate(TEMPLATE_NAME);

        StringWriter stringWriter = new StringWriter();

        template.process(dataModel, stringWriter);

        String processed = stringWriter.toString();

        logger.info("Output: " + processed);

        return processed;
    }
}
