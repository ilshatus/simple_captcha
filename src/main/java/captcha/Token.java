package captcha;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Class which represents verification token
 */
@Entity
public class Token implements Serializable {
    @Id
    private String value;

    @ManyToOne
    @JoinColumn(referencedColumnName = "privateKey")
    private Client client;

    public Token() {
    }

    public Token(String value, Client client) {
        this.value = value;
        this.client = client;
    }

    @JsonProperty("response")
    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @JsonIgnore
    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }
}
