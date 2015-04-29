package org.reportbay.datasource.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.reportbay.common.domain.BaseJPAEntity;

@Entity
@Table(name = "database_type")
@NamedQueries({ @NamedQuery(name = "DatabaseType.findAll", query = "SELECT dt FROM DatabaseType dt") })
public class DatabaseType extends BaseJPAEntity {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public enum DatabaseFamily {
		MsSQL, MySQL, Oracle
	}

	@TableGenerator(name = "DbType_Gen", table = "id_gen", pkColumnName = "gen_name", 
					valueColumnName = "gen_val", pkColumnValue = "DbType_Gen", 
					allocationSize = 1)
	@Id
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "DbType_Gen")
	private int id;
	private String name;
	@Enumerated(EnumType.STRING)
	private DatabaseFamily family;
	@Column(name = "url_pattern")
	private String urlPattern;
	@Column(name = "driver_name")
	private String driverName;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public DatabaseFamily getFamily() {
		return family;
	}

	public void setFamily(DatabaseFamily family) {
		this.family = family;
	}

	public String getUrlPattern() {
		return urlPattern;
	}

	public void setUrlPattern(String urlPattern) {
		this.urlPattern = urlPattern;
	}

	public String getDriverName() {
		return driverName;
	}

	public void setDriverName(String driverName) {
		this.driverName = driverName;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		
		HashCodeBuilder hcb = new HashCodeBuilder(INITIAL_HASH, PRIME_HASH_MULTIPLIER);
		
		hcb.append(driverName)
		   .append(family)
		   .append(id)
		   .append(name)
		   .append(urlPattern);

		return hcb.toHashCode();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(Object ref) {
		
		if(!super.equals(ref)){
			return false;
		}
		
		DatabaseType testRef = (DatabaseType) ref;
		
		EqualsBuilder eb = new EqualsBuilder();
		
		eb.append(driverName, testRef.driverName)
		  .append(family, testRef.family)
		  .append(id, testRef.id)
		  .append(name, testRef.name)
		  .append(urlPattern, testRef.urlPattern);
		
		return eb.isEquals();
	}
}
