package hr.algebra.insurancebackend.unit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.time.Year;
import java.util.Optional;
import java.util.List;
import java.util.Arrays;

import hr.algebra.insurancebackend.command.VehicleCommand;
import hr.algebra.insurancebackend.domain.Vehicle;
import hr.algebra.insurancebackend.dto.VehicleInfoDTO;
import hr.algebra.insurancebackend.repository.VehicleRepository;
import hr.algebra.insurancebackend.security.domain.UserInfo;
import hr.algebra.insurancebackend.security.service.AuthService;
import hr.algebra.insurancebackend.service.VehicleServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@ExtendWith(MockitoExtension.class)
class VehicleServiceImplTest {

    @Mock
    private VehicleRepository vehicleRepository;

    @Mock
    private AuthService authService;

    @InjectMocks
    private VehicleServiceImpl vehicleService;

    private UserInfo testUser;
    private Vehicle testVehicle;
    private VehicleInfoDTO testVehicleDTO;

    @BeforeEach
    public void setUp() {
        testUser = new UserInfo();
        testUser.setId(1L);

        testVehicle = new Vehicle();
        testVehicle.setId(1L);
        testVehicle.setUserInfo(testUser);
        testVehicle.setBrand("Toyota");
        testVehicle.setModel("Corolla");
        testVehicle.setManufacturingYear(Year.of(2020));

        testVehicleDTO = new VehicleInfoDTO(testVehicle);
    }

    @Test
    void testCreateVehicle() {
        when(vehicleRepository.save(any(Vehicle.class))).thenReturn(testVehicle);

        Optional<VehicleInfoDTO> result = vehicleService.createVehicle(testVehicleDTO, testUser);

        assertTrue(result.isPresent());

        assertEquals(testVehicleDTO, result.get());


    }

    @Test
    void testGetVehicleOfUserInfo() {
        when(vehicleRepository.findByUserInfoId(testUser.getId())).thenReturn(Optional.of(testVehicle));

        Optional<Vehicle> result = vehicleService.getVehicleOfUserInfo(testUser);

        assertTrue(result.isPresent());
        assertEquals(testVehicle, result.get());
    }

    @Test
    void testGetAuthenticatedVehicle() {
        when(authService.getCurrentUser()).thenReturn(testUser);
        when(vehicleRepository.findByUserInfoId(testUser.getId())).thenReturn(Optional.of(testVehicle));

        Vehicle result = vehicleService.getAuthenticatedVehicle();

        assertEquals(testVehicle, result);
    }

    @Test
    void testGetAuthenticatedVehicleNotFound() {
        when(authService.getCurrentUser()).thenReturn(testUser);
        when(vehicleRepository.findByUserInfoId(testUser.getId())).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> {
            vehicleService.getAuthenticatedVehicle();
        });
    }

    @Test
    void testUpdate() {
        VehicleCommand vehicleCommand = new VehicleCommand();
        vehicleCommand.setBrand("UpdatedBrand");
        vehicleCommand.setModel("UpdatedModel");
        vehicleCommand.setManufacturingYear(Year.of(2021));

        when(vehicleRepository.findById(1L)).thenReturn(Optional.of(testVehicle));
        when(vehicleRepository.save(any(Vehicle.class))).thenReturn(testVehicle);

        VehicleInfoDTO result = vehicleService.update(1L, vehicleCommand);

        assertNotNull(result);
        assertEquals("UpdatedBrand", result.getBrand());
        assertEquals("UpdatedModel", result.getModel());
    }

    @Test
    void testGetAll() {
        List<Vehicle> vehicles = Arrays.asList(testVehicle);
        when(vehicleRepository.findAll()).thenReturn(vehicles);

        List<VehicleInfoDTO> result = vehicleService.getAll();

        assertEquals(1, result.size());
        assertEquals(testVehicleDTO, result.get(0));
    }

    @Test
    void testGetById() {
        when(vehicleRepository.findById(1L)).thenReturn(Optional.of(testVehicle));

        Optional<VehicleInfoDTO> result = vehicleService.getById(1L);

        assertTrue(result.isPresent());
        assertEquals(testVehicleDTO, result.get());
    }

    @Test
    void testDeleteVehicle() {
        when(vehicleRepository.findById(1L)).thenReturn(Optional.of(testVehicle));
        doNothing().when(vehicleRepository).delete(testVehicle);

        vehicleService.deleteVehicle(1L);

        verify(vehicleRepository, times(1)).delete(testVehicle);
    }

    @Test
    void testGetByPlate() {
        when(vehicleRepository.findByPlate("ABC123")).thenReturn(Optional.of(testVehicle));

        Optional<VehicleInfoDTO> result = vehicleService.getByPlate("ABC123");

        assertTrue(result.isPresent());
        assertEquals(testVehicleDTO, result.get());
    }
}

