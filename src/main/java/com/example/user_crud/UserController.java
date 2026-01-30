package com.example.user_crud;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("/api/users")
public class UserController {

    /**
     * Repository per l'accesso ai dati degli utenti
     */
    @Autowired
    private UserRepository userRepository;

    /**
     * Crea l'utente salvandolo nel database
     * @param user L'utente da creare
     * @return L'utente creato con lo status code 201 Created
     */
    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user){
        User savedUser = userRepository.save(user);
        return new ResponseEntity<>(savedUser, HttpStatus.CREATED);
    }
    

    /**
     * Recupera un utente dal database dato il suo id
     * @param id L'id dell'utente da recuperare
     * @return L'utente recuperato con lo status code 200 se l'operazione va a buon fine
     * o 404 se non esiste
     */
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById( @PathVariable Long id){
        Optional<User> user = userRepository.findById(id);
        return user.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }


    /**
     * Cerca utenti per nome e cognome
     * @param name
     * @param surname
     * @return Lista di utenti che corrispondono ai criteri di ricerca con lo status code 200 
     * se viene trovato almeno un utente
     */
    @GetMapping("/search")
    public ResponseEntity<List<User>> searchUsers(
        @RequestParam(required = false) String name,
        @RequestParam(required = false) String surname
    )
    {
        List<User> results = userRepository.findByNameContainingIgnoreCaseAndSurnameContainingIgnoreCase(name == null ? "" : name, surname == null ? "" : surname);
        return new ResponseEntity<>(results, HttpStatus.OK);

    }


    /**
     * Aggiorna un utente esistente
     * @param id
     * @param user
     * @return L'utente aggiornato con lo status code 200 se l'operazione va a buon fine
     * o 404 Not Found se l'utente con quell'id non esiste
     */
    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User user){
        //mi chiedo se l'utente con quell'id esiste
        if(!userRepository.existsById(id))
            return ResponseEntity.notFound().build();
        
        user.setId(id);
        User updatedUser = userRepository.save(user);
        return new ResponseEntity<>(updatedUser, HttpStatus.OK);
    }

    /**
     * Elimina un utente dato il suo id
     * @param id
     * @return Lo status code 204 se l'eliminazione va a buon fine
     * o 404 se l'utente con quell'id non esiste
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id){
        if(!userRepository.existsById(id))
            return ResponseEntity.notFound().build();

        userRepository.deleteById(id);
        // se l'eliminazione va a buon fine, ritorno 204 No Content
        return ResponseEntity.noContent().build();

    }


    /**
     * Carica un file CSV contenente utenti e li salva nel database
     * @param file
     * @return Lo status code 200 se il caricamento va a buon fine
     * o 400 se il file è vuoto
     * o 500 se c'è un errore durante l'elaborazione del file, con messaggio di errore
     */
    @PostMapping(value= "/upload", consumes = {"multipart/form-data"})
    public ResponseEntity<String> uploadCSV(@RequestParam("file") MultipartFile file){
        if(file.isEmpty()){
            return ResponseEntity.badRequest().body("il file è vuoto");
        }
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))){

            // Utilizzo di OpenCSV per mappare le righe del file CSV in oggetti User
            CsvToBean<User> cvsToBean = new CsvToBeanBuilder<User>(reader)
                .withType(User.class)
                .withSeparator(',')
                .withIgnoreLeadingWhiteSpace(true)
                .build();

            // Parsing del file CSV
            List<User> users = cvsToBean.parse();
            System.out.println("Utenti trovati nel file " + users.size());

            // Salvataggio degli utenti nel database
            userRepository.saveAll(users);

            return ResponseEntity.ok("Caricati con successo " + users.size());
        }catch(Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Errore durante l'elaborazione del file");
        }
    }
}
