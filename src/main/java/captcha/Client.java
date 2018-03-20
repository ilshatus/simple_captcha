package captcha;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Collection;

/**
 * Class which represents client
 */
@Entity
public class Client implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(unique = true)
    private String publicKey;

    @Column(unique = true)
    private String privateKey;

    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL, orphanRemoval = true)
    private Collection<CaptchaTask> captchaTasks;

    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL, orphanRemoval = true)
    private Collection<Token> tokens;

    public Client() {
    }

    public Client(String secretKey, String publicKey) {
        this.privateKey = secretKey;
        this.publicKey = publicKey;
    }

    @JsonIgnore
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @JsonProperty("public")
    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    @JsonProperty("secret")
    public String getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Client client = (Client) o;

        if (privateKey != null ? !privateKey.equals(client.privateKey) : client.privateKey != null) return false;
        return publicKey != null ? publicKey.equals(client.publicKey) : client.publicKey == null;
    }

    @Override
    public int hashCode() {
        int result = privateKey != null ? privateKey.hashCode() : 0;
        result = 31 * result + (publicKey != null ? publicKey.hashCode() : 0);
        return result;
    }
}
