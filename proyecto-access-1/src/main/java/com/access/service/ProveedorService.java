package com.access.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.access.model.Proveedor;
import com.access.repository.ProveedorRepository;

@Service
public class ProveedorService {
	private final ProveedorRepository proveedorRepository;
	
	public ProveedorService(ProveedorRepository proveedorRepository) {
		this.proveedorRepository = proveedorRepository;
	}
		
	public List<Proveedor> getProveedorByID(Integer id) {
        List<Proveedor> proveedor = proveedorRepository.getProveedorByID(id);
		return proveedor;
    }
}
