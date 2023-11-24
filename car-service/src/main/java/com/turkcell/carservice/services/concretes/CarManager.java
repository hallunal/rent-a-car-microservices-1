package com.turkcell.carservice.services.concretes;

import com.turkcell.carservice.entities.Car;
import com.turkcell.carservice.entities.Image;
import com.turkcell.carservice.entities.dtos.requests.CreateCarRequestDto;
import com.turkcell.carservice.repositories.CarRepository;
import com.turkcell.carservice.services.abstracts.CarService;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CarManager implements CarService {

  private final CarRepository carRepository;
  private final KafkaTemplate<String, String> kafkaTemplate;

  @Override
  public void add(CreateCarRequestDto request) {
    Car car =
        Car.builder()
            .inventoryCode(request.getInventoryCode())
            .brand(request.getBrand())
            .model(request.getModel())
            .colour(request.getColour())
            .modelYear(request.getModelYear())
            .dailyPrice(request.getDailyPrice())
            .state(request.getState())
            .build();
    carRepository.save(car);
  }

  @Override
  public void update(String inventoryCode, CreateCarRequestDto request) {
    Car car = carRepository.findByInventoryCode(inventoryCode);
    car.setBrand(request.getBrand());
    car.setModel(request.getModel());
    car.setModelYear(request.getModelYear());
    car.setColour(request.getColour());
    car.setInventoryCode(request.getInventoryCode());
    car.setDailyPrice(request.getDailyPrice());
    car.setImages(new ArrayList<>());
    car.setState(request.getState());
    carRepository.save(car);
  }

  @Override
  public void delete(String inventoryCode) {
    Car car = carRepository.findByInventoryCode(inventoryCode);
    carRepository.delete(car);
  }

  @Override
  public List<Car> getAll() {
    return carRepository.findAll();
  }

  @Override
  public Car getByInventoryCode(String inventoryCode) {
    return carRepository.findByInventoryCode(inventoryCode);
  }

  @Override
  public Boolean getStateByInventoryCode(String inventoryCode) {
    Car car = getByInventoryCode(inventoryCode);
    kafkaTemplate.send("notificationTopic", " Arac durumu gosterildi..");
    if (car != null) {
      return car.getState();
    }
    return false;
  }

  public List<Image> getImagesByInventoryCode(String inventoryCode) {
    Car car = getByInventoryCode(inventoryCode);
    return car.getImages();
  }
}
