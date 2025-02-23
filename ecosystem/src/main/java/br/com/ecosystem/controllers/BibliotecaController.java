package br.com.ecosystem.controllers;

import java.util.List;

import br.com.ecosystem.dtos.BibliotecaDto;
import br.com.ecosystem.services.BibliotecaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/biblioteca")
public class BibliotecaController {

    private final BibliotecaService bibliotecaService;

    @Autowired
    public BibliotecaController(BibliotecaService bibliotecaService) {
        this.bibliotecaService = bibliotecaService;
    }

    @PostMapping("/upload")
    public ResponseEntity<List<BibliotecaDto>> uploadCsv(@RequestParam("file") MultipartFile file) {
        List<BibliotecaDto> trabalhos = bibliotecaService.carregarCSV(file);
        return ResponseEntity.ok(trabalhos);
    }
}
