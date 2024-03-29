package openerp.containertransport.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import openerp.containertransport.algorithms.entity.*;
import openerp.containertransport.algorithms.entity.output.TransportContainerSolutionOutput;
import openerp.containertransport.algorithms.entity.output.TripOutput;
import openerp.containertransport.algorithms.solver.HeuristicSolver;
import openerp.containertransport.algorithms.solver.TransportContainerInput;
import openerp.containertransport.dto.*;
import openerp.containertransport.entity.Relationship;
import openerp.containertransport.service.*;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class AutoSolutionRouterServiceImpl implements AutoSolutionRouterService {
    private final TruckService truckService;
    private final TrailerService trailerService;
    private final ContainerServiceImpl containerService;
    private final FacilityService facilityService;
    private final OrderService orderService;
    private final RelationshipService relationshipService;
    private final ShipmentService shipmentService;
    private final TripService tripService;
    private final HeuristicSolver heuristicSolver;

    @Override
    public ShipmentModel autoSolutionRouter(String shipmentUid) {

        ShipmentModel shipmentModel = shipmentService.getShipmentByUid(shipmentUid);

        TransportContainerInput transportContainerInput = new TransportContainerInput();
        transportContainerInput.setStartTime(shipmentModel.getExecutedTime());

        // get Trucks
        TruckFilterRequestDTO truckFilterRequestDTO = new TruckFilterRequestDTO();
        truckFilterRequestDTO.setStatus("AVAILABLE");
//        truckFilterRequestDTO.setPage(0);
//        truckFilterRequestDTO.setPageSize(3);
        List<TruckModel> truckList = truckService.filterTruck(truckFilterRequestDTO).getTruckModels();
        List<TruckInput> truckInputs = convertToTruckInput(truckList);
        transportContainerInput.setTruckInputs(truckInputs);

        // get Trailers
        TrailerFilterRequestDTO trailerFilterRequestDTO = new TrailerFilterRequestDTO();
        trailerFilterRequestDTO.setStatus("AVAILABLE");
        List<TrailerModel> trailerModels = trailerService.filterTrailer(trailerFilterRequestDTO).getTrailerModels();
        List<TrailerInput> trailerInputs = convertToTrailerInput(trailerModels);
        transportContainerInput.setTrailerInputs(trailerInputs);

        // request
        OrderFilterRequestDTO orderFilterRequestDTO = new OrderFilterRequestDTO();
        List<String> status = new ArrayList<>();
        status.add("ORDERED");
        orderFilterRequestDTO.setStatus(status);
        List<OrderModel> orderModels = orderService.filterOrders(orderFilterRequestDTO).getOrderModels();
        List<Request> requests = convertToRequest(orderModels);
        transportContainerInput.setRequests(requests);

        // depot truck
        FacilityFilterRequestDTO facilityFilterTruck = new FacilityFilterRequestDTO();
        facilityFilterTruck.setType("Truck");
        List<FacilityModel> facilityModelTrucks = facilityService.filterFacility(facilityFilterTruck).getFacilityModels();
        List<DepotTruck> depotTrucks = convertTruckDepot(facilityModelTrucks);
        transportContainerInput.setDepotTruck(depotTrucks);

        // depot trailer
        FacilityFilterRequestDTO facilityFilterTrailer = new FacilityFilterRequestDTO();
        facilityFilterTrailer.setType("Trailer");
        List<FacilityModel> facilityModelsTrailer = facilityService.filterFacility(facilityFilterTrailer).getFacilityModels();
        List<DepotTrailer> depotTrailers = convertTrailerDepot(facilityModelsTrailer);
        transportContainerInput.setDepotTrailer(depotTrailers);

        // facility
        FacilityFilterRequestDTO facilityFilter = new FacilityFilterRequestDTO();
//        facilityFilter.setType("Container");
        List<FacilityModel> facilityModels = facilityService.filterFacility(facilityFilter).getFacilityModels();
        List<FacilityInput> facilities = convertFacilityInput(facilityModels);
        transportContainerInput.setFacilityInputs(facilities);

        // relationship
        List<Relationship> relationships = relationshipService.getAllRelationShip();
        List<DistanceElement> distanceElements = convertToDistantInput(relationships);
        transportContainerInput.setDistances(distanceElements);

        long startTime = System.currentTimeMillis();
        TransportContainerSolutionOutput transportContainerSolutionOutput = heuristicSolver.solve(transportContainerInput);
        long endTime = System.currentTimeMillis();
        log.info("Time process {}", endTime - startTime);
        shipmentModel.setTimeTest(endTime - startTime);
        for (Map.Entry<Integer, TripOutput> tripOutput : transportContainerSolutionOutput.getTripOutputs().entrySet()) {
            if(tripOutput.getValue().getPoints() != null && tripOutput.getValue().getPoints().size() != 0) {

                List<TripItemModel> tripItemModelList = new ArrayList<>();
                List<Long> orderIds = new ArrayList<>();
                int seq = 1;
                for (Point point : tripOutput.getValue().getPoints()) {

                    TripItemModel tripItemModel = new TripItemModel();
                    tripItemModel.setSeq(seq);
                    tripItemModel.setAction(point.getAction());
                    tripItemModel.setFacilityId(Long.valueOf(point.getFacilityId()));
                    tripItemModel.setOrderCode(point.getOrderCode());
                    tripItemModel.setOrderUid(point.getOrderId());
                    tripItemModel.setContainerId(point.getContainerId());
                    tripItemModel.setTrailerId(point.getTrailerId() != null ? Long.valueOf(point.getTrailerId()) : null);
                    tripItemModel.setType(point.getType());
                    if (point.getId() != null) {
                        if(!orderIds.contains(point.getId())) {
                            orderIds.add(point.getId());
                        }
                    }
                    tripItemModelList.add(tripItemModel);
                    seq += 1;
                }

                TripModel tripModel = new TripModel();
                tripModel.setTruckId(tripOutput.getKey().longValue());
                tripModel.setTripItemModelList(tripItemModelList);
                tripModel.setOrderIds(orderIds);
                tripModel.setTotalDistant(tripOutput.getValue().getTotalDistant());
                tripModel.setTotalTime(tripOutput.getValue().getTotalTime());

                TripModel tripModelCreate = tripService.createTrip(tripModel, shipmentUid, shipmentModel.getCreatedByUserId());
            }
        }

        return shipmentModel;
    }

    public List<TruckInput> convertToTruckInput(List<TruckModel> truckModels) {
        List<TruckInput> truckInputs = new ArrayList<>();
        truckModels.forEach((item) -> {
            TruckInput truckInput = new TruckInput();
            truckInput.setTruckID((int) item.getId());
            truckInput.setLocationId((int) item.getFacilityResponsiveDTO().getFacilityId());
            truckInput.setTruckCode(item.getTruckCode());
            truckInputs.add(truckInput);
        });
        return truckInputs;
    }

    public List<TrailerInput> convertToTrailerInput(List<TrailerModel> trailerModels) {
        List<TrailerInput> trailerInputs = new ArrayList<>();
        trailerModels.forEach((trailerModel) -> {
            TrailerInput trailerInput = new TrailerInput();
            trailerInput.setTrailerID((int) trailerModel.getId());
            trailerInput.setTrailerCode(trailerModel.getTrailerCode());
            trailerInput.setFacilityId((int) trailerModel.getFacilityResponsiveDTO().getFacilityId());
            trailerInputs.add(trailerInput);
        });
        return trailerInputs;
    }

    public List<Request> convertToRequest(List<OrderModel> orderModels) {
        List<Request> requests = new ArrayList<>();
        orderModels.forEach(orderModel -> {
            Request request = new Request();
            request.setRequestId(orderModel.getId());
            request.setRequestUid(orderModel.getUid());
            request.setOrderCode(orderModel.getOrderCode());
            request.setContainerID(orderModel.getContainerModel().getId());
            request.setWeightContainer(orderModel.getContainerModel().getSize());
            request.setFromLocationID((int) orderModel.getFromFacility().getFacilityId());
            request.setToLocationID((int) orderModel.getToFacility().getFacilityId());
            request.setLatestTimePickup(orderModel.getLatePickupTime());
            request.setLatestTimeDelivery(orderModel.getLateDeliveryTime());
            request.setIsBreakRomooc(orderModel.getIsBreakRomooc());
            request.setType(orderModel.getType());
            requests.add(request);
        });
        return requests;
    }

    public List<DepotTruck> convertTruckDepot(List<FacilityModel> facilityModels) {
        List<DepotTruck> depotTrucks = new ArrayList<>();
        facilityModels.forEach(facilityModel -> {
            DepotTruck depotTruck = new DepotTruck();
            depotTruck.setDepotTruckId((int) facilityModel.getId());
            depotTrucks.add(depotTruck);
        });
        return depotTrucks;
    }

    public List<DepotTrailer> convertTrailerDepot(List<FacilityModel> facilityModels) {
        List<DepotTrailer> depotTrailers = new ArrayList<>();
        facilityModels.forEach(facilityModel -> {
            DepotTrailer depotTrailer = new DepotTrailer();
            depotTrailer.setDepotTrailerId((int) facilityModel.getId());
            depotTrailers.add(depotTrailer);
        });
        return depotTrailers;
    }

    public List<FacilityInput> convertFacilityInput(List<FacilityModel> facilityModels) {
        List<FacilityInput> facilityInputs = new ArrayList<>();
        facilityModels.forEach(facilityModel -> {
            FacilityInput facilityInput = new FacilityInput();
            facilityInput.setFacilityId((int) facilityModel.getId());
            facilityInput.setTimeProcessPickup(facilityModel.getProcessingTimePickUp() != null ? facilityModel.getProcessingTimePickUp() : 0);
            facilityInput.setTimeProcessDrop(facilityModel.getProcessingTimeDrop() != null ? facilityModel.getProcessingTimeDrop() : 0);
            facilityInputs.add(facilityInput);
        });
        return facilityInputs;
    }

    public List<DistanceElement> convertToDistantInput(List<Relationship> relationships) {
        List<DistanceElement> distanceElements = new ArrayList<>();
        relationships.forEach(relationship -> {
            DistanceElement distanceElement = new DistanceElement();
            distanceElement.setFromFacility((int) relationship.getFromFacility().longValue());
            distanceElement.setToFacility((int) relationship.getToFacility().longValue());
            distanceElement.setDistance(relationship.getDistant());
            distanceElement.setTravelTime(relationship.getTime());
            distanceElements.add(distanceElement);
        });
        return distanceElements;
    }
}
