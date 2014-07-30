package Snmp;

import java.io.IOException;

import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.Target;
import org.snmp4j.TransportMapping;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.*;
import org.snmp4j.transport.DefaultUdpTransportMapping;

import Data.DataNode;

/**
 * Класс реализуюший интерфейс для работы с SNMP
 *
 */
class Api {
	Snmp snmp;
    public final String inPacketOID = ".1.3.6.1.2.1.2.2.1.10.";
    public final String outPacketOID = ".1.3.6.1.2.1.2.2.1.16.";
    public final String maxSpeedOID = ".1.3.6.1.2.1.2.2.1.5.";
    public final String interfaceTypeOID = ".1.3.6.1.2.1.2.2.1.2.";
    public final String portTypeOID = ".1.3.6.1.2.1.2.2.1.3.";
    public final String MACOID = ".1.3.6.1.2.1.2.2.1.6.";
    public final String portNumberOID = ".1.3.6.1.2.1.2.1.0";
    public final String deviceNameOID = ".1.3.6.1.2.1.1.5.0";
	
	/** 
	 * {@link #SNMPAPI()}
	 */
	public Api() throws Throwable {
        @SuppressWarnings("rawtypes")
		TransportMapping transport = new DefaultUdpTransportMapping();
        snmp = new Snmp(transport);
        transport.listen();
	}

	/**
	 * Метод возврающий данные полученные по SNMP
	 * @param oid - Object IDentifier, идентификатор объекта
	 * @param ip - Internet Protocol, межсетевой протокол
	 * @return event.getResponse().get(0).getVariable()
	 * @throws Throwable
	 */
    public Variable getData(String oid, DataNode node) throws Throwable {
        ResponseEvent event = get(new OID[] { new OID(oid) }, node);
        Variable v = event.getResponse().get(0).getVariable();
        return v;
    }

	/**
	 * Метод для обработки нескольких OID
	 * @param oids - Object IDentifier, идентификатор объекта
	 * @return event
	 * @throws IOException
	 */
	public ResponseEvent get(OID oids[], DataNode node) throws Throwable {
		
		PDU pdu = new PDU();
		pdu.setType(PDU.GET);
		
		for (OID oid : oids) {
			pdu.add(new VariableBinding(oid));
		}
		
		ResponseEvent event = snmp.send(pdu, getTarget(node), null);
		if(event != null) {
			return event;
		}
		throw new RuntimeException("GET timed out");
	}
    
	/**
	 * Этот метод возвращает цель(сигнал), который содержит информацию о том, 
	 * где данные должны быть извлечены и как.
	 * @return target 
	 */

	private Target getTarget(DataNode node) {
		
		Address targetAddress = GenericAddress.parse("udp:"+ node.getIpAddress() + "/161"); //адрес cервера мониторинга
		CommunityTarget target = new CommunityTarget();
		target.setAddress(targetAddress);
		target.setRetries(1);
		target.setTimeout(2500);
		target.setVersion(SnmpConstants.version2c);
		target.setCommunity(new OctetString(node.getSnmpC()));
		
		return target;
	}

}

