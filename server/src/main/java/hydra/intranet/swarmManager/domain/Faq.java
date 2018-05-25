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
public class Faq implements Serializable {

	private static final long serialVersionUID = -6086011448734396338L;

	@Id
	private String id;

	@NotNull
	private String value;

}
