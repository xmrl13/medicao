package measurement.service;

import measurement.dto.MeasurementDTO;
import measurement.model.Measurement;
import measurement.repository.MeasurementRepository;
import measurementplaceitem.dto.MeasurementPlaceItemDTO;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MeasurementService {

    @Autowired
    private MeasurementRepository measurementRepository;

    @Transactional
    public MeasurementDTO createMedicao(MeasurementDTO measurementDTO) {

        if (measurementDTO.getStart().isAfter(measurementDTO.getEnd())) {
            throw new IllegalArgumentException("A data de início não deve ser posterior a data de fim");
        }

        if (measurementRepository.existsByProjectContractAndStartLessThanEqualAndEndGreaterThanEqual(measurementDTO.getProjectContract(), measurementDTO.getEnd(), measurementDTO.getStart())) {
            throw new IllegalArgumentException("Já existe uma medição no mesmo período para esta obra.");
        }

        if (measurementRepository.existsByYearMonth(measurementDTO.getYearMonth())){
            throw new IllegalArgumentException("Já existe uma medição para essa competência");
        }

        Measurement savedMeasurement = new Measurement(measurementDTO.getProjectContract(), measurementDTO.getStart(), measurementDTO.getEnd(), measurementDTO.getYearMonth());
        measurementRepository.save(savedMeasurement);
        return new MeasurementDTO(savedMeasurement.getProjectContract(), savedMeasurement.getStart(), savedMeasurement.getEnd(), savedMeasurement.getYearMonth());
    }

    @Transactional
    public MeasurementPlaceItemDTO add(MeasurementDTO measurementDTO) {
        return new MeasurementPlaceItemDTO();
    }

    public boolean measuramenteExistsById(Long id) {
        return measurementRepository.existsById(id);
    }
}