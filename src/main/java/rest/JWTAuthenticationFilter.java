package rest;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.SignedJWT;
import exceptions.AuthenticationException;

import javax.annotation.Priority;
import javax.annotation.security.DenyAll;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Context;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Provider
@Priority(Priorities.AUTHENTICATION)
public class JWTAuthenticationFilter implements ContainerRequestFilter {

    private static final List<Class<? extends Annotation>> securityAnnotations = Arrays.asList(DenyAll.class, PermitAll.class, RolesAllowed.class);
    @Context
    private ResourceInfo resourceInfo;

    @Override
    public void filter(ContainerRequestContext request) throws IOException {
        if (isSecuredResource()) {

            String token = request.getHeaderString("x-access-token");//
            if (token == null) {
                request.abortWith(exceptions.GenericExceptionMapper.makeErrRes(token, 403));
                return;
            }
            try {
                UserPrincipal user = getUserPrincipalFromTokenIfValid(token);
                //What if the client had logged out????
                request.setSecurityContext(new JWTSecurityContext(user, request));
            } catch (AuthenticationException | ParseException | JOSEException ex) {
                Logger.getLogger(JWTAuthenticationFilter.class.getName()).log(Level.SEVERE, null, ex);
                request.abortWith(exceptions.GenericExceptionMapper.makeErrRes("Token not valid (timed out?)", 403));
            }
        }
    }

    private boolean isSecuredResource() {
        for (Class<? extends Annotation> securityClass : securityAnnotations) {
            if (resourceInfo.getResourceMethod().isAnnotationPresent(securityClass)) {
                return true;
            }
        }
        for (Class<? extends Annotation> securityClass : securityAnnotations) {
            if (resourceInfo.getResourceClass().isAnnotationPresent(securityClass)) {
                return true;
            }
        }
        return false;
    }

    private UserPrincipal getUserPrincipalFromTokenIfValid(String token) throws ParseException, JOSEException, AuthenticationException {
        SignedJWT signedJWT = SignedJWT.parse(token);
        //Is it a valid token (generated with our shared key)
        JWSVerifier verifier = new MACVerifier(SharedSecret.getSharedKey());

        if (signedJWT.verify(verifier)) {
            if (new Date().getTime() > signedJWT.getJWTClaimsSet().getExpirationTime().getTime()) {
                throw new AuthenticationException("Your Token is no longer valid");
            }
            String name = signedJWT.getJWTClaimsSet().getClaim("name").toString();

            return new UserPrincipal(name);
        } else {
            throw new JOSEException("User could not be extracted from token");
        }
    }
}
