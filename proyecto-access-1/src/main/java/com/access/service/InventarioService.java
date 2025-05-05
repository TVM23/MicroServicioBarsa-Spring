package com.access.service;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.access.dto.inventario.InventarioEntradaDTO;
import com.access.dto.inventario.InventarioItemDTO;
import com.access.dto.inventario.InventarioSalidaDTO;
import com.access.model.Materia;

@Service
public class InventarioService {
	private final JdbcTemplate jdbcTemplate;
	private final PapeletaService papeletaService;
	private final MateriaService materiaService;
	private final ProveedorService proveedorService;
	
	public InventarioService(JdbcTemplate jdbcTemplate, PapeletaService papeletaService, MateriaService materiaService, ProveedorService proveedorService) {
        this.jdbcTemplate = jdbcTemplate;
        this.papeletaService = papeletaService;
        this.materiaService = materiaService;
        this.proveedorService = proveedorService;
    }
	
	@Transactional
    public ResponseEntity<?> createSalidaInventario(InventarioSalidaDTO dto) {
        if (papeletaService.getPapeletasByFolio(dto.getFolio()).isEmpty()) {
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", "Este folio de papeleta no existe"));
        }

        // Validación de existencia
        for (InventarioItemDTO item : dto.getItems()) {
            List<Materia> materia = materiaService.getMateriaByCodigo(item.getCodigo());
            if (materia.isEmpty()) {
                return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "La materia con el código " + item.getCodigo() + " no existe"));
            }

            if (materia.get(0).getExistencia() < item.getCantidad()) {
                return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Cantidad insuficiente para la materia con código " + item.getCodigo()));
            }
        }

        // Insertar InventarioSalida
        String sqlSalida = "INSERT INTO Inventario_Salida (Folio, Fecha, Razon, Destino, Notas, Usuario) VALUES (?, ?, ?, ?, ?, ?)";
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sqlSalida, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, dto.getFolio());
            ps.setString(2, dto.getFecha());
            ps.setString(3, dto.getReason());
            ps.setString(4, dto.getDestination());
            ps.setString(5, dto.getNotes());
            ps.setString(6, dto.getCreatedBy());
            return ps;
        }, keyHolder);

        Long idSalida = keyHolder.getKey().longValue();

        // Insertar detalles
        String sqlDetalle = "INSERT INTO Detalle_Inventario_Salida (CodigoMat, Cantidad, Id_Salida) VALUES (?, ?, ?)";

        for (InventarioItemDTO item : dto.getItems()) {
            jdbcTemplate.update(sqlDetalle,
                item.getCodigo(),
                item.getCantidad(),
                idSalida
            );

            // Opcional: actualizar existencia en Materia
            jdbcTemplate.update("UPDATE Materia SET Existencia = Existencia - ? WHERE CodigoMat = ?",
                item.getCantidad(),
                item.getCodigo()
            );
        }

        return ResponseEntity.ok(Map.of("message", "Salida de inventario creado exitosamente"));
    }
	
	@Transactional
    public ResponseEntity<?> createEntradaInventario(InventarioEntradaDTO dto) {
        if(proveedorService.getProveedorByID(dto.getProveedorId()).isEmpty()) {
        	return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Este proveedor no existe"));
        }

        // Validación de existencia
        for (InventarioItemDTO item : dto.getItems()) {
            List<Materia> materia = materiaService.getMateriaByCodigo(item.getCodigo());
            if (materia.isEmpty()) {
                return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "La materia con el código " + item.getCodigo() + " no existe"));
            }
        }

        // Insertar InventarioEntrada
        String sqlSalida = "INSERT INTO Inventario_Entrada (ProveedorId, Fecha, MontoTotal, Notas, Usuario) VALUES (?, ?, ?, ?, ?)";
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sqlSalida, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, dto.getProveedorId());
            ps.setString(2, dto.getFecha());
            ps.setDouble(3, dto.getTotalAmount());
            ps.setString(4, dto.getNotes());
            ps.setString(5, dto.getCreatedBy());
            return ps;
        }, keyHolder);

        Long idSalida = keyHolder.getKey().longValue();

        // Insertar detalles
        String sqlDetalle = "INSERT INTO Detalle_Inventario_Entrada (CodigoMat, Cantidad, PCompra, Id_Salida) VALUES (?, ?, ?, ?)";

        for (InventarioItemDTO item : dto.getItems()) {
            List<Materia> materia = materiaService.getMateriaByCodigo(item.getCodigo());
            Double precio = materia.get(0).getPCompra();

            jdbcTemplate.update(sqlDetalle,
                item.getCodigo(),
                item.getCantidad(),
                precio,
                idSalida
            );

            // Opcional: actualizar existencia en Materia
            jdbcTemplate.update("UPDATE Materia SET Existencia = Existencia + ? WHERE CodigoMat = ?",
                item.getCantidad(),
                item.getCodigo()
            );
        }

        return ResponseEntity.ok(Map.of("message", "Entrada de inventario creada exitosamente"));
    }
}
