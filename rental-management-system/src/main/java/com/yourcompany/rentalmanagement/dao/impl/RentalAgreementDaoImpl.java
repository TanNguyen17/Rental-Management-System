package com.yourcompany.rentalmanagement.dao.impl;

import com.yourcompany.rentalmanagement.dao.RentalAgreementDao;
import com.yourcompany.rentalmanagement.model.*;
import com.yourcompany.rentalmanagement.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RentalAgreementDaoImpl implements RentalAgreementDao {
    Transaction transaction = null;
    Map<String, Object> data = new HashMap<>();
    @Override
    public List<RentalAgreement> getAllRentalAgreements() {
        return List.of();
    }

    @Override
    public Map<String, Object> createRentalAgreement(RentalAgreement rentalAgreement, long tenantId, Property property, long ownerId, long hostId, List<Long> subTenantIds) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            List<Tenant> subTenants = new ArrayList<>();

            for (Long id : subTenantIds) {
                subTenants.add(session.get(Tenant.class, id));
            }

            Tenant tenant = session.get(Tenant.class, tenantId);
            Owner owner = session.get(Owner.class, ownerId);
            Host host = session.get(Host.class, hostId);

            rentalAgreement.setOwner(owner);
            rentalAgreement.setHost(host);

            tenant.addRentalAgreement(rentalAgreement);
            for (Tenant subTenant : subTenants) {
                subTenant.addRentalAgreement(rentalAgreement);
            }

            property.setRentalAgreement(rentalAgreement);
            property.setStatus(Property.propertyStatus.RENTED);

            session.persist(rentalAgreement);
            transaction.commit();

            data.put("rentalAgreement", rentalAgreement);
            data.put("status", "success");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }
}
