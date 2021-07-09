### PatientClinicalReportsProject
##The Project is about patient details along with his clinical reports


**create patient model class**
```
package com.baba.clients.api.model;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

@Entity
public class Patient {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id;
	private String lastName;
	private String firstName;
	private int age;
	@OneToMany(cascade=CascadeType.ALL,fetch=FetchType.EAGER,mappedBy="patient")
	private List<ClinicalData> clinicalData;
	
	public List<ClinicalData> getClinicalData() {
		return clinicalData;
	}
	public void setClinicalData(List<ClinicalData> clinicalData) {
		this.clinicalData = clinicalData;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public int getAge() {
		return age;
	}
	public void setAge(int age) {
		this.age = age;
	}
	
	
	
}

```
**create clinicalData model class**
#it store clinical report data

```
package com.baba.clients.api.model;

import java.sql.Timestamp;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
@Entity
@Table(name="clinicaldata")
@JsonIgnoreProperties({"patient"})
public class ClinicalData {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id;
	private String componentName;
	private String componentValue;
	private Timestamp measuredDateTime;
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="patient_id",nullable=false)
	private Patient patient;

	public Patient getPatient() {
		return patient;
	}

	public void setPatient(Patient patient) {
		this.patient = patient;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getComponentName() {
		return componentName;
	}

	public void setComponentName(String componentName) {
		this.componentName = componentName;
	}

	public String getComponentValue() {
		return componentValue;
	}

	public void setComponentValue(String componentValue) {
		this.componentValue = componentValue;
	}

	public Timestamp getMeasureDateTime() {
		return measuredDateTime;
	}

	public void setMeasureDateTime(Timestamp measureDateTime) {
		this.measuredDateTime = measureDateTime;
	}

	
}

```

***create repository class for patient class for curd operations*
```
package com.baba.clients.api.repos;

import org.springframework.data.jpa.repository.JpaRepository;

import com.baba.clients.api.model.Patient;

public interface PatientRepo extends JpaRepository<Patient, Integer> {

}

```

***create repository class for clinicalData class for curd operations*

```
package com.baba.clients.api.repos;

import org.springframework.data.jpa.repository.JpaRepository;

import com.baba.clients.api.model.ClinicalData;

public interface ClinicalDataRepo extends JpaRepository<ClinicalData, Integer> {

}

```
**now we write service class for patient**

```
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

```
**service class for clinicalData**


```
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

``
**create dto class for clinicalData Request**

```
package com.baba.clients.api.dto;

public class ClinicalDataRequest {

	private int patientId;
	private String componentName;
	private String componentValue;

	public int getPatientId() {
		return patientId;
	}

	public void setPatientId(int patientId) {
		this.patientId = patientId;
	}

	public String getComponentName() {
		return componentName;
	}

	public void setComponentName(String componentName) {
		this.componentName = componentName;
	}

	public String getComponentValue() {
		return componentValue;
	}

	public void setComponentValue(String componentValue) {
		this.componentValue = componentValue;
	}

}

```

**propertes file**

```
cxf.jaxrs.classes-scan=true
cxf.jaxrs.classes-scan-packages=com.fasterxml.jackson.jaxrs,com.baba.clients.api,org.apache.cxf.rs.security.cors


server.servlet.context-path=/clinicalservices
cxf.path=/

spring.datasource.url=jdbc:mysql://localhost:3306/clinicals
spring.datasource.username=root
spring.datasource.password=Covid2020

server.port=8083
```
