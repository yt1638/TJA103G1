package com.showise.cinema.model;

import java.sql.Timestamp;
import java.time.LocalDate;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.web.bind.annotation.RequestParam;

public interface CinemaRepository extends JpaRepository<CinemaVO,Integer>{
}
