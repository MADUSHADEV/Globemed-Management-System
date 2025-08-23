package lk.usj.OPD_Management.java.mediator;

import lk.usj.OPD_Management.java.dto.DoctorDTO;

import java.time.LocalDate;

public interface AppointmentFormMediator {
    void onPatientSearched(String username);
    void onSpecialistAreaChanged(String specialistArea) throws Exception;
    void onDoctorSelected(DoctorDTO doctor);
    void onDateChanged(LocalDate date);
    void onTimeChanged(String time);
    void validateForm();
    void resetForm();
}
