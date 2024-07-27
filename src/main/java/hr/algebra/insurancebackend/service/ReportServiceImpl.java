package hr.algebra.insurancebackend.service;

import hr.algebra.insurancebackend.domain.*;
import hr.algebra.insurancebackend.dto.AcceptReportRequestDTO;
import hr.algebra.insurancebackend.dto.ReportDTO;
import hr.algebra.insurancebackend.dto.ReportRequestDTO;
import hr.algebra.insurancebackend.exceptions.ValidationException;
import hr.algebra.insurancebackend.repository.InfoReportDriverRepository;
import hr.algebra.insurancebackend.repository.ReportRepository;
import hr.algebra.insurancebackend.repository.VehicleRepository;
import hr.algebra.insurancebackend.security.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

import static hr.algebra.insurancebackend.utils.VehicleDriverRelation.getDriverOfVehicle;

@Service
public class ReportServiceImpl implements ReportService {

    @Autowired
    private ReportRepository reportRepository;
    @Autowired
    private InfoReportDriverRepository infoReportDriverRepository;
    @Autowired
    private AuthService authService;
    @Autowired
    private VehicleService vehicleService;
    @Autowired
    private VehicleRepository vehicleRepository;

    @Override
    public ReportDTO openAReport(ReportRequestDTO reportRequestDTO) throws ValidationException {
        Vehicle vehicleA = vehicleService.getAuthenticatedVehicle();

        Driver driverA = getDriverOfVehicle(vehicleA, reportRequestDTO.getDriverAId())
                .orElseThrow(() -> new IllegalArgumentException("The driverA is not associated with vehicle A"));

        InfoReportDriver infoReportDriverA = InfoReportDriver
                .builder()
                .damages(reportRequestDTO.getDamages())
                .vehicle(vehicleA)
                .driver(driverA)
                .status(InfoReportDriverStatus.ACCEPTED)
                .build();

        Vehicle vehicleB = vehicleRepository
                .findByPlate(reportRequestDTO.getVehicleB())
                .orElseThrow(() -> new UsernameNotFoundException("There is no vehicleB"));

        Driver driverB = getDriverOfVehicle(vehicleB, reportRequestDTO.getDriverBId())
                .orElseThrow(() -> new IllegalArgumentException("The driverB is not associated with vehicle B"));

        InfoReportDriver infoReportDriverB = InfoReportDriver
                .builder()
                .vehicle(vehicleB)
                .driver(driverB)
                .status(InfoReportDriverStatus.WAITING)
                .build();

        Report report = new Report(reportRequestDTO);
        InfoReportDriver infoReportDriverASaved = infoReportDriverRepository.save(infoReportDriverA);
        InfoReportDriver infoReportDriverBSaved = infoReportDriverRepository.save(infoReportDriverB);
        report.setInfoReportDriverA(infoReportDriverASaved);
        report.setInfoReportDriverB(infoReportDriverBSaved);

        return new ReportDTO(reportRepository.save(report));
    }

    @Override
    public List<ReportDTO> getRejectedReports() {
        return reportRepository
                .findAllByVehicleIdAndRejected(vehicleService.getAuthenticatedVehicle().getId())
                .stream()
                .map(ReportDTO::new)
                .toList();
    }
    @Override
    public List<ReportDTO> getWaitingReports() {
        return reportRepository
                .findAllByVehicleIdAndWaiting(vehicleService.getAuthenticatedVehicle().getId())
                .stream()
                .map(ReportDTO::new)
                .toList();
    }

    @Override
    public List<ReportDTO> getAcceptedReports() {
        return reportRepository
                .findAllByVehicleIdAndAccepted(vehicleService.getAuthenticatedVehicle().getId())
                .stream()
                .map(ReportDTO::new)
                .toList();
    }

    @Override
    public List<ReportDTO> getAllReports() {
        return reportRepository
                .findAllByVehicleId(vehicleService.getAuthenticatedVehicle().getId())
                .stream()
                .map(ReportDTO::new)
                .toList();
    }

    @Override
    public void acceptReport(AcceptReportRequestDTO acceptReportRequestDTO, long id) throws ValidationException {
        Vehicle vehicle = vehicleService.getAuthenticatedVehicle();
        Report report = reportRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Id is not correct"));

        InfoReportDriver infoReportDriverB = report.getInfoReportDriverB();

        if(!infoReportDriverB.getVehicle().equals(vehicle)){
            throw new IllegalArgumentException("Vehicle is not in this report, or unless cannot acccept this report");
        }

        infoReportDriverB.setDamages(acceptReportRequestDTO.getDamages());
        infoReportDriverB.setStatus(InfoReportDriverStatus.ACCEPTED);
        infoReportDriverRepository.save(infoReportDriverB);
    }

    @Override
    public void rejectReport(long id) {
        Vehicle vehicle = vehicleService.getAuthenticatedVehicle();
        Report report = reportRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Id is not correct"));

        InfoReportDriver infoReportDriverB = report.getInfoReportDriverB();
        if(!infoReportDriverB.getVehicle().equals(vehicle)){
            throw new IllegalArgumentException("Vehicle is not in this report, or unless cannot reject this report");
        }

        infoReportDriverB.setStatus(InfoReportDriverStatus.REJECTED);
        infoReportDriverRepository.save(infoReportDriverB);
    }

    @Override
    public long getNumOfReports() {
        return reportRepository.count();
    }

    @Override
    public long getNumOfReportsWaiting() {
        return reportRepository.countWaitingReports();
    }

    @Override
    public long getNumOfReportsRejected() {
        return reportRepository.countRejectedReports();
    }

    @Override
    public long getNumOfReportsAccepted() {
        return reportRepository.countAcceptedReports();
    }
}
