package com.api.Summit.API.service.impl;

import com.api.Summit.API.model.entities.Negocio;
import com.api.Summit.API.model.entities.User;
import com.api.Summit.API.model.repository.NegocioRepository;
import com.api.Summit.API.model.repository.UserRepository;
import com.api.Summit.API.view.dto.NegocioDTO;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NegocioService {

    private final NegocioRepository negocioRepository;
    private final UserRepository userRepository;

    // Obtener todos los negocios de un usuario
    @Transactional(readOnly = true)
    public List<NegocioDTO> getNegociosByUsuario(Long usuarioId) {
        User usuario = userRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + usuarioId));

        return usuario.getNegocios().stream()
                .map(NegocioDTO::fromNegocio)
                .collect(Collectors.toList());
    }

    // Obtener un negocio específico por ID (solo si pertenece al usuario)
    @Transactional(readOnly = true)
    public NegocioDTO getNegocioByIdAndUsuario(Long negocioId, Long usuarioId) {
        Negocio negocio = negocioRepository.findById(negocioId)
                .orElseThrow(() -> new RuntimeException("Negocio no encontrado con ID: " + negocioId));

        // Verificar que el usuario tenga acceso a este negocio
        boolean tieneAcceso = negocio.getUsuarios().stream()
                .anyMatch(user -> user.getId().equals(usuarioId));

        if (!tieneAcceso) {
            throw new RuntimeException("No tienes acceso a este negocio");
        }

        return NegocioDTO.fromNegocio(negocio);
    }

    // Crear nuevo negocio y asignarlo al usuario
    @Transactional
    public NegocioDTO createNegocio(NegocioDTO negocioDTO, Long usuarioId) {
        User usuario = userRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + usuarioId));

        Negocio negocio = Negocio.builder()
                .nombre(negocioDTO.getNombre())
                .descripcion(negocioDTO.getDescripcion())
                .build();

        Negocio negocioGuardado = negocioRepository.save(negocio);

        // Asignar el negocio al usuario
        usuario.addNegocio(negocioGuardado);
        userRepository.save(usuario);

        return NegocioDTO.fromNegocio(negocioGuardado);
    }

    // Actualizar negocio (solo si pertenece al usuario)
    @Transactional
    public NegocioDTO updateNegocio(Long negocioId, NegocioDTO negocioDTO, Long usuarioId) {
        Negocio negocio = negocioRepository.findById(negocioId)
                .orElseThrow(() -> new RuntimeException("Negocio no encontrado con ID: " + negocioId));

        // Verificar que el usuario tenga acceso a este negocio
        boolean tieneAcceso = negocio.getUsuarios().stream()
                .anyMatch(user -> user.getId().equals(usuarioId));

        if (!tieneAcceso) {
            throw new RuntimeException("No tienes permisos para modificar este negocio");
        }

        negocio.setNombre(negocioDTO.getNombre());
        negocio.setDescripcion(negocioDTO.getDescripcion());

        Negocio negocioActualizado = negocioRepository.save(negocio);
        return NegocioDTO.fromNegocio(negocioActualizado);
    }

    // Eliminar negocio (solo si pertenece al usuario)
    @Transactional
    public void deleteNegocio(Long negocioId, Long usuarioId) {
        Negocio negocio = negocioRepository.findById(negocioId)
                .orElseThrow(() -> new RuntimeException("Negocio no encontrado con ID: " + negocioId));

        // Verificar que el usuario tenga acceso a este negocio
        boolean tieneAcceso = negocio.getUsuarios().stream()
                .anyMatch(user -> user.getId().equals(usuarioId));

        if (!tieneAcceso) {
            throw new RuntimeException("No tienes permisos para eliminar este negocio");
        }

        // Remover el negocio de todos los usuarios antes de eliminar
        negocio.getUsuarios().forEach(usuario -> usuario.removeNegocio(negocio));
        negocioRepository.delete(negocio);
    }

    // Verificar si un usuario tiene acceso a un negocio
    @Transactional(readOnly = true)
    public boolean usuarioTieneAccesoANegocio(Long negocioId, Long usuarioId) {
        return negocioRepository.findById(negocioId)
                .map(negocio -> negocio.getUsuarios().stream()
                        .anyMatch(user -> user.getId().equals(usuarioId)))
                .orElse(false);
    }

    @Transactional
    public NegocioDTO createNegocioSinUsuario(NegocioDTO negocioDTO) {
        // Verificar si ya existe un negocio con el mismo nombre
        if (negocioRepository.existsByNombre(negocioDTO.getNombre())) {
            throw new RuntimeException("Ya existe un negocio con el nombre: " + negocioDTO.getNombre());
        }

        Negocio negocio = Negocio.builder()
                .nombre(negocioDTO.getNombre())
                .descripcion(negocioDTO.getDescripcion())
                .build();

        Negocio negocioGuardado = negocioRepository.save(negocio);
        return NegocioDTO.fromNegocio(negocioGuardado);
    }

    @Transactional
    public NegocioDTO asignarUsuarioANegocio(Long negocioId, Long usuarioId) {
        Negocio negocio = negocioRepository.findById(negocioId)
                .orElseThrow(() -> new RuntimeException("Negocio no encontrado con ID: " + negocioId));

        User usuario = userRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + usuarioId));

        // Verificar si el usuario ya tiene acceso a este negocio
        boolean yaTieneAcceso = negocio.getUsuarios().stream()
                .anyMatch(user -> user.getId().equals(usuarioId));

        if (yaTieneAcceso) {
            throw new RuntimeException("El usuario ya tiene acceso a este negocio");
        }

        // Asignar el negocio al usuario
        usuario.addNegocio(negocio);
        userRepository.save(usuario);

        return NegocioDTO.fromNegocio(negocio);
    }

    @Transactional
    public NegocioDTO removerUsuarioDeNegocio(Long negocioId, Long usuarioId) {
        Negocio negocio = negocioRepository.findById(negocioId)
                .orElseThrow(() -> new RuntimeException("Negocio no encontrado con ID: " + negocioId));

        User usuario = userRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + usuarioId));

        // Verificar si el usuario tiene acceso a este negocio
        boolean tieneAcceso = negocio.getUsuarios().stream()
                .anyMatch(user -> user.getId().equals(usuarioId));

        if (!tieneAcceso) {
            throw new RuntimeException("El usuario no tiene acceso a este negocio");
        }

        // Remover el negocio del usuario
        usuario.removeNegocio(negocio);
        userRepository.save(usuario);

        return NegocioDTO.fromNegocio(negocio);
    }

    @Transactional(readOnly = true)
    public List<NegocioDTO> getAllNegocios() {
        return negocioRepository.findAll().stream()
                .map(NegocioDTO::fromNegocio)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteNegocioAdmin(Long negocioId) {
        Negocio negocio = negocioRepository.findById(negocioId)
                .orElseThrow(() -> new RuntimeException("Negocio no encontrado con ID: " + negocioId));

        // Remover el negocio de todos los usuarios antes de eliminar
        negocio.getUsuarios().forEach(usuario -> usuario.removeNegocio(negocio));
        negocioRepository.delete(negocio);
    }

    @Transactional(readOnly = true)
    public Negocio findEntityById(Long id) {
        return negocioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Negocio no encontrado con ID: " + id));
    }
}
