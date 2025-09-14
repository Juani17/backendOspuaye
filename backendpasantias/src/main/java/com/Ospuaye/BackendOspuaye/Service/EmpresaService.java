package com.Ospuaye.BackendOspuaye.Service;

import com.Ospuaye.BackendOspuaye.Entity.Domicilio;
import com.Ospuaye.BackendOspuaye.Entity.Empresa;
import com.Ospuaye.BackendOspuaye.Repository.BeneficiarioRepository;
import com.Ospuaye.BackendOspuaye.Repository.DomicilioRepository;
import com.Ospuaye.BackendOspuaye.Repository.EmpresaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class EmpresaService extends BaseService<Empresa, Long> {

    private final EmpresaRepository empresaRepository;
    private final BeneficiarioRepository beneficiarioRepository;
    private final DomicilioRepository domicilioRepository;


    public EmpresaService(EmpresaRepository empresaRepository,
                          BeneficiarioRepository beneficiarioRepository, DomicilioRepository domicilioRepository) {
        super(empresaRepository);
        this.empresaRepository = empresaRepository;
        this.beneficiarioRepository = beneficiarioRepository;
        this.domicilioRepository = domicilioRepository;
    }

    @Override
    @Transactional
    public Empresa crear(Empresa entity) throws Exception {
        if (entity == null) throw new IllegalArgumentException("La empresa no puede ser nula");
        if (entity.getCuit() == null || entity.getCuit().isBlank())
            throw new IllegalArgumentException("El CUIT es obligatorio");
        String cuit = entity.getCuit().trim();
        if (!cuit.matches("\\d{11}"))
            throw new IllegalArgumentException("CUIT inválido (debe tener 11 dígitos)");
        if (empresaRepository.existsByCuit(cuit))
            throw new IllegalArgumentException("Ya existe una empresa con ese CUIT");

        if (entity.getRazonSocial() == null || entity.getRazonSocial().isBlank())
            throw new IllegalArgumentException("La razón social es obligatoria");

        entity.setCuit(cuit);
        if (entity.getActivo() == null) entity.setActivo(true);
        if (entity.getDomicilio() != null && entity.getDomicilio().getId() != null) {
            Domicilio domicilio = domicilioRepository.findById(entity.getDomicilio().getId())
                    .orElseThrow(() -> new RuntimeException("Domicilio no encontrado"));
            entity.setDomicilio(domicilio);
        }

        return empresaRepository.save(entity);
    }

    @Override
    @Transactional
    public Empresa actualizar(Empresa entity) throws Exception {
        if (entity == null || entity.getId() == null)
            throw new IllegalArgumentException("La empresa o su ID no pueden ser nulos");

        Empresa existente = empresaRepository.findById(entity.getId())
                .orElseThrow(() -> new IllegalArgumentException("Empresa no encontrada"));

        // Validar CUIT
        if (entity.getCuit() != null && !entity.getCuit().isBlank()) {
            String nuevoCuit = entity.getCuit().trim();
            if (!nuevoCuit.matches("\\d{11}"))
                throw new IllegalArgumentException("CUIT inválido (11 dígitos)");
            if (!nuevoCuit.equals(existente.getCuit()) && empresaRepository.existsByCuit(nuevoCuit))
                throw new IllegalArgumentException("Otro registro ya utiliza ese CUIT");
            existente.setCuit(nuevoCuit);
        }

        // Validar razón social
        if (entity.getRazonSocial() != null && !entity.getRazonSocial().isBlank()) {
            existente.setRazonSocial(entity.getRazonSocial().trim());
        }

        // Validar estado activo/desactivo
        if (entity.getActivo() != null) {
            if (!entity.getActivo()) {
                var asociados = beneficiarioRepository.findByEmpresa(existente);
                if (!asociados.isEmpty()) {
                    throw new IllegalArgumentException("No se puede desactivar la empresa porque tiene beneficiarios asociados");
                }
            }
            existente.setActivo(entity.getActivo());
        }

        return empresaRepository.save(existente);
    }

    @Transactional(readOnly = true)
    public Empresa buscarPorCuit(String cuit) {
        if (cuit == null || cuit.isBlank()) return null;
        return empresaRepository.findByCuit(cuit.trim()).orElse(null);
    }

    @Transactional(readOnly = true)
    public List<Empresa> listarPorActivo(Boolean activo) {
        return empresaRepository.findByActivo(activo);
    }

    @Override
    @Transactional
    public void eliminar(Long id) throws Exception {
        if (id == null) throw new IllegalArgumentException("El ID no puede ser nulo");
        Empresa e = empresaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Empresa no encontrada"));

        var asociados = beneficiarioRepository.findByEmpresa(e);
        if (!asociados.isEmpty()) {
            throw new IllegalArgumentException("No se puede eliminar la empresa porque tiene beneficiarios asociados.");
        }
        super.eliminar(id);
    }
}
