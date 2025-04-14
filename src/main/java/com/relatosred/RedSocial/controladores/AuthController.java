package com.relatosred.RedSocial.controladores;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AuthController {

    @GetMapping("/")
    // Renderiza la plantilla Thymeleaf formulario.html
    public String formulario() {
        return "formulario";
    }

    /* @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/admin")
    public String admin() {
        return "admin";
    } */
}