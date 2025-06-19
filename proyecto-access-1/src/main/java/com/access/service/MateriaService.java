package com.access.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.access.dto.ImagenDTO;
import com.access.dto.PaginationResult;
import com.access.dto.materia.CreateMateriaDTO;
import com.access.dto.materia.MateriaPaginationDTO;
import com.access.model.Imagen;
import com.access.model.Materia;
import com.access.repository.MateriaRepository;
import com.access.service.cloudinary.CloudinaryService;

@Service
public class MateriaService {

	@Autowired
	private CloudinaryService cloudinaryService;
	private final BitacoraService bitacoraservice;
	private final MateriaRepository materiaRepository;

	public MateriaService(MateriaRepository materiaRepository, BitacoraService bitacoraService) {
		this.materiaRepository = materiaRepository;
		this.bitacoraservice = bitacoraService;
	}

	public List<Imagen> getImagenesMateria(String codigo) {
		List<Imagen> imagenes = materiaRepository.getImagenesMateria(codigo);
		return imagenes;
	}

	public List<Materia> getMateriaByDescripcion(String descripcion) {
		List<Materia> materia = materiaRepository.getMateriaByDescripcion(descripcion);
		return materia;
	}

	public List<Materia> getMateriaByCodigo(String codigo) {
		List<Materia> materia = materiaRepository.getMateriaByCodigo(codigo);
		return materia;
	}

	// Este metodo es para que el servicio de las notifs obtenga materia no borradas
	// saber que materias checar y ver si se genera notif o no
	public List<Materia> getMateriasNoBorradas() {
		List<Materia> materias = materiaRepository.getMateriasNoBorradas();
		return materias;
	}

	public PaginationResult<List<Materia>> getMateriasFiltradas(MateriaPaginationDTO dto) {
		int pageValue = dto.getPage();
		int limitValue = dto.getLimit();
		int offset = (pageValue - 1) * limitValue;

		StringBuilder sql = new StringBuilder();
		List<Object> params = new ArrayList<>(); // Lista para almacenar los parámetros

		if (dto.getCodigoMat() != null) {
			sql.append(" AND CodigoMat LIKE ?");
			params.add("%" + dto.getCodigoMat() + "%");
		}
		if (dto.getDescripcion() != null) {
			sql.append(" AND Descripcion LIKE ?");
			params.add("%" + dto.getDescripcion() + "%");
		}
		if (dto.getUnidad() != null) {
			sql.append(" AND Unidad = ?");
			params.add(dto.getUnidad());
		}
		if (dto.getProceso() != null) {
			sql.append(" AND Proceso = ?");
			params.add(dto.getProceso());
		}
		if (dto.getBorrado() != null) {
			sql.append(" AND Borrado = ?");
			params.add(dto.getBorrado());
		}

		// Contar el total de registros
		int totalItems = materiaRepository.contarElementosMaterias(sql.toString(), params);
		// Calcular el número total de páginas
		int totalPages = (int) Math.ceil((double) totalItems / limitValue);

		// Consulta paginada
		List<Materia> data = materiaRepository.getMateriasList(sql.toString(), params, limitValue, offset);

		return new PaginationResult<>(totalItems, totalPages, pageValue, data);
	}

	public ResponseEntity<?> createNewMateria(CreateMateriaDTO dto, String usuario) {
		if (getMateriaByDescripcion(dto.getDescripcion()).isEmpty()) {
			if (getMateriaByCodigo(dto.getCodigoMat()).isEmpty()) {
				// Guardar la materia y las URLs de las imágenes
				materiaRepository.createNewMateria(dto);
				if (dto.getImagenes() != null && !dto.getImagenes().isEmpty()) {
					for (ImagenDTO img : dto.getImagenes()) {
						materiaRepository.insertImgMateria(dto.getCodigoMat(), img.getUrl(), img.getPublic_id());
					}
				}
				bitacoraservice.registroInventario(true, dto.getCodigoMat(), usuario, dto.getExistencia(), 0.0, null);
				// notificacionService.evaluarNotificacion(dto.getCodigoMat());
				return ResponseEntity.ok(Map.of("message", "Materia creada correctamente"));
			} else {
				this.deleteImagesDueError(dto);
				return ResponseEntity.status(HttpStatus.BAD_REQUEST)
						.body(Map.of("error", "Ya existe una materia con ese codigo"));
			}
		} else {
			this.deleteImagesDueError(dto);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(Map.of("error", "Ya existe una materia con esa descripcion"));
		}
	}

