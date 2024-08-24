package eh13.prac.dlnlvocatestlogicprac.repository;

import eh13.prac.dlnlvocatestlogicprac.model.Vocabulary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VocabularyRepository extends JpaRepository<Vocabulary, Long> {

}
