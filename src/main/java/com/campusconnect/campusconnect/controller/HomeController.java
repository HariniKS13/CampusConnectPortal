package com.campusconnect.campusconnect.controller;

import com.campusconnect.campusconnect.entity.StudentRequest;
import com.campusconnect.campusconnect.repository.StudentRequestRepository;

import jakarta.servlet.http.HttpSession;
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
    // LEGACY NOTICES REDIRECT
    // =========================

    @GetMapping("/notices")
    public String noticesRedirect() {
        return "redirect:/";
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
        request.setRegNo(regNo.trim().toUpperCase());
        request.setDepartment(department);
        request.setYear(year);
        request.setFromDate(fromDate);
        request.setToDate(toDate);
        request.setReason(reason);
        request.setStatus("PENDING");

        repository.save(request);

        return "redirect:/success?type=LEAVE&regNo=" + regNo.trim().toUpperCase();
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
        request.setRegNo(regNo.trim().toUpperCase());
        request.setDepartment(department);
        request.setYear(year);
        request.setReason(reason);
        request.setStatus("PENDING");

        repository.save(request);

        return "redirect:/success?type=BONAFIDE&regNo=" + regNo.trim().toUpperCase();
    }

    // =========================
    // SUCCESS
    // =========================

    @GetMapping("/success")
    public String success(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String regNo,
            Model model) {
        model.addAttribute("type", type != null ? type : "APPLICATION");
        model.addAttribute("regNo", regNo != null ? regNo : "");
        return "success";
    }

    // =========================
    // STUDENT STATUS PAGE
    // =========================

    @GetMapping("/status")
    public String statusPage(
            @RequestParam(required = false) String regNo,
            Model model) {

        if (regNo != null && !regNo.trim().isEmpty()) {
            String trimmedRegNo = regNo.trim().toUpperCase();
            List<StudentRequest> requests = repository.findByRegNoOrderByIdDesc(trimmedRegNo);
            model.addAttribute("requests", requests);
            model.addAttribute("regNo", trimmedRegNo);
            model.addAttribute("searched", true);
        } else {
            model.addAttribute("requests", null);
            model.addAttribute("regNo", null);
            model.addAttribute("searched", false);
        }

        return "status";
    }

    // =========================
    // STUDENT CHECK STATUS
    // =========================

    @PostMapping("/status")
    public String checkStatus(
            @RequestParam String regNo,
            Model model) {

        String trimmedRegNo = regNo != null ? regNo.trim().toUpperCase() : "";
        List<StudentRequest> requests = repository.findByRegNoOrderByIdDesc(trimmedRegNo);

        model.addAttribute("requests", requests);
        model.addAttribute("regNo", trimmedRegNo);
        model.addAttribute("searched", true);

        return "status";
    }

    // =========================
    // STAFF LOGIN PAGE
    // =========================

    @GetMapping("/staff/login")
    public String staffLogin(HttpSession session) {
        if (Boolean.TRUE.equals(session.getAttribute("staffLoggedIn"))) {
            return "redirect:/staff/dashboard";
        }
        return "staff-login";
    }

    // =========================
    // STAFF LOGIN SUBMIT
    // =========================

    @PostMapping("/staff/login")
    public String staffLoginSubmit(
            @RequestParam String email,
            @RequestParam String password,
            HttpSession session,
            Model model) {

        String staffEmail = "dhilipkumarece@siet.ac.in";
        String staffPassword = "DilipHODVLSI@2028";

        if (staffEmail.equalsIgnoreCase(email.trim()) && staffPassword.equals(password)) {
            session.setAttribute("staffLoggedIn", true);
            session.setAttribute("staffEmail", staffEmail);
            session.setAttribute("staffName", "Dr. Dhilip Kumar (HOD VLSI/ECE)");
            return "redirect:/staff/dashboard";
        }

        model.addAttribute("error", "Invalid staff email or password. Please try again.");
        return "staff-login";
    }

    // =========================
    // STAFF DASHBOARD
    // =========================

    @GetMapping("/staff/dashboard")
    public String staffDashboard(
            @RequestParam(required = false, defaultValue = "ALL") String filter,
            HttpSession session,
            Model model) {

        if (!Boolean.TRUE.equals(session.getAttribute("staffLoggedIn"))) {
            return "redirect:/staff/login";
        }

        List<StudentRequest> requests;
        if ("PENDING".equalsIgnoreCase(filter)) {
            requests = repository.findByStatusOrderByIdDesc("PENDING");
        } else if ("APPROVED".equalsIgnoreCase(filter)) {
            requests = repository.findByStatusOrderByIdDesc("APPROVED");
        } else if ("REJECTED".equalsIgnoreCase(filter)) {
            requests = repository.findByStatusOrderByIdDesc("REJECTED");
        } else {
            requests = repository.findAllByOrderByIdDesc();
        }

        long totalCount = repository.count();
        long pendingCount = repository.countByStatus("PENDING");
        long approvedCount = repository.countByStatus("APPROVED");
        long rejectedCount = repository.countByStatus("REJECTED");

        model.addAttribute("requests", requests);
        model.addAttribute("filter", filter.toUpperCase());
        model.addAttribute("totalCount", totalCount);
        model.addAttribute("pendingCount", pendingCount);
        model.addAttribute("approvedCount", approvedCount);
        model.addAttribute("rejectedCount", rejectedCount);
        model.addAttribute("staffName", session.getAttribute("staffName"));

        return "staff-dashboard";
    }

    // =========================
    // APPROVE REQUEST
    // =========================

    @PostMapping("/staff/approve")
    public String approveRequest(
            @RequestParam long id,
            HttpSession session) {

        if (!Boolean.TRUE.equals(session.getAttribute("staffLoggedIn"))) {
            return "redirect:/staff/login";
        }

        StudentRequest request = repository.findById(id).orElse(null);
        if (request != null) {
            request.setStatus("APPROVED");
            repository.save(request);
        }

        return "redirect:/staff/dashboard";
    }

    // =========================
    // REJECT REQUEST
    // =========================

    @PostMapping("/staff/reject")
    public String rejectRequest(
            @RequestParam long id,
            HttpSession session) {

        if (!Boolean.TRUE.equals(session.getAttribute("staffLoggedIn"))) {
            return "redirect:/staff/login";
        }

        StudentRequest request = repository.findById(id).orElse(null);
        if (request != null) {
            request.setStatus("REJECTED");
            repository.save(request);
        }

        return "redirect:/staff/dashboard";
    }

    // =========================
    // STAFF LOGOUT
    // =========================

    @GetMapping("/staff/logout")
    public String staffLogout(HttpSession session) {
        session.invalidate();
        return "redirect:/staff/login";
    }
}