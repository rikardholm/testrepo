package test;

import org.springframework.test.context.TestContext;
import org.springframework.test.context.TestExecutionListener;

import javax.jcr.Repository;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;

public class JcrTestExecutionListener implements TestExecutionListener {
    @Override
    public void beforeTestClass(TestContext testContext) throws Exception {
        Repository repository = testContext.getApplicationContext().getBean(Repository.class);

        Session session = repository.login(new SimpleCredentials("admin","admin".toCharArray()));
    }

    @Override
    public void prepareTestInstance(TestContext testContext) throws Exception {

    }

    @Override
    public void beforeTestMethod(TestContext testContext) throws Exception {

    }

    @Override
    public void afterTestMethod(TestContext testContext) throws Exception {

    }

    @Override
    public void afterTestClass(TestContext testContext) throws Exception {

    }
}
