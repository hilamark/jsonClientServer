package hila;

import java.io.*; 
import java.net.*; 
import java.nio.charset.StandardCharsets;
import java.util.Iterator;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

public class Server {
	
	public void startServer() throws Exception{
		
		// Init server
		HttpServer httpServer = HttpServer.create(new InetSocketAddress("localhost", 8080), 0);
        httpServer.createContext("/", new JsonHandler());

        // Start the server
        httpServer.start();
	} 
	
	
    static class JsonHandler implements HttpHandler {
 
        @Override
        public void handle(HttpExchange httpExcng) throws IOException {
            if (httpExcng.getRequestMethod().equalsIgnoreCase("POST")) {
 
                try {
 
                    // Get content length from header
                    Headers requestHeaders = httpExcng.getRequestHeaders();
                    int contentLength = Integer.parseInt(requestHeaders.getFirst("Content-length"));
                    byte[] data = new byte[contentLength];
                    
                    // Get request body
                    InputStream is = httpExcng.getRequestBody();
                    int length = is.read(data);
                    String dataStr = new String(data, StandardCharsets.UTF_8);
                    System.out.println("FROM CLIENT: " + dataStr);  
                    
                    // Parse data to JSON object and get values
                    JSONParser jsonParser = new JSONParser();
        			JSONObject jsonDataObject = (JSONObject) jsonParser.parse(dataStr.trim());
        			String Data_Val = (String) jsonDataObject.get("data");
        			String Data_CheckSum = (String) jsonDataObject.get("checksum");
        			
        			// Check if computed checksum matches the one in the request, print warning else
        			if(!Utils.computeCheckSum(Data_Val).equals(Data_CheckSum)){
        				System.out.println("WARNING checksum missmatch");
        				httpExcng.close();
        				return;
        			}
        			
        			// Update all numeric values
        			org.codehaus.jackson.JsonNode json = new ObjectMapper().readTree(Data_Val);
        			updateNumericVals(json);
        			
        			// Compute and update new data's checksum
        			Data_Val = json.toString();
        			jsonDataObject.put("data", Data_Val);
        			jsonDataObject.put("checksum", Utils.computeCheckSum(Data_Val));
        			
                    // Send response Headers
        			byte[] bytes = jsonDataObject.toString().getBytes();
                    httpExcng.sendResponseHeaders(HttpURLConnection.HTTP_OK, bytes.length);
 
                    // Send response Body
                    OutputStream os = httpExcng.getResponseBody();
                    os.write(bytes);
                    httpExcng.close();
 
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
 
        }
        
        public void updateNumericVals(JsonNode node) {
            Iterator<String> fieldNames = node.getFieldNames();
            
            while(fieldNames.hasNext()){
                String fieldName = fieldNames.next();
                JsonNode fieldValue = node.get(fieldName);
                
                // Check type
                if (fieldValue.isObject()) {
                   updateNumericVals(fieldValue);
                } 
                else if (fieldValue.isNumber()) {
                	// Raise value by 1 and update
                   ((ObjectNode)node).put(fieldName, fieldValue.asInt() + 1);
                }
            }
       }
    }
    
    
}

