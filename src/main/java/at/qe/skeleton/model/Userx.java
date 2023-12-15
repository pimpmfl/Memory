package at.qe.skeleton.model;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.data.domain.Persistable;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;
import java.util.Set;

/**
 * Entity representing users.
 */
@Entity
@Table(name = "userx")
public class Userx implements Persistable<String>, Serializable, Comparable<Userx> {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(length = 100)
    private String username;
    private String password;
    private String firstName;
    private String lastName;
    private String email;

    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createDate;
    @Column(nullable=false)
    boolean enabled;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private UserxRole role;
    @OneToMany(mappedBy = "author")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Set<Deck> createdDecks;

    @OneToMany(mappedBy = "id.subscriber")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Set<SubscribedDecks> subscribedDecks;

    @OneToMany(mappedBy = "id.user")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Set<CardRating> ratings;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public UserxRole getRole() {
        return role;
    }

    public void setRole(UserxRole role) {
        this.role = role;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Set<Deck> getCreatedDecks() {
        return createdDecks;
    }

    public void setCreatedDecks(Set<Deck> createdDecks) {
        this.createdDecks = createdDecks;
    }

    public Set<SubscribedDecks> getSubscribedDecks() {
        return subscribedDecks;
    }

    public void setSubscribedDecks(Set<SubscribedDecks> subscribedDecks) {
        this.subscribedDecks = subscribedDecks;
    }

    public Set<CardRating> getRatings() {
        return ratings;
    }

    public void setRatings(Set<CardRating> ratings) {
        this.ratings = ratings;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + Objects.hashCode(this.username);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof Userx)) {
            return false;
        }
        final Userx other = (Userx) obj;
        return Objects.equals(this.getId(), other.getId());
    }

    @Override
    public String toString() {
        return "at.qe.skeleton.model.User[ id=" + username + " ]";
    }

    @Override
    public String getId() {
        return getUsername();
    }

    public void setId(String id) {
        setUsername(id);
    }

    @Override
    public boolean isNew() {
        return (null == createDate);
    }

    @Override
    public int compareTo(Userx o) {
        return this.username.compareTo(o.getUsername());
    }
}
