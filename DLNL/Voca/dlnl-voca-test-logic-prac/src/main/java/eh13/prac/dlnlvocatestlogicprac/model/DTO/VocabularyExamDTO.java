package eh13.prac.dlnlvocatestlogicprac.model.DTO;

import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class VocabularyExamDTO {
	private Long vocabularyId;
	private int round;
	private List<WordExamDTO> words;

	@Data
	@Builder
	public static class WordExamDTO {
		private Long id;
		private String word;
		private String meaning;
		private Float rank;
		private Integer count;
	}
}