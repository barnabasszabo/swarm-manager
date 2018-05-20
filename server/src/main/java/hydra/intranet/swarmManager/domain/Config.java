package hydra.intranet.swarmManager.domain;

import java.io.Serializable;

import javax.validation.constraints.NotNull;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Builder;
import lombok.Data;

@Document
@Data
@Builder
public class Config implements Serializable {

	private static final long serialVersionUID = -6086011448734396248L;

	@Id
	private String id;

	@NotNull
	private String key;

	@NotNull
	private String value;
	
	private String description;

	@Builder.Default
	private ConfigType type = ConfigType.STRING;
}
