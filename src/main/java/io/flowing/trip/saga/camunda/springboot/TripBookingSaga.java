package io.flowing.trip.saga.camunda.springboot;

import java.util.HashMap;

import jakarta.annotation.PostConstruct;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.RepositoryService;
import org.camunda.bpm.engine.RuntimeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.flowing.trip.saga.camunda.adapter.BookFlightAdapter;
import io.flowing.trip.saga.camunda.adapter.BookHotelAdapter;
import io.flowing.trip.saga.camunda.adapter.CancelCarAdapter;
import io.flowing.trip.saga.camunda.adapter.CancelFlightAdapter;
import io.flowing.trip.saga.camunda.adapter.CancelHotelAdapter;
import io.flowing.trip.saga.camunda.adapter.ReserveCarAdapter;
import io.flowing.trip.saga.camunda.springboot.builder.SagaBuilder;

@Component
//@Singleton
public class TripBookingSaga {

  @Autowired
  private RuntimeService runtimeService;

  @Autowired
  private RepositoryService repositoryService;

  private ProcessEngine camunda;

  @PostConstruct
  public void defineSaga() {
    SagaBuilder saga = SagaBuilder.newSaga("trip") //
        .activity("Reserve car", ReserveCarAdapter.class) //
        .compensationActivity("Cancel car", CancelCarAdapter.class) //
        .activity("Book hotel", BookHotelAdapter.class) //
        .compensationActivity("Cancel hotel", CancelHotelAdapter.class) //
        .activity("Book flight", BookFlightAdapter.class) //
        .compensationActivity("Cancel flight", CancelFlightAdapter.class) //
        .end() //
        .triggerCompensationOnAnyError();

    repositoryService.createDeployment() //
        .addModelInstance("trip.bpmn", saga.getModel()) //
        .deploy();

//    File file = new File("result.bpmn");
//    Bpmn.writeModelToFile(file, saga.getModel());
  }
  
  public void bookTrip() {
    HashMap<String, Object> someVariables = new HashMap<>();
    // Could add some variables here - not used in simple demo
    runtimeService.startProcessInstanceByKey("trip", someVariables);
  }

}
