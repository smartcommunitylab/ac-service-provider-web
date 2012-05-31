/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.trentorise.smartcampus.ac.provider.adapters;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

/**
 *
 * @author Viktor Pravdin
 */
@Component
public class AttributesAdapter {

    public Map<String, String> getAttributes(String authority,
            HttpServletRequest request) {
        // TODO
        return new HashMap();
    }
    
    public List<String> getIdentifyingAttributes(String authority){
        // TODO
        return new ArrayList();
    }
    
}
