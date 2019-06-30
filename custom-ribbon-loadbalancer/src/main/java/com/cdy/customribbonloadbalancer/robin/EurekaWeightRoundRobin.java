package com.cdy.customribbonloadbalancer.robin;


import com.netflix.niws.loadbalancer.DiscoveryEnabledServer;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class EurekaWeightRoundRobin extends WeightRoundRobin{
	
	public com.netflix.loadbalancer.Server choose(List<? extends com.netflix.loadbalancer.Server> servers){
		return super.getServer(init(servers));
	}
	
	public List<WeightRoundRobin.Server> init(List<? extends com.netflix.loadbalancer.Server> servers){
		
		if (servers.size() == serverCount){
			return super.servers;
		}
		
		List<WeightRoundRobin.Server> serverList = servers.stream().map(e -> {
			Map<String, String> metadata = ((DiscoveryEnabledServer)e).getInstanceInfo().getMetadata();
			String weight = metadata.get("weight");
			return new Server(e, StringUtils.isBlank(weight) ? 1 : Integer.valueOf(weight));
		}).collect(Collectors.toList());
		
		maxWeight = greatestWeight(serverList);
		gcdWeight = greatestCommonDivisor(serverList);
		serverCount = serverList.size();
		super.servers = serverList;
		return serverList;
	}

 
}
