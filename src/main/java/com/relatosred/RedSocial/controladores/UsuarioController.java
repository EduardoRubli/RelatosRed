package com.relatosred.RedSocial.controladores;

import com.relatosred.RedSocial.entidades.Usuario;
import com.relatosred.RedSocial.servicios.UsuarioService;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

@RestController
@Validated
public class UsuarioController {

    private UsuarioService usuarioService;

    // Se usa constructor en lugar de @Autowired.
    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    // Valida que los campos no estén vacíos.
    private Boolean validarCampo(String campo) {
        if (campo == null || campo.trim().isEmpty()) {
            return false;
        }
        return true;
    }

    //Endpoint para crear un nuevo usuario.
    @PostMapping("/usuario/crear")
    public ResponseEntity<?> crearUsuario(@RequestParam(value = "nombre", required = true)
                                              @NotBlank(message = "El campo nombre es obligatorio.") String nombre,
                                          @RequestParam(value = "apellido", required = true)
                                              @NotBlank(message = "El campo apellido es obligatorio.") String apellido,
                                          @RequestParam(value = "alias", required = true)
                                              @NotBlank(message = "El campo alias es obligatorio.") String alias,
                                          @RequestParam(value = "email", required = true)
                                              @NotBlank(message = "El campo email es obligatorio.")
                                              @Email(message = "El formato de email debe ser válido.") String email,
                                          @RequestParam(value = "password", required = true)
                                              @NotBlank(message = "El campo contraseña es obligatorio.")
                                              @Size(min = 8, message = "La contraseña debe tener mínimo 8 caracteres.") String password,
                                          @RequestParam(value = "fechaNac", required = true)
                                              @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "Se requiere formato yyyy-MM-dd.") String fechaNacStr) {

        try {
            //DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            LocalDate fechaNac = LocalDate.parse(fechaNacStr);
            Usuario usuario = new Usuario(nombre, apellido, alias, email, password, fechaNac);

            // Creamos nuevo usuario desde usuarioService.
            Usuario usuarioCreado = usuarioService.crearUsuario(usuario);
            return ResponseEntity.ok(usuarioCreado);
        } catch (DateTimeParseException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Formato de fecha incorrecto.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Endpoint para buscar usuarios por nombre.
    @GetMapping("/admin/usuarios/buscar")
    public ResponseEntity<?> buscarUsuarios(@RequestParam String subcadena) {
        List<Usuario> usuarios = usuarioService.buscarUsuarios(subcadena);

        if (!usuarios.isEmpty()) {
            return ResponseEntity.ok(usuarios);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No se han encontrado usuarios.");
        }
    }

    // Endpoint para actualizar un usuario existente.
    @PutMapping("/usuario/actualizar")
    public ResponseEntity<?> actualizarUsuario(@RequestParam("email")
                                                   @NotBlank(message = "El campo email es obligatorio.")
                                                   @Email(message = "El formato de email debe ser válido.") String email,
                                               @RequestParam(value = "alias", required = false) String alias,
                                               @RequestParam(value = "password", required = false) String password,
                                               @RequestParam(value = "fechaNac", required = false) String fechaNacStr,
                                               @RequestParam(value = "sexo", required = false) String sexo,
                                               @RequestParam(value = "ciudad", required = false) String ciudad,
                                               @RequestParam(value = "avatarURL", required = false) String avatarURL) {
        try {
            Usuario usuario = null;
            // Recuperamos el usuario existente.
            // Se usa el email como identificador para usuario.
            usuario = usuarioService.obtenerUsuarioPorEmail(email);

            if (usuario != null) {
                // Actualizamos los campos si se han pasado valores.
                if (validarCampo(alias)) {
                    usuario.setAlias(alias);
                }
                if (validarCampo(password)) {
                    // usuario.setPassword(passwordEncoder.encode(password));
                    usuario.setPassword(password);
                }
                if (validarCampo(fechaNacStr)) {
                    try {
                        LocalDate fechaNac = LocalDate.parse(fechaNacStr);
                        usuario.setFechaNac(fechaNac);
                    } catch (DateTimeException e) {
                        return ResponseEntity.badRequest().body("Formato de fecha incorrecto.");
                    }
                }
                if (validarCampo(sexo)) {
                    usuario.setSexo(sexo);
                }
                if (validarCampo(ciudad)) {
                    usuario.setCiudad(ciudad);
                }
                if (validarCampo(avatarURL)) {
                    usuario.setAvatarURL(avatarURL);
                }

                // Delegamos la actualización al servicio.
                Usuario usuarioActualizado = usuarioService.actualizarUsuario(usuario);
                return ResponseEntity.ok(usuarioActualizado);
            } else {
              return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado.");
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/usuario/{idUsuario}")
    public ResponseEntity<?> eliminarUsuario(@PathVariable Long idUsuario) {
        try {
            // Cambia el estado de eliminado a true.
            usuarioService.eliminarUsuario(idUsuario);
            return ResponseEntity.ok().body("Usuario eliminado con éxito.");
        } catch (RuntimeException e) {
            if (e.getMessage().equals("Usuario no encontrado.")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
            } else if (e.getMessage().equals("El usuario ya esta eliminado.")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error inesperado: \n"
                    + e.getMessage());
        }
    }
}
