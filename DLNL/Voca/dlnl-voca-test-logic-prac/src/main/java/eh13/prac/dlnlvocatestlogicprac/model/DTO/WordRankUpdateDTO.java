package eh13.prac.dlnlvocatestlogicprac.model.DTO;

import java.util.List;
import lombok.Data;

@Data
public class WordRankUpdateDTO {
	private int round;
	private List<WordRankDTO> words;

	@Data
	public static class WordRankDTO {
		private Long id;
		private Float rank;
	}
}