/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.trentorise.smartcampus.ac.provider.adapters;

import eu.trentorise.smartcampus.ac.provider.AcProviderService;
import eu.trentorise.smartcampus.ac.provider.model.Attribute;
import eu.trentorise.smartcampus.ac.provider.model.Authority;
import eu.trentorise.smartcampus.ac.provider.model.User;
import java.util.*;
import javax.annotation.PostConstruct;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.transports.http.configuration.HTTPClientPolicy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 *
 * @author Viktor Pravdin
 */
@Component
public class AcServiceAdapter {

    private static final long DAY = 24L * 3600 * 1000;
    @Value("${ac.endpoint.url}")
    private String endpointUrl;
    @Autowired
    private AttributesAdapter attrAdapter;
    private AcProviderService service;

    @PostConstruct
    private void init() {
        JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();
        factory.setServiceClass(AcProviderService.class);
        factory.setAddress(endpointUrl);

        service = (AcProviderService) factory.create();

        Client client = ClientProxy.getClient(service);
        if (client != null) {
            HTTPConduit conduit = (HTTPConduit) client.getConduit();
            HTTPClientPolicy policy = new HTTPClientPolicy();
            policy.setConnectionTimeout(10000);
            policy.setReceiveTimeout(10000);
            policy.setAllowChunking(false);
            conduit.setClient(policy);
        }
    }

    public String updateUser(String authority, Map<String, String> attributes) {
        Authority auth = service.getAuthority(authority);
        if (auth == null) {
            throw new IllegalArgumentException("Unknown authority: " + authority);
        }
        List<String> ids = attrAdapter.getIdentifyingAttributes(authority);
        // Try to find an already existing user
        List<Attribute> list = new ArrayList<Attribute>();
        for (String key : ids) {
            if (!attributes.containsKey(key)) {
                throw new IllegalArgumentException(
                        "The required attribute is missing: " + key);
            }
            Attribute a = new Attribute();
            a.setAuthority(auth);
            a.setKey(key);
            a.setValue(attributes.get(key));
            list.add(a);
        }
        List<User> users = service.getUsersByAttributes(list);
        if (users.size() > 1) {
            throw new IllegalArgumentException(
                    "The request attributes identify more than one user");
        }
        list.clear();
        for (String key : attributes.keySet()) {
            String value = attributes.get(key);
            Attribute attr = new Attribute();
            attr.setAuthority(auth);
            attr.setKey(key);
            attr.setValue(value);
            list.add(attr);
        }
        String token = service.generateAuthToken();
        if (users.isEmpty()) {

            service.createUser(token, System.currentTimeMillis() + DAY, list);
        } else {
            User user = users.get(0);
            service.updateUser(user.getId(), token,
                    System.currentTimeMillis() + DAY, list);
        }
        return token;
    }

    public boolean deleteToken(String token) {
        return service.removeUser(token);
    }
    
    public Map<String,String> getAuthorities(){
        Collection<Authority> list=service.getAuthorities();
        Map<String,String> map=new HashMap<String,String>();
        for(Authority auth:list){
            map.put(auth.getName(),auth.getRedirectUrl());
        }
        return map;
    }
}
