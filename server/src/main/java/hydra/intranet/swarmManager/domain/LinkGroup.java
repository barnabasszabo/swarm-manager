package hydra.intranet.swarmManager.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@Document
@NoArgsConstructor
@AllArgsConstructor
public class LinkGroup implements Serializable {

	private static final long serialVersionUID = -1901098513014007456L;

	@Id
	@JsonIgnore
	private String id;

	@JsonIgnore
	private String poolId;

	private String displayName;

	@Builder.Default
	private Collection<Link> links = new ArrayList<>();
}
