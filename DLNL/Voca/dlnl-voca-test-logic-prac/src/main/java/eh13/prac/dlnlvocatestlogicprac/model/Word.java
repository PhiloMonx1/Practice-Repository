package eh13.prac.dlnlvocatestlogicprac.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "words")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Word {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "vocabulary_id", nullable = false)
	private Vocabulary vocabulary;

	@Column(length = 20)
	private String word;

	@ElementCollection
	@CollectionTable(name = "word_meanings", joinColumns = @JoinColumn(name = "word_id"))
	@Column(name = "meaning")
	@JsonBackReference
	private List<String> meaning;

	@Builder.Default
	private Float rank = 0.0F;

	@Builder.Default
	private Integer count = 0;

	public void calculateRank(Float rank) {
		this.rank += rank;
	}

	public void increaseCount() {
		this.count ++;
	}
}
