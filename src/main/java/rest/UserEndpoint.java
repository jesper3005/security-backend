package rest;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dto.UserDTO;
import facade.UserFacade;
import exceptions.AuthenticationException;
import java.text.ParseException;
import utils.PuSelector;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/user")
public class UserEndpoint {

    private static final UserFacade USER_FACADE = UserFacade.getInstance(PuSelector.getEntityManagerFactory("pu"));
    private static final Gson GSON = new Gson();

    @GET
    @Path("/information")
    @Produces(MediaType.APPLICATION_JSON)
    public Response login(String jsonString) throws AuthenticationException {
        JsonObject json = new JsonParser().parse(jsonString).getAsJsonObject();
        String email = json.get("email").getAsString();
        String token = json.get("token").getAsString();
        try {
            UserDTO user = USER_FACADE.getUserInformation(email, token);
            return Response.ok().entity(GSON.toJson(user)).build();
        } catch (AuthenticationException ex) {
            throw (AuthenticationException) ex;
        }
    }

    @GET
    @Path("/delete")
    @Produces(MediaType.APPLICATION_JSON)
    public Response delete(String jsonString) throws AuthenticationException {
        JsonObject json = new JsonParser().parse(jsonString).getAsJsonObject();
        String email = json.get("email").getAsString();
        String token = json.get("token").getAsString();
        try {
            USER_FACADE.deleteUser(email, token);
            return Response.ok().build();
        } catch (AuthenticationException ex) {
            throw (AuthenticationException) ex;
        }
    }

    @GET
    @Path("/attempts")
    @Produces(MediaType.APPLICATION_JSON)
    public Response attempts(String jsonString) throws AuthenticationException {
        JsonObject json = new JsonParser().parse(jsonString).getAsJsonObject();
        String email = json.get("email").getAsString();
        String token = json.get("token").getAsString();
        try {
            USER_FACADE.sendMailWithAttempts(email, token);
            return Response.ok().build();
        } catch (AuthenticationException ex) {
            throw (AuthenticationException) ex;
        }
    }
}
