package br.com.core.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import br.com.core.model.abstractentity.AbstractEntity;

@Entity
@Table(name ="course")
/*@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)*/
public class CourseEntity extends AbstractEntity {

	private static final long serialVersionUID = 1L;

	@NotNull(message ="the filed title is mandatory")
	@Column(nullable = false)
	private String title;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	

}
