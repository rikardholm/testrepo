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
        Session session;
        try {
            session = repository.login();
        } catch (RepositoryException e) {
            throw new IOException(e);
        }

        try {
            if (!session.propertyExists("/" + name)) {
                logger.warn("Path does not exist: {}", "/" + name);
                return null;
            }
        } catch (RepositoryException e) {
            session.logout();
            throw new IOException(e);
        }

        Property property;
        try {
            property = session.getProperty("/" + name);
        } catch (RepositoryException e) {
            session.logout();
            throw new IOException(e);
        }

        return property;
    }

    @Override
    public long getLastModified(Object templateSource) {
        Property property = property(templateSource);

        try {
            Node node = property.getParent();
            return node.getProperty("jcr:lastModified").getDate().getTimeInMillis();
        } catch (RepositoryException e) {
            try {
                logger.debug("Could not get jcr:lastModified of " + property.getParent().getPath(), e);
            } catch (RepositoryException e1) {
                logger.debug("Could not get path of node", e1);
            }
        }

        return -1;
    }

    @Override
    public Reader getReader(Object templateSource, String encoding) throws IOException {
        try {
            return new StringReader(property(templateSource).getString());
        } catch (RepositoryException e) {
            throw new IOException(e);
        }
    }

    @Override
    public void closeTemplateSource(Object templateSource) throws IOException {
        Property property = property(templateSource);

        try {
            property.getSession().logout();
        } catch (RepositoryException e) {
            logger.warn("Could not close session.", e);
        }
    }

    private Property property(Object templateSource) {
        return (Property) templateSource;
    }
}
