package com.hines.james;

import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

public class KafkaEnergyApplication extends Application<KafkaEnergyConfiguration> {
    public static void main(final String[] args) throws Exception{
        new KafkaEnergyApplication().run(args);
    }

    @Override
    public String getName() {
        return "kafka-energy";
    }

    @Override
    public void initialize(final Bootstrap<KafkaEnergyConfiguration> bootstrap){}

    @Override
    public void run(final KafkaEnergyConfiguration kafkaEnergyConfiguration,
                    final Environment environment) {
        final DeviceEventController deviceEventController = new DeviceEventController();

        environment.jersey().register(deviceEventController);
    }
}
