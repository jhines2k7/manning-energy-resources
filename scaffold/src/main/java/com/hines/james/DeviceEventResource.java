package com.hines.james;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.codehaus.jackson.map.ObjectMapper;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.Objects;

@Path("/")
public class DeviceEventResource {
    private final KafkaProducer kafkaProducer;
    private final DeviceEventDao dao;

    public DeviceEventResource(KafkaProducer<String, DeviceEvent> kafkaProducer, DeviceEventDao dao) {
        this.kafkaProducer = kafkaProducer;
        this.dao = dao;
    }

    @POST
    @Path("send/{uuid}")
    @Consumes({MediaType.APPLICATION_OCTET_STREAM, MediaType.APPLICATION_JSON})
    @Produces(MediaType.APPLICATION_JSON)
    public Response createDeviceEvent(@PathParam("uuid") String uuid, String event) {
        System.out.println("incoming event: " + event);
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            DeviceEventDTO deviceEventDTO = objectMapper.readValue(event, DeviceEventDTO.class);

            DeviceEvent.Builder deviceEventBuilder = DeviceEvent.newBuilder();
            deviceEventBuilder.setChargingSource(deviceEventDTO.getChargingSource());
            deviceEventBuilder.setProcessor4Temp(deviceEventDTO.getProcessor4Temp());
            deviceEventBuilder.setDeviceId(deviceEventDTO.getDeviceId());
            deviceEventBuilder.setProcessor2Temp(deviceEventDTO.getProcessor2Temp());
            deviceEventBuilder.setProcessor1Temp(deviceEventDTO.getProcessor1Temp());
            deviceEventBuilder.setCharging(deviceEventDTO.getCharging());
            deviceEventBuilder.setCurrentCapacity(deviceEventDTO.getCurrentCapacity());
            deviceEventBuilder.setInverterState(deviceEventDTO.getInverterState());
            deviceEventBuilder.setModuleLTemp(deviceEventDTO.getModuleLTemp());
            deviceEventBuilder.setModuleRTemp(deviceEventDTO.getModuleRTemp());
            deviceEventBuilder.setProcessor3Temp(deviceEventDTO.getProcessor3Temp());
            deviceEventBuilder.setSoCRegulator(deviceEventDTO.getSoCRegulator());
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
