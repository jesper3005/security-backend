package rest;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dto.UserDTO;
import facade.UserFacade;
import exceptions.AuthenticationException;
import java.text.ParseException;
import utils.PuSelector;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("user")
public class UserEndpoint {

    private static final UserFacade USER_FACADE = UserFacade.getInstance(PuSelector.getEntityManagerFactory("pu"));
    private static final Gson GSON = new Gson();

    @GET
    @Path("information")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response login(String jsonString) throws AuthenticationException, ParseException {
        JsonObject json = new JsonParser().parse(jsonString).getAsJsonObject();
        String token = json.get("token").getAsString();

        try {

            UserDTO user = USER_FACADE.getUserInformation(token);
            JsonObject responseJson = new JsonObject();
            responseJson.addProperty("user", GSON.toJson(user));
            return Response.ok(GSON.toJson(responseJson)).build();
        } catch (AuthenticationException ex) {
            throw (AuthenticationException) ex;
        }
    }
}
