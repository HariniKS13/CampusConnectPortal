package com.campusconnect.campusconnect.controller;

import com.campusconnect.campusconnect.entity.StudentRequest;
import com.campusconnect.campusconnect.repository.StudentRequestRepository;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class HomeController {

    private final StudentRequestRepository repository;

    public HomeController(StudentRequestRepository repository) {
        this.repository = repository;
    }

    // =========================
    // HOME
    // =========================

    @GetMapping("/")
    public String home() {
        return "index";
    }

    // =========================
    // LEAVE PAGE
    // =========================

    @GetMapping("/leave")
    public String leave() {
        return "leave";
    }

    // =========================
    // SUBMIT LEAVE
    // =========================

    @PostMapping("/leave")
    public String submitLeave(
            @RequestParam String studentName,
            @RequestParam String regNo,
            @RequestParam String department,
            @RequestParam String year,
            @RequestParam String fromDate,
            @RequestParam String toDate,
            @RequestParam String reason) {

        StudentRequest request = new StudentRequest();

        request.setRequestType("LEAVE");
        request.setStudentName(studentName);
        request.setRegNo(regNo);
        request.setDepartment(department);
        request.setYear(year);
        request.setFromDate(fromDate);
        request.setToDate(toDate);
        request.setReason(reason);
        request.setStatus("PENDING");

        repository.save(request);

        return "redirect:/success";
    }

    // =========================
    // BONAFIDE PAGE
    // =========================

    @GetMapping("/bonafide")
    public String bonafide() {
        return "bonafide";
    }

    // =========================
    // SUBMIT BONAFIDE
    // =========================

    @PostMapping("/bonafide")
    public String submitBonafide(
            @RequestParam String studentName,
            @RequestParam String regNo,
            @RequestParam String department,
            @RequestParam String year,
            @RequestParam String reason) {

        StudentRequest request = new StudentRequest();

        request.setRequestType("BONAFIDE");
        request.setStudentName(studentName);
        request.setRegNo(regNo);
        request.setDepartment(department);
        request.setYear(year);
        request.setReason(reason);
        request.setStatus("PENDING");

        repository.save(request);

        return "redirect:/success";
    }

    // =========================
    // NOTICES
    // =========================

    @GetMapping("/notices")
    public String notices() {
        return "notices";
    }

    // =========================
    // SUCCESS
    // =========================

    @GetMapping("/success")
    public String success() {
        return "success";
    }

    // =========================
    // STUDENT STATUS PAGE
    // =========================

    @GetMapping("/status")
    public String statusPage(Model model) {

        model.addAttribute("requests", null);
        model.addAttribute("regNo", null);

        return "status";
    }

    // =========================
    // STUDENT CHECK STATUS
    // =========================

    @PostMapping("/status")
    public String checkStatus(
            @RequestParam String regNo,
            Model model) {

        List<StudentRequest> requests =
                repository.findByRegNo(regNo);

        model.addAttribute("requests", requests);
        model.addAttribute("regNo", regNo);

        return "status";
    }

    // =========================
    // STAFF LOGIN PAGE
    // =========================

    @GetMapping("/staff/login")
    public String staffLogin() {
        return "staff-login";
    }

    // =========================
    // STAFF LOGIN
    // =========================

    @PostMapping("/staff/login")
    public String staffLoginSubmit(
            @RequestParam String email,
            @RequestParam String password,
            Model model) {

        String staffEmail = "dhilipkumarece@siet.ac.in";
        String staffPassword = "DilipHODVLSI@2028";

        if (staffEmail.equals(email)
                && staffPassword.equals(password)) {

            List<StudentRequest> requests =
                    repository.findByStatus("PENDING");

            model.addAttribute("requests", requests);

            return "staff-dashboard";
        }

        model.addAttribute(
                "error",
                "Invalid email or password"
        );

        return "staff-login";
    }

    // =========================
    // APPROVE REQUEST
    // =========================

    @PostMapping("/staff/approve")
    public String approveRequest(
            @RequestParam long id) {

        StudentRequest request =
                repository.findById(id).orElse(null);

        if (request != null) {
            request.setStatus("APPROVED");
            repository.save(request);
        }

        return "redirect:/staff/login";
    }

    // =========================
    // REJECT REQUEST
    // =========================

    @PostMapping("/staff/reject")
    public String rejectRequest(
            @RequestParam long id) {

        StudentRequest request =
                repository.findById(id).orElse(null);

        if (request != null) {
            request.setStatus("REJECTED");
            repository.save(request);
        }

        return "redirect:/staff/login";
    }

    // =========================
    // TEST ROUTE
    // =========================

    @GetMapping("/test-status")
    public String testStatus() {
        return "STATUS ROUTE IS WORKING";
    }
}