package com.hines.james;

import com.mitchseymour.kafka.serialization.avro.AvroSerdes;
import io.confluent.kafka.serializers.AbstractKafkaAvroSerDeConfig;
import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde;
import io.dropwizard.Application;
import io.dropwizard.cli.ConfiguredCommand;
import io.dropwizard.configuration.ResourceConfigurationSourceProvider;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.hibernate.HibernateBundle;
import io.dropwizard.jdbi3.JdbiFactory;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import net.sourceforge.argparse4j.inf.Namespace;
import net.sourceforge.argparse4j.inf.Subparser;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Produced;
import org.jdbi.v3.core.Jdbi;

import java.util.Properties;

public class KafkaStreamsCommand extends ConfiguredCommand<KafkaEnergyConfiguration> {
    public KafkaStreamsCommand(Application application) {
        super( "persist-events", "Saves device events to a database");
    }

    @Override
    public void configure(Subparser subparser) {
        addFileArgument(subparser);
    }

    @Override
    protected void run(Bootstrap<KafkaEnergyConfiguration> bootstrap,
                       Namespace namespace,
                       KafkaEnergyConfiguration kafkaEnergyConfiguration) throws Exception {
        bootstrap.setConfigurationSourceProvider(new ResourceConfigurationSourceProvider());

        HibernateBundle<KafkaEnergyConfiguration> hibernateBundle =
                new HibernateBundle<>(Device.class) {
                    @Override
                    public DataSourceFactory getDataSourceFactory(KafkaEnergyConfiguration kafkaEnergyConfiguration) {
                        return kafkaEnergyConfiguration.getDatabaseConfig();
                    }
                };

        kafkaEnergyConfiguration.getDatabaseConfig().getProperties().put("hibernate.current_session_context_class", "org.hibernate.context.internal.ThreadLocalSessionContext");

        hibernateBundle.run(kafkaEnergyConfiguration,
                new Environment("EnvName",
                        bootstrap.getObjectMapper(),
                        bootstrap.getValidatorFactory().getValidator(),
                        bootstrap.getMetricRegistry(),
                        bootstrap.getClassLoader()));

        bootstrap.addBundle(hibernateBundle);

        final DeviceDao dao = new DeviceDao(hibernateBundle.getSessionFactory());

        Properties properties = new Properties();
        properties.put(StreamsConfig.APPLICATION_ID_CONFIG, "energy-kafka-streams");
        properties.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "192.168.99.106:9092");
        properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        properties.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        properties.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, SpecificAvroSerde.class);
        properties.put(AbstractKafkaAvroSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, "http://192.168.99.06:8081");

        final Serde<DeviceEvent> deviceEventSerde = AvroSerdes.get(DeviceEvent.class);

        String sourceTopic = "kafka_energy_events_4";
        String sinkTopic = "kafka-energy-event-charging";

        StreamsBuilder builder = new StreamsBuilder();

        KStream<String, DeviceEvent> deviceEventsSource =
                builder.stream(sourceTopic, Consumed.with(new Serdes.StringSerde(), deviceEventSerde));

        deviceEventsSource
                .mapValues(DeviceEvent::getCharging).peek((key, charging) -> {
                    System.out.println("Device Key: " + key);
                    System.out.println("Device Charging Value: " + charging);

                    System.out.println("Saving charging info to database");

                    Device device = new Device();
                    device.setDeviceId(key);
                    device.setCharging(charging);

                    System.out.println(dao.create(device));
                }).to(sinkTopic, Produced.with(Serdes.String(), Serdes.Integer()));

        KafkaStreams energyKafkaStreamsApp = new KafkaStreams(builder.build(), properties);
        energyKafkaStreamsApp.cleanUp();
        energyKafkaStreamsApp.start();
    }
}
