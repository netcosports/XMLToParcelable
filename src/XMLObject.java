import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;


/**
 * Created by stephane on 26/03/14.
 */
public class XMLObject {

    public static final String IMPORT_JAVA_UTIL_ARRAY_LIST = "import java.util.ArrayList;\n\n";
    StringBuilder builderImports = new StringBuilder();
    StringBuilder builderStaticParams = new StringBuilder();
    StringBuilder builderClassParams = new StringBuilder();
    StringBuilder builderAddField = new StringBuilder();
    StringBuilder builderClose = new StringBuilder();
    StringBuilder builderAddFieldAttributes = new StringBuilder();
    StringBuilder builderConstructor = new StringBuilder();
    StringBuilder builderConstructorParcelable = new StringBuilder();
    StringBuilder builderWriteToParcel = new StringBuilder();

    private String packageName;
    public String className;
    private XMLObject currentSubObject;
    private int numberOfAttributes = 0;

    public XMLObject(String packageName, String className, Attributes attributes) {
        this.packageName = packageName;
        this.className = className;
        initBuilders();
        addAttributes(attributes);
    }

    public void initBuilders()
    {
        builderImports.setLength(0);
        builderStaticParams.setLength(0);
        builderClassParams.setLength(0);
        builderConstructor.setLength(0);
        builderConstructorParcelable.setLength(0);
        builderWriteToParcel.setLength(0);

        builderImports.append("package " + packageName + ";\n\n");
        builderImports.append("import org.xml.sax.Attributes;\n\nimport android.os.Parcel;\nimport android.os.Parcelable;\nimport android.text.TextUtils;\nimport com.netcosports.utils.xml.BaseXmlItem;\n\n");

        builderStaticParams.append("public class " + className + " extends BaseXmlItem implements Parcelable{\n");

        builderConstructorParcelable.append("\n	public " + className +"(final Parcel in) {\n");
        builderConstructor.append("\n	public " + className +"(Attributes attributes) {\n");

        builderAddField.append("\n	public void addField(String name, String value)");
        builderAddField.append("\n	{\n");

        builderClose.append("\tpublic void close()\n\t{\n");

        builderAddFieldAttributes.append("\n	public void addField(String name, Attributes attributes)");
        builderAddFieldAttributes.append("\n	{\n");

        builderWriteToParcel.append("	@Override\n	public void writeToParcel(Parcel dest, int flags) {\n");
    }

    public void close(String className, String currentValue)
    {
        if(currentSubObject != null)
        {
            currentSubObject.close(className, currentValue);
            if(className.equalsIgnoreCase(currentSubObject.className))
            {
                currentSubObject = null;
            }
        }
        else
        {
            if(currentValue != null && currentValue.length() != 0 && numberOfAttributes == 0)
            {
                Util.writeStatic(className, builderStaticParams);
                Util.writeClassParam(className, builderClassParams, false, null, false);
                Util.writeParcelConstructor(className, builderConstructorParcelable, false, null, false);
                Util.writeAddField(className, builderAddField, false, null, false);
                Util.writeToParcel(className, builderWriteToParcel, false, false);
            }
            finishBuilders();
        }
    }

    private void finishBuilders()
    {
        builderStaticParams.append("\n\n");
        builderClassParams.append("\n\n");
        builderConstructor.append("\n	}\n\n");
        builderAddField.append("\n	}\n\n");
        builderClose.append("\t\tisClosed = true;");

        builderClose.append("\n	}\n\n");
        builderAddFieldAttributes.append("\n	}\n\n");
        builderConstructorParcelable.append("\n	}\n\n");
        builderWriteToParcel.append("\n	}\n\n");

        builderImports.append(builderStaticParams.toString());
        builderImports.append(builderClassParams.toString());
        builderImports.append(builderConstructor.toString());
        builderImports.append(builderClose.toString());
        builderImports.append(builderAddField.toString());
        builderImports.append(builderAddFieldAttributes.toString());
        builderImports.append(builderConstructorParcelable.toString());
        builderImports.append(builderWriteToParcel.toString());


        // Parcelable management
        builderImports.append("	@Override\n	public int describeContents() {\n		return 0;\n	}\n\n");
        builderImports.append("	public static final Parcelable.Creator<" + className +"> CREATOR = new Parcelable.Creator<" + className +">() {\n		public " + className +" createFromParcel(final Parcel in) {\n			return new " + className +"(in);\n		}\n\n		public " + className +"[] newArray(final int size) {\n			return new " + className +"[size];\n		}\n	};");
        builderImports.append("\n}");

        Util.writeFile(builderImports.toString(), "/", className + ".java");
    }



    public void addAttributes(Attributes attributes)
    {
        numberOfAttributes = attributes.getLength();

        String key;
        for (int i = 0; i < attributes.getLength(); i++) {
            key = attributes.getLocalName(i);
            Util.writeStatic(key, builderStaticParams);
            Util.writeClassParam(key, builderClassParams, false, null, false);
            Util.writeParcelConstructor(key, builderConstructorParcelable, false, null, false);
            Util.writeConstructor(key, builderConstructor, false, null, false);
            Util.writeToParcel(key, builderWriteToParcel, false, false);
        }
    }

    public void addField(String value, Attributes attributes, boolean isList)
    {
        if(currentSubObject == null)
        {
            String subclassName = Util.toUppercaseFirst(value);

            currentSubObject = new XMLObject(packageName, subclassName, attributes);

            if(isList)
            {
                if(builderImports.lastIndexOf(IMPORT_JAVA_UTIL_ARRAY_LIST) == -1)
                    builderImports.append(IMPORT_JAVA_UTIL_ARRAY_LIST);

                Util.writeStatic(value, builderStaticParams);
                Util.writeClassParam(value, builderClassParams, false, subclassName, true);
                Util.writeParcelConstructor(value, builderConstructorParcelable, false, subclassName, true);
                Util.writeAddField(value, builderAddField, false, subclassName, true);
                Util.writeAddFieldAttributes(value, builderAddFieldAttributes, false, subclassName, true);
                Util.writeClose(value, builderClose, false, subclassName, true);
                Util.writeToParcel(value, builderWriteToParcel, false, true);
            }
            else
            {
                Util.writeStatic(value, builderStaticParams);
                Util.writeClassParam(value, builderClassParams, true, subclassName, false);
                Util.writeParcelConstructor(value, builderConstructorParcelable, true, subclassName, false);
//            Util.writeConstructor(value, builderConstructor, true, subclassName, false);
                Util.writeAddField(value, builderAddField, true, subclassName, false);
                Util.writeAddFieldAttributes(value, builderAddFieldAttributes, true, subclassName, false);
                Util.writeClose(value, builderClose, true, subclassName, false);
                Util.writeToParcel(value, builderWriteToParcel, true, false);
            }

        }
        else
        {
            currentSubObject.addField(value, attributes, isList);
        }
    }


}
