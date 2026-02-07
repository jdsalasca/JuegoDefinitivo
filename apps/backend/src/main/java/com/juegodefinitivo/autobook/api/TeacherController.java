package com.juegodefinitivo.autobook.api;

import com.juegodefinitivo.autobook.api.dto.AssignmentView;
import com.juegodefinitivo.autobook.api.dto.ClassroomDashboardResponse;
import com.juegodefinitivo.autobook.api.dto.ClassroomView;
import com.juegodefinitivo.autobook.api.dto.CreateAssignmentRequest;
import com.juegodefinitivo.autobook.api.dto.CreateClassroomRequest;
import com.juegodefinitivo.autobook.api.dto.CreateStudentRequest;
import com.juegodefinitivo.autobook.api.dto.LinkAttemptRequest;
import com.juegodefinitivo.autobook.api.dto.StudentView;
import com.juegodefinitivo.autobook.service.TeacherWorkspaceService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequestMapping("/api/teacher")
public class TeacherController {

    private final TeacherWorkspaceService workspaceService;

    public TeacherController(TeacherWorkspaceService workspaceService) {
        this.workspaceService = workspaceService;
    }

    @GetMapping("/classrooms")
    public List<ClassroomView> listClassrooms() {
        return workspaceService.listClassrooms();
    }

    @PostMapping("/classrooms")
    public ClassroomView createClassroom(@RequestBody CreateClassroomRequest request) {
        return workspaceService.createClassroom(request.name(), request.teacherName());
    }

    @PostMapping("/classrooms/{classroomId}/students")
    public StudentView addStudent(@PathVariable String classroomId, @RequestBody CreateStudentRequest request) {
        return workspaceService.addStudent(classroomId, request.name());
    }

    @GetMapping("/classrooms/{classroomId}/students")
    public List<StudentView> listStudents(@PathVariable String classroomId) {
        return workspaceService.listStudents(classroomId);
    }

    @PostMapping("/classrooms/{classroomId}/assignments")
    public AssignmentView createAssignment(@PathVariable String classroomId, @RequestBody CreateAssignmentRequest request) {
        return workspaceService.createAssignment(classroomId, request.title(), request.bookPath());
    }

    @GetMapping("/classrooms/{classroomId}/assignments")
    public List<AssignmentView> listAssignments(@PathVariable String classroomId) {
        return workspaceService.listAssignments(classroomId);
    }

    @PostMapping("/attempts/link")
    public void linkAttempt(@RequestBody LinkAttemptRequest request) {
        workspaceService.linkAttempt(request.studentId(), request.assignmentId(), request.sessionId());
    }

    @GetMapping("/classrooms/{classroomId}/dashboard")
    public ClassroomDashboardResponse getDashboard(@PathVariable String classroomId) {
        return workspaceService.getDashboard(classroomId);
    }

    @GetMapping(value = "/classrooms/{classroomId}/report.csv", produces = "text/csv")
    public ResponseEntity<byte[]> exportCsv(@PathVariable String classroomId) {
        String csv = workspaceService.exportClassroomCsv(classroomId);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"classroom-" + classroomId + "-report.csv\"")
                .contentType(new MediaType("text", "csv", StandardCharsets.UTF_8))
                .body(csv.getBytes(StandardCharsets.UTF_8));
    }
}
