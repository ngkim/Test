import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.security.cert.Certificate;

import org.glassfish.jersey.SslConfigurator;
import org.glassfish.jersey.client.filter.HttpBasicAuthFilter;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLPeerUnverifiedException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;

import java.net.URL;

import xml.RequestInfoEthernet;

public class TestHTTPClient {
	private String url = "http://220.123.31.55:7502/infoEthernet";

	public TestHTTPClient() {

	}

	// Perform XML Marsharling of Request
	public String getRequestXML(RequestInfoEthernet req) {

		String requestXml = "";
		try {
			JAXBContext jaxbContext = JAXBContext
					.newInstance(RequestInfoEthernet.class);
			Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

			StringWriter writer = new StringWriter();
			jaxbMarshaller.marshal(req, writer);

			requestXml = writer.toString();
		} catch (JAXBException e) {
			e.printStackTrace();
		}

		return requestXml;
	}

	public String getResponseXml(HttpResponse res) {
		String responseXml = "";

		try {
			BufferedReader rd = new BufferedReader(new InputStreamReader(res
					.getEntity().getContent()));

			String line = "";
			StringBuffer jb = new StringBuffer();
			while ((line = rd.readLine()) != null) {
				jb.append(line);
			}
			responseXml = jb.toString();

		} catch (IOException e) {
			e.printStackTrace();
		}

		return responseXml;
	}

	private void print_https_cert(HttpsURLConnection con) {

		if (con != null) {

			try {

				System.out.println("Response Code : " + con.getResponseCode());
				System.out.println("Cipher Suite : " + con.getCipherSuite());
				System.out.println("\n");

				Certificate[] certs = con.getServerCertificates();
				for (Certificate cert : certs) {
					System.out.println("Cert Type : " + cert.getType());
					System.out.println("Cert Hash Code : " + cert.hashCode());
					System.out.println("Cert Public Key Algorithm : "
							+ cert.getPublicKey().getAlgorithm());
					System.out.println("Cert Public Key Format : "
							+ cert.getPublicKey().getFormat());
					System.out.println("\n");
				}

			} catch (SSLPeerUnverifiedException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}

	}

	private void print_content(HttpsURLConnection con) {
		if (con != null) {

			try {

				System.out.println("****** Content of the URL ********");
				BufferedReader br = new BufferedReader(new InputStreamReader(
						con.getInputStream()));

				String input;

				while ((input = br.readLine()) != null) {
					System.out.println(input);
				}
				br.close();

			} catch (IOException e) {
				e.printStackTrace();
			}

		}

	}

	public HttpsURLConnection doPost() {
		URL https_url;
		HttpsURLConnection con = null;
		try {
			https_url = new URL(url);
			con = (HttpsURLConnection) https_url.openConnection();
			print_https_cert(con);
			print_content(con);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return con;
	}

	public HttpResponse doPost(HttpClient client) {
		HttpResponse res = null;
		String requestXml = null;
		try {
			RequestInfoEthernet reqEth = new RequestInfoEthernet();
			reqEth.setCid("ngkim");
			reqEth.setRid("123456");
			reqEth.setEid("eth123");

			HttpPost req = new HttpPost(url);
			requestXml = getRequestXML(reqEth);
			System.out.println(requestXml);
			StringEntity entity = new StringEntity(requestXml);
			req.setEntity(entity);

			// Get Response
			res = client.execute(req);
		} catch (org.apache.http.NoHttpResponseException e) {
			System.err
					.println("ERROR!!! The target server failed to respond!!!");
			res = null;
			// e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return res;
	}

	public HttpResponse doGet(HttpClient client) {
		HttpResponse res = null;
		try {
			HttpGet req = new HttpGet(url);
			// Get Response
			res = client.execute(req);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return res;
	}

	public static void main(String args[]) {

		SslConfigurator sslConfig = SslConfigurator.newInstance()
				.trustStoreFile("/home/ngkim/workspace/TestHTTPPost/trust/truststore_client")
				.trustStorePassword("asdfgh")
				.keyStoreFile("/home/ngkim/workspace/TestHTTPPost/trust/keystore_client")
				.keyPassword("asdfgh");
		
		Client client = ClientBuilder.newBuilder()
				.sslContext(sslConfig.createSSLContext()).build();
		
		// client basic auth demonstration
		// client.register(new HttpBasicAuthFilter("user", "password"));
		client.register(new HttpBasicAuthFilter("admin", "admin"));
		
		SSLContext sslContext = sslConfig.createSSLContext();
		
//		Protocol myhttps = new Protocol("https", new MySSLSocketFactory(), 443);
//		
//		
//		HttpClient httpclient;
////		= new HttpClient();
//		
//		httpclient.getHostConfiguration().setHost("www.whatever.com", 443, myhttps);
//		GetMethod httpget = new GetMethod("/");
//		try {
//		  httpclient.executeMethod(httpget);
//		  System.out.println(httpget.getStatusLine());
//		} finally {
//		  httpget.releaseConnection();
//		}

		TestHTTPClient thc = new TestHTTPClient();

		// HttpClient client = new DefaultHttpClient();

		// HttpResponse res = thc.doPost(client);

		thc.doPost();

		// if (res == null) {
		// System.err.println("ERROR!!! No Response from Server!!!");
		// } else {
		// String responseXml = thc.getResponseXml(res);
		// System.out.println(responseXml);
		// }
	}
}
