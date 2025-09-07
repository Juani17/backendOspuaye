package com.Ospuaye.BackendOspuaye.Controller;

import com.Ospuaye.BackendOspuaye.Entity.Localidad;
import com.Ospuaye.BackendOspuaye.Service.LocalidadService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/localidades")
public class LocalidadController extends BaseController<Localidad, Long> {

    private final LocalidadService localidadService;

    public LocalidadController(LocalidadService localidadService) {
        super(localidadService);
        this.localidadService = localidadService;
    }

    @GetMapping("/activas")
    public ResponseEntity<?> listarActivas() {
        try {
            List<Localidad> lista = localidadService.listarActivas();
            return ResponseEntity.ok(lista);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/departamento/{departamentoId}")
    public ResponseEntity<?> listarPorDepartamento(@PathVariable Long departamentoId) {
        try {
            List<Localidad> lista = localidadService.listarPorDepartamento(departamentoId);
            return ResponseEntity.ok(lista);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}
