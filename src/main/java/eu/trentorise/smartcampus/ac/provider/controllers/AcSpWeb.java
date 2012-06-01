/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.trentorise.smartcampus.ac.provider.controllers;

import eu.trentorise.smartcampus.ac.provider.adapters.AcServiceAdapter;
import eu.trentorise.smartcampus.ac.provider.adapters.AttributesAdapter;
import java.io.IOException;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
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
        Map<String, String> authorities = attrAdapter.getAuthorityUrls();
        model.addAttribute("authorities", authorities);
        return "authorities";
    }

    @RequestMapping(method = RequestMethod.GET, value = "/getToken/{authorityUrl}")
    public String getToken(@PathVariable("authorityUrl") String authorityUrl,
            HttpServletRequest request) {
        String token = service.updateUser(authorityUrl, request);
        return "redirect:success#" + token;
    }

    @RequestMapping(method = RequestMethod.DELETE,
    value = "/invalidateToken/{token}")
    public void deleteToken(@RequestParam("token") String token) {
        service.deleteToken(token);
    }

    @RequestMapping("/success")
    public void success() {
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public void badRequest(IllegalArgumentException ex, HttpServletResponse resp) throws IOException {
        resp.sendError(HttpServletResponse.SC_BAD_REQUEST, ex.getMessage());
    }
}
