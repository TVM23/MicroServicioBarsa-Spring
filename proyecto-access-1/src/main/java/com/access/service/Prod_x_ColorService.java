package com.access.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.access.dto.PaginationResult;
import com.access.dto.producto.ProductoxColorPaginationDTO;
import com.access.model.Producto_X_Color;
import com.access.repository.Prod_X_ColorRepository;

@Service
public class Prod_x_ColorService {

	private final Prod_X_ColorRepository prod_X_colorRepository;

	public Prod_x_ColorService(Prod_X_ColorRepository prod_X_colorRepository) {
		this.prod_X_colorRepository = prod_X_colorRepository;
	}

	public List<Producto_X_Color> getDetallesProductoXColor(String codigo, Integer colorId) {
		List<Producto_X_Color> info = prod_X_colorRepository.getDetallesProductoXColor(codigo, colorId);
		return info;
	}

	public PaginationResult<List<Producto_X_Color>> getProdColorFiltrados(ProductoxColorPaginationDTO dto) {
		int pageValue = dto.getPage();
		int limitValue = dto.getLimit();
		int offset = (pageValue - 1) * limitValue;

		// Construcción del SQL con INNER JOINs
		StringBuilder sql = new StringBuilder();

		List<Object> params = new ArrayList<>();

		// Aplicación de filtros dinámicos
		if (dto.getCodigo() != null) {
			sql.append(" AND pxc.Codigo = ?");
			params.add(dto.getCodigo());
		}
		if (dto.getColorId() != null) {
			sql.append(" AND pxc.ColorId = ?");
			params.add(dto.getColorId());
		}
		if (dto.getDesProducto() != null) {
			sql.append(" AND p.Descripcion LIKE ?");
			params.add("%" + dto.getDesProducto() + "%");
		}
		if (dto.getDesColor() != null) {
			sql.append(" AND c.Descripcion LIKE ?");
			params.add("%" + dto.getDesColor() + "%");
		}

		// Consulta para contar el total de elementos
		int totalItems = prod_X_colorRepository.contarElementosProd_X_Color(sql.toString(), params);

		int totalPages = (int) Math.ceil((double) totalItems / limitValue);

		// Consulta para obtener los datos paginados
		List<Producto_X_Color> data = prod_X_colorRepository.getListadoProd_X_Color(sql.toString(), params, limitValue,
				offset);

		return new PaginationResult<>(totalItems, totalPages, pageValue, data);
	}

	public void actualizarExisactualizarExistenciasProd_X_ColortenciasMateria(Boolean aumenta, Integer cantidad,
			String codigo, Integer colorId) {
		prod_X_colorRepository.actualizarExistenciasProd_X_Color(aumenta, cantidad, codigo, colorId);
	}

}
