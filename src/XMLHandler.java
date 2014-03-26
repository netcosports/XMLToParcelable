import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.HashMap;
import java.util.HashSet;


/**
 * Created by stephane on 26/03/14.
 */
public class XMLHandler extends DefaultHandler {

    Boolean currentElement = false;
    String currentValue = "";
    HashSet<String> elementsHandled = new HashSet<String>();
    private String packageName;
    XMLObject currentObject;
    HashMap<String, Boolean> lists;
    int currentLevel = 0;

    public XMLHandler(HashMap<String, Boolean> lists, String packageName) {
        this.packageName = packageName;
        this.lists = lists;
    }

    // Called when tag starts
    @Override
    public void startElement(String uri, String localName, String qName,
                             Attributes attributes) throws SAXException {

        currentLevel++;
        currentElement = true;
        currentValue = "";
        String name = null;
        if (localName != null && localName.length() != 0 && !elementsHandled.contains(localName)) {
            name = localName;
        }
        else if (qName != null && qName.length() != 0 && !elementsHandled.contains(qName)) {
            name = qName;
        }

        if(name != null)
        {
            if(currentObject == null)
            {
                String subclassName = Util.toUppercaseFirst(name);
                currentObject = new XMLObject(packageName, subclassName, attributes);
            }
            else
            {
                String key = Util.getKey(name, currentLevel);
                boolean isList = lists.containsKey(key) && lists.get(key);
                currentObject.addField(name, attributes, isList);
            }
        }

    }

    // Called when tag closing
    @Override
    public void endElement(String uri, String localName, String qName)
            throws SAXException {

        currentLevel--;
        currentElement = false;

        String name = null;
        if (localName != null && localName.length() != 0 && !elementsHandled.contains(localName)) {
            name = localName;
        }
        else if (qName != null && qName.length() != 0 && !elementsHandled.contains(qName)) {
            name = qName;
        }

        if(name != null && name.length() != 0)
        {
            elementsHandled.add(name);
            currentObject.close(name, currentValue);

            if(name.equalsIgnoreCase(currentObject.className))
                currentObject = null;
        }

        currentValue = "";
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
