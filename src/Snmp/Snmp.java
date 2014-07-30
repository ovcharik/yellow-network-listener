package Snmp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import Config.Config;
import Data.DataLoad;
import Data.DataNode;
import Data.DataPort;

/**
 * Класс реализующий методы обращения к сети по SNMP
 */

class LoadObject {
	
	public LoadObject(long in, long out, long date) {
		this.in = in;
		this.out = out;
		this.date = date;
	}
	
	public long in;
	public long out;
	public long date;
}

public class Snmp {
	private Config config;
	private Storage storage;
	private Api snmp = null;
	
	static private Snmp instance = null;
	
	HashMap<Integer, HashMap<Integer, LoadObject>> lastLoad = null;
	
	/**
	 * Метод возвращающий ссылку на объект
	 * @return instance
	 */
	static public Snmp getInstance() {
		if (instance == null) {
			instance = new Snmp();
		}
		return instance;
	}
	/**
	 * {@link #SNMP()} 
	 */
	public Snmp() {
		config = Config.getInstance();
		storage = Storage.getInstance();
		lastLoad = new HashMap<Integer, HashMap<Integer, LoadObject>>();
		try {
			snmp = new Api();
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Метод возвращающий нагрузку по порту (количество in/out пакетов)
	 * и время обращения
	 * @param node
	 * @return storage.getLoads(node);
	 * @throws Throwable
	 */
	public List<DataLoad> getLoad(DataNode node) throws Throwable {
		
		long date = System.currentTimeMillis();
		
		if (storage.getDate(node) == -1 || storage.getLoads(node) == null ||
				Math.abs(date - storage.getDate(node)) > config.getDCUpdateTimeOut()) {
			//System.out.println("**UPDATING CACHE**");
            List<DataLoad> loads = new ArrayList<DataLoad>();
			for (Integer i: node.getPorts().keySet()) {
			    // .1.3.6.1.2.1.2.2.1.10.i
				//System.out.print("port: " + i + "  ");
	            int inPacks = snmp.getData(snmp.inPacketOID + i, node).toInt();
			    // .1.3.6.1.2.1.2.2.1.16.i
	            //System.out.print("inPacks: " + inPacks + "  ");
	            int outPacks = snmp.getData(snmp.outPacketOID + i, node).toInt();
	            //System.out.println("outPacks: " + outPacks);
	            
	            long dIn = 0;
	            long dOut = 0;
	            long dDate = 0;
	            
	            if (lastLoad.get(node.getId()) == null) {
	            	lastLoad.put(node.getId(), new HashMap<Integer, LoadObject>());
	            }
	            if (lastLoad.get(node.getId()).get(i) == null) {
	            	lastLoad.get(node.getId()).put(i, new LoadObject(inPacks, outPacks, date));
	            }
	            else {
	            	dDate = (date - lastLoad.get(node.getId()).get(i).date) / 1000;
	            	if (dDate == 0) dDate = 1;
	            	
	            	dIn = Math.abs(delta(lastLoad.get(node.getId()).get(i).in, inPacks)) / dDate;
	            	dOut = Math.abs(delta(lastLoad.get(node.getId()).get(i).out, outPacks)) / dDate;
	            	
	            	lastLoad.get(node.getId()).get(i).in = inPacks;
	            	lastLoad.get(node.getId()).get(i).out = outPacks;
	            	lastLoad.get(node.getId()).get(i).date = date;
	            }
	            
			    loads.add(new DataLoad(node.getId(), date, dIn, dOut, i));
			}
			storage.put(node, loads, date);//сохранение узла, нагрузки и времени обращения
		}
		return storage.getLoads(node);
	}
	
	private long delta(long a, long b) {
	    if (a > 0 && b < 0) {
	        return b - a + 0xFFFFFFFF;
	    }
	    else {
	        return b - a;
	    }
	}
	
	/**
	 * Метод возвращающий информацию(тип порта, тип интерфейса, MAC адрес, 
	 * максимальную скорость) о портах
	 * @param node
	 * @return result
	 * @throws Throwable
	 */
	public HashMap<Integer, DataPort> getPortsInfo(DataNode node) throws Throwable {
		HashMap<Integer, DataPort> result = new HashMap<Integer, DataPort>();
        Set<Integer> keyPorts = node.getPorts().keySet();
        for (Integer i: keyPorts) {
            
        	String portType = snmp.getData(snmp.portTypeOID + i, node).toString();
            String interfaceType = snmp.getData(snmp.interfaceTypeOID + i, node).toString();
            String macAddr = snmp.getData(snmp.MACOID + i, node).toString();
            
            int maxSpeed = snmp.getData(snmp.maxSpeedOID + i, node).toInt();
            
            result.put(i, new DataPort(node.getId(), i, portType, interfaceType, maxSpeed, macAddr, ""));
		}
		
        return result;
	}
	
	/**
	 * Метод возвращающий информацию(количество портов и имя) о узле
	 * @param ip
	 * @return result
	 * @throws Throwable
	 */
	public DataNode getNodeInfo(DataNode node) throws Throwable {
		DataNode result = node;
        
		HashMap<Integer, DataPort> allPorts = new HashMap<Integer, DataPort>();
        Integer countOfPorts = 0;
        String deviceName = "";
        try {
		    countOfPorts = snmp.getData(snmp.portNumberOID, node).toInt(); // количество портов, запрос по snmp;
		    deviceName = snmp.getData(snmp.deviceNameOID, node).toString();// имя устройства, запрос по snmp
        } catch (IOException e) {
            //TODO process exception
        	return null;
        }
		
        for (Integer i = 1; i <= countOfPorts; i++) {
			allPorts.put(i, null);
		}
        
		result.setPorts(allPorts);
		allPorts = getPortsInfo(result);
		result.setPorts(allPorts);
		result.setDeviceName(deviceName);

		return result;
	}
}
