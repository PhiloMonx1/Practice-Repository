package eh13.prac.dlnlvocatestlogicprac.repository;

import eh13.prac.dlnlvocatestlogicprac.model.Word;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WordRepository extends JpaRepository<Word, Long> {

}
