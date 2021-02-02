package ru.khurry.voting.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import net.bytebuddy.asm.Advice;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Entity
@Table(name = "users")
public class User extends AbstractBaseEntity {
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "role")
    @ElementCollection(fetch = FetchType.EAGER)
    private Set<Role> roles;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "menu_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Menu menu;

    @NotBlank
    @Size(min = 2, max = 100)
    private String name;

    @Email
    @NotBlank
    @Size(max = 100)
    private String email;

    @NotBlank
    @Size(min = 5, max = 100)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    private LocalDateTime registered;

    public User() {}

    public User(Integer id, String name, String email, String password, LocalDateTime dateTime, Role... roles) {
        this(id, name, email, password, dateTime, Stream.of(roles).collect(Collectors.toSet()));

    }

    public User(Integer id, String name, String email, String password, LocalDateTime dateTime, Set<Role> roles) {
        this.id = id;
        this.roles = roles;
        this.name = name;
        this.email = email;
        this.password = password;
        this.registered = dateTime;
    }

    public User(User user) {
        this.id = user.id;
        this.roles = user.roles;
        this.name = user.name;
        this.email = user.email;
        this.password = user.password;
        this.registered = user.registered;
        this.menu = user.getMenu();
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Set<Role> getRoles() {
        return roles;
    }


    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public Menu getMenu() {
        return menu;
    }

    public void setMenu(Menu menu) {
        this.menu = menu;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setRegistered(LocalDateTime registered) {
        this.registered = registered;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public LocalDateTime getRegistered() {
        return registered;
    }
}

