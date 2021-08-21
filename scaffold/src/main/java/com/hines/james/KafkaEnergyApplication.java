package com.hines.james;

import io.confluent.kafka.serializers.AbstractKafkaAvroSerDeConfig;
import io.confluent.kafka.serializers.KafkaAvroSerializer;
import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerializer;
import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;

public class KafkaEnergyApplication extends Application<KafkaEnergyConfiguration> {
    private static KafkaProducer<String, DeviceEvent> kafkaProducer;

    public static void main(final String[] args) throws Exception{
        Properties properties = new Properties();
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "http://192.168.99.106:9092");
        properties.put(ProducerConfig.ACKS_CONFIG, "all");
        properties.put(ProducerConfig.RETRIES_CONFIG, 0);

        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, SpecificAvroSerializer.class);
        properties.put(AbstractKafkaAvroSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, "http://192.168.99.106:8081");

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
