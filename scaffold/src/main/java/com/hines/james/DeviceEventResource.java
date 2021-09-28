package com.hines.james;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.codehaus.jackson.map.ObjectMapper;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.HashMap;
import java.util.Objects;

@Path("/")
public class DeviceEventResource {
    private final KafkaProducer kafkaProducer;
    private final DeviceDao dao;

    public DeviceEventResource(KafkaProducer<String, DeviceEvent> kafkaProducer, DeviceDao dao) {
        this.kafkaProducer = kafkaProducer;
        this.dao = dao;
    }

    @GET
    @Path("charging/{uuid}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getDeviceCharging(@PathParam("uuid") String uuid) {
        HashMap<String, String> deviceCharging = dao.getChargeState("device_events", uuid);

        return Response
                .status(Response.Status.OK)
                .entity(deviceCharging)
                .build();
    }

    @POST
    @Path("send/{uuid}")
    @Consumes({MediaType.APPLICATION_OCTET_STREAM, MediaType.APPLICATION_JSON})
    @Produces(MediaType.APPLICATION_JSON)
    public Response createDeviceEvent(@PathParam("uuid") String uuid, String event) {
        System.out.println("incoming event: " + event);
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            Device device = objectMapper.readValue(event, Device.class);

            DeviceEvent.Builder deviceEventBuilder = DeviceEvent.newBuilder();
            deviceEventBuilder.setChargingSource(device.getChargingSource());
            deviceEventBuilder.setProcessor4Temp(device.getProcessor4Temp());
            deviceEventBuilder.setDeviceId(device.getDeviceId());
            deviceEventBuilder.setProcessor2Temp(device.getProcessor2Temp());
            deviceEventBuilder.setProcessor1Temp(device.getProcessor1Temp());
            deviceEventBuilder.setCharging(device.getCharging());
            deviceEventBuilder.setCurrentCapacity(device.getCurrentCapacity());
            deviceEventBuilder.setInverterState(device.getInverterState());
            deviceEventBuilder.setModuleLTemp(device.getModuleLTemp());
            deviceEventBuilder.setModuleRTemp(device.getModuleRTemp());
            deviceEventBuilder.setProcessor3Temp(device.getProcessor3Temp());
            deviceEventBuilder.setSoCRegulator(device.getSoCRegulator());
            DeviceEvent deviceEvent = deviceEventBuilder.build();

            System.out.println("device event: " + deviceEvent);

            String topic = "kafka_energy_events_4";

            ProducerRecord<String, DeviceEvent> producerRecord = new ProducerRecord<>(topic, deviceEvent.getDeviceId().toString(), deviceEvent);

            kafkaProducer.send(producerRecord, (recordMetadata, e) -> {
                if(Objects.isNull(e)) {
                    System.out.println("Success!");
                    System.out.println(recordMetadata.toString());
                } else {
                    e.printStackTrace();
                }
            });

            kafkaProducer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return Response.accepted().build();
    }
}
