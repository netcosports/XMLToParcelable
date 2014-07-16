import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;


public class Util {
    public static final String LEVEL = "level";

    public static void generateClassFromXML(String object, String packageName)
    {
        try {
            SAXParserFactory spf = SAXParserFactory.newInstance();
            SAXParser sp = spf.newSAXParser();
            XMLReader xr = sp.getXMLReader();

            FirstPassHandler firstPassHandler = new FirstPassHandler();
            xr.setContentHandler(firstPassHandler);
            InputSource inStream = new InputSource();
            inStream.setCharacterStream(new StringReader(object));
            xr.parse(inStream);


            XMLHandler handler = new XMLHandler(firstPassHandler.getResult(), packageName);
            xr.setContentHandler(handler);
            inStream = new InputSource();
            inStream.setCharacterStream(new StringReader(object));

            xr.parse(inStream);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
    }

    public static String getKey(String name, int currentLevel) {
        return name + Util.LEVEL + currentLevel;
    }

    public static void writeStatic(Object next, StringBuilder builderStaticParams) {
        if(next instanceof String)
        {
            builderStaticParams.append("	private final static String " + ((String)next).toUpperCase() + " = \"" + next + "\";\n");
        }
    }

    public static void writeFile(String string, String relativePath, String filename)
    {
        try {
            File root = new File("output/", relativePath);
            if (!root.exists()) {
                root.mkdirs();
            }

            if (root.canWrite()){
                File gpxfile = new File(root, filename);
                FileWriter gpxwriter = new FileWriter(gpxfile);
                BufferedWriter out = new BufferedWriter(gpxwriter);
                out.write(string);
                out.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void writeToParcel(Object next, StringBuilder builderWriteToParcel, boolean isJsonObject, boolean isJsonArray) {
        if(next instanceof String)
        {
            if(isJsonObject)
            {
                builderWriteToParcel.append("		dest.writeParcelable(" + next + ", 0);\n");
            }
            else if(isJsonArray)
            {
                builderWriteToParcel.append("		dest.writeList(" + next + ");\n");
            }
            else if(((String) next).equalsIgnoreCase("id") || ((String) next).contains("_id"))
                builderWriteToParcel.append("		dest.writeLong(" + next + ");\n");
            else
            {
                builderWriteToParcel.append("		dest.writeString(" + next + ");\n");
            }
        }
    }

    public static void writeConstructor(Object next, StringBuilder builderConstructor, boolean isJsonObject, String jsonObjectName, boolean isJsonArray) {
        if(next instanceof String)
        {
            if(isJsonObject)
            {
                builderConstructor.append("		this." + next + " = new " + jsonObjectName + "(attributes);\n");
//                builderConstructor.append("		JSONObject json" + next + " = json.optJSONObject(" + ((String)next).toUpperCase() + ");\n");
//                builderConstructor.append("		this." + next + " = json" + next + " != null ? new " + jsonObjectName + "(json" + next + ") : null;\n");
            }
//            else if(isJsonArray)
//            {
//                String nextArray = next + "Array";
//                String indexName = "index" + next;
//                builderConstructor.append("		this." + next + " = new ArrayList<" + jsonObjectName + ">();\n");
//                builderConstructor.append("		JSONArray " + nextArray + " = json.optJSONArray(" + ((String)next).toUpperCase() + ");\n");
//                builderConstructor.append("		if(" + nextArray + " != null) {\n");
//                builderConstructor.append("		    for (int "+ indexName + " = 0; " + indexName + " < "+ nextArray + ".length(); " + indexName +"++) {\n");
//                builderConstructor.append("		        " + next + ".add(new " + jsonObjectName + "(" + nextArray + ".optJSONObject(" + indexName + ")));\n");
//                builderConstructor.append("		    }\n");
//                builderConstructor.append("		}\n");
//
//            }
//            else
            else if(((String) next).equalsIgnoreCase("id") || ((String) next).contains("_id"))
                builderConstructor.append("		this." + next + " = Long.parseLong(attributes.getValue("+ ((String)next).toUpperCase() + "));\n");
            else
            {
                builderConstructor.append("		this." + next + " = attributes.getValue("+ ((String)next).toUpperCase() + ");\n");
            }
        }
    }

    public static void writeAddField(Object next, StringBuilder builderConstructor, boolean isJsonObject, String jsonObjectName, boolean isJsonArray) {
        if(next instanceof String)
        {
            if(isJsonObject)
            {
                builderConstructor.append("        if(" + next + " != null && !"+next+".isClosed()) {\n");
                builderConstructor.append("            " + next + ".addField(name, value);\n");
                builderConstructor.append("            return;\n");
                builderConstructor.append("        }\n");
            }
            else if(isJsonArray)
            {
                String item = jsonObjectName +"Item";
                builderConstructor.append("\t\tfor ("+jsonObjectName+" "+ item +" : " + next+ ") {\n" +
                        "                    if("+item+" != null && !"+item+".isClosed()) {\n" +
                        "                        "+item+".addField(name, value);\n" +
                        "                        return;\n" +
                        "                    }\n" +
                        "                }\n");
            }
            else
            {
                builderConstructor.append("        if(name.equals(" + ((String) next).toUpperCase() + ") && TextUtils.isEmpty(" + next + "))\n");
                builderConstructor.append("\t\t\t"+  next + " = value;\n\n");
            }
        }
    }

    public static void writeClose(Object next, StringBuilder builderConstructor, boolean isJsonObject, String jsonObjectName, boolean isJsonArray) {
        if(next instanceof String)
        {
            if(isJsonObject)
            {
                builderConstructor.append("\t\tif(!" + next + ".isClosed()) {\n");
                builderConstructor.append("\t\t\t" + next + ".close();\n");
                builderConstructor.append("\t\t\treturn;\n");
                builderConstructor.append("\t\t}\n");
            }
            else if(isJsonArray)
            {
                String item = jsonObjectName+"Item";
                builderConstructor.append("\t\t\tfor (" + jsonObjectName + " " + item + " : " + next + ") {\n" +
                        "\t\t    if(!" + item + ".isClosed()) {\n" +
                        "\t\t        " + item + ".close();\n" +
                        "\t\t        return;\n" +
                        "\t\t    }\n" +
                        "\t\t}\n");
            }
        }
    }

    public static void writeAddFieldAttributes(Object next, StringBuilder builderConstructor, boolean isJsonObject, String jsonObjectName, boolean isJsonArray) {
        if(next instanceof String)
        {
            if(isJsonObject)
            {
                builderConstructor.append("\t\tif((" + next + " == null && name.equals(" + ((String) next).toUpperCase() + ")) || (" + next + " != null && !"+next+".isClosed())) {\n");
                builderConstructor.append("\t\t\tif(" + next + " == null)\n");
                builderConstructor.append("\t\t\t\t" + next + " = new " + jsonObjectName +"(attributes);\n");
                builderConstructor.append("\t\t\telse\n");
                builderConstructor.append("\t\t\t\t" + next + ".addField(name, attributes);\n");
                builderConstructor.append("\t\t\treturn;\n");
                builderConstructor.append("\t\t}\n\n");
            }
            else if(isJsonArray)
            {
                String item = jsonObjectName +"Item";

                builderConstructor.append("\t\tfor (" + jsonObjectName + " " + item + " : " + next + ") {\n" +
                        "\t\t\tif(" + item + " != null && !" + item + ".isClosed()) {\n" +
                        "\t\t\t\t" + item + ".addField(name, attributes);\n" +
                        "\t\t\t\treturn;\n" +
                        "\t\t\t}\n" +
                        "\t\t}\n");

                builderConstructor.append("\t\tif(name.equals("+((String) next).toUpperCase()+")){\n");

                builderConstructor.append("\t\t\t"+next+".add(new "+jsonObjectName+"(attributes));\n");

                builderConstructor.append("\t\t\treturn;\n");
                builderConstructor.append("\t\t}\n\n");
            }
        }
    }

    public static void writeParcelConstructor(Object next, StringBuilder builderConstructorParcelable, boolean isJsonObject, String subClassName, boolean isJsonArray) {
        if(next instanceof String)
        {
            if(isJsonObject)
            {
                builderConstructorParcelable.append("		this." + next + " = in.readParcelable(" + subClassName + ".class.getClassLoader());\n");
            }
            else if(isJsonArray)
            {
                builderConstructorParcelable.append("		in.readList(" + next + ", " + subClassName + ".class.getClassLoader());\n");
            }
            else if(((String) next).equalsIgnoreCase("id") || ((String) next).contains("_id"))
                builderConstructorParcelable.append("		this." + next + " = in.readLong();\n");
            else
            {
                builderConstructorParcelable.append("		this." + next + " = in.readString();\n");
            }
        }
    }

    public static void writeClassParam(Object next, StringBuilder builderClassParams, boolean isJsonObject, String subClassName, boolean isJsonArray) {
        if(next instanceof String)
        {
            if(isJsonObject)
            {
                builderClassParams.append("	public " + subClassName + " " + next + ";\n");
            }
            else if(isJsonArray)
            {
                builderClassParams.append("	public ArrayList<" + subClassName + "> " + next + " = new ArrayList<" + subClassName + ">();\n");
            }
            else if(((String) next).equalsIgnoreCase("id") || ((String) next).contains("_id"))
                builderClassParams.append("	public long " + next + ";\n");
            else
            {
                builderClassParams.append("	public String " + next + ";\n");
            }
        }
    }

    public static String toUppercaseFirst(String v) {
        String subclassName = v;
        if(subclassName != null && subclassName.length() != 0)
            subclassName = subclassName.substring(0,1).toUpperCase() + subclassName.substring(1);
        return subclassName;
    }
}

