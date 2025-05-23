package com.relatosred.RedSocial.controladores;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AuthController {

  @GetMapping({"/index", "/"})
    // Renderiza index.html.
    public String index() {
        return "index";
    }

   @GetMapping({"/registro"})
    // Renderiza registro.html.
    public String registro() {
        return "registro";
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/admin")
    public String admin() {
        return "admin";
    }
}