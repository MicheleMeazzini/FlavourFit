package com.example.demo.controller;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1")
public class provaController {

    /*

    PER TESTARE I METODI FAR PARTIRE APP, APRIRE POSTMAN E METTETE COME INDIRIZZO
    http://localhost:8080/ contenuto request mapping/ contenuto get mapping*
    esempio
    http:localhost/8080/api/v1/provaGet
    OPPURE DIRETTAMENTE DALLO SWAGGER

     */

    // i metodi get sono per le letture da db
    @GetMapping("provaGet")
    public String hello() {
        return "ProvaGet";
    }

    // i metodi get sono per gli inserimenti nel db
    @PostMapping("provaPost")
    public String ProvaPost(){
        return "ProvaPost";
    }

    // i metodi get sono per gli aggiornamenti al db
    @PutMapping("provaPut")
    public String ProvaPut(){
        return "ProvaPut";
    }

    // i metodi get sono per le cancellazioni
    @DeleteMapping("provaDelete")
    public String ProvaDelete(){
        return "ProvaDelete";
    }
}
