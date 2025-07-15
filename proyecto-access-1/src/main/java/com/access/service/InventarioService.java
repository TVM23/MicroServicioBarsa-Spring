package com.access.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.access.dto.PaginationResult;
import com.access.dto.inventario.DetalleMovMateriaDTO;
import com.access.dto.inventario.DetalleMovProductoDTO;
import com.access.dto.inventario.MovimientoMateriaDTO;
import com.access.dto.inventario.MovimientoMateriaPagiDto;
import com.access.dto.inventario.MovimientoProductoPagiDTO;
import com.access.dto.inventario.MovimientosDTO;
import com.access.model.DetalleMovimientoMateria;
import com.access.model.Detalle_Movimiento;
import com.access.model.Materia;
import com.access.model.MovimientoInventario;
import com.access.model.MovimientoMateria;
import com.access.model.Movimientos;
import com.access.model.Producto_X_Color;
import com.access.repository.InventarioRepository;

@Service
public class InventarioService {
	private final InventarioRepository inventarioRepository;
	private final PapeletaService papeletaService;
	private final MateriaService materiaService;
	private final Prod_x_ColorService prod_x_ColorService;
	private final BitacoraService bitacoraservice;

	public InventarioService(InventarioRepository inventarioRepository, PapeletaService papeletaService,
			MateriaService materiaService, Prod_x_ColorService prod_x_ColorService, BitacoraService bitacoraService) {
		this.inventarioRepository = inventarioRepository;
		this.papeletaService = papeletaService;
		this.materiaService = materiaService;
		this.prod_x_ColorService = prod_x_ColorService;
		this.bitacoraservice = bitacoraService;
		;
	}

	public List<MovimientoInventario> getMovimientoInventarioByMovId(Integer movId) {
		List<MovimientoInventario> movInv = inventarioRepository.getMovimientoInventarioByMovId(movId);
		return movInv;
	}

	public List<DetalleMovimientoMateria> getDetallesMovMateria(Integer consecutivo) {
		List<DetalleMovimientoMateria> movDetMat = inventarioRepository.getDetallesMovMateria(consecutivo);
		return movDetMat;
	}

	public List<Detalle_Movimiento> getDetallesMovProd(Integer consecutivo) {
		List<Detalle_Movimiento> movDetProd = inventarioRepository.getDetallesMovProd(consecutivo);
		return movDetProd;
	}

