package hydra.intranet.swarmManager.service.detector;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
class ResourceTmp {
	long cpu;
	long memory;
}
