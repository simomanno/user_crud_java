package com.example.user_crud;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    /**
     * Cerca utenti per nome e cognome
     * @param name
     * @param surname
     * @return Lista di utenti che corrispondono ai criteri di ricerca con status code 200 
     * se viene trovato almeno un utente
     */
    public List<User> search(String name, String surname){
        return userRepository.findByNameAndSurname(name, surname);
    }
}
