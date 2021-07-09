package com.baba.clients.api.endpoint;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.apache.cxf.rs.security.cors.CrossOriginResourceSharing;
import org.springframework.beans.factory.annotation.Autowired;

import com.baba.clients.api.dto.ClinicalDataRequest;
import com.baba.clients.api.model.ClinicalData;
import com.baba.clients.api.model.Patient;
import com.baba.clients.api.repos.ClinicalDataRepo;
import com.baba.clients.api.repos.PatientRepo;

@Path("api")
@Consumes("application/json")
@Produces("application/json")
@CrossOriginResourceSharing(allowAllOrigins=true)
public class ClinicalDataService {

	@Autowired
	PatientRepo patientrepo;
	@Autowired
	ClinicalDataRepo clinicalrepo;
	@Path("/clinicals")
	@POST
	public ClinicalData saveClinicalData(ClinicalDataRequest request) {
		
		Patient patient=patientrepo.findById(request.getPatientId()).get();
		
		ClinicalData clinicalData=new ClinicalData();
		clinicalData.setPatient(patient);
		clinicalData.setComponentName(request.getComponentName());
		clinicalData.setComponentValue(request.getComponentValue());
		return clinicalrepo.save(clinicalData);
		
	}
}
