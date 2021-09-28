package com.hines.james;

import io.dropwizard.hibernate.AbstractDAO;
import org.hibernate.SessionFactory;

import java.util.Optional;

public class DeviceDao extends AbstractDAO<Device> {
    public DeviceDao(SessionFactory factory) {
        super(factory);
    }

    public Optional<Device> findById(String id) {
        return Optional.ofNullable(get(id));
    }

    public Device create(Device device) {
        return persist(device);
    }
}
