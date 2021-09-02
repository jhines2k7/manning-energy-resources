package com.hines.james;

import io.confluent.kafka.serializers.AbstractKafkaAvroSerDeConfig;
import io.confluent.kafka.serializers.KafkaAvroSerializer;
import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerializer;
import io.dropwizard.Application;
import io.dropwizard.jdbi3.JdbiFactory;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.jdbi.v3.core.Jdbi;

import java.util.Properties;

public class KafkaEnergyApplication extends Application<KafkaEnergyConfiguration> {
    private static KafkaProducer<String, DeviceEvent> kafkaProducer;
    private static KafkaEnergyApplication kafkaEnergyApplication;

    public static void main(final String[] args) throws Exception{
        Properties properties = new Properties();
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "http://192.168.99.106:9092");
        properties.put(ProducerConfig.ACKS_CONFIG, "all");
        properties.put(ProducerConfig.RETRIES_CONFIG, 0);

        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, SpecificAvroSerializer.class);
        properties.put(AbstractKafkaAvroSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, "http://192.168.99.106:8081");

        kafkaProducer = new KafkaProducer<>(properties);

        kafkaEnergyApplication = new KafkaEnergyApplication();
        kafkaEnergyApplication.run(args);
    }

    @Override
    public String getName() {
        return "kafka-energy";
    }

    @Override
    public void initialize(final Bootstrap<KafkaEnergyConfiguration> bootstrap){
        bootstrap.addCommand(new KafkaStreamsCommand(kafkaEnergyApplication));
    }

    @Override
    public void run(final KafkaEnergyConfiguration kafkaEnergyConfiguration,
                    final Environment environment) {
        final JdbiFactory factory = new JdbiFactory();
        final Jdbi jdbi = factory.build(environment, kafkaEnergyConfiguration.getDataSourceFactory(), "postgresql");
        final DeviceEventDao dao = jdbi.onDemand(DeviceEventDao.class);
        final DeviceEventResource deviceEventResource = new DeviceEventResource(kafkaProducer, dao);

        environment.jersey().register(deviceEventResource);
    }
}
