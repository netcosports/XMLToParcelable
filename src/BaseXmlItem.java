import org.xml.sax.Attributes;

/**
 * Created by stephane on 26/03/14.
 */
public abstract class BaseXmlItem {

    public void close()
    {
        isClosed = true;
    }


    public boolean isClosed = false;
    public boolean isClosed()
    {
        return isClosed;
    }


    public abstract void addField(String name, String value);


    public abstract void addField(String name, Attributes attributes);
}
