import java.util.Iterator;
import java.util.Map;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RestFulClientAdapter {
	private static final Logger logger = LoggerFactory
			.getLogger(RestFulClientAdapter.class);

	private SSLContext sslContext = null;
	private String baseUri = "";
	private ClientRequestFilter authFilter = null;
	private WebTarget rootTarget = null;

	public SSLContext getSslContext() {
		return sslContext;
	}

	public void setSslContext(SSLContext sslContext) {
		this.sslContext = sslContext;
	}

	public String getBaseUri() {
		return baseUri;
	}

	public void setBaseUri(String baseUri) {
		this.baseUri = baseUri;
	}

	public ClientRequestFilter getAuthFilter() {
		return authFilter;
	}

	public void setAuthFilter(ClientRequestFilter authFilter) {
		this.authFilter = authFilter;
	}

	public WebTarget getRootTarget() {
		return rootTarget;
	}

	public void setRootTarget(WebTarget rootTarget) {
		this.rootTarget = rootTarget;
	}

	private static class CustomizedHostnameVerifier implements HostnameVerifier {
		public boolean verify(String hostname, SSLSession session) {
			return true;
		}
	}

	public void init() throws Exception {

		/*
		 * tring testuri =
		 * "https://localhost:7502/createEthernet?createEthernet=%3C?xml+version%3D'1.0'+encoding%3D'UTF-8'
		 * +standalone%3D
		 * 'yes'?%3E%3CEthernet+name%3D'myEth'+description%3D'DC+to+DC+tunnel'%3E%3Ccid%3ECustomer-Id%3C/cid%3E%3CQOS
		 * +exceed%3D
		 * '100M'+bandWidth%3D'1G'/%3E%3Crid%3ETest-Request-ID%3C/rid%3E%3Cstatus%3EOK%3C/status%3E%3CUNIPeer+vlan%3D'1001'
		 * +port%3D
		 * '10'+id%3D'switch1-ID'/%3E%3CUNIPeer+vlan%3D'1002'+port%3D'20'+id%3D'switch2-ID'/%3E%3CUNIPeer+vlan%3D'1003'+
		 * port%3D
		 * '30'+id%3D'switch3-ID'/%3E%3CeType%3EE-LAN%3C/eType%3E%3C/Ethernet%3E";
		 */

		try {
			Client client = null;
			if (sslContext != null)
				client = ClientBuilder.newBuilder()
						.hostnameVerifier(new CustomizedHostnameVerifier())
						.sslContext(sslContext).build();
			else
				client = ClientBuilder.newBuilder()
						.hostnameVerifier(new CustomizedHostnameVerifier())
						.build();

			if (authFilter != null)
				client.register(authFilter);

			rootTarget = client.target(baseUri);
		} catch (Exception ex) {
			logger.error("RestFulClientAdapter init failed.", ex);
			throw ex;
		}
	}

	public Object get(String path, MediaType mediaType, Class<?> clz)
			throws Exception {
		try {
			Object response = rootTarget.path(path).request(mediaType).get(clz);
			return response;
		} catch (Exception ex) {
			logger.error("[{}] get failed.", path, ex);
			throw ex;
		}
	}

	public Object get(String path, MediaType mediaType, String paraName,
			Object param, Class<?> clz) throws Exception {
		try {
			Object response = rootTarget.path(path).queryParam(paraName, param)
					.request(mediaType).get(clz);
			// Object response = rootTarget.request().get(clz);
			// System.out.println("==============================="+rootTarget.path(path).queryParam(paraName,
			// param).request(mediaType).head());

			return response;
		} catch (Exception ex) {
			logger.error("[{}] get failed.", path, ex);
			throw ex;
		}
	}

	public Object get(String path, MediaType mediaType,
			Map<String, Object> paraMap, Class<?> clz) throws Exception {
		try {

			WebTarget target = rootTarget.path(path);
			Object response = null;
			Iterator<String> keyiterator = paraMap.keySet().iterator();
			while (keyiterator.hasNext()) {
				String key = (String) keyiterator.next();
				target = target.queryParam(key, paraMap.get(key));
			}
			response = target.request(mediaType).get(clz);
			return response;
		} catch (Exception ex) {
			logger.error("[{}] get failed.", path, ex);
			throw ex;
		}
	}

	public Object post(String path, MediaType mediaType, Object param,
			Class<?> clz) throws Exception {
		try {
			Object response = rootTarget.path(path).request(mediaType)
					.post(Entity.entity(param, mediaType), clz);
			return response;
		} catch (Exception ex) {
			logger.error("[{}] get failed.", path, ex);
			throw ex;
		}
	}
}