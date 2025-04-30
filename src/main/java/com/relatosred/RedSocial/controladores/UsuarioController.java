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

    // Usamos constructor en lugar de @Autowired para inyectar.
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
    public ResponseEntity<Usuario> crearUsuario(@RequestParam(value = "nombre", required = true)
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

        LocalDate fechaNac;
        try {
            //DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            fechaNac = LocalDate.parse(fechaNacStr);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Formato de fecha incorrecto (yyyy-MM-dd).");
        }

            Usuario usuario = new Usuario(nombre, apellido, alias, email, password, fechaNac);

            // Creamos nuevo usuario desde usuarioService.
            Usuario usuarioCreado = usuarioService.crearUsuario(usuario);
            return ResponseEntity.status(HttpStatus.CREATED).body(usuarioCreado);
    }

    // Endpoint para buscar usuarios por nombre.
    @GetMapping("/admin/usuarios/buscar")
    public ResponseEntity<List<Usuario>> buscarUsuarios(@RequestParam String subcadena) {
        List<Usuario> usuarios = usuarioService.buscarUsuarios(subcadena);
        // Devolvemos lista de usuarios aunque esté vacía.
        return ResponseEntity.ok(usuarios);
    }

    // Endpoint para actualizar un usuario existente.
    @PutMapping("/usuario/actualizar")
    public ResponseEntity<Usuario> actualizarUsuario(@RequestParam("email")
                                                   @NotBlank(message = "El campo email es obligatorio.")
                                                   @Email(message = "El formato de email debe ser válido.") String email,
                                               @RequestParam(value = "alias", required = false) String alias,
                                               @RequestParam(value = "password", required = false) String password,
                                               @RequestParam(value = "fechaNac", required = false) String fechaNacStr,
                                               @RequestParam(value = "sexo", required = false) String sexo,
                                               @RequestParam(value = "ciudad", required = false) String ciudad,
                                               @RequestParam(value = "avatarURL", required = false) String avatarURL) {
            // Recuperamos el usuario existente.
            // Se usa el email como identificador para usuario.
            Usuario usuario = usuarioService.obtenerUsuarioPorEmail(email);

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
                        throw new IllegalArgumentException("Formato de fecha incorrecto (yyyy-MM-dd).");
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

                // Delegamos la actualización a UsuarioService.
                Usuario usuarioActualizado = usuarioService.actualizarUsuario(usuario);
                return ResponseEntity.ok(usuarioActualizado);
    }

    @PutMapping("/usuario/{idUsuario}")
    public ResponseEntity<String> eliminarUsuario(@PathVariable Long idUsuario) {
            // Cambia el estado de eliminado a true.
            usuarioService.eliminarUsuario(idUsuario);
            return ResponseEntity.ok().body("Usuario eliminado con éxito.");
    }
}