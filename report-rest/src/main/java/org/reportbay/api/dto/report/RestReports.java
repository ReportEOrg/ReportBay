package org.reportbay.api.dto.report;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.reportbay.snapshot.domain.ReportSnapShotBase;

public class RestReports implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private List<ReportSnapShotBase> liteReports = new ArrayList<ReportSnapShotBase>();

	/**
	 * @return the liteReports
	 */
	public List<ReportSnapShotBase> getLiteReports() {
		return liteReports;
	}

	/**
	 * @param liteReports the listReports to set
	 */
	public void setLiteReports(List<ReportSnapShotBase> liteReports) {
		this.liteReports = liteReports;
	}
	
}