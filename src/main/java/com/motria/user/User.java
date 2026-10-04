package com.motria.user;

import com.motria.workshop.Workshop;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/** Cuenta con la que se inicia sesión (administrador del taller o técnico). */
@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    /** Hash BCrypt, nunca la contraseña en texto plano. */
    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private boolean active = true;

    @Column(nullable = false)
    private boolean mustChangePassword;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "workshop_id", nullable = false)
    private Workshop workshop;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_authority",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "authority_id"))
    private Set<Authority> authorities = new HashSet<>();

    public List<String> authorityNames() {
        return authorities.stream().map(Authority::getName).sorted().toList();
    }

    public boolean hasRole(String role) {
        return authorities.stream().anyMatch(authority -> authority.getName().equals(role));
    }
}
