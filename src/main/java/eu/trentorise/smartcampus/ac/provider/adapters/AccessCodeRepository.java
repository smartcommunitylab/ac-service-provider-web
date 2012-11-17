package eu.trentorise.smartcampus.ac.provider.adapters;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Component;

@Component
public class AccessCodeRepository {

	private final static int EXP_TIME = 60000;
	
	private static Map<String, CodeDescriptor> map = new HashMap<String, CodeDescriptor>();
	
	public synchronized String generateAcessCode(String authToken) {
		String code = UUID.randomUUID().toString();
		CodeDescriptor d = new CodeDescriptor(authToken, System.currentTimeMillis()+EXP_TIME);
		map.put(code, d);
		return code;
	}
	
	public synchronized String validateAccessCode(String code) {
		if (map.get(code) != null) {
			CodeDescriptor d = map.remove(code);
			if (System.currentTimeMillis() < d.expTime) {
				return d.authToken;
			}
		}
		return null;
	}
	
	private static class CodeDescriptor {
		String authToken;
		long expTime;
		public CodeDescriptor(String authToken, long expTime) {
			super();
			this.authToken = authToken;
			this.expTime = expTime;
		}
	}
	
}
