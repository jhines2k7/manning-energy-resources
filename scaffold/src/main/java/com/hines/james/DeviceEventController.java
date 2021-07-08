package com.hines.james;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/")
public class DeviceEventController {
    @POST
    @Path("send/{uuid}")
    @Consumes({MediaType.APPLICATION_OCTET_STREAM, MediaType.APPLICATION_JSON})
    @Produces(MediaType.APPLICATION_JSON)
    public Response createDeviceEvent(@PathParam("uuid") String uuid, String event) {
        System.out.println("incoming event: " + event);

        return Response.accepted().build();
    }
}
