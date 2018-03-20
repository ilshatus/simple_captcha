package captcha;

import javax.persistence.*;
import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.Serializable;
import java.util.Date;

/**
 * Class which represents CAPTCHA task
 */
@Entity
public class CaptchaTask implements Serializable {
    private static final String FONT = "Arial"; // customize generating image font
    private static final int FONT_STYLE = Font.BOLD | Font.ITALIC; // customize generating image font style
    private static final int FONT_SIZE = 30; // customize generating image font size

    @Id
    private String id;
    private String answer;

    @ManyToOne
    @JoinColumn(referencedColumnName = "publicKey")
    private Client client;

    @Temporal(TemporalType.TIMESTAMP)
    private Date expireDate;

    public CaptchaTask() {
    }

    public CaptchaTask(String id, String answer, Client client, Date expireDate) {
        this.id = id;
        this.answer = answer;
        this.client = client;
        this.expireDate = expireDate;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public Date getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(Date expireDate) {
        this.expireDate = expireDate;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    /**
     * Generates image representation of task from answer
     * @return BufferedImage
     */
    public BufferedImage getImage() {
        Font font = new Font(FONT, FONT_STYLE, FONT_SIZE);
        AffineTransform affineTransform = new AffineTransform();
        affineTransform.setToTranslation(1, 1);
        FontRenderContext fontRenderContext = new FontRenderContext(affineTransform, false, false);
        TextLayout textLayout = new TextLayout(answer, font, fontRenderContext);

        double margin = FONT_SIZE / 5;
        double width = textLayout.getBounds().getMaxX() + 2 * margin;
        double height = 3 * (textLayout.getBounds().getMaxY() - textLayout.getBounds().getMinY());

        BufferedImage image = new BufferedImage((int) width, (int) height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graph = image.createGraphics();

        graph.setColor(Color.WHITE);
        graph.fillRect(0, 0, (int) width, (int) height);

        affineTransform.setToTranslation(margin, 2 * height / 3);
        graph.setColor(Color.BLACK);
        graph.draw(textLayout.getOutline(affineTransform));

        return image;
    }

    /**
     * Generates json string from object of this class:
     * In case of system property "production" is "0"
     *   json string looks like {"request":id,"answer":answer}, 'id' and 'answer' attributes of current class
     * Otherwise
     *   json string looks like {"request":id}, 'id' attribute of current class
     * @return json String
     */
    public String toJSON() {
        if (System.getProperty("production").equals("0")) {
            return "{\"request\":\"" + id + "\",\"answer\":\"" + answer + "\"}";
        }
        return "{\"request\":\"" + id + "\"}";
    }

}
