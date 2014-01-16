package lab.jcrtemplateloader;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.junit.*;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import test.JcrSessionTestRule;

import javax.inject.Inject;
import javax.jcr.*;
import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
@TestExecutionListeners
public class JcrTemplateLoaderTest {
    private static Logger logger = LoggerFactory.getLogger(JcrTemplateLoaderTest.class);

    @Inject
    private Configuration configuration;

    @Inject
    private Repository repository;

    @Rule
    public JcrSessionTestRule jcrSessionTestRule = new JcrSessionTestRule(repository);

    private final Map<String,String> model = new HashMap<String, String>();
    private Session session;

    @Before
    public void setUp() throws Exception {
        session = login(repository);

        fillRepository(session);
    }

    @After
    public void tearDown() throws Exception {
        session.getNode("/templates").remove();
        session.save();
        session.logout();
    }

    @Test
    public void context_loads() throws Exception {
    }

    @Test
    public void should_replace_simple_text() throws Exception {
        model.put("template", "replaced template");

        Template template = configuration.getTemplate("templates/simple/content");

        String processed = process(template, model);

        assertThat(processed, is("This is a replaced template"));
    }

    @Test
    public void should_include_a_header() throws Exception {
        Template template = configuration.getTemplate("templates/including/content");

        String processed = process(template, model);

        assertThat(processed, is("This is the header. Should have the header."));
    }

    private void fillRepository(Session session) throws RepositoryException {
        Node rootNode = session.getRootNode();

        rootNode.addNode("templates");

        Node templateNode = rootNode.addNode("templates/simple");

        templateNode.setProperty("content", "This is a ${template}");

        Node header = rootNode.addNode("templates/header");
        header.setProperty("content","This is the header.");

        Node including = rootNode.addNode("templates/including");
        including.setProperty("content","<#include '/templates/header/content'> Should have the header.");

        session.save();
    }

    private String process(Template template, Map<String, String> model) throws TemplateException, IOException {
        StringWriter stringWriter = new StringWriter();

        template.process(model, stringWriter);

        String processed = stringWriter.toString();

        logger.info("Output: {}", processed);
        return processed;
    }

    private Session login(Repository repository) throws RepositoryException {
        return repository.login(new SimpleCredentials("admin", "admin".toCharArray()));
    }
}
