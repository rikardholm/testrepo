package lab.jcrtemplateloader;

import freemarker.cache.TemplateLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.*;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

public class JcrTemplateLoader implements TemplateLoader {
    private static Logger logger = LoggerFactory.getLogger(JcrTemplateLoader.class);

    private Repository repository;

    public JcrTemplateLoader(Repository repository) {
        this.repository = repository;
    }

    @Override
    public Object findTemplateSource(String name) throws IOException {
        Session session = login();

        try {
            String absolutePath = "/" + name;

            if (!session.propertyExists(absolutePath)) {
                logger.warn("Path does not exist: {}", absolutePath);
                return null;
            }

            return absolutePath;
        } catch (RepositoryException e) {
            throw new IOException(e);
        } finally {
            session.logout();
        }
    }

    @Override
    public long getLastModified(Object templateSource) {
        String path = (String) templateSource;

        Session session;
        try {
            session = login();
        } catch (IOException e) {
            logger.info("Could not login", e);
            return -1;
        }

        Property property;
        try {
            property = session.getProperty(path);
        } catch (RepositoryException e) {
            logger.info("Could not get property " + templateSource, e);
            session.logout();
            return -1;
        }

        try {
            Node node = property.getParent();

            if (!node.hasProperty("jcr:lastModified")) {
                logger.warn("Node does not have property jcr:lastModified. Path: {}", node.getPath());
                return -1;
            }

            return node.getProperty("jcr:lastModified").getDate().getTimeInMillis();
        } catch (RepositoryException e) {
            logger.info("Could not get jcr:lastModified of " + path, e);
        } finally {
            session.logout();
        }

        return -1;
    }

    @Override
    public Reader getReader(Object templateSource, String encoding) throws IOException {
        String path = (String) templateSource;

        Session session = login();

        try {
            return new StringReader(session.getProperty(path).getString());
        } catch (RepositoryException e) {
            throw new IOException(e);
        } finally {
            session.logout();
        }
    }

    @Override
    public void closeTemplateSource(Object templateSource) throws IOException {
    }

    private Session login() throws IOException {
        Session session;
        try {
            session = repository.login();
        } catch (RepositoryException e) {
            throw new IOException(e);
        }
        return session;
    }
}
