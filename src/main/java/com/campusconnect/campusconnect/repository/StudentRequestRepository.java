package com.campusconnect.campusconnect.repository;

import com.campusconnect.campusconnect.entity.StudentRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StudentRequestRepository extends JpaRepository<StudentRequest, Long> {

    List<StudentRequest> findAllByOrderByIdDesc();

    List<StudentRequest> findByStatus(String status);

    List<StudentRequest> findByStatusOrderByIdDesc(String status);

    List<StudentRequest> findByRegNo(String regNo);

    List<StudentRequest> findByRegNoOrderByIdDesc(String regNo);

    long countByStatus(String status);
}