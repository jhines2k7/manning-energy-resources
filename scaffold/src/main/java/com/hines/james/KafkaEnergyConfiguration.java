package com.hines.james;

import io.dropwizard.Configuration;
import io.dropwizard.db.DataSourceFactory;
import org.codehaus.jackson.annotate.JsonProperty;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

public class KafkaEnergyConfiguration extends Configuration {
    @Valid
    @NotNull
    private DataSourceFactory dataSourceFactory = new DataSourceFactory();

    @JsonProperty("database")
    public DataSourceFactory getDataSourceFactory() {
        dataSourceFactory.setDriverClass("org.postgresql.Driver");
        dataSourceFactory.setUrl("jdbc:postgresql://192.168.99.110:5432/kafka_energy");
        dataSourceFactory.setUser("postgres");
        dataSourceFactory.setPassword("secret");

        return dataSourceFactory;
    }
}
