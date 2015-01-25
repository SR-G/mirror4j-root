package org.tensin.mirror4j.model.operations;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;
import org.tensin.common.tools.documentation.updater.Description;
import org.tensin.mirror4j.Mirror4JException;
import org.tensin.mirror4j.bean.BeanField;
import org.tensin.mirror4j.model.events.IEvent;

/**
 * The Class HTTPExecution.
 */
@Root(name = "http")
@Description(value = "Activate a given url")
public class HTTPExecution extends AbstractOperation implements IOperation {

    /** Logger. */
    private static final Logger LOGGER = LogManager.getLogger();

    /** The command. */
    @Attribute
    @Description(value = "The url to activate", example = "http://domotique.myhome.com/")
    @BeanField
    private String url;

    /*
     * (non-Javadoc)
     * @see org.tensin.mirror4j.model.operations.IOperation#execute()
     */
    @Override
    public void execute(final IEvent event) throws Mirror4JException {
        LOGGER.info("Now activating URL [" + url + "]");

        BufferedReader in = null;
        try {
            final URL destUrl = new URL(url);
            in = new BufferedReader(new InputStreamReader(destUrl.openStream()));
            String inputLine;

            final StringBuilder sb = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                sb.append(inputLine).append("\n");
            }
            LOGGER.debug("Result is \n" + sb.toString());

        } catch (final MalformedURLException e) {
            throw new Mirror4JException("Error while activating [" + url + "]", e);
        } catch (final IOException e) {
            throw new Mirror4JException("Error while activating [" + url + "]", e);
        } finally {
            IOUtils.closeQuietly(in);
        }
    }

    /**
     * Gets the url.
     *
     * @return the url
     */
    public String getUrl() {
        return url;
    }

    /**
     * Sets the url.
     *
     * @param url
     *            the new url
     */
    public void setUrl(final String url) {
        this.url = url;
    }

    /*
     * (non-Javadoc)
     * @see org.tensin.mirror4j.model.operations.IOperation#start()
     */
    @Override
    public void start() throws Mirror4JException {

    }

    /*
     * (non-Javadoc)
     * @see org.tensin.mirror4j.model.operations.IOperation#stop()
     */
    @Override
    public void stop() throws Mirror4JException {

    }
}
