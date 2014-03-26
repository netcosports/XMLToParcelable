import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Scanner;


public class Main {

	public static void main(String[] args)
	{
		InputStreamReader istream = new InputStreamReader(System.in) ;
		BufferedReader bufRead = new BufferedReader(istream) ;
        Scanner stdin = new Scanner(new BufferedInputStream(System.in));

		try {
			System.out.println("Please enter your package name: ");
			String packageName = bufRead.readLine();
			
			System.out.println("Please Enter Your XML or URL: ");

            StringBuilder builder = new StringBuilder();
            String line;
            while (stdin.hasNextLine()) {
                line = stdin.nextLine();
                builder.append(line);
                if(line == null || line.isEmpty() || line.equals("\\n"))
                    break;
            }

            String json = builder.toString();
            if(json != null && json.startsWith("http"))
            {
                builder.setLength(0);
                URL url = new URL(json);
                URLConnection conn = url.openConnection();

                BufferedReader br = new BufferedReader(
                        new InputStreamReader(conn.getInputStream()));

                String inputLine;

                while ((inputLine = br.readLine()) != null) {
                    builder.append(inputLine);
                    builder.append("\n");
                }

                json = builder.toString();
            }

			Util.generateClassFromXML(json, packageName);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}
