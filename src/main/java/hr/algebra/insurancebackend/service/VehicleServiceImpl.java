package hr.algebra.insurancebackend.service;

import hr.algebra.insurancebackend.command.VehicleCommand;
import hr.algebra.insurancebackend.domain.Report;
import hr.algebra.insurancebackend.repository.InfoReportDriverRepository;
import hr.algebra.insurancebackend.repository.ReportRepository;
import hr.algebra.insurancebackend.security.domain.UserInfo;
import hr.algebra.insurancebackend.domain.Vehicle;
import hr.algebra.insurancebackend.dto.VehicleInfoDTO;
import hr.algebra.insurancebackend.repository.VehicleRepository;
import hr.algebra.insurancebackend.security.repository.UserRepository;
import hr.algebra.insurancebackend.security.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class VehicleServiceImpl implements VehicleService {

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    @Lazy
    private AuthService authService;

    @Autowired
    private ReportRepository reportRepository;

    @Autowired
    private InfoReportDriverRepository infoReportDriverRepository;

    @Override
    public Optional<VehicleInfoDTO> createVehicle(VehicleInfoDTO vehicle, UserInfo userInfo) {
        return Optional.of(new VehicleInfoDTO(vehicleRepository.save(new Vehicle(vehicle, userInfo))));
    }

    @Override
    public Optional<Vehicle> getVehicleOfUserInfo(UserInfo userInfo) {
        return vehicleRepository.findByUserInfoId(userInfo.getId());
    }

    @Override
    public Vehicle getAuthenticatedVehicle(){
        UserInfo currentUser = authService.getCurrentUser();
        return getVehicleOfUserInfo(currentUser)
                .orElseThrow(() -> new UsernameNotFoundException("There is no vehicle authenticated"));
    }


    @Override
    public VehicleInfoDTO update(Long id, VehicleCommand vehicleCommand) {
        Optional<Vehicle> byId = vehicleRepository.findById(id);
        if(byId.isEmpty()) throw new IllegalArgumentException("Vehicle Not found");
        Vehicle vehicle = byId.get();
        vehicle.setModel(vehicleCommand.getModel());
        vehicle.setBrand(vehicleCommand.getBrand());
        vehicle.setManufacturingYear(vehicleCommand.getManufacturingYear());
        Vehicle save = vehicleRepository.save(vehicle);
        return new VehicleInfoDTO(save);
    }

    @Override
    public List<VehicleInfoDTO> getAll() {
        return vehicleRepository
                .findAll()
                .stream()
                .map(VehicleInfoDTO::new)
                .toList();
    }

    @Override
    public Optional<VehicleInfoDTO> getById(Long id) {
        return vehicleRepository.findById(id).map(VehicleInfoDTO::new);
    }

    @Override
    public void deleteVehicle(Long id) {
        Optional<Vehicle> byId = vehicleRepository.findById(id);
        if(byId.isEmpty()) throw new IllegalArgumentException("Vehicle Not found");
        List<Report> allByVehicleId = reportRepository.findAllByVehicleId(byId.get().getId());
        allByVehicleId.forEach(report -> {
            reportRepository.deleteById(report.getId());
            infoReportDriverRepository.deleteById(report.getInfoReportDriverA().getId());
            infoReportDriverRepository.deleteById(report.getInfoReportDriverB().getId());
        });
        vehicleRepository.delete(byId.get());
    }

    @Override
    public  Optional<VehicleInfoDTO> getByPlate(String plate) {
        return vehicleRepository
                .findByPlate(plate)
                .map(VehicleInfoDTO::new);
    }

    @Override
    public long getNumOfVehicles() {
        return vehicleRepository.count();
    }
}
