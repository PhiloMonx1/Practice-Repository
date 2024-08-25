package eh13.prac.dlnlvocatestlogicprac.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import eh13.prac.dlnlvocatestlogicprac.model.DTO.VocabularyDetailDTO;
import eh13.prac.dlnlvocatestlogicprac.model.DTO.VocabularyExamDTO;
import eh13.prac.dlnlvocatestlogicprac.model.DTO.WordRankUpdateDTO;
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
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
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
			allWords = mapper.readValue(inputStream,
					new TypeReference<List<Map<String, List<String>>>>() {
					});
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

	public VocabularyDetailDTO getVocabularyDetail(Long vocabularyId) {
		Vocabulary vocabulary = vocabularyRepository.findById(vocabularyId)
				.orElseThrow(() -> new RuntimeException("단어장을 찾지 못했습니다."));

		return VocabularyDetailDTO.builder()
				.vocabularyId(vocabulary.getId())
				.name(vocabulary.getName())
				.words(vocabulary.getWords().stream()
						.map(this::convertToWordSummary)
						.collect(Collectors.toList()))
				.build();
	}

	private VocabularyDetailDTO.WordSummaryDTO convertToWordSummary(Word word) {
		return VocabularyDetailDTO.WordSummaryDTO.builder()
				.id(word.getId())
				.word(word.getWord())
				.meaning(word.getMeaning())
				.rank(word.getRank())
				.count(word.getCount())
				.build();
	}

	@Transactional
	public VocabularyExamDTO generateVocabularyExam(Long vocabularyId) {
		Vocabulary vocabulary = vocabularyRepository.findById(vocabularyId)
				.orElseThrow(() -> new RuntimeException("단어장을 찾지 못했습니다."));

		List<Word> allWords = vocabulary.getWords();
		List<Word> zeroCountWords = allWords.stream()
				.filter(w -> w.getCount() == 0)
				.collect(Collectors.toList());
		List<Word> lowRankWords = allWords.stream()
				.filter(w -> w.getCount() > 0)
				.sorted(Comparator.comparing(Word::getRank))
				.limit(20)
				.collect(Collectors.toList());

		Set<Word> examWords = new HashSet<>();
		examWords.addAll(getRandomWords(zeroCountWords, Math.min(10, zeroCountWords.size())));
		examWords.addAll(getRandomWords(lowRankWords, Math.min(15, lowRankWords.size())));

		if (examWords.size() < 25) {
			examWords.addAll(getRandomWords(zeroCountWords, 25 - examWords.size()));
		}

		List<Word> shuffledExamWords = new ArrayList<>(examWords);
		Collections.shuffle(shuffledExamWords);

		return VocabularyExamDTO.builder()
				.vocabularyId(vocabularyId)
				.round(1)
				.words(shuffledExamWords.stream()
						.map(this::convertToWordExamDTO)
						.collect(Collectors.toList()))
				.build();
	}

	private List<Word> getRandomWords(List<Word> words, int count) {
		Collections.shuffle(words);
		return words.stream().limit(count).collect(Collectors.toList());
	}

	private VocabularyExamDTO.WordExamDTO convertToWordExamDTO(Word word) {
		return VocabularyExamDTO.WordExamDTO.builder()
				.id(word.getId())
				.word(word.getWord())
				.meaning(getRandomMeaning(word.getMeaning()))
				.rank(word.getRank())
				.count(word.getCount())
				.build();
	}

	private String getRandomMeaning(List<String> meanings) {
		return meanings.get(new Random().nextInt(meanings.size()));
	}

	@Transactional
	public void updateWordRanks(Long vocabularyId, WordRankUpdateDTO updateDTO) {
		Vocabulary vocabulary = vocabularyRepository.findById(vocabularyId)
				.orElseThrow(() -> new RuntimeException("단어장을 찾지 못했습니다."));

		Map<Long, Word> wordMap = vocabulary.getWords().stream()
				.collect(Collectors.toMap(Word::getId, w -> w));

		for (WordRankUpdateDTO.WordRankDTO wordRankDTO : updateDTO.getWords()) {
			Word word = wordMap.get(wordRankDTO.getId());
			if (word != null) {
				word.calculateRank(wordRankDTO.getRank());
				word.increaseCount();
			}
		}

		wordRepository.saveAll(wordMap.values());
	}
}
