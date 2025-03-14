package com.access.controller;

import java.sql.SQLException;
import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.access.model.Papeleta;
import com.access.service.PapeletaService;

@RestController
@RequestMapping("/papeleta")
public class PapeletaController {

	 private final PapeletaService papeletaService;

	    public PapeletaController(PapeletaService papeletaService) {
	        this.papeletaService = papeletaService;
	    }

	    @GetMapping
	    public List<Papeleta> getAllMaterias() {
	        return papeletaService.getAllPapeletas();
	    }
	    
	    @GetMapping(value="/{codigo}")
	    public List<Papeleta> getByCodigo(@PathVariable Integer folio) {
	        return papeletaService.getPapeletasByFolio(folio);
	    }

	    @PostMapping()
	    public Papeleta addPapeleta(@RequestBody Papeleta papeleta) throws SQLException {
	        return papeletaService.addPapeleta(papeleta);
	    }
	    
	  
}
