package com.access.controller;

import java.sql.SQLException;
import java.util.List;

import org.springframework.context.event.EventListener;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.access.dto.materia.MateriaPaginationDTO;
import com.access.model.Materia;
import com.access.service.MateriaService;

@RestController
@RequestMapping("/materia")
public class MateriaController {

	 private final MateriaService materiaService;
	 
	    public MateriaController(MateriaService materiaService) {
	        this.materiaService = materiaService;
	    }

	    @GetMapping
	    public List<Materia> getAllMaterias() {
	        return materiaService.getAllMaterias();
	    }
	    
	    @GetMapping(value="/{codigo}")
	    public List<Materia> getByCodigo(@PathVariable String codigo) {
	        return materiaService.getMateriasByCodigoMat(codigo);
	    }

	    @PostMapping()
	    public Materia guardarMateria(@RequestBody Materia materia) throws SQLException {
	        return materiaService.addMateria(materia);
	    }
	    
	    @DeleteMapping(value="/{codigo}")
	    public List<Materia> deleteMateria(@PathVariable String codigo) throws SQLException {
	        return materiaService.deleteMateria(codigo);
	    }
	  
}
