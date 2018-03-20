import captcha.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Calendar;
import java.util.HashMap;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@WebAppConfiguration
public class CaptchaControllerTest {
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private ClientRepository clients;
    private Client client;
    private Client client2;

    @Autowired
    private CaptchaTaskRepository captchaTasks;

    @Autowired
    private TokenRepository tokens;

    @Before
    public void init() throws Exception {
        // Set testing mode
        System.setProperty("production", "0");
        // Set ttl 5 seconds
        System.setProperty("ttl", "5");
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).dispatchOptions(true).build();
        mockMvc.perform(get("/client/register"));
        mockMvc.perform(get("/client/register"));
        client = clients.findById(1L).get();
        client2 = clients.findById(2L).get();
    }

    @Test
    public void newTask() throws Exception {
        mockMvc.perform(get("/captcha/new?" + "public=" + client.getPublicKey()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.request", Matchers.any(String.class)))
                .andExpect(jsonPath("$.answer", Matchers.any(String.class)));
    }

    @Test
    public void newTaskClientNotFoundError() throws Exception {
        mockMvc.perform(get("/captcha/new?public=not-exist"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void getImageClientNotFoundError() throws Exception {
        String response = mockMvc.perform(get("/captcha/new?public=" + client.getPublicKey()))
                .andReturn().getResponse().getContentAsString();
        ObjectMapper mapper = new ObjectMapper();
        HashMap o = mapper.readValue(response, HashMap.class);
        mockMvc.perform(get("/captcha/image?public=not-exist&request=" + o.get("request")))
                .andExpect(status().isNotFound());
    }

    @Test
    public void getImageTaskNotFoundError() throws Exception {
        mockMvc.perform(get("/captcha/image?public=" + client.getPublicKey() + "&request=not-exist"))
                .andExpect(status().isNotFound());
    }

    private HashMap newTaskResponse() throws Exception {
        String responseJson = mockMvc.perform(get("/captcha/new?public=" + client.getPublicKey()))
                .andReturn()
                .getResponse()
                .getContentAsString();
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(responseJson, HashMap.class);
    }

    @Test
    public void getImageTaskNotBelongToClientError() throws Exception {
        HashMap response = newTaskResponse();

        mockMvc.perform(get("/captcha/image?public=" + client2.getPublicKey()
                + "&request=" + response.get("request")))
                .andExpect(status().isConflict());
    }

    @Test
    public void getImageTimeExpiredError() throws Exception {
        HashMap response = newTaskResponse();

        CaptchaTask captchaTask = captchaTasks.findById(response.get("request").toString()).get();

        while (captchaTask.getExpireDate().after(Calendar.getInstance().getTime())) { }

        mockMvc.perform(get("/captcha/image?public=" + client.getPublicKey()
                + "&request=" + response.get("request")))
                .andExpect(status().isRequestTimeout());

        assert !captchaTasks.findById(captchaTask.getId()).isPresent();
    }

    @Test
    public void getImage() throws Exception {
        HashMap response = newTaskResponse();

        mockMvc.perform(get("/captcha/image?public=" + client.getPublicKey() + "&request=" + response.get("request")))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.IMAGE_PNG_VALUE));
    }

    @Test
    public void solveClientNotFoundError() throws Exception {
        HashMap response = newTaskResponse();

        mockMvc.perform(post("/captcha/solve")
                .param("public", "not-exist")
                .param("request", response.get("request").toString())
                .param("answer", response.get("answer").toString()))
                .andExpect(status().isNotFound());
    }

    @Test
    public void solveTaskNotFoundError() throws Exception {
        HashMap response = newTaskResponse();

        mockMvc.perform(post("/captcha/solve")
                .param("public", client.getPublicKey())
                .param("request", "not-exist")
                .param("answer", "something"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void solveTaskNotBelongToClientError() throws Exception {
        HashMap response = newTaskResponse();

        mockMvc.perform(post("/captcha/solve")
                .param("public", client2.getPublicKey())
                .param("request", response.get("request").toString())
                .param("answer", response.get("answer").toString()))
                .andExpect(status().isConflict());
    }

    @Test
    public void solveWrongAnswerError() throws Exception {
        HashMap response = newTaskResponse();

        mockMvc.perform(post("/captcha/solve")
                .param("public", client.getPublicKey())
                .param("request", response.get("request").toString())
                .param("answer", "wrong-answer"))
                .andExpect(status().isConflict()).andReturn().getResponse();
    }

    @Test
    public void solveTimeExpiredError() throws Exception {
        HashMap response = newTaskResponse();

        CaptchaTask captchaTask = captchaTasks.findById(response.get("request").toString()).get();

        while (captchaTask.getExpireDate().after(Calendar.getInstance().getTime())) { }

        mockMvc.perform(post("/captcha/solve")
                .param("public", client.getPublicKey())
                .param("request", response.get("request").toString())
                .param("answer", response.get("answer").toString()))
                .andExpect(status().isRequestTimeout());

        assert !captchaTasks.findById(response.get("request").toString()).isPresent();
    }

    @Test
    public void solve() throws Exception {
        HashMap response = newTaskResponse();

        mockMvc.perform(post("/captcha/solve")
                .param("public", client.getPublicKey())
                .param("request", response.get("request").toString())
                .param("answer", response.get("answer").toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response", Matchers.any(String.class)));

        assert !captchaTasks.findById(response.get("request").toString()).isPresent();
    }

    private HashMap solveResponse(HashMap responseTask) throws Exception {
        String responseJson = mockMvc.perform(post("/captcha/solve")
                .param("public", client.getPublicKey())
                .param("request", responseTask.get("request").toString())
                .param("answer", responseTask.get("answer").toString()))
                .andReturn()
                .getResponse()
                .getContentAsString();
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(responseJson, HashMap.class);
    }

    @Test
    public void verifyTokenClientNotFoundError() throws Exception {
        HashMap response = solveResponse(newTaskResponse());

        mockMvc.perform(get("/captcha/verify?secret=not-exist"
                + "&response=" + response.get("response")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", Matchers.is(false)))
                .andExpect(jsonPath("$.errorCode", Matchers.is("Client not found")));

    }

    @Test
    public void verifyTokenTokenNotFoundError() throws Exception {
        HashMap response = solveResponse(newTaskResponse());

        mockMvc.perform(get("/captcha/verify?secret=" + client.getPrivateKey()
                + "&response=not-exist"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", Matchers.is(false)))
                .andExpect(jsonPath("$.errorCode", Matchers.is("Token not found")));

    }

    @Test
    public void verifyTokenTokenNotBelongToClient() throws Exception {
        HashMap response = solveResponse(newTaskResponse());

        mockMvc.perform(get("/captcha/verify?secret=" + client2.getPrivateKey()
                + "&response=" + response.get("response")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", Matchers.is(false)))
                .andExpect(jsonPath("$.errorCode", Matchers.is("Token do not belong to client")));
    }

    @Test
    public void verifyToken() throws Exception {
        HashMap response = solveResponse(newTaskResponse());

        mockMvc.perform(get("/captcha/verify?secret=" + client.getPrivateKey()
                + "&response=" + response.get("response")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", Matchers.is(true)))
                .andExpect(jsonPath("$.errorCode", Matchers.nullValue()));

        assert !tokens.findById(response.get("response").toString()).isPresent();
    }

}
