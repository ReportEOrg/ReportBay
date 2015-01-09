package org.reporte.model.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.reporte.common.domain.BaseJPAEntity;

@Entity
@Table(name = "attribute_mapping")
public class AttributeMapping extends BaseJPAEntity{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@TableGenerator(name = "AttrMapping_Gen", table = "id_gen", pkColumnName = "gen_name", 
					valueColumnName = "gen_val", pkColumnValue = "AttrMapping_Gen", 
					allocationSize = 1)
	@Id
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "AttrMapping_Gen")
	private int id;
	@Column(name = "referenced_column")
	private String referencedColumn;
	private String alias;
	@Column(name = "type_name")
	private String typeName;
	@Column(name = "`order`")
	private int order;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getReferencedColumn() {
		return referencedColumn;
	}

	public void setReferencedColumn(String referencedColumn) {
		this.referencedColumn = referencedColumn;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public String getTypeName() {
		return typeName;
	}

	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}

	public int getOrder() {
		return order;
	}

	public void setOrder(int order) {
		this.order = order;
	}

	@Override
	public int hashCode() {
		
		HashCodeBuilder hcb = new HashCodeBuilder(INITIAL_HASH, PRIME_HASH_MULTIPLIER);
		
		hcb.append(alias)
		   .append(id)
		   .append(order)
		   .append(referencedColumn)
		   .append(typeName);
		
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
		
		AttributeMapping testRef = (AttributeMapping) ref;
		
		EqualsBuilder eb = new EqualsBuilder();
		
		eb.append(alias, testRef.alias)
		  .append(id, testRef.id)
		  .append(order, testRef.order)
		  .append(referencedColumn, testRef.referencedColumn)
		  .append(typeName, testRef.typeName);
		
		return eb.isEquals();
	}
}
