package rest;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import entity.User;
import facade.UserFacade;
import exceptions.AuthenticationException;
import exceptions.GenericExceptionMapper;
import utils.PuSelector;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

@Path("login")
public class LoginEndpoint {

    public static final int TOKEN_EXPIRE_TIME = 1000 * 60 * 30; // ms * sec * min = 30 min
    public static final UserFacade userFacade = UserFacade.getInstance(PuSelector.getEntityManagerFactory("pu"));

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response login(String jsonString) throws AuthenticationException {
        JsonObject json = new JsonParser().parse(jsonString).getAsJsonObject();
        String email = json.get("email").getAsString();
        String password = json.get("password").getAsString();
        String ip = json.get("ip").getAsString();

        try {
            User user = userFacade.getVeryfiedUser(email, password, ip);
            JsonObject responseJson = new JsonObject();
            responseJson.addProperty("email", email);
            responseJson.addProperty("token", createToken(user));
            return Response.ok(new Gson().toJson(responseJson)).build();
        } catch (JOSEException | AuthenticationException ex) {
            if (ex instanceof AuthenticationException) {
                throw (AuthenticationException) ex;
            }
            Logger.getLogger(GenericExceptionMapper.class.getName()).log(Level.SEVERE, null, ex);
        }
        throw new AuthenticationException("Somthing went wrong! Please try again");
    }

    private String createToken(User user) throws JOSEException {
        String firstNameLetter = user.getFirstName().substring(0, 1);
        String lastNameLetter = user.getLastName().substring(0, 1);
        int ageTimesID = user.getAge() * user.getId();
        String name = firstNameLetter + lastNameLetter + ageTimesID;

        String issuer = "the_turtle_troopers";

        JWSSigner signer = new MACSigner(SharedSecret.getSharedKey());
        Date date = new Date();
        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .subject(name)
                .claim("id", user.getId())
                .claim("allowed", true)
                .claim("issuer", issuer)
                .issueTime(date)
                .expirationTime(new Date(date.getTime() + TOKEN_EXPIRE_TIME))
                .build();
        SignedJWT signedJWT = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claimsSet);
        signedJWT.sign(signer);
        return signedJWT.serialize();
    }
}