	@Transactional
	public ResponseEntity<?> createMovimientoMateria(MovimientoMateriaDTO dto) {
		if (dto.getFolio() != 0) {
			if (papeletaService.getPapeletasByFolio(dto.getFolio()).isEmpty()) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST)
						.body(Map.of("error", "Este folio de papeleta no existe"));
			}
		}

		// Validación de existencia
		for (DetalleMovMateriaDTO item : dto.getDetalles()) {
			List<Materia> materia = materiaService.getMateriaByCodigo(item.getCodigoMat());
			if (materia.isEmpty()) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST)
						.body(Map.of("error", "La materia con el código " + item.getCodigoMat() + " no existe"));
			}

			/*
			 * if (materia.get(0).getExistencia() < item.getCantidad()) { return
			 * ResponseEntity .status(HttpStatus.BAD_REQUEST) .body(Map.of("error",
			 * "Cantidad insuficiente para la materia con código " + item.getCodigo())); }
			 */
		}
		// Insertar Movimiento_Materia
		GeneratedKeyHolder keyHolder = inventarioRepository.insertMovimientoMateria(dto);
		Long consecutivo = keyHolder.getKey().longValue();
		// Insertar detalles
		List<MovimientoInventario> movimiento = getMovimientoInventarioByMovId(dto.getMovId());
		if (movimiento.isEmpty()) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(Map.of("error", "Este tipo de movimiento de inventario no existe"));
		}
		Boolean aumenta = movimiento.get(0).getAumenta(); // Obtenemos si el movimoento de inventario represanta
															// entradas o salidas
		for (DetalleMovMateriaDTO item : dto.getDetalles()) {
			List<Materia> materia = materiaService.getMateriaByCodigo(item.getCodigoMat());
			Materia mat = materia.get(0);
			if(dto.getMovId() == 5) {
				Double merma = mat.getMerma() / 100.0;
				Double cantidadSalida = item.getCantidad() + (item.getCantidad() * merma);
				inventarioRepository.insertDetalleMovimientoMateria(consecutivo, item.getCodigoMat(),
						cantidadSalida, mat.getExistencia(), mat.getPCompra(), item.getProcesada());
				// Actualizar existencia en Materia
				materiaService.actualizarExistenciasMateria(aumenta, cantidadSalida, item.getCodigoMat());
				// Registrar en bitacora
				bitacoraservice.insertBitacoraRegistro(dto.getMovId(), "Materia", dto.getFolio(), item.getCodigoMat(),
						dto.getUsuario().toUpperCase(), cantidadSalida, materia.get(0).getExistencia(), null);
				// notificacionService.evaluarNotificacion(item.getCodigoMat());
			}else {
				inventarioRepository.insertDetalleMovimientoMateria(consecutivo, item.getCodigoMat(),
						item.getCantidad(), mat.getExistencia(), mat.getPCompra(), item.getProcesada());
				// Actualizar existencia en Materia
				materiaService.actualizarExistenciasMateria(aumenta, item.getCantidad(), item.getCodigoMat());
				// Registrar en bitacora
				bitacoraservice.insertBitacoraRegistro(dto.getMovId(), "Materia", dto.getFolio(), item.getCodigoMat(),
						dto.getUsuario().toUpperCase(), item.getCantidad(), materia.get(0).getExistencia(), null);
				// notificacionService.evaluarNotificacion(item.getCodigoMat());
			}
		}
		return ResponseEntity.ok(Map.of("message", "Movimiento de materia generado exitosamente"));
	}

	@Transactional
	public ResponseEntity<?> createMovimientoProducto(MovimientosDTO dto) {
		// Validación de existencia
		for (DetalleMovProductoDTO item : dto.getDetalles()) {
			List<Producto_X_Color> prod = prod_x_ColorService.getDetallesProductoXColor(item.getCodigo(),
					item.getColorId());
			if (prod.isEmpty()) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST)
						.body(Map.of("error", "No existe el producto con el codigo " + item.getCodigo()
								+ " o con el Id color " + item.getColorId()));
			}
			/*
			 * if (materia.get(0).getExistencia() < item.getCantidad()) { return
			 * ResponseEntity .status(HttpStatus.BAD_REQUEST) .body(Map.of("error",
			 * "Cantidad insuficiente para la materia con código " + item.getCodigo())); }
			 */
		}
		// Insertar Movimiento (Producto)
		GeneratedKeyHolder keyHolder = inventarioRepository.insertMovimientoProducto(dto);
		Long consecutivo = keyHolder.getKey().longValue();

		// Insertar detalles
		List<MovimientoInventario> movimiento = getMovimientoInventarioByMovId(dto.getMovId());
		if (movimiento.isEmpty()) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(Map.of("error", "Este tipo de movimiento de inventario no existe"));
		}
		Boolean aumenta = movimiento.get(0).getAumenta();
		for (DetalleMovProductoDTO item : dto.getDetalles()) {
			List<Producto_X_Color> prod = prod_x_ColorService.getDetallesProductoXColor(item.getCodigo(),
					item.getColorId());
			inventarioRepository.insertDetalleMovimientoProd(consecutivo, item);
			// Actualizar existencia en Materia
			prod_x_ColorService.actualizarExisactualizarExistenciasProd_X_ColortenciasMateria(aumenta,
					item.getCantidad(), item.getCodigo(), item.getColorId());

			bitacoraservice.insertBitacoraRegistro(dto.getMovId(), "Producto", dto.getFolio(), item.getCodigo(),
					dto.getUsuario().toUpperCase(), item.getCantidad().doubleValue(),
					prod.get(0).getExistencia().doubleValue(), item.getColorId());

		}

		return ResponseEntity.ok(Map.of("message", "Movimiento de productos generado exitosamente"));
	}

	public PaginationResult<List<MovimientoMateria>> getListadoMovMateria(MovimientoMateriaPagiDto dto) {
		int pageValue = dto.getPage();
		int limitValue = dto.getLimit();
		int offset = (pageValue - 1) * limitValue;
		StringBuilder sql = new StringBuilder();
		List<Object> params = new ArrayList<>();

		if (dto.getFechaInicio() != null && dto.getFechaFin() != null) {
			sql.append(" AND Fecha BETWEEN ? AND ?");
			params.add(dto.getFechaInicio());
			params.add(dto.getFechaFin());
		}

		if (dto.getFolio() != null) {
			sql.append(" AND Folio = ?");
			params.add(dto.getFolio());
		}

		if (dto.getCodigoMat() != null || dto.getDescripcion() != null) {
			sql.append(" AND Consecutivo IN (SELECT dmm.Consecutivo FROM Detalle_Movimiento_Materia dmm "
					+ "INNER JOIN Materia m ON dmm.CodigoMat = m.CodigoMat WHERE 1=1");

			if (dto.getCodigoMat() != null) {
				sql.append(" AND LOWER(dmm.CodigoMat) LIKE ?");
				params.add("%" + dto.getCodigoMat().toLowerCase() + "%");
			}

			if (dto.getDescripcion() != null) {
				sql.append(" AND LOWER(m.Descripcion) LIKE ?");
				params.add("%" + dto.getDescripcion().toLowerCase() + "%");
			}

			sql.append(")");
		}

		if (dto.getMovId() != null) {
			sql.append(" AND MovId = ?");
			params.add(dto.getMovId());
		}

		if (dto.getConsecutivo() != null) {
			sql.append(" AND Consecutivo = ?");
			params.add(dto.getConsecutivo());
		}

		if (dto.getObservacion() != null) {
			sql.append(" AND Observacion LIKE ?");
			params.add("%" + dto.getObservacion() + "%");
		}

		if (dto.getUsuario() != null) {
			sql.append(" AND Usuario LIKE ?");
			params.add("%" + dto.getUsuario() + "%");
		}
		// Conteo total
		int totalItems = inventarioRepository.contarElementosMovimientoMateria(sql.toString(), params);

		// Paginación
		int totalPages = (int) Math.ceil((double) totalItems / limitValue);
		List<MovimientoMateria> data = inventarioRepository.getMovimientoMateriaList(sql.toString(), params, limitValue,
				offset);
		return new PaginationResult<>(totalItems, totalPages, pageValue, data);
	}

	public PaginationResult<List<Movimientos>> getListadoMovProducto(MovimientoProductoPagiDTO dto) {
		int pageValue = dto.getPage();
		int limitValue = dto.getLimit();
		int offset = (pageValue - 1) * limitValue;

		StringBuilder sql = new StringBuilder();
		List<Object> params = new ArrayList<>();

		if (dto.getFechaInicio() != null && dto.getFechaFin() != null) {
			sql.append(" AND Fecha BETWEEN ? AND ?");
			params.add(dto.getFechaInicio());
			params.add(dto.getFechaFin());
		}

		if (dto.getFolio() != null) {
			sql.append(" AND Folio = ?");
			params.add(dto.getFolio());
		}

		if (dto.getCodigo() != null || dto.getDescripcion() != null) {
			sql.append(" AND Consecutivo IN (SELECT dmp.Consecutivo FROM Detalle_Movimiento dmp "
					+ "INNER JOIN Producto p ON dmp.Codigo = p.Codigo WHERE 1=1");

			if (dto.getCodigo() != null) {
				sql.append(" AND LOWER(dmp.Codigo) LIKE ?");
				params.add("%" + dto.getCodigo().toLowerCase() + "%");
			}

			if (dto.getDescripcion() != null) {
				sql.append(" AND LOWER(p.Descripcion) LIKE ?");
				params.add("%" + dto.getDescripcion().toLowerCase() + "%");
			}

			sql.append(")");
		}

		if (dto.getMovId() != null) {
			sql.append(" AND MovId = ?");
			params.add(dto.getMovId());
		}

		if (dto.getConsecutivo() != null) {
			sql.append(" AND Consecutivo = ?");
			params.add(dto.getConsecutivo());
		}

		if (dto.getUsuario() != null) {
			sql.append(" AND Usuario LIKE ?");
			params.add("%" + dto.getUsuario() + "%");
		}

		// Conteo total
		int totalItems = inventarioRepository.contarElementosMovimientoProd(sql.toString(), params);
		// Paginación
		int totalPages = (int) Math.ceil((double) totalItems / limitValue);

		List<Movimientos> data = inventarioRepository.getMovimientoProdList(sql.toString(), params, limitValue, offset);
		return new PaginationResult<>(totalItems, totalPages, pageValue, data);
	}
}
