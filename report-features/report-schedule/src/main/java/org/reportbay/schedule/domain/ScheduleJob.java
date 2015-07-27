package org.reportbay.schedule.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.reportbay.common.domain.BaseJPAEntity;

@Entity
@Table(name = "schedule_job")
@NamedQueries({ @NamedQuery(name = "ScheduleJob.findAll", query = "SELECT e FROM ScheduleJob e")})
public class ScheduleJob extends BaseJPAEntity{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@TableGenerator(name = "Schedule_Job_Gen", table = "id_gen", pkColumnName = "gen_name", 
					valueColumnName = "gen_val", pkColumnValue = "Schedule_Job_Gen", 
					allocationSize = 1)
	@Id
	@GeneratedValue(strategy = GenerationType.TABLE, generator="Schedule_Job_Gen")
	private int id;

	@Column(name="creation_date", length=19)
	@Temporal(TemporalType.TIMESTAMP)
	private Date creationDate;
	
	@Column(name="last_update_date", length=19)
	@Temporal(TemporalType.TIMESTAMP)
	private Date lastUpdateDate;
	
	@Column(name="name", length=45)
	private String name;
	
	@Column(name="schedule", length=45)
	private String schedule;
	
	@Column(name="template_id", length=11)
	private int templateId;
	
	@Column(name="callback_url", length=255)
	private String callbackUrl;

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @return the creationDate
	 */
	public Date getCreationDate() {
		return creationDate;
	}

	/**
	 * @param creationDate the creationDate to set
	 */
	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	/**
	 * @return the lastUpdateDate
	 */
	public Date getLastUpdateDate() {
		return lastUpdateDate;
	}

	/**
	 * @param lastUpdateDate the lastUpdateDate to set
	 */
	public void setLastUpdateDate(Date lastUpdateDate) {
		this.lastUpdateDate = lastUpdateDate;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the schedule
	 */
	public String getSchedule() {
		return schedule;
	}

	/**
	 * @param schedule the schedule to set
	 */
	public void setSchedule(String schedule) {
		this.schedule = schedule;
	}

	/**
	 * @return the templateId
	 */
	public int getTemplateId() {
		return templateId;
	}

	/**
	 * @param templateId the templateId to set
	 */
	public void setTemplateId(int templateId) {
		this.templateId = templateId;
	}

	/**
	 * @return the callbackUrl
	 */
	public String getCallbackUrl() {
		return callbackUrl;
	}

	/**
	 * @param callbackUrl the callbackUrl to set
	 */
	public void setCallbackUrl(String callbackUrl) {
		this.callbackUrl = callbackUrl;
	}
}