package com.savvis.spirits.report.common.interceptor;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

public class DAOLogger {
	private static final String METHOD_INSERT = "insert";
	private static final String METHOD_UPDATE = "update";
	private static final String METHOD_DELETE = "delete";
	private static final String OPERATION_INSERT = "Persist";
	private static final String OPERATION_UPDATE = "Update";
	private static final String OPERATION_DELETE = "Remove";
	private static final String KEY_PRE_OPERATION_MSG = "preOperationMsg";
	private static final String KEY_POST_OPREATION_ARG = "postOperationArg";
	private static final String PRE_PERSIST = "Persisting {} with given information into database..";
	private static final String PRE_MERGE = "Updating respective {} in the database with given information..";
	private static final String PRE_REMOVE = "Deleting {} with given id[{}] from database..";
	private static final String POST_OPERATION = "{} operation completed successfully.";
	
	private Logger getLogger(InvocationContext context) throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		Logger logger = null;
		
		Class<? extends Object> targetType = context.getTarget().getClass();
		
		if (LogInterceptable.class.isAssignableFrom(targetType)) {
			Method getLogger = targetType.getDeclaredMethod("getLogger");
			logger = (Logger) getLogger.invoke(context.getTarget());
		}
		return logger;
	}
	
	private String getTargetEntityName(Class<? extends Object> targetType) {
		for (Type genericInterface : targetType.getGenericInterfaces()) {
			if (genericInterface instanceof ParameterizedType) {
				Type[] genericTypes = ((ParameterizedType) genericInterface).getActualTypeArguments();
				// Max one and only one. i.e. LogInterceptable<Entity>
				return ((Class<?>) genericTypes[0]).getSimpleName(); 
			}
		}
		return null;
	}
	
	private Map<String,String> getPreOperationLogMsgTemplate(String targetMethodName) {
		Map<String, String> logMsgTemplate = new HashMap<String, String>();  
		String preOperationMsg = null;
		String postOperationArg = null;
		
		switch (targetMethodName) {
		case METHOD_INSERT :
			preOperationMsg = PRE_PERSIST;
			postOperationArg = OPERATION_INSERT;
			break;
		case METHOD_UPDATE :
			preOperationMsg = PRE_MERGE;
			postOperationArg = OPERATION_UPDATE;
			break;
		case METHOD_DELETE :
			preOperationMsg = PRE_REMOVE;
			postOperationArg = OPERATION_DELETE;
			break;
		default:
			// Do Nothing.
		}
		
		logMsgTemplate.put(KEY_PRE_OPERATION_MSG, preOperationMsg);
		logMsgTemplate.put(KEY_POST_OPREATION_ARG, postOperationArg);
		
		return logMsgTemplate;
	}
	
	private String getLogMsgTemplateForParamDetails(InvocationContext context) {
		StringBuilder sb = new StringBuilder("Invoked as " + context.getMethod().getName() + "(");
		int noOfParams = context.getParameters().length;
		int lastIndex = noOfParams - 1;
		
		for (int index = 0; index < noOfParams; index++) {
			if (noOfParams == 1 || index == lastIndex) {
				sb.append("{})");
			} else {
				sb.append("{}, ");
			}
		}
		return sb.toString();
	}
	
	@AroundInvoke
	public Object log(InvocationContext context) throws Exception {
		Object returnObj = null;
		
		Logger logger = getLogger(context);
		if (logger != null) {
			Map<String, String> logMsgTemplate = getPreOperationLogMsgTemplate(context.getMethod().getName());
			String logMsgTemplateString = logMsgTemplate.get(KEY_PRE_OPERATION_MSG);
			if (StringUtils.isEmpty(logMsgTemplateString)) {
				// method is not supported by DAOLogger. Just proceed.
				returnObj = context.proceed();
			} else {
				logger.debug(logMsgTemplateString, getTargetEntityName(context.getTarget().getClass()));
				logger.trace(getLogMsgTemplateForParamDetails(context), context.getParameters());
				returnObj = context.proceed();
				logger.debug(POST_OPERATION, logMsgTemplate.get(KEY_POST_OPREATION_ARG));
			}
		}
		return returnObj;
	}

}
