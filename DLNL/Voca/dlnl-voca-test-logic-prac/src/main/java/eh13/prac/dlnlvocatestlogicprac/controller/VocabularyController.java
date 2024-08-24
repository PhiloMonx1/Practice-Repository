package eh13.prac.dlnlvocatestlogicprac.controller;

import eh13.prac.dlnlvocatestlogicprac.model.DTO.VocabularyDetailDTO;
import eh13.prac.dlnlvocatestlogicprac.model.DTO.VocabularyExamDTO;
import eh13.prac.dlnlvocatestlogicprac.service.VocabularyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/vocabulary")
public class VocabularyController {

	@Autowired
	private VocabularyService vocabularyService;

	@PostMapping("/generate")
	public ResponseEntity<String> generateVocabulary(@RequestBody String name) {
		return ResponseEntity.ok(vocabularyService.generateVocabulary(name));
	}

	@GetMapping("/{vocabularyId}")
	public ResponseEntity<VocabularyDetailDTO> getVocabularyDetail(@PathVariable Long vocabularyId) {
		return ResponseEntity.ok(vocabularyService.getVocabularyDetail(vocabularyId));
	}

	@GetMapping("/{vocabularyId}/exam")
	public ResponseEntity<VocabularyExamDTO> getVocabularyExam(@PathVariable Long vocabularyId) {
		return ResponseEntity.ok(vocabularyService.generateVocabularyExam(vocabularyId));
	}
}
