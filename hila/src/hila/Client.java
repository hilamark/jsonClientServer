package hila;

import java.io.*; 
import java.nio.file.Paths;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;


public class Client {
	
	private static final String JSON_FILE_PATH = Paths.get(".").toAbsolutePath().normalize().toString() + "\\json\\myJson.json";//"C:/Users/Michael/Desktop/myJson.json";

	public void startClient() throws Exception  {  
		// Init
		String url = "http://localhost:8080";
		HttpPost postRequest = new HttpPost(url);
		DefaultHttpClient httpClient = new DefaultHttpClient();
		
		// Read JSON
		JSONObject jsonData = readJson(JSON_FILE_PATH);
		
		StringEntity input = new StringEntity(jsonData.toJSONString());
        input.setContentType("application/json");
        postRequest.setEntity(input);
        
        HttpResponse response = httpClient.execute(postRequest);
        BufferedReader br = new BufferedReader(new InputStreamReader((response.getEntity().getContent())));
        
        String output;
        StringBuffer totalOutput = new StringBuffer();
        while ((output = br.readLine()) != null) {
            totalOutput.append(output);
        }
        
        // Get json from the string
        JSONParser jsonParser = new JSONParser();
		JSONObject jsonDataObject = (JSONObject) jsonParser.parse(totalOutput.toString().trim());
		String Data_Val = (String) jsonDataObject.get("data");
        
        // Save data to the file
		FileWriter out = new FileWriter(JSON_FILE_PATH);
		out.write(Data_Val);
		out.flush();
		out.close();
        
		System.out.println("FROM SERVER: " + totalOutput.toString());  
		httpClient.getConnectionManager().shutdown();
	} 
	
	private static JSONObject readJson(String filePath){
		String data = "";
		String checkSum = "";
		JSONObject jsonOutObject = new JSONObject();
		
        try {
        	// Read the json file
			FileReader reader = new FileReader(filePath);
			
			JSONParser jsonParser = new JSONParser();
			JSONObject jsonDataObject = (JSONObject) jsonParser.parse(reader);
			
			// Get a String from the JSON object
			data = jsonDataObject.toJSONString();
			
			// Compute checksum
			checkSum = Utils.computeCheckSum(data);
			
			jsonOutObject.put("data", data);
			jsonOutObject.put("checksum", checkSum);

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 
        return jsonOutObject;
	}
}
