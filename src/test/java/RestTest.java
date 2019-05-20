import org.junit.Test;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

public class RestTest {

    @Test
    public void callRestAPI() {
        Client client = ClientBuilder.newClient();
        WebTarget target = client.target("http://localhost:9090/api/manager/startdownload?fileName=" +
                "https://s3.amazonaws.com/testingdownload/image1.jpg&outputFolder=/Users/rupashree");
        Response response = target.request().get();
        String value = response.readEntity(String.class);
        System.out.println(value);
        response.close();

    }
}