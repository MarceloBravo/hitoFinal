package com.mabc.e_shop.infrastructure.security;

import com.mabc.e_shop.infrastructure.persistence.entity.User;
import com.mabc.e_shop.infrastructure.persistence.repositories.UserJpaRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Adaptador de usuario del sistema al contrato {@link UserDetailsService}
 * de Spring Security.
 *
 * <p>Convierte la entidad persistida {@link User} en los datos que la
 * infraestructura de seguridad necesita: credencial, estado de actividad y
 * autorizaciones derivadas del rol.
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserJpaRepository userRepository;

    /**
     * Crea el servicio de detalles con el repositorio de usuarios.
     *
     * @param userRepository repositorio Spring Data de usuarios.
     */
    public UserDetailsServiceImpl(UserJpaRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + email));

        boolean isActive = user.getActive() != null && user.getActive();
        String authority = "ROLE_" + user.getRole().toUpperCase();

        return new org.springframework.security.core.userdetails.User(
            user.getEmail(),
            user.getPassword(),
            isActive,
            true,
            true,
            true,
            List.of(new SimpleGrantedAuthority(authority))
        );
    }
}