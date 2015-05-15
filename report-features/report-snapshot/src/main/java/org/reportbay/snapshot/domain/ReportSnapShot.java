package org.reportbay.snapshot.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

@Entity
@Table(name = "report_snap_shot")
@NamedQueries({ @NamedQuery(name = "ReportSnapShot.findAll", query = "SELECT e FROM ReportSnapShot e"),
				@NamedQuery(name = "ReportSnapShot.findAllBase", query="Select NEW org.reportbay.snapshot.domain.ReportSnapShotBase(e.id, e.creationDate, e.templateType,e.templateId, e.reportName) FROM ReportSnapShot e ")})
public class ReportSnapShot extends ReportSnapShotBase{
	
	public ReportSnapShot(){
		super();
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Lob
	@Column(name="snap_shot")
	private byte[] snapShot;
	

	/**
	 * @return the snapShot
	 */
	public byte[] getSnapShot() {
		return snapShot;
	}

	/**
	 * @param snapShot the snapShot to set
	 */
	public void setSnapShot(byte[] snapShot) {
		this.snapShot = snapShot;
	}
}


