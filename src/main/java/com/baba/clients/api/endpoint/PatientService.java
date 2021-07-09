package com.baba.clients.api.endpoint;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import org.apache.cxf.rs.security.cors.CrossOriginResourceSharing;
import org.springframework.beans.factory.annotation.Autowired;

import com.baba.clients.api.model.ClinicalData;
import com.baba.clients.api.model.Patient;
import com.baba.clients.api.repos.PatientRepo;

@Path("api")
@Consumes("application/json")
@Produces("application/json")
@CrossOriginResourceSharing(allowAllOrigins=true)
public class PatientService {
	
	@Autowired
	PatientRepo repository;
	
	@Path("/patients")
	@POST
	public Patient createPatient(Patient patient) {
		return repository.save(patient);
	}
	
	@Path("/patients")
	@GET
	public List<Patient> getPatients() {
		return repository.findAll();
	}
	
	@Path("/patients/{id}")
	@GET
	public Patient getPatient(@PathParam("id") int id) {
		return repository.findById(id).get();
	}
	
	@Path("/patients/analyze/{id}")
	@GET
	public Patient Analyze(@PathParam("id") int id) {
		
		Patient patient=repository.findById(id).get();
		List<ClinicalData> clinicalData=new ArrayList<>(patient.getClinicalData());
		for(ClinicalData eachEntry:clinicalData) {
			if(eachEntry.getComponentName().equals("hw")) {
				String[] heightandweight=eachEntry.getComponentValue().split("/");
				String height=heightandweight[0];
				String weight=heightandweight[1];
				
				float heightInmeters=Float.parseFloat(height)*0.4536F;
				Float bmi=Float.parseFloat(weight)/(heightInmeters*heightInmeters);
				ClinicalData bmiData=new ClinicalData();
				bmiData.setComponentName("bmi");
				bmiData.setComponentValue(bmi.toString());
				
				patient.getClinicalData().add(bmiData) ;
			}
		}
		return patient;
	}
}
