/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.trentorise.smartcampus.ac.provider.controllers;

import eu.trentorise.smartcampus.ac.provider.adapters.AcServiceAdapter;
import eu.trentorise.smartcampus.ac.provider.adapters.AttributesAdapter;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 *
 * @author Viktor Pravdin
 */
@Controller
public class AcSpWeb {

    @Autowired
    private AcServiceAdapter service;
    
    @Autowired
    private AttributesAdapter attrAdapter;

    @RequestMapping(method = RequestMethod.GET, value = "/getToken")
    public String showAuthorities(Model model) {
        Map<String,String> authorities=service.getAuthorities();
        model.addAttribute("authorities", authorities);
        return "authorities";
    }

    @RequestMapping(method = RequestMethod.GET, value = "/getToken/{authority}")
    public String getToken(@RequestParam("authority") String authority,
            HttpServletRequest request) {
        Map<String,String> attributes=attrAdapter.getAttributes(authority,
                request);
        String token=service.updateUser(authority, attributes);
        return "redirect:success#"+token;
    }

    @RequestMapping(method = RequestMethod.DELETE,
    value = "/invalidateToken/{token}")
    public void deleteToken(@RequestParam("token") String token) {
        service.deleteToken(token);
    }
    
    @RequestMapping(    "/success")
    public void success() {
        
    }
    
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public @ResponseBody String badRequest(IllegalArgumentException ex){
        return ex.getMessage();
    }
}
