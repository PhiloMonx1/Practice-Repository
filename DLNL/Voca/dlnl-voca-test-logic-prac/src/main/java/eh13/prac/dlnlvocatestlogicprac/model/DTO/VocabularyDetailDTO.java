package eh13.prac.dlnlvocatestlogicprac.model.DTO;

import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class VocabularyDetailDTO {
	private Long vocabularyId;
	private String name;
	private List<WordSummaryDTO> words;

	@Data
	@Builder
	public static class WordSummaryDTO {
		private Long id;
		private String word;
		private List<String> meaning;
		private Float rank;
		private Integer count;
	}
}