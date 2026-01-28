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
    @Autowired
    private UserRepository userRepository;

    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user){
        User savedUser = userRepository.save(user);
        return new ResponseEntity<>(savedUser, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById( @PathVariable Long id){
        Optional<User> user = userRepository.findById(id);
        return user.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/search")
    public ResponseEntity<List<User>> searchUsers(
        @RequestParam(required = false) String name,
        @RequestParam(required = false) String surname
    )
    {
        List<User> results = userRepository.findByNameAndSurname(name == null ? "" : name, surname == null ? "" : surname);
        return new ResponseEntity<>(results, HttpStatus.OK);

    }

    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User user){
        //mi chiedo se l'utente con quell'id esiste
        if(!userRepository.existsById(id))
            return ResponseEntity.notFound().build();
        
        user.setId(id);
        User updatedUser = userRepository.save(user);
        return new ResponseEntity<>(updatedUser, HttpStatus.OK);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id){
        if(!userRepository.existsById(id))
            return ResponseEntity.notFound().build();

        userRepository.deleteById(id);
        return ResponseEntity.noContent().build();

    }

    @PostMapping(value= "/upload", consumes = {"multipart/form-data"})
    public ResponseEntity<String> uploadCSV(@RequestParam("file") MultipartFile file){
        if(file.isEmpty()){
            return ResponseEntity.badRequest().body("il file è vuoto");
        }
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))){
            CsvToBean<User> cvsToBean = new CsvToBeanBuilder<User>(reader)
                .withType(User.class)
                .withSeparator(',')
                .withIgnoreLeadingWhiteSpace(true)
                .build();

            List<User> users = cvsToBean.parse();
            System.out.println("Utenti trovati nel file " + users.size());
            userRepository.saveAll(users);

            return ResponseEntity.ok("Caricati con successo " + users.size());
        }catch(Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Errore durante l'elaborazione del file");
        }
    }
}