	public ResponseEntity<?> updateMateria(CreateMateriaDTO dto, String usuario) {
		List<Materia> materiaConsulta = getMateriaByCodigo(dto.getCodigoMat());
		if (!materiaConsulta.isEmpty()) {
			List<Materia> mat = getMateriaByDescripcion(dto.getDescripcion());
			if (mat.isEmpty() || (mat.size() == 1 && mat.get(0).getCodigoMat().equals(dto.getCodigoMat()))) {
				Double existAnt = materiaConsulta.get(0).getExistencia();
				String codigo = dto.getCodigoMat();
				materiaRepository.updateMateria(dto);
				// Borrar imágenes anteriores asociadas
				List<Imagen> imagenesAntiguas = getImagenesMateria(codigo);
				for (Imagen img : imagenesAntiguas) {
					String public_id = img.getPublic_id();
					cloudinaryService.deleteImageCloudinary(public_id);
				}
				materiaRepository.deleteImagenesByCodigoMat(codigo);
				// Guardar las URLs de las imágenes
				if (dto.getImagenes() != null && !dto.getImagenes().isEmpty()) {
					for (ImagenDTO img : dto.getImagenes()) {
						materiaRepository.insertImgMateria(dto.getCodigoMat(), img.getUrl(), img.getPublic_id());
					}
				}
				bitacoraservice.registroInventario(false, dto.getCodigoMat(), usuario, dto.getExistencia(), existAnt,
						null);
				// notificacionService.evaluarNotificacion(dto.getCodigoMat());
				return ResponseEntity.ok(Map.of("message", "Materia actualizada correctamente"));

			} else {
				this.deleteImagesDueError(dto);
				return ResponseEntity.status(HttpStatus.BAD_REQUEST)
						.body(Map.of("error", "Ya existe una materia con esa descripcion"));
			}
		} else {
			this.deleteImagesDueError(dto);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(Map.of("error", "No existe ninguna materia con ese codigo"));
		}
	}

	public ResponseEntity<?> deleteMateria(String codigo, String usuario) {
		List<Materia> materia = getMateriaByCodigo(codigo);
		if (!materia.isEmpty()) {
			Materia mat = materia.get(0);
			materiaRepository.logicDeleteMateria(codigo);
			// Borrar imágenes anteriores asociadas
			List<Imagen> imagenesAntiguas = getImagenesMateria(codigo);
			for (Imagen img : imagenesAntiguas) {
				String public_id = img.getPublic_id();
				cloudinaryService.deleteImageCloudinary(public_id);
			}
			materiaRepository.deleteImagenesByCodigoMat(codigo);
			bitacoraservice.registroInventario(false, mat.getCodigoMat(), usuario, mat.getExistencia(),
					mat.getExistencia(), null);
			// notificacionService.deleteNotificacionCodigo(codigo);
			return ResponseEntity.ok(Map.of("message", "Materia borrada correctamente"));
		} else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(Map.of("error", "No existe ninguna materia con este código"));
		}
	}

	public void deleteImagesDueError(CreateMateriaDTO dto) {
		List<ImagenDTO> imagenesEliminar = dto.getImagenes();
		for (ImagenDTO img : imagenesEliminar) {
			String public_id = img.getPublic_id();
			cloudinaryService.deleteImageCloudinary(public_id);
		}
	}

}
