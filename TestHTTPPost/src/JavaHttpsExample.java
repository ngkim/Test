import java.net.URL;
import java.io.*;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.SslConfigurator;
import org.glassfish.jersey.client.filter.HttpBasicAuthFilter;


public class JavaHttpsExample {
	private static class CustomizedHostnameVerifier implements HostnameVerifier
	{
		public boolean verify(String hostname, SSLSession session) {
			return true;
		}
	}

	public static void main(String[] args) throws Exception {
		String httpsURL = "https://220.123.31.55:7502/createEthernet";

		SslConfigurator sslConfig = SslConfigurator.newInstance()
				.trustStoreFile("./trust/truststore_client")
				.trustStorePassword("asdfgh")
				.keyStoreFile("./trust/keystore_client")
				.keyPassword("asdfgh");
		
		Client client = ClientBuilder.newBuilder()
				.hostnameVerifier(new CustomizedHostnameVerifier())
				.sslContext(sslConfig.createSSLContext())
				.build();

		client.register(new HttpBasicAuthFilter("admin", "admin"));
		
//		Response res = client.target(httpsURL).request(MediaType.APPLICATION_XML_TYPE).get();
		
		Response res = client.target(httpsURL).request(MediaType.APPLICATION_XML_TYPE).post(null);

		System.out.println("Git Test");
		System.out.println(res.readEntity(String.class));
	 
	    client.close();
		
/*		HttpsURLConnection con;
		try {
			con = (HttpsURLConnection) myurl.openConnection();
			con.setHostnameVerifier(new CustomizedHostnameVerifier());
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}

		BufferedReader in = null;
		try {
			InputStream ins = con.getInputStream();
			InputStreamReader isr = new InputStreamReader(ins);
			in = new BufferedReader(isr);
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}

		String inputLine;
		while ((inputLine = in.readLine()) != null) {
			System.out.println(inputLine);
		}

		in.close();*/
	}
}