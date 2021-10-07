package com.hines.james;

import io.confluent.kafka.serializers.AbstractKafkaAvroSerDeConfig;
import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerializer;
import io.dropwizard.Application;
import io.dropwizard.configuration.ResourceConfigurationSourceProvider;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.hibernate.HibernateBundle;
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

    private final HibernateBundle<KafkaEnergyConfiguration> hibernateBundle =
        new HibernateBundle<KafkaEnergyConfiguration>(Device.class) {
            @Override
            public DataSourceFactory getDataSourceFactory(KafkaEnergyConfiguration kafkaEnergyConfiguration) {
                return kafkaEnergyConfiguration.getDatabaseConfig();
            }
        };

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
        bootstrap.setConfigurationSourceProvider(new ResourceConfigurationSourceProvider());

        bootstrap.addBundle(hibernateBundle);

        bootstrap.addCommand(new KafkaStreamsCommand(kafkaEnergyApplication));

        super.initialize(bootstrap);
    }

    @Override
    public void run(final KafkaEnergyConfiguration kafkaEnergyConfiguration,
                    final Environment environment) {
        final DeviceDao dao = new DeviceDao(hibernateBundle.getSessionFactory());
        final DeviceEventResource deviceEventResource = new DeviceEventResource(kafkaProducer, dao);

        environment.jersey().register(deviceEventResource);
    }
}
