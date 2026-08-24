package com.mycompany.smp.controller;

import com.mycompany.smp.util.CommonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/v1/test")
public class TestController {
    @Autowired
    private CommonUtil commonUtil;

    @GetMapping("/all")
    public String allAccess(){
        return "Public Content."+ commonUtil.loggedInUser().getFirstName() + " "+ " "+ commonUtil.loggedInUser().getLastName();
    }

    // Change hasAuthority to hasRole, and remove the 'ROLE_' prefix inside the brackets
    @GetMapping("/CONSUMER")
    @PreAuthorize("hasRole('CONSUMER') or hasRole('ADMIN')")
    public String UserAccess(){
        return "MODERATOR/ADMIN content. " + commonUtil.loggedInUser().getFirstName() + " "+ " "+ commonUtil.loggedInUser().getLastName();
    }


    @GetMapping("/consumer")
    @PreAuthorize("hasRole('CONSUMER')")
    public String moderatorAccess() {

        return "USER Board."+ commonUtil.loggedInUser().getFirstName() + " "+ " "+ commonUtil.loggedInUser().getLastName();
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public String adminAccess(){
        return "Admin Board. " + commonUtil.loggedInUser().getFirstName() + " "+ " "+ commonUtil.loggedInUser().getLastName();
    }
}
