package com.hines.james;

import io.dropwizard.hibernate.AbstractDAO;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.Optional;

public class DeviceDao extends AbstractDAO<Device> {
    private final SessionFactory factory;

    public DeviceDao(SessionFactory factory) {
        super(factory);

        this.factory = factory;
    }

    public Optional<Device> findById(String id) {
        return Optional.ofNullable(get(id));
    }

    public Device create(Device device) {
        Device newDevice;

        Transaction transaction = factory.getCurrentSession().beginTransaction();

        newDevice = persist(device);

        transaction.commit();

        return newDevice;
    }
}
