package com.user_service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name="name", nullable = false)
    private String name;
    @Column(name="user_name", nullable = false, unique = true)
    private String userName;
    @Column(name="password", nullable = false)
    private String password;
    @Column(name="email", nullable = true, unique = true)
    private String email;
    @Enumerated(EnumType.STRING)
    private UserRole role;

    public UserRole getRole(){
        return (this.role == null) ? UserRole.USER : this.role;
    }
}
