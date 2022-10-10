package ch.surech.chronos.analyser.persistence.model;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "person")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Person {

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "graph_id", nullable = false, unique = true)
    private String graphId;

    @Column(name = "surname")
    private String surname;

    @Column(name = "given_name")
    private String givenName;

    @Column(name = "display_name")
    private String displayName;

    @Column(name = "mail")
    private String mail;

    @Column(name = "user_principal_name")
    private String userPrincipalName;
}
