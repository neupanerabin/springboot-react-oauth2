package org.auth2.oauth2learnapp.entity;

/*
 * @author : rabin
 */

import jakarta.persistence.*;
import lombok.*;
import org.auth2.oauth2learnapp.enums.Role;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)    // store enum name, not ordinal
    private Role role;

    private String image;
}
