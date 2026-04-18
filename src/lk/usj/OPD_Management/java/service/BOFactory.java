package lk.usj.OPD_Management.java.service;

import lk.usj.OPD_Management.java.dao.custom.Impl.AppointmentDAOImpl;
import lk.usj.OPD_Management.java.service.custom.TreatmentPlanBO;
import lk.usj.OPD_Management.java.service.custom.impl.*;

public class BOFactory {
    private static BOFactory boFactory;

    private BOFactory() {
    }

    public static BOFactory getInstance() {
        if (boFactory == null) {
            boFactory = new BOFactory();
        }

        return boFactory;
    }

    public static BOFactory getBOFactory(){
        if(boFactory == null){
            boFactory = new BOFactory();
        }
        return boFactory;
    }

    @SuppressWarnings("unchecked")
    public <T extends SuperBO> T getBO(BOFactory.BOTypes boType) {
        switch(boType) {
            case LOG_IN:
                return (T) new LoginBOImpl();
            case PATIENT:
                return (T) new PatientBOImpl();
            case VISITOR:
                return (T) new VisitorBOImpl();
            case DOCTOR:
                return (T) new DoctorBOImpl();
            case APPOINTMENT:
                return (T) new AppointmentBOImpl();
            case TREATMENT_PLAN:
                return (T) new TreatmentPlanBOImpl();
            case POSTAL:
                return (T) new PostalBOImpl();
            case RECEPTIONIST:
                return (T) new ReceptionistBOImpl();
            default:
                throw new IllegalArgumentException("Unsupported BO type: " + boType);
        }
    }

    // Option 2: specific accessor (safer)
    public TreatmentPlanBO getTreatmentPlanBO() {
        return new TreatmentPlanBOImpl();
    }

    public static enum BOTypes {
         LOG_IN,
        PATIENT,
        VISITOR,
        DOCTOR,
        APPOINTMENT,
        POSTAL,
        TREATMENT_PLAN,
        RECEPTIONIST
    }
}
