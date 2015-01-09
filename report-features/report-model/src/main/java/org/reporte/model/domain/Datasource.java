package org.reporte.model.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.reporte.common.domain.BaseJPAEntity;

@Entity
@Table(name = "datasource")
@NamedQueries({ @NamedQuery(name = "Datasource.findAll", query = "SELECT ds FROM Datasource ds") })
public class Datasource extends BaseJPAEntity{
	private static final long serialVersionUID = 604370700984201521L;

	@TableGenerator(name = "Datasource_Gen", table = "id_gen", pkColumnName = "gen_name", 
					valueColumnName = "gen_val", pkColumnValue = "Datasource_Gen", 
					allocationSize = 1)
	@Id
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "Datasource_Gen")
	private int id;
	private String name;
	private String description;
	@ManyToOne(optional = false)
	@JoinColumn(name = "db_type", referencedColumnName = "id")
	private DatabaseType type;
	private String hostname;
	private String port;
	private String username;
	private String password;
	@Column(name = "schema_name")
	private String schema;

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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public DatabaseType getType() {
		return type;
	}

	public void setType(DatabaseType type) {
		this.type = type;
	}

	public String getHostname() {
		return hostname;
	}

	public void setHostname(String hostname) {
		this.hostname = hostname;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getSchema() {
		return schema;
	}

	public void setSchema(String schema) {
		this.schema = schema;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		
		HashCodeBuilder hcb = new HashCodeBuilder(INITIAL_HASH, PRIME_HASH_MULTIPLIER);
		
		//use method instead of attribute for those possible lazy load (e.g. joinColumn) 
		hcb.append(description)
		   .append(hostname)
		   .append(id)
		   .append(name)
		   .append(password)
		   .append(port)
		   .append(schema)
		   .append(getType())
		   .append(username);
		
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

		Datasource testRef = (Datasource) ref;
		
		EqualsBuilder eb = new EqualsBuilder();
		
		//use method instead of attribute for those possible lazy load (e.g. joinColumn) 
		eb.append(description, testRef.description)
		  .append(hostname, testRef.hostname)
		  .append(id, testRef.id)
		  .append(name, testRef.name)
		  .append(password, testRef.password)
		  .append(port, testRef.port)
		  .append(schema, testRef.schema)
		  .append(getType(), testRef.getType())
		  .append(username, testRef.username);

		return eb.isEquals();
	}

	@Override
	public String toString() {
		return "Datasource [id=" + id + ", name=" + name + ", description="
				+ description + ", type=" + type + ", hostname=" + hostname
				+ ", port=" + port + ", username=" + username + ", password="
				+ password + ", schema=" + schema + "]";
	}
	
	
}
