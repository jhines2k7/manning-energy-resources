package com.hines.james;

import io.confluent.kafka.streams.serdes.avro.GenericAvroSerde;
import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde;
import io.dropwizard.cli.Command;
import io.dropwizard.setup.Bootstrap;
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
import org.apache.kafka.streams.kstream.KTable;

import java.util.Collections;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

public class KafkaStreamsCommand extends Command {
    public KafkaStreamsCommand() {
        super("persist-events", "Saves device events to a database");
    }

    @Override
    public void configure(Subparser subparser) {

    }

    @Override
    public void run(Bootstrap<?> bootstrap, Namespace namespace) throws Exception {
        Properties properties = new Properties();
        properties.put(StreamsConfig.APPLICATION_ID_CONFIG, "energy-kafka-streams");
        properties.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "192.168.99.106:9092");
        properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        properties.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        properties.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, SpecificAvroSerde.class);
        properties.put("schema.registry.url", "http://192.168.99.06:8081");

        final Map<String, String> serdeConfig = Collections.singletonMap("schema.registry.url", "http://192.168.99.06:8081");

        final Serde<DeviceEvent> valueSpecificAvroSerde = new SpecificAvroSerde<>();
        valueSpecificAvroSerde.configure(serdeConfig, false);

        String sourceTopic = "kafka-energy-events";
        String sinkTopic = "kafka-energy-event-charging";

        StreamsBuilder builder = new StreamsBuilder();

        KStream<String, DeviceEvent> deviceEventsSource = builder.stream(sourceTopic, Consumed.with(new Serdes.StringSerde(), valueSpecificAvroSerde));

        deviceEventsSource
            .mapValues(DeviceEvent::getCharging).to(sinkTopic);

        KafkaStreams energyKafkaStreamsApp = new KafkaStreams(builder.build(), properties);
        energyKafkaStreamsApp.cleanUp();
        energyKafkaStreamsApp.start();
    }
}
