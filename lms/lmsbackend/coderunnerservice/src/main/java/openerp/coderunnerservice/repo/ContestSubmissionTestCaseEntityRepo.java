package openerp.coderunnerservice.repo;

import openerp.coderunnerservice.entity.ContestSubmissionTestCaseEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ContestSubmissionTestCaseEntityRepo extends JpaRepository<ContestSubmissionTestCaseEntity, UUID> {
    List<ContestSubmissionTestCaseEntity> findAllByContestSubmissionId(UUID contestSubmissionId);

    List<ContestSubmissionTestCaseEntity> findAllByContestSubmissionIdAndContestIdAndProblemIdAndSubmittedByUserLoginIdAndTestCaseId(UUID contestSubmissionId, String contestId, String problemId, String submittedByUserLoginId, UUID testCaseId);

    List<ContestSubmissionTestCaseEntity> findAllByContestSubmissionIdAndTestCaseId(UUID contestSubmissionId, UUID testCaseId);
}
