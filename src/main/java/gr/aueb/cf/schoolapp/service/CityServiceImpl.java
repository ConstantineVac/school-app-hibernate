package gr.aueb.cf.schoolapp.service;

import gr.aueb.cf.schoolapp.dao.CityDAOHibernateImpl;
import gr.aueb.cf.schoolapp.dao.exceptions.CityDAOException;
import gr.aueb.cf.schoolapp.dto.CityInsertDTO;
import gr.aueb.cf.schoolapp.dto.CityUpdateDTO;
import gr.aueb.cf.schoolapp.model.City;

import gr.aueb.cf.schoolapp.service.exceptions.CityNotFoundException;

import java.util.List;
import java.util.Optional;

public class CityServiceImpl implements ICityService {

    private CityDAOHibernateImpl cityDAO;


    public CityServiceImpl(CityDAOHibernateImpl cityDAO) {
        this.cityDAO = cityDAO;
    }

    @Override
    public City insertCity(CityInsertDTO dto) throws CityDAOException {
        if (dto == null) return null;
        City city;
        try {
            city = map(dto);
            System.out.println("Service returned city: " +city.getName());
            return cityDAO.insert(city);
        }  catch (CityDAOException e) {
            e.printStackTrace();
            throw e;
        }

    }
    @Override
    public City updateCity(CityUpdateDTO dto) throws CityDAOException, CityNotFoundException {
        if (dto == null) return null;
        City city ;
        try {
            city = map(dto);

            if (cityDAO.getById(city.getId()) == null) {
                throw new CityNotFoundException(city);
            }
            return cityDAO.update(city);
        }catch (CityDAOException | CityNotFoundException e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public void deleteCity(int id) throws CityDAOException, CityNotFoundException {
        City city;
        try {
            city = cityDAO.getById(id);

            if (city == null) {
                throw new CityNotFoundException("City with id: " + id + " was not found");
            }
            cityDAO.delete(id);
        } catch (CityDAOException | CityNotFoundException e) {
            e.printStackTrace();
            throw e;
        }
    }
    @Override
    public List<City> getCitiesByCityName(String name) throws CityDAOException, CityNotFoundException {
        if (name == null) {
            throw new IllegalArgumentException("City name cannot be null");
        }
        try {
            Optional<List<City>> citiesOptional = cityDAO.getByCityName(name);

            if (!citiesOptional.isPresent()) {
                throw new CityNotFoundException("No cities found with name: " + name);
            }

            return citiesOptional.get();

        } catch (CityDAOException e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public City getCityById(int id) throws CityDAOException, CityNotFoundException {
        City city;
        try {
            city = cityDAO.getById(id);

            if (city == null) {
                throw new CityNotFoundException("Search Error: City with id: " + id + " was not found");
            }
            return city;
        } catch (CityDAOException e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public List<City> getAllCities() {
        List<City> cities = null;
        try {
            cities = cityDAO.getAllCities();
        } catch (CityDAOException e) {
            e.printStackTrace();
            // handle exception
        }
        return cities;
    }

    private City map(CityInsertDTO dto) {
        return new City(null, dto.getName());
    }

    private City map(CityUpdateDTO dto) {return new City(dto.getId(), dto.getName());
    }
}
