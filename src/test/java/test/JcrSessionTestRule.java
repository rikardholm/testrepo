package test;

import org.junit.rules.ExternalResource;

import javax.jcr.Repository;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;

public class JcrSessionTestRule extends ExternalResource {
    private Session session;
    private Repository repository;


    public JcrSessionTestRule(Repository repository) {
        this.repository = repository;
    }

    @Override
    protected void before() throws Throwable {
        session = repository.login(new SimpleCredentials("admin", "admin".toCharArray()));
    }

    @Override
    protected void after() {
        session.logout();
    }


}
