package hydra.intranet.swarmManager.domain;

import java.io.Serializable;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Builder
@Document
@EqualsAndHashCode
public class Pool implements Serializable {

	private static final long serialVersionUID = -8869779825505220206L;

	@Id
	private String id;

	private String displayName;

	private String description;

	private String code;

	private Long weight;

}
