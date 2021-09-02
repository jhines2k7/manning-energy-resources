package com.hines.james;

import com.mitchseymour.kafka.serialization.avro.AvroSerdes;
import io.confluent.kafka.serializers.AbstractKafkaAvroSerDeConfig;
import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde;
import io.dropwizard.Application;
import io.dropwizard.Configuration;
import io.dropwizard.cli.Command;
import io.dropwizard.cli.EnvironmentCommand;
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

public class KafkaStreamsCommand extends EnvironmentCommand<KafkaEnergyConfiguration> {
    public KafkaStreamsCommand(Application application) {
        super(application, "persist-events", "Saves device events to a database");
    }

    @Override
    public void configure(Subparser subparser) {

    }

    @Override
    protected void run(Environment environment, Namespace namespace, KafkaEnergyConfiguration kafkaEnergyConfiguration) throws Exception {
        final JdbiFactory factory = new JdbiFactory();
        final Jdbi jdbi = factory.build(environment, kafkaEnergyConfiguration.getDataSourceFactory(), "postgresql");
        final DeviceEventDao dao = jdbi.onDemand(DeviceEventDao.class);

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
                .mapValues(DeviceEvent::getCharging).peek((s, integer) -> {
                    System.out.println("Device Key: " + s);
                    System.out.println("Device Charging Value: " + integer);
                }).to(sinkTopic, Produced.with(Serdes.String(), Serdes.Integer()));

        KafkaStreams energyKafkaStreamsApp = new KafkaStreams(builder.build(), properties);
        energyKafkaStreamsApp.cleanUp();
        energyKafkaStreamsApp.start();
    }
}
