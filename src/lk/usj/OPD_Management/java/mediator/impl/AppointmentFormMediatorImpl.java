package lk.usj.OPD_Management.java.mediator.impl;

import lk.usj.OPD_Management.java.common.Common;
import lk.usj.OPD_Management.java.controller.receptionist.appointment.ReceptionistAppointmentEditAppointmentController;
import lk.usj.OPD_Management.java.dto.DoctorDTO;
import lk.usj.OPD_Management.java.dto.PatientDTO;
import lk.usj.OPD_Management.java.mediator.AppointmentFormMediator;
import lk.usj.OPD_Management.java.service.custom.DoctorBO;
import lk.usj.OPD_Management.java.service.custom.PatientBO;
import lk.usj.OPD_Management.java.service.custom.impl.DoctorBOImpl;
import lk.usj.OPD_Management.java.service.custom.impl.PatientBOImpl;

import java.time.LocalDate;

public class AppointmentFormMediatorImpl implements AppointmentFormMediator {
    private ReceptionistAppointmentEditAppointmentController controller;
    private PatientBO patientBO = new PatientBOImpl();
    private DoctorBO doctorBO = new DoctorBOImpl();

    public AppointmentFormMediatorImpl(ReceptionistAppointmentEditAppointmentController controller) {
        this.controller = controller;
    }


    @Override
    public void onPatientSearched(String username) {
        try {
            PatientDTO patient = patientBO.searchPatient(username);
            controller.updatePatientFields(patient);
            validateForm();
        } catch (Exception e) {
            controller.clearPatientFields();
            Common.showError("Patient not found");
        }
    }

    @Override
    public void onSpecialistAreaChanged(String specialistArea) throws Exception {
        controller.clearDoctorSelection();
        if (!"Choose".equals(specialistArea)) {
            controller.loadDoctorTable(specialistArea);
        }
        validateForm();
    }

    @Override
    public void onDoctorSelected(DoctorDTO doctor) {
        controller.updateDoctorLabel(doctor.getName());
        validateForm();
    }

    @Override
    public void onDateChanged(LocalDate date) {
        // Check if date is valid (not in past, not weekend, etc.)
        if (date != null && date.isBefore(LocalDate.now())) {
            Common.showWarning("Cannot select past dates");
            controller.clearDate();
        }
        validateForm();
    }

    @Override
    public void onTimeChanged(String time) {
        validateForm();
    }

    @Override
    public void validateForm() {
        boolean isValid = controller.hasPatientData() &&
                controller.hasDoctorSelected() &&
                controller.hasValidDate() &&
                controller.hasTimeSelected();

        controller.setSaveButtonEnabled(isValid);
    }

    @Override
    public void resetForm() {
        controller.clearAllFields();
    }


}
