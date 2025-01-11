package com.yourcompany.rentalmanagement.dao.impl;

import com.yourcompany.rentalmanagement.model.*;
import com.yourcompany.rentalmanagement.dao.RentalAgreementDao;
import com.yourcompany.rentalmanagement.model.Payment;
import com.yourcompany.rentalmanagement.model.RentalAgreement;
import com.yourcompany.rentalmanagement.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RentalAgreementDaoImpl implements RentalAgreementDao {
    private List<RentalAgreement> rentalAgreements;
    private Query<RentalAgreement> query;
    private RentalAgreement rentalAgreement;
    private Transaction transaction;
    Map<String, Object> data = new HashMap<>();
    Map<String, Object> result = new HashMap<>();

    public RentalAgreementDaoImpl() {
        transaction = null;
        rentalAgreements = new ArrayList<>();
    }

    @Override
    public List<RentalAgreement> getAllRentalAgreements() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            query = session.createQuery("from RentalAgreement", RentalAgreement.class);
            return query.list();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
        return rentalAgreements;
    }

    @Override
    public RentalAgreement getRentalAgreementById(long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            rentalAgreement = session.get(RentalAgreement.class, id);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rentalAgreement;
    }

    @Override
    public void loadData() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            rentalAgreements = session.createQuery("from RentalAgreement ", RentalAgreement.class).list();
            //rentalAgreements.forEach(System.out::println);
            System.out.println(rentalAgreements.size());
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

    @Override
    public List<RentalAgreement> getRentalAgreementsById(long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            rentalAgreements = session.createQuery("FROM RentalAgreement r WHERE r.host.id = :hostId", RentalAgreement.class).setParameter("hostId", id).getResultList();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
        return rentalAgreements;
    }

    public List<Payment> getRelatedPayments(List<RentalAgreement> rentalAgreements) {
        List<Payment> relatedPayments = new ArrayList<>();

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();

            // Collect IDs of all rental agreements
            List<Long> rentalAgreementIds = rentalAgreements.stream()
                    .map(RentalAgreement::getId)
                    .collect(Collectors.toList());

            // Query to fetch payments related to all rental agreements
            relatedPayments = session.createQuery("FROM Payment p WHERE p.rentalAgreement.id IN :rentalAgreementIds", Payment.class)
                    .setParameter("rentalAgreementIds", rentalAgreementIds)
                    .getResultList();

            // Commit the transaction
            transaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return relatedPayments;
    }

    @Override
    public void createRentalAgreement(RentalAgreement rentalAgreement) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.save(rentalAgreement);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }

    }

    @Override
    public Map<String, Object> updateRentalAgreementById(long id, Map<String, Object> data) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()){
            transaction = session.beginTransaction();

            rentalAgreement = session.get(RentalAgreement.class, 1);
            System.out.println(rentalAgreement);

            if (rentalAgreement != null){
                if (data.get("property").getClass() == CommercialProperty.class){
                    rentalAgreement.setCommercialProperty((CommercialProperty) data.get("property"));
                    rentalAgreement.setResidentialProperty(null);
                } else {
                    rentalAgreement.setCommercialProperty(null);
                    rentalAgreement.setResidentialProperty((ResidentialProperty) data.get("property"));
                }
            }

            if (data.get("status") != null) {
                rentalAgreement.setStatus((RentalAgreement.rentalAgreementStatus) data.get("status"));
            } else {
                rentalAgreement.setStatus(RentalAgreement.rentalAgreementStatus.NEW);
            }

            if (data.get("host") != null) {
                rentalAgreement.setHost((Host) data.get("host"));
            } else {
                rentalAgreement.setHost(null);
            }

            if (data.get("owner") != null) {
                rentalAgreement.setOwner((Owner) data.get("owner"));
            } else {
                rentalAgreement.setOwner(null);
            }

            rentalAgreement.setStartContractDate((LocalDate) data.get("startDate"));
            rentalAgreement.setEndContractDate((LocalDate) data.get("endDate"));
            rentalAgreement.setRentingFee((Double) data.get("rentingFee"));
            rentalAgreement.setTenants((List<Tenant>) data.get("subTenants"));

            transaction.commit();
            result.put("status", "success");
            result.put("message", "Address updated successfully");
        } catch (Exception e) {
            e.printStackTrace();
            result.put("status", "failed");
            result.put("message", e.getMessage());
        }
        return result;
    }

    @Override
    public Map<String, Object> deleteRentalAgreementById(long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();

            rentalAgreement = session.get(RentalAgreement.class, id);

            if (rentalAgreement != null){
                // rentalAgreement.getHost().setRentalAgreements(rentalAgreement.getHost().getRentalAgreements()
                // .remove(rentalAgreement));
                session.remove(rentalAgreement);
            }

            transaction.commit();
            result.put("status", "success");
            result.put("message", "Address updated successfully");
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
            result.put("status", "failed");
            result.put("message", e.getMessage());
        }

        System.out.println("Row deleted!");
        return result;
    }


    @Override
    public List<RentalAgreement> getRentalAgreementsByRole(UserRole role, long userId) {
        List<RentalAgreement> rentalAgreements = new ArrayList<>();
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<RentalAgreement> query;
            if (role.equals(UserRole.TENANT)) {
                // Query to fetch rental agreements for tenants
                query = session.createQuery(
                        "SELECT rA FROM RentalAgreement rA JOIN rA.tenants t WHERE t.id = :id",
                        RentalAgreement.class
                );
                query.setParameter("id", userId);
            } else {
                // Handle other roles or throw an exception
                throw new IllegalArgumentException("Unsupported role: " + role);
            }
            rentalAgreements = query.list();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rentalAgreements;
    }



            @Override
            public Map<String, Object> createRentalAgreement(RentalAgreement rentalAgreement, long tenantId, Property property,long ownerId, long hostId, List<Long > subTenantIds){
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


    public static void main(String[] args) {
        RentalAgreementDaoImpl test = new RentalAgreementDaoImpl();
        List<RentalAgreement> db = test.getAllRentalAgreements();
        System.out.println("======================");
        for (RentalAgreement rentalAgreement : db) {
            System.out.println(rentalAgreement.getTenants());
        }
    }
}