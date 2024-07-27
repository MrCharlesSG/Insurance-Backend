package hr.algebra.insurancebackend.aspect;

import hr.algebra.insurancebackend.dto.AcceptReportRequestDTO;
import hr.algebra.insurancebackend.dto.ReportRequestDTO;
import hr.algebra.insurancebackend.exceptions.ValidationException;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class ReportValidationAspect {

    @Pointcut("execution(* hr.algebra.insurancebackend.service.ReportService.acceptReport(..))")
    public void acceptReportPointcut() {}

    @Before("acceptReportPointcut() && args(acceptReportRequestDTO, id)")
    public void validateInput(AcceptReportRequestDTO acceptReportRequestDTO, long id) throws ValidationException {
        if (acceptReportRequestDTO == null) {
            throw new ValidationException("Request can not be null");
        }
        if (id <= 0) {
            throw new ValidationException("ID must be positive");
        }
        if (acceptReportRequestDTO.getDamages() == null || acceptReportRequestDTO.getDamages().isBlank()) {
            throw new ValidationException("Damages must not be blank");
        }
    }

    @Pointcut("execution(* hr.algebra.insurancebackend.service.ReportService.openAReport(..))")
    public void openAReportPointcut() {}

    @Before("openAReportPointcut() && args(reportRequestDTO)")
    public void validateReportRequestDTO(ReportRequestDTO reportRequestDTO) throws ValidationException {
        if (reportRequestDTO == null) {
            throw new ValidationException("ReportRequestDTO object cannot be null");
        }
        if (reportRequestDTO.getDamages() == null || reportRequestDTO.getDamages().isBlank()) {
            throw new ValidationException("Damages field cannot be empty");
        }
        if (reportRequestDTO.getVehicleB() == null || reportRequestDTO.getVehicleB().isBlank()) {
            throw new ValidationException("VehicleB field cannot be empty");
        }
        if (reportRequestDTO.getDate() == null) {
            throw new ValidationException("Date field cannot be null");
        }
        if (reportRequestDTO.getPlace() == null || reportRequestDTO.getPlace().isBlank()) {
            throw new ValidationException("Place field cannot be empty");
        }
        if (reportRequestDTO.getDetails() == null || reportRequestDTO.getDetails().isBlank()) {
            throw new ValidationException("Details field cannot be empty");
        }
    }
}
