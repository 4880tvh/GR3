//package openerp.openerpresourceserver.service;
//
//import lombok.AllArgsConstructor;
//import lombok.extern.log4j.Log4j2;
//import openerp.openerpresourceserver.entity.Services;
//import openerp.openerpresourceserver.repo.ServicesRepo;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//import java.util.NoSuchElementException;
//import java.util.Optional;
//
//@Log4j2
//@AllArgsConstructor(onConstructor_ = @Autowired)
//@Service
//public class ServicesServiceImpl implements ServicesService {
//
//    private ServicesRepo serviceRepo;
//
//    @Override
//    public List<Services> getAllServices() {
//        return serviceRepo.findAll();
//    }
//
//    @Override
//    public Services getServiceById(Integer serviceId) {
//        Optional<Services> service = serviceRepo.findById(serviceId);
//        if (service.isEmpty()) {
//            throw new NoSuchElementException("Service with ID " + serviceId + " does not exist");
//        }
//        return service.get();
//    }
//
//    @Override
//    public void addService(Services service) {
//        serviceRepo.save(service);
//    }
//
//    @Override
//    public void deleteServiceById(Integer serviceId) {
//        if (!serviceRepo.existsById(serviceId)) {
//            throw new NoSuchElementException("Service with ID " + serviceId + " does not exist");
//        }
//        serviceRepo.deleteById(serviceId);
//    }
//
//    @Override
//    public void updateServiceById(Integer serviceId, Services updatedService) {
//        Optional<Services> serviceOptional = serviceRepo.findById(serviceId);
//        if (serviceOptional.isEmpty()) {
//            throw new NoSuchElementException("Service with ID " + serviceId + " does not exist");
//        }
//        Services existingService = serviceOptional.get();
//        existingService.setServiceName(updatedService.getServiceName());
//        existingService.setServiceType(updatedService.getServiceType());
//        existingService.setUserId(updatedService.getUserId());
//        existingService.setApartmentId(updatedService.getApartmentId());
//        existingService.setSchedules(updatedService.getSchedules());
//        serviceRepo.save(existingService);
//    }
//}
