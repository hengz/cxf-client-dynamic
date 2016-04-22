package com.vickz.cxf_client_dynamic;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Vector;

import javax.wsdl.*;
import javax.wsdl.factory.WSDLFactory;
import javax.wsdl.xml.WSDLReader;

import org.apache.cxf.endpoint.Client;
import org.apache.cxf.jaxws.endpoint.dynamic.JaxWsDynamicClientFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * DynamicInvoke
 * @author zhangheng
 * @version 1.0
 *
 */
public class DynamicInvoke {
    private static final Logger logger = LoggerFactory
            .getLogger(DynamicInvoke.class);

    public Object[] invokeWSDL(String wsdlURI, String funcName, Object[] params) throws Exception {
        validateInvoking(wsdlURI, funcName, params);
        Object[] res = null;
        res = invoke(wsdlURI, funcName, params);
        return res;
    }

    private void validateInvoking(String wsdlURI, String funcName,
            Object[] params) throws WSDLException {
        //获取服务的 definitions 根节点
        WSDLFactory factory = WSDLFactory.newInstance();
        WSDLReader reader = factory.newWSDLReader();
        Definition definition = reader.readWSDL(wsdlURI);
        if (definition == null) {
            throw new WSDLException(WSDLException.OTHER_ERROR,
                    "No WSDL impl definition found.");
        }
        //获取服务端口
        Vector<PortType> allPorts = new Vector<PortType>();
        Map ports = definition.getPortTypes();
        Set s = ports.entrySet();
        Iterator it = s.iterator();
        while (it.hasNext()) {
            Entry entry = (Entry) it.next();
            Object obj = entry.getValue();
            if (obj instanceof PortType) {
                allPorts.add((PortType) obj);
            }
        }

        //get function and parameters
        Iterator<PortType> iter = allPorts.iterator();
        while (iter.hasNext()) {
            PortType portType = iter.next();
            List operations = portType.getOperations();
            Iterator operIter = operations.iterator();
            while (operIter.hasNext()) {
                Operation operation = (Operation) operIter.next();
                String operationName = operation.getName();
                if (funcName.equals(operationName)) {
//                    Message inputs = operation.getInput().getMessage();
                    return;
                }

                //                Object object = (Object) operIter.next();

            }
        }

        throw new WSDLException(funcName, "Function name not found!");
    }

    private Object[] invoke(String wsdlURI, String funcName, Object[] params) throws Exception {
        logger.info("Invoking webservice: " + wsdlURI + "\nFunction: "
                + funcName);
        JaxWsDynamicClientFactory dcf = JaxWsDynamicClientFactory.newInstance();
        Client client = dcf.createClient(wsdlURI);
        Object[] res = null;
        res = client.invoke(funcName, params);

        return res;
    }

    public static void main(String args[]) {
        String wsdlURI = "http://localhost:8080/cxf-server/services/testCXF?wsdl";
        String funcName = "testString";
        List paramsList = new ArrayList();
        paramsList.add("wori");
        DynamicInvoke dynamicInvoke = new DynamicInvoke();
        try {
            Object[] results = dynamicInvoke.invokeWSDL(wsdlURI, funcName,
                paramsList.toArray());
            System.out.println(results[0].toString());
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
