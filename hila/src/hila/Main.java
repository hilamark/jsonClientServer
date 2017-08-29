package hila;


public class Main {
	
	public static void main(String arg[]){
		System.out.println("Starting!"); 
		Client jsonClient = new Client();
		Server jsonServer = new Server();
		
		try {
			jsonServer.startServer();
			jsonClient.startClient();
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	 }
}
