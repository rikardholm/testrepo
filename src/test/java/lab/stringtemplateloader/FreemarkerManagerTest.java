package lab.stringtemplateloader;

import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration()
public class FreemarkerManagerTest {

    public static final String CONTENT_WITHOUT_DIRECTIVES = "template content";
    public static final String TEMPLATE_NAME = "asdf";
    public static final DateTime LAST_MODIFIED = DateTime.parse("2013-12-29T00:39");

    @Inject
    private TemplateProcessor target;

    @Inject
    private TemplateRepository templateRepository;

    private Map<String, Object> model = new HashMap<String, Object>();

    String result;

    @Test
    public void applicationContext_should_load() throws Exception {
    }

    @Test
    public void should_process_template() throws Exception {
        templateRepository.putTemplate(TEMPLATE_NAME, CONTENT_WITHOUT_DIRECTIVES, LAST_MODIFIED);

        result = target.process(TEMPLATE_NAME, model);

        assertThat(result, is(equalTo(CONTENT_WITHOUT_DIRECTIVES)));
    }

    @Test
    public void should_process_template_with_directives() throws Exception {
        templateRepository.putTemplate(TEMPLATE_NAME, "Replace ${replaceMe} please.", LAST_MODIFIED);

        model.put("replaceMe", "KA-BLOOM");

        result = target.process(TEMPLATE_NAME, model);

        assertThat(result, is(equalTo("Replace KA-BLOOM please.")));
    }

    @Test
    public void should_be_able_to_update_template() throws Exception {
        String templateName = "qwerty";

        templateRepository.putTemplate(templateName, "First version", LAST_MODIFIED);

        target.process(templateName, model);

        templateRepository.putTemplate(templateName, "Second version", LAST_MODIFIED.plusMinutes(10));

        result = target.process(templateName, model);

        assertThat(result, is(equalTo("Second version")));
    }

    @Test
    public void should_support_including_of_Templates() throws Exception {
        templateRepository.putTemplate("templateA", "Content from A", LAST_MODIFIED);

        target.process("templateA", model);

        templateRepository.putTemplate("templateB", "<#include 'templateA'> with content from B", LAST_MODIFIED.plusMinutes(10));

        result = target.process("templateB", model);

        assertThat(result, is(equalTo("Content from A with content from B")));
    }

    @Test(expected = TemplateConfigurationException.class)
    public void should_throw_TemplateConfigurationException_if_template_is_unprocessable() throws Exception {
        templateRepository.putTemplate(TEMPLATE_NAME, "Content with ${badVariable}", LAST_MODIFIED);

        result = target.process(TEMPLATE_NAME, model);
    }
}
