package hydra.intranet.swarmManager.domain;

import java.io.Serializable;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SwarmResource implements Serializable {

	private static final long serialVersionUID = 7922821812463333492L;

	private long limitCpu;
	private double limitCpuInCore;
	private long reservedCpu;
	private double reservedCpuInCore;

	private long limitMemory;
	private double limitMemoryInGB;
	private long reservedMemory;
	private double reservedMemoryInGB;

	public SwarmResource addLimitCpu(Long newLimitCpu) {
		limitCpu += newLimitCpu;
		limitCpuInCore = limitCpu * 1.0 / 1000000000;
		return this;
	}

	public SwarmResource addReservedCpu(Long newReservedCpu) {
		reservedCpu += newReservedCpu;
		reservedCpuInCore = reservedCpu * 1.0 / 1000000000;
		return this;
	}

	public SwarmResource addLimitMemory(Long newLimitMemory) {
		limitMemory += newLimitMemory;
		limitMemoryInGB = limitMemory * 1.0 / 1024 / 1024 / 1024;
		return this;
	}

	public SwarmResource addReservedMemory(Long newReservedMemory) {
		reservedMemory += newReservedMemory;
		reservedMemoryInGB = reservedMemory * 1.0 / 1024 / 1024 / 1024;
		return this;
	}

}
