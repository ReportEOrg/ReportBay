/**
 * 
 */
package org.reportbay.reporttemplate.service.impl;

import java.util.Comparator;

import org.reportbay.reporttemplate.domain.CrossTabTemplateDetail;
import org.reportbay.reporttemplate.domain.GroupOrAggregate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author ahamed.nijamudeen
 * @version 1.0
 * @since 02-Feb-2015
 * @see {@link CrossTabTemplateDetail}
 *
 */
public class CrossTabDetailsComparator implements Comparator<CrossTabTemplateDetail> {
	
	private static final Logger LOG = LoggerFactory.getLogger(CrossTabDetailsComparator.class);

	/**
	 * Sort {@link CrossTabTemplateDetail} by SqlFunction and attributeDisplaySequence
	 */
	@Override
	public int compare(CrossTabTemplateDetail o1, CrossTabTemplateDetail o2) {
		/*
		 * Order the object by GroupBy first and its SequenceId then followed by other SqlFunction and its sequence.
		 * 
		 */
		LOG.info("compare(CrossTabTemplateDetail o1 [{}] CrossTabTemplateDetail o2 [{}]",o1.getGroupOrAggregate(),o2.getGroupOrAggregate());
		if (o1.getGroupOrAggregate().equals(o2.getGroupOrAggregate())) {
			LOG.info("o1 == 02");
			//Check if both the SQL function are of GroupBy
			if(o1.getGroupOrAggregate().equals(GroupOrAggregate.GROUPING)){
				//Lower the sequence Id should come first.
				//Eg. 1 should come before 2. in the order of sequence number
				if (o1.getAttributeDisplaySequence()<o2.getAttributeDisplaySequence()) {
					LOG.info("o1 < o2. Return -1");
					return -1;
				}else if(o1.getAttributeDisplaySequence()>o2.getAttributeDisplaySequence()){
					LOG.info("o1 > o2. Return 1");
					return 1;
				}
			}else if(o1.getGroupOrAggregate().equals(GroupOrAggregate.AGGREGATE)){
				//If the SQL function is other then type GroupBy. Sort them by Sequence Id.
				if (o1.getAttributeDisplaySequence()<o2.getAttributeDisplaySequence()) {
					LOG.info("o1 < o2. Return -1");
					return -1;
				}else if(o1.getAttributeDisplaySequence()>o2.getAttributeDisplaySequence()){
					LOG.info("o1 > o2. Return 1");
					return 1;
				}
			}
		}else if (!o1.getGroupOrAggregate().equals(o2.getGroupOrAggregate())){
			LOG.info("o1 != 02");
			if(o1.getGroupOrAggregate().equals(GroupOrAggregate.GROUPING)){
				LOG.info("o1 has GroupBy. Hence Return -1");
				return -1;
			}else if(o2.getGroupOrAggregate().equals(GroupOrAggregate.GROUPING)){
				LOG.info("o2 has GroupBy. Hence Return 1");
				return 1;
			}
		}
		return 0;
	}

}
