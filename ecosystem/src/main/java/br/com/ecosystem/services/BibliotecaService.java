package br.com.ecosystem.services;

import br.com.ecosystem.dtos.BibliotecaDto;
import br.com.ecosystem.models.Biblioteca;
import br.com.ecosystem.repositories.BibliotecaRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.opencsv.CSVReader;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BibliotecaService {

    private final BibliotecaRepository bibliotecaRepository;

    public BibliotecaService(BibliotecaRepository bibliotecaRepository) {
        this.bibliotecaRepository = bibliotecaRepository;
    }

    public List<BibliotecaDto> processarCsv(MultipartFile file) {
        List<BibliotecaDto> trabalhos = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8));
             CSVReader csvReader = new CSVReader(reader)) {

            String[] headers = csvReader.readNext();
            if (headers == null) {
                throw new RuntimeException("Arquivo CSV vazio ou inválido.");
            }

            String[] linha;
            while ((linha = csvReader.readNext()) != null) {
                String nomeSobrenome = linha[2];
                String nomeFormatado = formatarNomesAutores(nomeSobrenome);

                BibliotecaDto bibliotecaDto = new BibliotecaDto(
                        linha[0], linha[1], nomeFormatado, Integer.parseInt(linha[3]), linha[4],
                        linha[5], linha[6], linha[7], linha[8], linha[9], linha[10]
                );
                trabalhos.add(bibliotecaDto);
            }

        } catch (Exception e) {
            throw new RuntimeException("Erro ao processar o arquivo CSV: " + e.getMessage());
        }

        return salvarNoBanco(trabalhos);
    }

    private List<BibliotecaDto> salvarNoBanco(List<BibliotecaDto> trabalhosDto) {
        List<Biblioteca> trabalhos = trabalhosDto.stream().map(dto -> new Biblioteca(
                dto.getCodigo(), dto.getTitulo(), dto.getAutor(), dto.getAno(),
                dto.getReferencia(), dto.getLink(), dto.getTipo(), dto.getMidia(),
                dto.getLinkDrive(), dto.getImagem(), dto.getObservacoes()
        )).collect(Collectors.toList());

        bibliotecaRepository.saveAll(trabalhos);
        return trabalhosDto;
    }

    public String formatarNomesAutores(String autores) {
        String[] listaAutores = autores.split(";");
        List<String> nomesFormatados = new ArrayList<>();

        for (String autor : listaAutores) {
            String[] partes = autor.trim().split(",");
            if (partes.length == 2) {
                nomesFormatados.add(partes[1].trim() + " " + partes[0].trim());
            } else {
                nomesFormatados.add(autor.trim());
            }
        }
        return String.join(", ", nomesFormatados);
    }

    public List<BibliotecaDto> listarTodos() {
        return bibliotecaRepository.findAll().stream()
                .map(b -> new BibliotecaDto(b.getCodigo(), b.getTitulo(), b.getAutor(), b.getAno(), b.getReferencia(),
                        b.getLink(), b.getTipo(), b.getMidia(), b.getLinkDrive(), b.getImagem(), b.getObservacoes()))
                .collect(Collectors.toList());
    }

    public Optional<BibliotecaDto> buscarPorId(String id) {
        return bibliotecaRepository.findById(id)
                .map(b -> new BibliotecaDto(b.getCodigo(), b.getTitulo(), b.getAutor(), b.getAno(), b.getReferencia(),
                        b.getLink(), b.getTipo(), b.getMidia(), b.getLinkDrive(), b.getImagem(), b.getObservacoes()));
    }

    public BibliotecaDto atualizar(String id, BibliotecaDto bibliotecaDto) {
        Biblioteca biblioteca = bibliotecaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Registro não encontrado."));

        biblioteca.setTitulo(bibliotecaDto.getTitulo());
        biblioteca.setAutor(bibliotecaDto.getAutor());
        biblioteca.setAno(bibliotecaDto.getAno());
        biblioteca.setReferencia(bibliotecaDto.getReferencia());
        biblioteca.setLink(bibliotecaDto.getLink());
        biblioteca.setTipo(bibliotecaDto.getTipo());
        biblioteca.setMidia(bibliotecaDto.getMidia());
        biblioteca.setLinkDrive(bibliotecaDto.getLinkDrive());
        biblioteca.setImagem(bibliotecaDto.getImagem());
        biblioteca.setObservacoes(bibliotecaDto.getObservacoes());

        bibliotecaRepository.save(biblioteca);
        return bibliotecaDto;
    }

    public BibliotecaDto atualizarParcialmente(String id, BibliotecaDto bibliotecaDto) {
        Biblioteca biblioteca = bibliotecaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Registro não encontrado."));

        if (bibliotecaDto.getTitulo() != null) biblioteca.setTitulo(bibliotecaDto.getTitulo());
        if (bibliotecaDto.getAutor() != null) biblioteca.setAutor(bibliotecaDto.getAutor());
        if (Objects.nonNull(bibliotecaDto.getAno())) {
            biblioteca.setAno(bibliotecaDto.getAno());
        }
        if (bibliotecaDto.getReferencia() != null) biblioteca.setReferencia(bibliotecaDto.getReferencia());
        if (bibliotecaDto.getLink() != null) biblioteca.setLink(bibliotecaDto.getLink());
        if (bibliotecaDto.getTipo() != null) biblioteca.setTipo(bibliotecaDto.getTipo());
        if (bibliotecaDto.getMidia() != null) biblioteca.setMidia(bibliotecaDto.getMidia());
        if (bibliotecaDto.getLinkDrive() != null) biblioteca.setLinkDrive(bibliotecaDto.getLinkDrive());
        if (bibliotecaDto.getImagem() != null) biblioteca.setImagem(bibliotecaDto.getImagem());
        if (bibliotecaDto.getObservacoes() != null) biblioteca.setObservacoes(bibliotecaDto.getObservacoes());

        bibliotecaRepository.save(biblioteca);
        return bibliotecaDto;
    }

    public void deletar(String id) {
        if (!bibliotecaRepository.existsById(id)) {
            throw new RuntimeException("Registro não encontrado.");
        }
        bibliotecaRepository.deleteById(id);
    }
}
