package com.hines.james;

import io.dropwizard.hibernate.AbstractDAO;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.context.internal.ManagedSessionContext;

import java.util.Optional;

public class DeviceDao extends AbstractDAO<Device> {
    private final SessionFactory factory;

    public DeviceDao(SessionFactory factory) {
        super(factory);

        this.factory = factory;
    }

    public Optional<Device> findById(String id) {
        Session session = factory.openSession();

        ManagedSessionContext.bind(session);

        Optional<Device> device = Optional.ofNullable(get(id));

        session.close();

        return device;
    }

    public Device create(Device device) {
        Device newDevice;

        Transaction transaction = factory.getCurrentSession().beginTransaction();

        newDevice = persist(device);

        transaction.commit();

        return newDevice;
    }
}
