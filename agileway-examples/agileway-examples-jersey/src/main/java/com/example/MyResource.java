package com.example;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import java.util.Date;

/**
 * Root resource (exposed at "myresource" path)
 */
@Path("myresource")
public class MyResource {

    /**
     * Method handling HTTP GET requests. The returned object will be sent
     * to the client as "text/plain" media type.
     *
     * @return String that will be returned as a text/plain response.
     */
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String getText() {
        return "Got text!";
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getJson() {
        return "Got json! , 你好呀";
    }

    @Path("/object")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public P getObject() {
        P p = new P();
        p.id = "p-0001";
        p.intValue = 3;
        p.date = new Date();
        return p;
    }

    @Path("/bool")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public boolean getBoolean() {
        return true;
    }


    @Path("/exception")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getException() {
        throw new RuntimeException("any exception");
    }

    @Path("/webapplicationexception")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getWebApplicationException() {
        throw new WebApplicationException("web application exception");
    }

    static class P {
        String id;
        int intValue;
        Date date;

        public Date getDate() {
            return date;
        }

        public void setDate(Date date) {
            this.date = date;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public int getIntValue() {
            return intValue;
        }

        public void setIntValue(int intValue) {
            this.intValue = intValue;
        }
    }
}
