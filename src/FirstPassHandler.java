import com.sun.org.apache.xpath.internal.operations.Bool;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.HashMap;
import java.util.HashSet;


/**
 * Created by stephane on 26/03/14.
 */
public class FirstPassHandler extends DefaultHandler {


    Boolean currentElement = false;
    String currentValue = "";
    HashMap<String, Boolean> lists = new HashMap<String, Boolean>();
    int currentLevel = 0;

    String closedName;

    public HashMap<String, Boolean> getResult()
    {
        return lists;
    }


    public FirstPassHandler() {
    }

    // Called when tag starts
    @Override
    public void startElement(String uri, String localName, String qName,
                             Attributes attributes) throws SAXException {

        currentLevel++;
        String name = null;
        if (localName != null && localName.length() != 0) {
            name = localName;
        }
        else if (qName != null && qName.length() != 0) {
            name = qName;
        }

        if(name != null) {
            if(name.equals(closedName)) {
                String key = Util.getKey(name, currentLevel);
                lists.put(key, Boolean.TRUE);
            }
        }

    }



    // Called when tag closing
    @Override
    public void endElement(String uri, String localName, String qName)
            throws SAXException {
        currentLevel--;
        String name = null;
        if (localName != null && localName.length() != 0) {
            name = localName;
        }
        else if (qName != null && qName.length() != 0) {
            name = qName;
        }

        if(name != null) {
            closedName = name;
        }
    }

    // Called to get tag characters
    @Override
    public void characters(char[] ch, int start, int length)
            throws SAXException {

        if (currentElement) {
            currentValue = currentValue +  new String(ch, start, length);
        }
    }

}
