package secuso.org.privacyfriendlywerwolf.client;

import java.util.List;
import java.util.Random;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;


public class RestServiceImpl implements RestService {


    /*
     * (non-Javadoc)
     * @see rest.inter.RestInterface#login()
     */
    @GET
    @Path("/login")
    public Response login() {
        String clientID = String.valueOf(new Random().nextInt());
        System.out.println("Login: Generated Id: " + clientID);
        return Response.ok(clientID).build();
    }

    /*
     * (non-Javadoc)
     * @see rest.inter.RestInterface#logout()
     */
    @GET
    @Path("/{id}/logout")
    public Response logout(@PathParam("id") String id) {
        System.out.println("Logout");
        return Response.status(200).entity("Logout").build();
    }


}

