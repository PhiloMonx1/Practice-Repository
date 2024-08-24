package eh13.prac.dlnlvocatestlogicprac.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import eh13.prac.dlnlvocatestlogicprac.model.Vocabulary;
import eh13.prac.dlnlvocatestlogicprac.model.Word;
import eh13.prac.dlnlvocatestlogicprac.repository.VocabularyRepository;
import eh13.prac.dlnlvocatestlogicprac.repository.WordRepository;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

@Service
public class VocabularyService {

	@Autowired
	private VocabularyRepository vocabularyRepository;

	@Autowired
	private WordRepository wordRepository;

	private List<Map<String, List<String>>> allWords;

	@PostConstruct
	public void init() {
		try {
			ObjectMapper mapper = new ObjectMapper();
			InputStream inputStream = new ClassPathResource("word_data.json").getInputStream();
			allWords = mapper.readValue(inputStream, new TypeReference<List<Map<String, List<String>>>>(){});
		} catch (IOException e) {
			throw new RuntimeException("JSON 파일을 읽지 못했습니다.", e);
		}
	}

	@Transactional
	public String generateVocabulary(String name) {
		List<Map<String, List<String>>> randomWords = getRandomWords(200);

		Vocabulary vocabulary = new Vocabulary().builder()
				.name(name)
				.build();
		vocabulary = vocabularyRepository.save(vocabulary);

		for (Map<String, List<String>> wordMap : randomWords) {
			String wordStr = wordMap.keySet().iterator().next();
			List<String> meanings = wordMap.get(wordStr);

			Word word = new Word().builder()
					.word(wordStr)
					.meaning(meanings)
					.vocabulary(vocabulary)
					.build();
			wordRepository.save(word);
		}

		return String.format("%s 번 단어장 생성 완료", vocabulary.getId());
	}
	private List<Map<String, List<String>>> getRandomWords(int count) {
		List<Map<String, List<String>>> shuffled = new ArrayList<>(allWords);
		Collections.shuffle(shuffled);
		return shuffled.subList(0, count);
	}
}
