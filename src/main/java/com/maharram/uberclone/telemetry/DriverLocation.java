package com.maharram.uberclone.telemetry;

import com.maharram.uberclone.dtos.DriverDTO;
import com.maharram.uberclone.models.Location;
import jakarta.validation.constraints.NotNull;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class DriverLocation {

  @Autowired private RedisTemplate<String, Object> redisTemplate;

  @KafkaListener(topics = "driver-location")
  public void registerLocationToRedis(
      @NotNull ConsumerRecord<String, DriverDTO.DriverTelemtry> record) {
    DriverDTO.DriverTelemtry driverTelemtry = record.value();

    redisTemplate
        .opsForValue()
        .set(record.value().id().toString(), driverTelemtry, 15, TimeUnit.SECONDS);

  }
}
