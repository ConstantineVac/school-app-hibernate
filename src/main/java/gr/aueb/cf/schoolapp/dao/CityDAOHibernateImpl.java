package gr.aueb.cf.schoolapp.dao;


import gr.aueb.cf.schoolapp.dao.exceptions.CityDAOException;
import gr.aueb.cf.schoolapp.model.City;


import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

public class CityDAOHibernateImpl implements ICityDAO {

    private EntityManager entityManager;

    public CityDAOHibernateImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public Optional<List<City>> getByCityName(String name) throws CityDAOException {
        TypedQuery<City> query = entityManager.createQuery("FROM City c WHERE c.name LIKE :name", City.class);
        query.setParameter("name", name + "%");
        List<City> cities = query.getResultList();
        return cities.isEmpty() ? Optional.empty() : Optional.of(cities);
    }

    @Override
    public City getById(int id) throws CityDAOException {
        return entityManager.find(City.class, id);
    }

    @Override
    public City insert(City city) throws CityDAOException {
        try {
            entityManager.getTransaction().begin();  // Start a transaction
            entityManager.persist(city);
            entityManager.getTransaction().commit(); // Commit the transaction
            return city;
        } catch (Exception e) {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback(); // Rollback in case of an error
            }
            throw new CityDAOException("Error inserting city", e);
        }
    }

    @Override
    public City update(City city) throws CityDAOException {
        try {
            entityManager.getTransaction().begin();
            City updatedCity = entityManager.merge(city);
            entityManager.getTransaction().commit();
            return updatedCity;
        } catch (Exception e) {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            throw new CityDAOException("Error updating city", e);
        }
    }

    @Override
    public void delete(int id) throws CityDAOException {
        try {
            entityManager.getTransaction().begin();
            City city = getById(id);
            if (city != null) {
                entityManager.remove(city);
            } else {
                throw new CityDAOException("City with ID: " + id + " not found");
            }
            entityManager.getTransaction().commit();
        } catch (Exception e) {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            throw new CityDAOException("Error deleting city", e);
        }
    }


    @Override
    public List<City> getAllCities() throws CityDAOException {
        TypedQuery<City> query = entityManager.createQuery("FROM City", City.class);
        return query.getResultList();
    }
}
