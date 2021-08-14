package com.hines.james;

import io.confluent.kafka.serializers.KafkaAvroSerializer;
import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;

public class KafkaEnergyApplication extends Application<KafkaEnergyConfiguration> {
    private static KafkaProducer<String, DeviceEvent> kafkaProducer;

    public static void main(final String[] args) throws Exception{
        Properties properties = new Properties();
        properties.setProperty("bootstrap.servers", "http://192.168.99.106:9092");
        properties.setProperty("acks", "1");
        properties.setProperty("retries", "10");

        properties.setProperty("key.serializer", StringSerializer.class.getName());
        properties.setProperty("value.serializer", KafkaAvroSerializer.class.getName());
        properties.setProperty("schema.registry.url", "http://192.168.99.106:8081");

        kafkaProducer = new KafkaProducer<>(properties);

        new KafkaEnergyApplication().run(args);
    }

    @Override
    public String getName() {
        return "kafka-energy";
    }

    @Override
    public void initialize(final Bootstrap<KafkaEnergyConfiguration> bootstrap){
        bootstrap.addCommand(new KafkaStreamsCommand());
    }

    @Override
    public void run(final KafkaEnergyConfiguration kafkaEnergyConfiguration,
                    final Environment environment) {
        final DeviceEventResource deviceEventResource = new DeviceEventResource(kafkaProducer);

        environment.jersey().register(deviceEventResource);
    }
}
