package cz.fi.muni.pa165.hotelbookingmanager.service.impl;

import cz.fi.muni.pa165.hotelbookingmanager.dao.interfaces.ClientDAO;
import cz.fi.muni.pa165.hotelbookingmanager.entities.Client;
import cz.fi.muni.pa165.hotelbookingmanager.service.interfaces.ClientService;
import cz.fi.muni.pa165.hotelbookingmanager.transferobjects.ClientTO;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Filip Bogyai
 */

@Service
public class ClientServiceImpl implements ClientService {

    @Autowired
    private ClientDAO clientDAO;
    
    @Autowired
    private Validator validator;
    
    @Autowired
    private Mapper mapper;

    public void setClientDAO(ClientDAO clientDAO) {
        this.clientDAO = clientDAO;
    }

    public void setValidator(Validator validator) {
        this.validator = validator;
    }
    
    public void setMapper(Mapper mapper) {
        this.mapper = mapper;
    }
    
    @Override
    @Transactional
    public void createClient(ClientTO client) {
        if (client == null)
            throw new IllegalArgumentException("client cannot be null");
        if (client.getId() != null)
            throw new IllegalArgumentException("client cannot have manually assigned id");
        Client clientDO = mapper.map(client, Client.class);
        
        validateClientAttrributes(clientDO);
        clientDAO.create(clientDO);
    }

    @Override
    public ClientTO findClient(Long id) {
        if (id == null)
            throw new IllegalArgumentException("id cannot be null");
        ClientTO clientTO = mapper.map(clientDAO.get(id), ClientTO.class);
        
        return clientTO;
    }

    @Override
    @Transactional
    public void updateClient(ClientTO client) {
         if (client == null)
            throw new IllegalArgumentException("client cannot be null");
        if (client.getId() == null || clientDAO.get(client.getId()) == null) 
            throw new IllegalArgumentException("trying to update non-existent client");
        Client clientDO = mapper.map(client, Client.class);
        
        validateClientAttrributes(clientDO);
        clientDAO.update(clientDO);
    }
    

    @Override
    @Transactional
    public void deleteClient(ClientTO client) {
        if (client == null)
            throw new IllegalArgumentException("client cannot be null");
        Client clientDO = mapper.map(client, Client.class);
        
        clientDAO.delete(clientDO);
    }

    @Override
    public List<ClientTO> findAllClients() {
        
        List<Client> clients= clientDAO.findAll();
        List<ClientTO> clientsTO = new ArrayList<>();
        for(Client clientDO : clients){
            clientsTO.add(mapper.map(clientDO, ClientTO.class));
        }
        
        return clientsTO;
    }

     @Override
     public List<ClientTO> findClientsByName(String name) {
         if((name == null) || (name.trim().equals("")))
             throw new IllegalArgumentException("name cannot be null");
         
         List<Client> clients= clientDAO.findClientsByName(name);
         List<ClientTO> clientsTO = new ArrayList<>();
         for(Client clientDO : clients){
             clientsTO.add(mapper.map(clientDO, ClientTO.class));
         }
         
        return clientsTO;
    }
    
    private void validateClientAttrributes(Client client) throws IllegalArgumentException {
        
        Set<ConstraintViolation<Client>> validationResults = validator.validate(client);
        if (!validationResults.isEmpty())
            throw new IllegalArgumentException("client parameters are invalid: " + validationResults.iterator().next().getMessage());
    }
    

}
