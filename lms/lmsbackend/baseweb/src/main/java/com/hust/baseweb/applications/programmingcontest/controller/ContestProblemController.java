package com.hust.baseweb.applications.programmingcontest.controller;

import com.google.gson.Gson;
import com.hust.baseweb.applications.chatgpt.ChatGPTService;
import com.hust.baseweb.applications.programmingcontest.constants.Constants;
import com.hust.baseweb.applications.programmingcontest.entity.*;
import com.hust.baseweb.applications.programmingcontest.exception.MiniLeetCodeException;
import com.hust.baseweb.applications.programmingcontest.model.*;
import com.hust.baseweb.applications.programmingcontest.repo.*;
import com.hust.baseweb.applications.programmingcontest.service.ContestService;
import com.hust.baseweb.applications.programmingcontest.service.ProblemTestCaseService;
import com.hust.baseweb.applications.programmingcontest.service.helper.cache.ProblemTestCaseServiceCache;
import com.hust.baseweb.entity.UserLogin;
import com.hust.baseweb.model.ListPersonModel;
import com.hust.baseweb.model.PersonModel;
import com.hust.baseweb.service.UserService;
import io.lettuce.core.dynamic.annotation.Param;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.io.InputStream;
import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@CrossOrigin
@AllArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class ContestProblemController {

    ProblemTestCaseService problemTestCaseService;
    ContestRepo contestRepo;
    ContestSubmissionRepo contestSubmissionRepo;
    ContestProblemRepo contestProblemRepo;
    UserService userService;
    ContestService contestService;
    UserRegistrationContestRepo userRegistrationContestRepo;
    UserContestProblemRoleRepo userContestProblemRoleRepo;
    ProblemTestCaseServiceCache cacheService;
    ChatGPTService chatGPTService;

    @PostMapping("/create-problem")
    public ResponseEntity<?> createContestProblem(
        Principal principal,
        @RequestParam("ModelCreateContestProblem") String json,
        @RequestParam("files") MultipartFile[] files
    ) throws MiniLeetCodeException {
        ProblemEntity resp = problemTestCaseService.createContestProblem(principal.getName(), json, files);
        return ResponseEntity.status(200).body(resp);
    }

    @PostMapping("/get-test-case-result/{problemId}")
    public ResponseEntity<?> getTestCaseResult(
        @PathVariable("problemId") String problemId,
        @RequestBody ModelGetTestCaseResult testCaseResult, Principal principal
    ) throws Exception {
        log.info("get test case result {}", problemId);
        ModelGetTestCaseResultResponse resp = problemTestCaseService.getTestCaseResult(problemId, principal.getName(),
                                                                                       testCaseResult);
        return ResponseEntity.status(200).body(resp);
    }

    @Secured("ROLE_TEACHER")
    @GetMapping("/get-all-contest-problems")
    public ResponseEntity<?> getAllContestProblems(Principal principal) {
        List<ProblemEntity> problems = problemTestCaseService.getAllProblems();
        return ResponseEntity.ok().body(problems);
    }

    @GetMapping("/get-all-contest-problems-general-info")
    public ResponseEntity<?> getAllContestProblemsGeneralInfo(Principal principal) {
        List<ModelProblemGeneralInfo> problems = problemTestCaseService.getAllProblemsGeneralInfo();
        return ResponseEntity.ok().body(problems);
    }


    @Secured("ROLE_TEACHER")
    @PostMapping("/problem/generate-statement")
    public ResponseEntity<?> suggestProblemStatement(@RequestBody ProblemSuggestionRequest suggestion) throws Exception {
        String problemStatement = chatGPTService.getChatGPTAnswer(suggestion.generateRequest());
        return ResponseEntity.status(200).body(problemStatement);
    }

    @Secured("ROLE_TEACHER")
    @GetMapping("/problem-details/{problemId}")
    public ResponseEntity<?> getProblemDetails(@PathVariable("problemId") String problemId, Principal teacher) throws Exception {
        log.info("getProblemDetails problemId {}", problemId);
        try {
            ModelCreateContestProblemResponse problemResponse = problemTestCaseService.getContestProblemDetailByIdAndTeacher(problemId, teacher.getName());
            return ResponseEntity.status(200).body(problemResponse);
        } catch (MiniLeetCodeException e) {
            return ResponseEntity.status(e.getCode()).body(e.getMessage());
        }
    }

    @GetMapping("/get-problem-detail-view-by-student/{problemId}")
    public ResponseEntity<?> getProblemDetailViewByStudent(
        Principal principal,
        @PathVariable("problemId") String problemId
    ) {
        try {
            ModelCreateContestProblemResponse problemEntity = problemTestCaseService.getContestProblem(problemId);
            ModelStudentViewProblemDetail model = new ModelStudentViewProblemDetail();
            model.setProblemStatement(problemEntity.getProblemDescription());
            model.setProblemName(problemEntity.getProblemName());
            return ResponseEntity.ok().body(model);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.ok().body("NOTFOUND");
    }

    @Secured("ROLE_TEACHER")
    @GetMapping("/get-problem-detail-view-by-manager/{problemId}")
    public ResponseEntity<?> getProblemDetailViewByManager(Principal principal, @PathVariable String problemId) {
        try {
            ModelCreateContestProblemResponse problemEntity = problemTestCaseService.getContestProblem(problemId);
            ModelStudentViewProblemDetail model = new ModelStudentViewProblemDetail();
            model.setProblemStatement(problemEntity.getProblemDescription());
            model.setProblemName(problemEntity.getProblemName());
            model.setCreatedStamp(problemEntity.getCreatedAt());
            model.setAttachment(problemEntity.getAttachment());
            model.setAttachmentNames(problemEntity.getAttachmentNames());
            PersonModel person = userService.findPersonByUserLoginId(problemEntity.getUserId());
            model.setCreatedByUserFullName(person.getFullName());
            return ResponseEntity.ok().body(model);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.ok().body("NOTFOUND");

    }

    @GetMapping("/get-problem-detail-view-by-student-in-contest/{problemId}/{contestId}")
    public ResponseEntity<?> getProblemDetailViewByStudent(
        Principal principal,
        @PathVariable("problemId") String problemId, @PathVariable("contestId") String contestId
    ) {
        try {
            ContestEntity contestEntity = contestRepo.findContestByContestId(contestId);
            ContestProblem cp = contestProblemRepo.findByContestIdAndProblemId(contestId, problemId);
            if (cp == null) {
                return ResponseEntity.ok().body("NOTFOUND");
            }
            ModelCreateContestProblemResponse problemEntity = problemTestCaseService.getContestProblem(problemId);
            ModelStudentViewProblemDetail model = new ModelStudentViewProblemDetail();
            if (contestEntity.getProblemDescriptionViewType() != null &&
                contestEntity.getProblemDescriptionViewType()
                             .equals(ContestEntity.CONTEST_PROBLEM_DESCRIPTION_VIEW_TYPE_HIDDEN)) {
                model.setProblemStatement(" ");
            } else {
                model.setProblemStatement(problemEntity.getProblemDescription());
            }

            model.setSubmissionMode(cp.getSubmissionMode());
            model.setProblemName(cp.getProblemRename());
            model.setProblemCode(cp.getProblemRecode());
            model.setAttachment(problemEntity.getAttachment());
            model.setAttachmentNames(problemEntity.getAttachmentNames());
            return ResponseEntity.ok().body(model);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.ok().body("NOTFOUND");
    }

    @PostMapping("/update-problem-detail/{problemId}")
    public ResponseEntity<?> updateProblemDetails(
        @PathVariable("problemId") String problemId, Principal principal,
        @RequestParam("ModelUpdateContestProblem") String json, @RequestParam("files") MultipartFile[] files
    )
        throws Exception {
        //log.info("updateProblemDetails problemId {}", problemId);
        List<UserContestProblemRole> L = userContestProblemRoleRepo.findAllByProblemIdAndUserId(
            problemId,
            principal.getName());
        boolean hasPermission = false;
        for (UserContestProblemRole e : L) {
            if (e.getRoleId().equals(UserContestProblemRole.ROLE_EDITOR) ||
                e.getRoleId().equals(UserContestProblemRole.ROLE_OWNER)) {
                hasPermission = true;
                break;
            }
        }
        if (!hasPermission) {
            return ResponseEntity.status(403).body("No permission");
            // return ResponseEntity.status(HttpStatus.OK).body("No permission");
        }
        ProblemEntity problemResponse = problemTestCaseService.updateContestProblem(
            problemId,
            principal.getName(),
            json,
            files);
        return ResponseEntity.status(HttpStatus.OK).body(problemResponse);
    }

    @PostMapping("/check-compile")
    public ResponseEntity<?> checkCompile(@RequestBody ModelCheckCompile modelCheckCompile, Principal principal)
        throws Exception {
        ModelCheckCompileResponse resp = problemTestCaseService.checkCompile(modelCheckCompile, principal.getName());
        return ResponseEntity.status(200).body(resp);
    }

    @PostMapping("/save-test-case/{problemId}")
    public ResponseEntity<?> saveTestCase(
        @PathVariable("problemId") String problemId,
        @RequestBody ModelSaveTestcase modelSaveTestcase
    ) {
        TestCaseEntity testCaseEntity = problemTestCaseService.saveTestCase(problemId, modelSaveTestcase);
        return ResponseEntity.status(200).body(testCaseEntity);
    }

    @GetMapping("/get-problem-submission/{id}")
    public ResponseEntity<?> getProblemSubmissionById(@PathVariable("id") UUID id, Principal principal)
        throws MiniLeetCodeException {
        log.info("getProblemSubmissionById id {}", id);
        ModelProblemSubmissionDetailResponse modelProblemSubmissionDetailResponse = problemTestCaseService
            .findProblemSubmissionById(id, principal.getName());
        return ResponseEntity.status(200).body(modelProblemSubmissionDetailResponse);
    }

    @PostMapping("/create-contest")
    public ResponseEntity<?> createContest(@RequestBody @Valid ModelCreateContest modelCreateContest, Principal principal)
        throws Exception {
        log.info("createContest {}", modelCreateContest);
        ContestEntity contest = problemTestCaseService.createContest(modelCreateContest, principal.getName());
        return ResponseEntity.status(200).body(contest);
    }

    @PostMapping("/edit-contest/{contestId}")
    public ResponseEntity<?> editContest(
        @RequestBody ModelUpdateContest modelUpdateContest, Principal principal,
        @PathVariable("contestId") String contestId
    ) throws Exception {
        log.info("edit contest modelUpdateContest {}", modelUpdateContest);

        problemTestCaseService.updateContest(modelUpdateContest, principal.getName(), contestId);

        return ResponseEntity.status(200).body(null);
    }

    @PostMapping("/save-problem-to-contest")
    public ResponseEntity<?> saveProblemToContest(
        @RequestBody ModelProblemInfoInContest modelProblemInfoInContest,
        Principal principal
    ) throws Exception {

        problemTestCaseService.saveProblemInfoInContest(modelProblemInfoInContest, principal.getName());

        return ResponseEntity.status(200).body("ok");
    }

    @DeleteMapping("/remove-problem-from-contest")
    public ResponseEntity<?> removeProblemFromContest(
        @RequestParam String contestId,
        @RequestParam String problemId,
        Principal principal
    ) {

        problemTestCaseService.removeProblemFromContest(contestId, problemId, principal.getName());

        return ResponseEntity.status(200).body("ok");
    }

    @GetMapping("/get-list-roles-contest")
    public ResponseEntity<?> getListRolesContest() {
        List<String> L = UserRegistrationContestEntity.getListRoles();
        return ResponseEntity.ok().body(L);
    }

    @GetMapping("/get-contest-paging")
    public ResponseEntity<?> getContestPaging(Pageable pageable, @Param("sortBy") String sortBy) {
        log.info("getContestPaging sortBy {} pageable {}", sortBy, pageable);
        if (sortBy != null) {
            pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(sortBy));
        } else {
            pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(),
                                      Sort.by("createdAt").ascending());
        }
        ModelGetContestPageResponse modelGetContestPageResponse = problemTestCaseService.getContestPaging(pageable);
        return ResponseEntity.status(200).body(modelGetContestPageResponse);
    }

    @GetMapping("/get-contest-detail/{contestId}")
    public ResponseEntity<?> getContestDetail(@PathVariable("contestId") String contestId, Principal principal) {
        log.info("getContestDetail constestid {}", contestId);
        ModelGetContestDetailResponse response = problemTestCaseService.getContestDetailByContestIdAndTeacher(
            contestId,
            principal.getName());
        return ResponseEntity.status(200).body(response);
    }

    @PostMapping("/update-contest-submission-source-code")
    public ResponseEntity<?> updateContestSubmisionSourceCode(
        Principal principal,
        @RequestBody ModelUpdateContestSubmission input
    ) {
        log.info("updateContestSubmisionSourceCode, new source code = " + input.getModifiedSourceCodeSubmitted());
        ContestSubmissionEntity sub = problemTestCaseService.updateContestSubmissionSourceCode(input);
        return ResponseEntity.ok().body(sub);
    }

    @GetMapping("/get-code-similarity-summary-of-participants/{contestId}")
    public ResponseEntity<?> getCodeSimilaritySummaryOfParticipants(
        Principal principal,
        @PathVariable String contestId
    ) {
        List<ModelReponseCodeSimilaritySummaryParticipant> res = problemTestCaseService.getListModelReponseCodeSimilaritySummaryParticipant(
            contestId);
        return ResponseEntity.ok().body(res);
    }

    @PostMapping("/get-code-similarity")
    public ResponseEntity<?> getCodeSimilarity(Principal principal, @RequestBody ModelGetCodeSimilarityParams input) {
        List<CodePlagiarism> codePlagiarism = problemTestCaseService.findAllBy(input);
        return ResponseEntity.ok().body(codePlagiarism);
    }

    @PostMapping("/get-code-similarity-cluster")
    public ResponseEntity<?> getCodeSimilarityCluster(
        Principal principal,
        @RequestBody ModelGetCodeSimilarityParams input
    ) {
        List<ModelSimilarityClusterOutput> res = problemTestCaseService.computeSimilarityClusters(input);
        return ResponseEntity.ok().body(res);
    }

    @PostMapping("/check-code-similarity/{contestId}")
    public ResponseEntity<?> checkCodeSimilarity(
        Principal principal, @RequestBody ModelCheckSimilarityInput I,
        @PathVariable String contestId
    ) {
        log.info("checkCodeSimilarity, contestId = " + contestId);
        ModelCodeSimilarityOutput res = problemTestCaseService.checkSimilarity(contestId, I);
        return ResponseEntity.ok().body(res);
    }

    @GetMapping("/get-list-contest-problem-student/{contestId}")
    public ResponseEntity<?> getListContestProblemViewedByStudent(@PathVariable("contestId") String contestId) {
        ContestEntity contestEntity = contestRepo.findContestByContestId(contestId);
        List<ProblemEntity> listProblem = contestEntity.getProblems();
        return ResponseEntity.status(200).body(listProblem);
    }

    @GetMapping("/get-list-contest-problem-student-V2/{contestId}")
    public ResponseEntity<?> getListContestProblemViewedByStudentV2(
        @PathVariable("contestId") String contestId,
        Principal principal
    ) {
        String userId = principal.getName();
        ContestEntity contestEntity = contestService.findContest(contestId);

        List<ProblemEntity> listProblem = contestEntity.getProblems();
        List<String> listAcceptedProblem = contestSubmissionRepo.findAcceptedProblemsOfUser(userId, contestId);
        List<ModelProblemMaxSubmissionPoint> listTriedProblem = contestSubmissionRepo.findSubmittedProblemsOfUser(
            userId,
            contestId);

        Map<String, Long> mapProblemToMaxSubmissionPoint = new HashMap<>();
        for (ModelProblemMaxSubmissionPoint problem : listTriedProblem) {
            mapProblemToMaxSubmissionPoint.put(problem.getProblemId(), problem.getMaxPoint());
        }

        List<ModelStudentOverviewProblem> responses = new ArrayList<>();

        if (contestEntity.getStatusId().equals(ContestEntity.CONTEST_STATUS_RUNNING)) {
            for (ProblemEntity problem : listProblem) {
                String problemId = problem.getProblemId();

                ContestProblem contestProblem = contestProblemRepo.findByContestIdAndProblemId(contestId, problemId);

                ModelStudentOverviewProblem response = new ModelStudentOverviewProblem();
                response.setProblemId(problemId);
                response.setProblemName(contestProblem.getProblemRename());
                response.setProblemCode(contestProblem.getProblemRecode());
                response.setLevelId(problem.getLevelId());

                List<String> tags = problem.getTags().stream().map(TagEntity::getName).collect(Collectors.toList());
                response.setTags(tags);

                if (mapProblemToMaxSubmissionPoint.containsKey(problemId)) {
                    response.setSubmitted(true);
                    response.setMaxSubmittedPoint(mapProblemToMaxSubmissionPoint.get(problemId));
                }

                if (listAcceptedProblem.contains(problemId)) {
                    response.setAccepted(true);
                }

                responses.add(response);
            }
        }
        return ResponseEntity.status(200).body(responses);
    }

    @GetMapping("/get-all-tags")
    public ResponseEntity<?> getAllTags() {

        List<TagEntity> listTag = problemTestCaseService.getAllTags();
        return ResponseEntity.status(200).body(listTag);
    }

    @PostMapping("/add-tag")
    public ResponseEntity<?> addNewTag(@RequestBody ModelTag tagInput) {

        TagEntity tag = problemTestCaseService.addNewTag(tagInput);

        return ResponseEntity.status(200).body(tag);
    }

    //@Secured("ROLE_STUDENT")
    @PostMapping("/student-register-contest/{contestId}")
    public ResponseEntity<?> studentRegisterContest(@PathVariable("contestId") String contestId, Principal principal)
        throws MiniLeetCodeException {
        log.info("studentRegisterContest {}", contestId);
        ModelStudentRegisterContestResponse resp = problemTestCaseService.studentRegisterContest(
            contestId,
            principal.getName());
        return ResponseEntity.status(200).body(resp);
    }

    //@Secured("ROLE_TEACHER")
    @GetMapping("/get-all-contests-paging-by-admin")
    public ResponseEntity<?> getAllContestPagingByAdmin(
        Principal principal, Pageable pageable,
        @Param("sortBy") String sortBy
    ) {
        log.info("getContestPagingByUserCreate");
        if (sortBy != null) {
            pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(sortBy));
        } else {
            pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(),
                                      Sort.by("createdAt").ascending());
        }
        ModelGetContestPageResponse resp = problemTestCaseService.getAllContestsPagingByAdmin(
            principal.getName(),
            pageable);
        return ResponseEntity.status(200).body(resp);
    }

    //@Secured("ROLE_TEACHER")
    @GetMapping("/get-contest-by-user-role")
    public ResponseEntity<?> getContestPagingByUserRole(Principal principal) {
        String userId = principal.getName();
        List<ModelGetContestResponse> resp = problemTestCaseService
            .getContestByUserRole(principal.getName());
        return ResponseEntity.status(200).body(resp);
    }

    //@Secured("ROLE_TEACHER")
    @GetMapping("/get-user-register-successful-contest/{contestId}")
    public ResponseEntity<?> getUserRegisterSuccessfulContest(
        @PathVariable("contestId") String contestId,
        Pageable pageable
    ) {
        log.info("get User Register Successful Contest ");
        ListModelUserRegisteredContestInfo resp = problemTestCaseService
            .getListUserRegisterContestSuccessfulPaging(pageable, contestId);
        return ResponseEntity.status(200).body(resp);
    }

    //@Secured("ROLE_TEACHER")
    @GetMapping("/get-members-of-contest/{contestId}")
    public ResponseEntity<?> getMembersOfContest(Principal principal, @PathVariable String contestId) {
        List<ModelMemberOfContestResponse> res = problemTestCaseService.getListMemberOfContest(contestId);
        return ResponseEntity.ok().body(res);
    }

    //@Secured("ROLE_TEACHER")
    @PostMapping("/remove-member-from-contest")
    public ResponseEntity<?> removeMemberFromContest(
        Principal principal,
        @RequestBody ModelRemoveMemberFromContestInput input
    ) {
        boolean res = problemTestCaseService.removeMemberFromContest(input.getId());
        return ResponseEntity.ok().body(res);
    }

    //@Secured("ROLE_TEACHER")
    @PostMapping("/update-permission-of-member-to-contest")
    public ResponseEntity<?> updatePermissionOfMemberToContest(
        Principal principal,
        @RequestBody ModelUpdatePermissionMemberToContestInput input
    ) {
        boolean res = problemTestCaseService.updatePermissionMemberToContest(principal.getName(), input);
        return ResponseEntity.ok().body(res);
    }

    //@Secured("ROLE_TEACHER")
    @GetMapping("/get-user-register-pending-contest/{contestId}")
    public ResponseEntity<?> getUserRegisterPendingContest(
        @PathVariable("contestId") String contestId,
        Pageable pageable, @Param("size") String size, @Param("page") String page
    ) {
        log.info("get User Register Pending Contest pageable {} size {} page {} contest id {}", pageable, size, page,
                 contestId);
        ListModelUserRegisteredContestInfo resp = problemTestCaseService
            .getListUserRegisterContestPendingPaging(pageable, contestId);
        return ResponseEntity.status(200).body(resp);
    }

    //@Secured("ROLE_TEACHER")
    @GetMapping("/get-pending-registered-users-of-contest/{contestId}")
    public ResponseEntity<?> getPendingRegisteredUserOfContest(Principal principal, @PathVariable String contestId) {
        List<ModelMemberOfContestResponse> res = problemTestCaseService.getPendingRegisteredUsersOfContest(contestId);
        return ResponseEntity.ok().body(res);
    }

    //@Secured("ROLE_TEACHER")
    @GetMapping("/search-user/{contestId}")
    public ResponseEntity<?> searchUser(
        @PathVariable("contestId") String contestId, Pageable pageable,
        @Param("keyword") String keyword
    ) {
        if (keyword == null) {
            keyword = "";
        }
        ListModelUserRegisteredContestInfo resp = problemTestCaseService.searchUser(pageable, contestId, keyword);
        return ResponseEntity.status(200).body(resp);
    }

    //@Secured("ROLE_TEACHER")
    @GetMapping("/search-user-based-keyword")
    public ResponseEntity<?> searchUserBaseKeyword(
        Pageable pageable,
        @Param("keyword") String keyword
    ) {
        if (keyword == null) {
            keyword = "";
        }
        log.info("searchUserBaseKeywordm keyword = " + keyword);
        ListPersonModel resp = problemTestCaseService.searchUserBaseKeyword(pageable, keyword);
        return ResponseEntity.status(200).body(resp);
    }

    //@Secured("ROLE_TEACHER")
    @PostMapping("/techer-manager-student-register-contest")
    public ResponseEntity<?> teacherManagerStudentRegisterContest(
        Principal principal,
        @RequestBody ModelTeacherManageStudentRegisterContest request
    ) throws MiniLeetCodeException {
        log.info("teacherManagerStudentRegisterContest");
        problemTestCaseService.teacherManageStudentRegisterContest(principal.getName(), request);
        return ResponseEntity.status(200).body(null);
    }

    //@Secured("ROLE_TEACHER")
    @PostMapping("/approve-registered-user-2-contest")
    public ResponseEntity<?> approveRegisteredUser2Contest(
        Principal principal,
        @RequestBody ModelApproveRegisterUser2ContestInput input
    ) {
        try {
            boolean ok = problemTestCaseService.approveRegisteredUser2Contest(principal.getName(), input);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.ok().body("");
        }
        return ResponseEntity.ok().body(true);
    }

    @GetMapping("/get-contest-registered-student")
    public ResponseEntity<?> getContestRegisteredStudent(Principal principal) {
        ModelGetContestPageResponse res = problemTestCaseService.getRegisteredContestsByUser(principal.getName());
        return ResponseEntity.ok().body(res);
    }

    @GetMapping("/get-contest-paging-registered")
    public ResponseEntity<?> getContestRegisteredByStudentPaging(
        Pageable pageable, @Param("sortBy") String sortBy,
        Principal principal
    ) {
        //log.info("getContestRegisteredByStudentPaging sortBy {} pageable {}", sortBy, pageable);
        if (sortBy != null) {
            pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(sortBy));
        } else {
            pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(),
                                      Sort.by("createdAt").ascending());
        }
        ModelGetContestPageResponse modelGetContestPageResponse = problemTestCaseService
            .getRegisteredContestByUser(pageable, principal.getName());
        return ResponseEntity.status(200).body(modelGetContestPageResponse);
    }

    @GetMapping("/get-contest-paging-not-registered")
    public ResponseEntity<?> getContestNotRegisteredByStudentPaging(
        Pageable pageable, @Param("sortBy") String sortBy,
        Principal principal
    ) {
        log.info("getContestRegisteredByStudentPaging sortBy {} pageable {}", sortBy, pageable);
        if (sortBy != null) {
            pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(sortBy));
        } else {
            pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(),
                                      Sort.by("createdAt").ascending());
        }
        ModelGetContestPageResponse modelGetContestPageResponse = problemTestCaseService
            .getNotRegisteredContestByUser(pageable, principal.getName());
        return ResponseEntity.status(200).body(modelGetContestPageResponse);
    }

    @GetMapping("/get-contest-problem-submission-detail-by-testcase-of-a-submission/{submissionId}")
    public ResponseEntity<?> getContestProblemSubmissionDetailByTestCaseOfASubmission(
        Principal principal,
        @PathVariable UUID submissionId
    ) {
        List<ModelProblemSubmissionDetailByTestCaseResponse> retLst = problemTestCaseService
            .getContestProblemSubmissionDetailByTestCaseOfASubmission(submissionId);
        return ResponseEntity.ok().body(retLst);
    }

    @GetMapping(
        "/get-contest-problem-submission-detail-by-testcase-of-a-submission-viewed-by-participant/{submissionId}")
    public ResponseEntity<?> getContestProblemSubmissionDetailByTestCaseOfASubmissionViewedByParticipant(
        Principal principal, @PathVariable UUID submissionId
    ) {
        List<ModelProblemSubmissionDetailByTestCaseResponse> retLst = problemTestCaseService
            .getContestProblemSubmissionDetailByTestCaseOfASubmissionViewedByParticipant(submissionId);
        return ResponseEntity.ok().body(retLst);
    }

    @GetMapping("/get-contest-problem-submission-detail-by-testcase")
    public ResponseEntity<?> getContestProblemSubmissionDetailByTestCase(
        Principal principal,
        @RequestParam int page, int size, Pageable pageable
    ) {
        log.info("getContestProblemSubmissionDetailByTestCase, page = " + page + " size = " + size);
        Pageable sortedByCreatedStampDsc = PageRequest.of(page, size, Sort.by("createdStamp").descending());

        Page<ModelProblemSubmissionDetailByTestCaseResponse> lst = problemTestCaseService
            .getContestProblemSubmissionDetailByTestCase(sortedByCreatedStampDsc);

        return ResponseEntity.ok().body(lst);
    }

    @PostMapping("/upload-update-test-case/{testCaseId}")
    public ResponseEntity<?> uploadUpdateTestCase(
        Principal principal,
        @PathVariable String testCaseId,
        @RequestParam("inputJson") String inputJson,
        @RequestParam("file") MultipartFile file
    ) {

        Gson gson = new Gson();
        ModelProgrammingContestUploadTestCase modelUploadTestCase = gson.fromJson(
            inputJson,
            ModelProgrammingContestUploadTestCase.class);
        String problemId = modelUploadTestCase.getProblemId();
        UUID testCaseUUID = UUID.fromString(testCaseId);
        log.info("uploadUpdateTestCase, problemId = " + problemId + " tesCaseId = " + testCaseId + " testCaseUUID = "
                 + testCaseUUID);
        StringBuilder testCase = new StringBuilder();
        ModelUploadTestCaseOutput res = new ModelUploadTestCaseOutput();
        if (file != null) {
            try {
                InputStream inputStream = file.getInputStream();
                Scanner in = new Scanner(inputStream);
                while (in.hasNext()) {
                    String line = in.nextLine();
                    testCase.append(line).append("\n");
                    // System.out.println("contestSubmitProblemViaUploadFile: read line: " + line);
                }
                in.close();
                log.info("uploadUpdateTestCase, testCase not null, testCase = " + testCase.length());
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            log.info("uploadUpdateTestCase, multipart file is null");
        }

        res = problemTestCaseService.uploadUpdateTestCase(testCaseUUID, testCase.toString(), modelUploadTestCase,
                                                          principal.getName());
        return ResponseEntity.ok().body(res);
    }

    @PostMapping("/update-test-case-without-file/{testCaseId}")
    public ResponseEntity<?> uploadUpdateTestCaseWithoutFile(
        Principal principal,
        @PathVariable String testCaseId,
        @RequestParam("inputJson") String inputJson
    ) {

        Gson gson = new Gson();
        ModelProgrammingContestUploadTestCase modelUploadTestCase = gson.fromJson(
            inputJson,
            ModelProgrammingContestUploadTestCase.class);
        String problemId = modelUploadTestCase.getProblemId();
        UUID testCaseUUID = UUID.fromString(testCaseId);
        log.info("uploadUpdateTestCaseWithoutFile, problemId = " + problemId + " tesCaseId = " + testCaseId
                 + " testCaseUUID = " + testCaseUUID);
        ModelUploadTestCaseOutput res = new ModelUploadTestCaseOutput();
        res = problemTestCaseService.uploadUpdateTestCase(testCaseUUID, null, modelUploadTestCase, principal.getName());
        return ResponseEntity.ok().body(res);

    }

    @PostMapping("/upload-test-case")
    public ResponseEntity<?> uploadTestCase(
        Principal principal,
        @RequestParam("inputJson") String inputJson,
        @RequestParam("file") MultipartFile file
    ) {
        Gson gson = new Gson();
        ModelProgrammingContestUploadTestCase modelUploadTestCase = gson.fromJson(
            inputJson,
            ModelProgrammingContestUploadTestCase.class);
        String problemId = modelUploadTestCase.getProblemId();
        log.info("uploadTestCase, problemId = " + problemId);
        StringBuilder testCase = new StringBuilder();
        ModelUploadTestCaseOutput res = new ModelUploadTestCaseOutput();
        try {
            InputStream inputStream = file.getInputStream();
            Scanner in = new Scanner(inputStream);
            while (in.hasNext()) {
                String line = in.nextLine();
                testCase.append(line).append("\n");
                // System.out.println("contestSubmitProblemViaUploadFile: read line: " + line);
            }
            in.close();
            res = problemTestCaseService.addTestCase(testCase.toString(), modelUploadTestCase, principal.getName());
            return ResponseEntity.ok().body(res);
        } catch (Exception e) {
            e.printStackTrace();
        }
        res.setStatus("FAILURE");
        res.setMessage("Exception!!");
        return ResponseEntity.ok().body(res);
    }

    @PostMapping("/contest-submit-problem-via-upload-file-v3")
    public ResponseEntity<?> contestSubmitProblemViaUploadFileV3(
        Principal principal,
        @RequestParam("inputJson") String inputJson,
        @RequestParam("file") MultipartFile file
    ) {
        Gson gson = new Gson();
        ModelContestSubmitProgramViaUploadFile model = gson.fromJson(
            inputJson,
            ModelContestSubmitProgramViaUploadFile.class);
        ContestEntity contestEntity = contestRepo.findContestByContestId(model.getContestId());
        if (contestEntity.getJudgeMode() != null &&
            contestEntity.getJudgeMode().equals(ContestEntity.ASYNCHRONOUS_JUDGE_MODE_QUEUE)) {
            //log.info("contestSubmitProblemViaUploadFileV3, mode using queue");
            return contestSubmitProblemViaUploadFileV2(principal, inputJson, file);
        }
        //log.info("contestSubmitProblemViaUploadFileV3, mode synchronous, NOT using queue");
        return contestSubmitProblemViaUploadFile(principal, inputJson, file);
    }

    @PostMapping("/contest-submit-problem-via-upload-file")
    public ResponseEntity<?> contestSubmitProblemViaUploadFile(
        Principal principal,
        @RequestParam("inputJson") String inputJson,
        @RequestParam("file") MultipartFile file
    ) {
        //log.info("contestSubmitProblemViaUploadFile, inputJson = " + inputJson);
        Gson gson = new Gson();
        ModelContestSubmitProgramViaUploadFile model = gson.fromJson(
            inputJson,
            ModelContestSubmitProgramViaUploadFile.class);
        ContestEntity contestEntity = contestRepo.findContestByContestId(model.getContestId());
        ContestProblem cp = contestProblemRepo.findByContestIdAndProblemId(model.getContestId(), model.getProblemId());
        Date currentDate = new Date();
        int timeTest = ((int) (currentDate.getTime() - contestEntity.getStartedAt().getTime())) /
                       (60 * 1000); // minutes
        // System.out.println(currentDate);
        // System.out.println(testStartDate);
        // System.out.println(timeTest);
        // System.out.println(test.getDuration());
        //log.info("contestSubmitProblemViaUploadFile, currentDate = " + currentDate + ", contest started at"
        //        + contestEntity.getStartedAt()
        //        + " timeTest = " + timeTest + " contestSolvingTime = " + contestEntity.getContestSolvingTime());

        // if (timeTest > contestEntity.getContestSolvingTime()) {
        if (!contestEntity.getStatusId().equals(ContestEntity.CONTEST_STATUS_RUNNING)) {
            //log.info("contestSubmitProblemViaUploadFile, TIME OUT!!!!! currentDate = " + currentDate
            //        + ", contest started at" + contestEntity.getStartedAt()
            //        + " timeTest = " + timeTest + " contestSolvingTime = " +
            //       contestEntity.getContestSolvingTime());

            // return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(null);

            ModelContestSubmissionResponse resp = ModelContestSubmissionResponse.builder()
                                                                                .status("TIME_OUT")
                                                                                .testCasePass("0")
                                                                                .runtime(new Long(0))
                                                                                .memoryUsage(new Float(0))
                                                                                .problemName("")
                                                                                .contestSubmissionID(null)
                                                                                .submittedAt(null)
                                                                                .score(0L)
                                                                                .numberTestCasePassed(0)
                                                                                .totalNumberTestCase(0)
                                                                                .build();
            return ResponseEntity.ok().body(resp);
        }

        List<UserRegistrationContestEntity> userRegistrations = userRegistrationContestRepo
            .findUserRegistrationContestEntityByContestIdAndUserIdAndStatusAndRoleId(model.getContestId(),
                                                                                     principal.getName(),
                                                                                     UserRegistrationContestEntity.STATUS_SUCCESSFUL,
                                                                                     UserRegistrationContestEntity.ROLE_PARTICIPANT);
        if (userRegistrations == null || userRegistrations.size() == 0) {
            ModelContestSubmissionResponse resp = ModelContestSubmissionResponse.builder()
                                                                                .status(
                                                                                    "PARTICIPANT_NOT_APPROVED_OR_REGISTERED")
                                                                                .message(
                                                                                    "Participant is not approved or not registered")
                                                                                .testCasePass("0")
                                                                                .runtime(new Long(0))
                                                                                .memoryUsage(new Float(0))
                                                                                .problemName("")
                                                                                .contestSubmissionID(null)
                                                                                .submittedAt(null)
                                                                                .score(0L)
                                                                                .numberTestCasePassed(0)
                                                                                .totalNumberTestCase(0)
                                                                                .build();
            //log.info("contestSubmitProblemViaUploadFile: Participant not approved or registered");
            return ResponseEntity.ok().body(resp);

        }

        for (UserRegistrationContestEntity u : userRegistrations) {
            if (u.getPermissionId() != null
                && u.getPermissionId().equals(UserRegistrationContestEntity.PERMISSION_FORBIDDEN_SUBMIT)) {
                ModelContestSubmissionResponse resp = ModelContestSubmissionResponse.builder()
                                                                                    .status(
                                                                                        "PARTICIPANT_HAS_NOT_PERMISSION_TO_SUBMIT")
                                                                                    .message(
                                                                                        "Participant has not permission to submit")
                                                                                    .testCasePass("0")
                                                                                    .runtime(new Long(0))
                                                                                    .memoryUsage(new Float(0))
                                                                                    .problemName("")
                                                                                    .contestSubmissionID(null)
                                                                                    .submittedAt(null)
                                                                                    .score(0L)
                                                                                    .numberTestCasePassed(0)
                                                                                    .totalNumberTestCase(0)
                                                                                    .build();
                //log.info("contestSubmitProblemViaUploadFile: Participant has not permission to submit");
                return ResponseEntity.ok().body(resp);

            }
        }

        List<ContestSubmissionEntity> submissions = contestSubmissionRepo
            .findAllByContestIdAndUserIdAndProblemId(model.getContestId(), principal.getName(),
                                                     model.getProblemId());
        if (submissions.size() >= contestEntity.getMaxNumberSubmissions()) {
            ModelContestSubmissionResponse resp = ModelContestSubmissionResponse.builder()
                                                                                .status("MAX_NUMBER_SUBMISSIONS_REACHED")
                                                                                .message(
                                                                                    "Maximum Number of Submissions " +
                                                                                    contestEntity.getMaxNumberSubmissions()
                                                                                    +
                                                                                    " Reached! Cannot submit more")
                                                                                .testCasePass("0")
                                                                                .runtime(new Long(0))
                                                                                .memoryUsage(new Float(0))
                                                                                .problemName("")
                                                                                .contestSubmissionID(null)
                                                                                .submittedAt(null)
                                                                                .score(0L)
                                                                                .numberTestCasePassed(0)
                                                                                .totalNumberTestCase(0)
                                                                                .build();
            //log.info("contestSubmitProblemViaUploadFile: Maximum Number of Submissions "
            //        + contestEntity.getMaxNumberSubmissions() + " Reached! Cannot submit more");
            return ResponseEntity.ok().body(resp);
        }

        try {
            StringBuilder source = new StringBuilder();
            InputStream inputStream = file.getInputStream();
            Scanner in = new Scanner(inputStream);
            while (in.hasNext()) {
                String line = in.nextLine();
                source.append(line).append("\n");
                // System.out.println("contestSubmitProblemViaUploadFile: read line: " + line);
            }
            in.close();

            if (source.length() > contestEntity.getMaxSourceCodeLength()) {
                ModelContestSubmissionResponse resp = ModelContestSubmissionResponse.builder()
                                                                                    .status(
                                                                                        "MAX_SOURCE_CODE_LENGTH_VIOLATIONS")
                                                                                    .message(
                                                                                        "Max source code length violations " +
                                                                                        source.length() +
                                                                                        " > "
                                                                                        +
                                                                                        contestEntity.getMaxSourceCodeLength() +
                                                                                        " ")
                                                                                    .testCasePass("0")
                                                                                    .runtime(new Long(0))
                                                                                    .memoryUsage(new Float(0))
                                                                                    .problemName("")
                                                                                    .contestSubmissionID(null)
                                                                                    .submittedAt(null)
                                                                                    .score(0L)
                                                                                    .numberTestCasePassed(0)
                                                                                    .totalNumberTestCase(0)
                                                                                    .build();
                //log.info("contestSubmitProblemViaUploadFile: Max Source code Length violations " + source.length()
                //        + " > " + contestEntity.getMaxSourceCodeLength() + " --> Cannot submit more");
                return ResponseEntity.ok().body(resp);
            }
            ModelContestSubmission request = new ModelContestSubmission(model.getContestId(), model.getProblemId(),
                                                                        source.toString(), model.getLanguage());
            ModelContestSubmissionResponse resp = null;
            if (contestEntity.getSubmissionActionType()
                             .equals(ContestEntity.CONTEST_SUBMISSION_ACTION_TYPE_STORE_AND_EXECUTE)) {
                if (cp != null &&
                    cp.getSubmissionMode() != null &&
                    cp.getSubmissionMode().equals(ContestProblem.SUBMISSION_MODE_SOLUTION_OUTPUT)) {
                    //log.info("contestSubmitProblemViaUploadFile, mode submit output");
                    resp = problemTestCaseService.submitContestProblemStoreOnlyNotExecute(
                        request,
                        principal.getName(),
                        principal.getName());
                } else {
                    resp = problemTestCaseService.submitContestProblemTestCaseByTestCase(
                        request,
                        principal.getName());

                }
            } else {
                resp = problemTestCaseService.submitContestProblemStoreOnlyNotExecute(
                    request,
                    principal.getName(),
                    principal.getName());
            }
            //log.info("resp {}", resp);
            return ResponseEntity.status(200).body(resp);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.ok().body("OK");
    }

    @PostMapping("/contest-submit-problem-via-upload-file-v2")
    public ResponseEntity<?> contestSubmitProblemViaUploadFileV2(
        Principal principal,
        @RequestParam("inputJson") String inputJson,
        @RequestParam("file") MultipartFile file
    ) {

        String userId = principal.getName();
        Gson gson = new Gson();
        ModelContestSubmitProgramViaUploadFile model = gson.fromJson(
            inputJson,
            ModelContestSubmitProgramViaUploadFile.class);
        ContestEntity contestEntity = contestRepo.findContestByContestId(model.getContestId());
        ContestProblem cp = contestProblemRepo.findByContestIdAndProblemId(model.getContestId(), model.getProblemId());

        if (!contestEntity.getStatusId().equals(ContestEntity.CONTEST_STATUS_RUNNING)) {
            ModelContestSubmissionResponse resp = buildSubmissionResponseTimeOut();
            return ResponseEntity.ok().body(resp);
        }

        List<UserRegistrationContestEntity> userRegistrations = userRegistrationContestRepo
            .findUserRegistrationContestEntityByContestIdAndUserIdAndStatusAndRoleId(model.getContestId(),
                                                                                     userId,
                                                                                     UserRegistrationContestEntity.STATUS_SUCCESSFUL,
                                                                                     UserRegistrationContestEntity.ROLE_PARTICIPANT);
        if (userRegistrations == null || userRegistrations.size() == 0) {
            ModelContestSubmissionResponse resp = buildSubmissionResponseNotRegistered();
            return ResponseEntity.ok().body(resp);

        }

        for (UserRegistrationContestEntity u : userRegistrations) {
            if (u.getPermissionId() != null
                && u.getPermissionId().equals(UserRegistrationContestEntity.PERMISSION_FORBIDDEN_SUBMIT)) {
                ModelContestSubmissionResponse resp = buildSubmissionResponseNoPermission();
                return ResponseEntity.ok().body(resp);
            }
        }

        int numOfSubmissions = contestSubmissionRepo
            .countAllByContestIdAndUserIdAndProblemId(model.getContestId(), userId, model.getProblemId());
        if (numOfSubmissions >= contestEntity.getMaxNumberSubmissions()) {
            ModelContestSubmissionResponse resp = buildSubmissionResponseReachMaxSubmission(contestEntity.getMaxNumberSubmissions());
            return ResponseEntity.ok().body(resp);
        }

        long submissionInterval = contestEntity.getMinTimeBetweenTwoSubmissions();
        if (submissionInterval > 0) {
            Date now = new Date();
            Long lastSubmitTime = cacheService.findUserLastProblemSubmissionTimeInCache(model.getProblemId(), userId);
            if (lastSubmitTime != null) {
                long diffBetweenNowAndLastSubmit = now.getTime() - lastSubmitTime;
                if (diffBetweenNowAndLastSubmit < submissionInterval * 1000) {
                    ModelContestSubmissionResponse resp = buildSubmissionResponseNotEnoughTimeBetweenSubmissions(
                        submissionInterval);
                    return ResponseEntity.ok().body(resp);
                }
            }
            cacheService.addUserLastProblemSubmissionTimeToCache(model.getProblemId(), userId);
        }

        try {
            StringBuilder source = new StringBuilder();
            InputStream inputStream = file.getInputStream();
            Scanner in = new Scanner(inputStream);
            while (in.hasNext()) {
                String line = in.nextLine();
                source.append(line).append("\n");
            }
            in.close();

            if (source.length() > contestEntity.getMaxSourceCodeLength()) {
                ModelContestSubmissionResponse resp = buildSubmissionResponseReachMaxSourceLength(
                    source.length(),
                    contestEntity.getMaxSourceCodeLength());
                return ResponseEntity.ok().body(resp);
            }
            ModelContestSubmission request = new ModelContestSubmission(model.getContestId(), model.getProblemId(),
                                                                        source.toString(), model.getLanguage());
            ModelContestSubmissionResponse resp = null;
            if (contestEntity.getSubmissionActionType()
                             .equals(ContestEntity.CONTEST_SUBMISSION_ACTION_TYPE_STORE_AND_EXECUTE)) {
                if (cp != null &&
                    cp.getSubmissionMode() != null &&
                    cp.getSubmissionMode().equals(ContestProblem.SUBMISSION_MODE_SOLUTION_OUTPUT)) {
                    resp = problemTestCaseService.submitContestProblemStoreOnlyNotExecute(request, userId, userId);
                } else {
                    resp = problemTestCaseService.submitContestProblemTestCaseByTestCaseWithFile(
                        request,
                        userId,
                        userId);
                }
            } else {
                resp = problemTestCaseService.submitContestProblemStoreOnlyNotExecute(request, userId, userId);
            }

            return ResponseEntity.status(200).body(resp);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.ok().body("OK");
    }

    private ModelContestSubmissionResponse buildSubmissionResponseTimeOut() {
        return ModelContestSubmissionResponse.builder()
                                             .status("TIME_OUT")
                                             .testCasePass("0")
                                             .runtime(0L)
                                             .memoryUsage((float) 0)
                                             .problemName("")
                                             .contestSubmissionID(null)
                                             .submittedAt(null)
                                             .score(0L)
                                             .numberTestCasePassed(0)
                                             .totalNumberTestCase(0)
                                             .build();
    }

    private ModelContestSubmissionResponse buildSubmissionResponseProblemNotFound() {
        return ModelContestSubmissionResponse.builder()
                                             .status("PROBLEM_NO_FOUND")
                                             .testCasePass("0")
                                             .runtime(0L)
                                             .memoryUsage((float) 0)
                                             .problemName("")
                                             .contestSubmissionID(null)
                                             .submittedAt(null)
                                             .score(0L)
                                             .numberTestCasePassed(0)
                                             .totalNumberTestCase(0)
                                             .build();
    }

    private ModelContestSubmissionResponse buildSubmissionResponseInvalidFilename(String fn) {
        return ModelContestSubmissionResponse.builder()
                                             .status("Invalid filename " + fn)
                                             .testCasePass("0")
                                             .runtime(0L)
                                             .memoryUsage((float) 0)
                                             .problemName("")
                                             .contestSubmissionID(null)
                                             .submittedAt(null)
                                             .score(0L)
                                             .numberTestCasePassed(0)
                                             .totalNumberTestCase(0)
                                             .build();
    }

    private ModelContestSubmissionResponse buildSubmissionResponseNotRegistered() {
        return ModelContestSubmissionResponse.builder()
                                             .status("PARTICIPANT_NOT_APPROVED_OR_REGISTERED")
                                             .message("Participant is not approved or not registered")
                                             .testCasePass("0")
                                             .runtime(0L)
                                             .memoryUsage((float) 0)
                                             .problemName("")
                                             .contestSubmissionID(null)
                                             .submittedAt(null)
                                             .score(0L)
                                             .numberTestCasePassed(0)
                                             .totalNumberTestCase(0)
                                             .build();
    }

    private ModelContestSubmissionResponse buildSubmissionResponseNoPermission() {
        return ModelContestSubmissionResponse.builder()
                                             .status("PARTICIPANT_HAS_NOT_PERMISSION_TO_SUBMIT")
                                             .message("Participant has no permission to submit")
                                             .testCasePass("0")
                                             .runtime(0L)
                                             .memoryUsage((float) 0)
                                             .problemName("")
                                             .contestSubmissionID(null)
                                             .submittedAt(null)
                                             .score(0L)
                                             .numberTestCasePassed(0)
                                             .totalNumberTestCase(0)
                                             .build();
    }

    private ModelContestSubmissionResponse buildSubmissionResponseReachMaxSubmission(int maxNumberSubmission) {
        return ModelContestSubmissionResponse.builder()
                                             .status("MAX_NUMBER_SUBMISSIONS_REACHED")
                                             .message("Maximum Number of Submissions " + maxNumberSubmission
                                                      + " Reached! Cannot submit more")
                                             .testCasePass("0")
                                             .runtime(0L)
                                             .memoryUsage((float) 0)
                                             .problemName("")
                                             .contestSubmissionID(null)
                                             .submittedAt(null)
                                             .score(0L)
                                             .numberTestCasePassed(0)
                                             .totalNumberTestCase(0)
                                             .build();
    }

    private ModelContestSubmissionResponse buildSubmissionResponseReachMaxSourceLength(
        int sourceLength,
        int maxLength
    ) {
        return ModelContestSubmissionResponse.builder()
                                             .status("MAX_SOURCE_CODE_LENGTH_VIOLATIONS")
                                             .message("Max source code length violations " + sourceLength + " exceeded "
                                                      + maxLength + " ")
                                             .testCasePass("0")
                                             .runtime(0L)
                                             .memoryUsage((float) 0)
                                             .problemName("")
                                             .contestSubmissionID(null)
                                             .submittedAt(null)
                                             .score(0L)
                                             .numberTestCasePassed(0)
                                             .totalNumberTestCase(0)
                                             .build();
    }

    private ModelContestSubmissionResponse buildSubmissionResponseNotEnoughTimeBetweenSubmissions(long interval) {
        return ModelContestSubmissionResponse.builder()
                                             .status("SUBMISSION_INTERVAL_VIOLATIONS")
                                             .message("Not enough time between 2 submissions (" + interval + "s) ")
                                             .testCasePass("0")
                                             .runtime(0L)
                                             .memoryUsage((float) 0)
                                             .problemName("")
                                             .contestSubmissionID(null)
                                             .submittedAt(null)
                                             .score(0L)
                                             .numberTestCasePassed(0)
                                             .totalNumberTestCase(0)
                                             .build();
    }

    @GetMapping("/test-jmeter")
    public ResponseEntity<?> testJmeter(@RequestParam String s) {
        s = s.concat("Hello");
        return ResponseEntity.ok().body(s);
    }

    @PostMapping("/evaluate-submission/{submissionId}")
    public ResponseEntity<?> evaluateSubmission(Principal principal, @PathVariable UUID submissionId) {
        problemTestCaseService.evaluateSubmission(submissionId);
        return ResponseEntity.ok().body("ok");
    }

    @PostMapping("/evaluate-batch-submission-of-contest/{contestId}")
    public ResponseEntity<?> evaluateBatchSubmissionContest(Principal principal, @PathVariable String contestId) {
        log.info("evaluateBatchSubmissionContest, contestId = " + contestId);
        // ModelEvaluateBatchSubmissionResponse res =
        // problemTestCaseService.evaluateBatchSubmissionContest(contestId);
        ModelEvaluateBatchSubmissionResponse res = problemTestCaseService.reJudgeAllSubmissionsOfContest(contestId);
        return ResponseEntity.ok().body(res);
    }

    @PostMapping("/evaluate-batch-not-evaluated-submission-of-contest/{contestId}")
    public ResponseEntity<?> evaluateBatchNotEvaluatedSubmissionContest(
        Principal principal,
        @PathVariable String contestId
    ) {
        log.info("evaluateBatchNotEvaluatedSubmissionContest, contestId = " + contestId);
        // ModelEvaluateBatchSubmissionResponse res =
        // problemTestCaseService.evaluateBatchSubmissionContest(contestId);
        ModelEvaluateBatchSubmissionResponse res = problemTestCaseService.judgeAllSubmissionsOfContest(contestId);
        return ResponseEntity.ok().body(res);
    }

    @PostMapping("/submit-solution-output-of-testcase")
    public ResponseEntity<?> submitSolutionOutputOfATestCase(
        Principal principale,
        @RequestParam("inputJson") String inputJson,
        @RequestParam("file") MultipartFile file
    ) {
        Gson gson = new Gson();
        ModelSubmitSolutionOutputOfATestCase model = gson.fromJson(
            inputJson,
            ModelSubmitSolutionOutputOfATestCase.class);
        try {
            StringBuilder solutionOutput = new StringBuilder();
            InputStream inputStream = file.getInputStream();
            Scanner in = new Scanner(inputStream);
            while (in.hasNext()) {
                String line = in.nextLine();
                solutionOutput.append(line).append("\n");
                //System.out.println("submitSolutionOutputOfATestCase: read line: " + line);
            }
            in.close();
            ModelContestSubmissionResponse resp = problemTestCaseService.submitSolutionOutputOfATestCase(
                principale.getName(),
                solutionOutput.toString(),
                model
            );
            log.info("resp {}", resp);
            return ResponseEntity.status(200).body(resp);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.ok().body("OK");

    }

    @PostMapping("/submit-solution-output")
    public ResponseEntity<?> submitSolutionOutput(
        Principal principale,
        @RequestParam("inputJson") String inputJson,
        @RequestParam("file") MultipartFile file
    ) {
        log.info("submitSolutionOutput, inputJson = " + inputJson);
        Gson gson = new Gson();
        ModelSubmitSolutionOutput model = gson.fromJson(inputJson, ModelSubmitSolutionOutput.class);
        try {
            StringBuilder solutionOutput = new StringBuilder();
            InputStream inputStream = file.getInputStream();
            Scanner in = new Scanner(inputStream);
            while (in.hasNext()) {
                String line = in.nextLine();
                solutionOutput.append(line).append("\n");
                System.out.println("submitSolutionOutput: read line: " + line);
            }
            in.close();
            ModelContestSubmissionResponse resp = problemTestCaseService.submitSolutionOutput(solutionOutput.toString(),
                                                                                              model.getContestId(),
                                                                                              model.getProblemId(),
                                                                                              model.getTestCaseId(),
                                                                                              principale.getName());
            log.info("resp {}", resp);
            return ResponseEntity.status(200).body(resp);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.ok().body("OK");
    }

    @GetMapping("/public-ranking/{contestId}")
    public ResponseEntity<?> getRankingContestPublic(
        @PathVariable("contestId") String contestId,
        @RequestParam Constants.GetPointForRankingType getPointForRankingType
    ) {
        ContestEntity contest = contestService.findContestWithCache(contestId);
        if (!contest.getIsPublic()) {
            return ResponseEntity.status(400).body("This contest is not public");
        }
        if (contestSubmissionRepo.countAllByContestId(contestId) > 500) {
            return ResponseEntity
                .status(400)
                .body("This contest size is too big. Contact the contest manager for ranking table");
        }

        List<ContestSubmissionsByUser> res = problemTestCaseService.getRankingByContestIdNew(contestId, getPointForRankingType);

        return ResponseEntity.status(200).body(res);
    }

    @GetMapping("/get-ranking-contest-new/{contestId}")
    public ResponseEntity<?> getRankingContestNewVersion(
        @PathVariable("contestId") String contestId,
        @RequestParam Constants.GetPointForRankingType getPointForRankingType
    ) {
        List<ContestSubmissionsByUser> res = problemTestCaseService.getRankingByContestIdNew(contestId, getPointForRankingType);
        return ResponseEntity.status(200).body(res);
    }

    @PostMapping("/recalculate-ranking/{contestId}")
    public ResponseEntity<?> recalculateRanking(@PathVariable("contestId") String contestId) {
        log.info("/recalculate-ranking/ contestid {}", contestId);
        problemTestCaseService.calculateContestResult(contestId);
        return ResponseEntity.status(200).body(null);
    }

    @GetMapping("/get-problem-public-paging")
    public ResponseEntity<?> getProblemPublicPaging(Pageable pageable) {
        Page<ProblemEntity> page = problemTestCaseService.getPublicProblemPaging(pageable);
        return ResponseEntity.status(200).body(page);
    }

    @GetMapping("/get-test-case-list-by-problem/{problemId}")
    public ResponseEntity<?> getTestCaseListByProblem(@PathVariable("problemId") String problemId) {
        List<ModelGetTestCase> list = problemTestCaseService.getTestCaseByProblem(problemId);
        return ResponseEntity.status(200).body(list);
    }

    @GetMapping("/get-test-case-detail-short/{testCaseId}")
    public ResponseEntity<?> getTestCaseDetailShort(@PathVariable("testCaseId") UUID testCaseId)
        throws MiniLeetCodeException {
        ModelGetTestCaseDetail resp = problemTestCaseService.getTestCaseDetailShort(testCaseId);
        return ResponseEntity.status(200).body(resp);
    }

    @GetMapping("/get-test-case-detail/{testCaseId}")
    public ResponseEntity<?> getTestCaseDetail(@PathVariable("testCaseId") UUID testCaseId)
        throws MiniLeetCodeException {
        ModelGetTestCaseDetail resp = problemTestCaseService.getTestCaseDetail(testCaseId);
        return ResponseEntity.status(200).body(resp);
    }

    @PostMapping("/update-test-case/{testCaseId}")
    public ResponseEntity<?> updateDateTestCase(
        @PathVariable("testCaseId") UUID testCaseId,
        @RequestBody ModelSaveTestcase modelSaveTestcase
    ) throws MiniLeetCodeException {
        problemTestCaseService.editTestCase(testCaseId, modelSaveTestcase);
        return ResponseEntity.status(200).body(null);
    }

    @PostMapping("/add-user-to-contest")
    public ResponseEntity<?> addUserContest(@RequestBody ModelAddUserToContest modelAddUserToContest) {
        problemTestCaseService.addUserToContest(modelAddUserToContest);
        return ResponseEntity.status(200).body(null);
    }

    @PostMapping("/delete-user-contest")
    public ResponseEntity<?> deleteUserFromContest(@RequestBody ModelAddUserToContest modelAddUserToContest)
        throws MiniLeetCodeException {
        problemTestCaseService.deleteUserContest(modelAddUserToContest);
        return ResponseEntity.status(200).body(null);
    }

    @GetMapping("/get-contests-using-a-problem/{problemId}")
    public ResponseEntity<?> getContestsUsingAProblem(Principal principal, @PathVariable String problemId) {
        List<ModelGetContestResponse> res = problemTestCaseService.getContestsUsingAProblem(problemId);
        return ResponseEntity.ok().body(res);
    }

    @GetMapping("/get-contest-result-on-problem-of-a-user/{userLoginId}")
    public ResponseEntity<?> getContestResultOnProblemOfAUser(
        @PathVariable("userLoginId") String userLoginId,
        Pageable pageable
    ) {
        log.info("getContestResultOnProblemOfAUser, user = " + userLoginId);
        List<ContestSubmission> lst = problemTestCaseService.getNewestSubmissionResults(userLoginId);

        return ResponseEntity.status(200).body(lst);
    }

    @GetMapping("/get-contest-submission-paging-of-a-user/{userLoginId}")
    public ResponseEntity<?> getContestSubmissionPagingOfAUser(
        @PathVariable("userLoginId") String userLoginId,
        Pageable pageable
    ) {
        log.info("getContestSubmissionPagingOfAUser, user = " + userLoginId);
        pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by("createdAt").descending());
        Page<ContestSubmission> page = problemTestCaseService.findContestSubmissionByUserLoginIdPaging(
            pageable,
            userLoginId);
        log.info("page {}", page);
        return ResponseEntity.status(200).body(page);
    }

    @GetMapping("/get-contest-submission-paging-of-a-user-and-contest/{contestId}")
    public ResponseEntity<?> getContestSubmissionPagingOfCurrentUser(
        Principal principal,
        @PathVariable String contestId, Pageable pageable
    ) {
        //log.info(
        //        "getContestSubmissionPagingOfCurrentUser, user = " + principal.getName() + " contestId = " + contestId);
        pageable = PageRequest.of(0, Integer.MAX_VALUE, Sort.by("createdAt").descending());
        Page<ContestSubmission> page = problemTestCaseService
            .findContestSubmissionByUserLoginIdAndContestIdPaging(pageable, principal.getName(), contestId);
        //log.info("page {}", page);
        return ResponseEntity.status(200).body(page);
    }

    @GetMapping("/get-contest-submission-in-problem-paging-of-a-user-and-contest")
    public ResponseEntity<?> getContestSubmissionInProblemPagingOfCurrentUser(
        Principal principal,
        @RequestParam("contestid") String contestId, @RequestParam("problemid") String problemId, Pageable pageable
    ) {
        pageable = PageRequest.of(0, Integer.MAX_VALUE, Sort.by("createdAt").descending());
        Page<ContestSubmission> page = problemTestCaseService
            .findContestSubmissionByUserLoginIdAndContestIdAndProblemIdPaging(
                pageable,
                principal.getName(),
                contestId,
                problemId);
        return ResponseEntity.status(200).body(page);
    }

    @Secured("ROLE_TEACHER")
    @GetMapping("/get-contest-submission-paging/{contestId}")
    public ResponseEntity<?> getContestSubmissionPaging(
        @PathVariable("contestId") String contestId,
        @RequestParam String search,
        @RequestParam int page,
        @RequestParam int size
    ) {
        Pageable pageRequest = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<ContestSubmission> res = problemTestCaseService.findContestSubmissionByContestIdPaging(
            pageRequest,
            contestId,
            search);
        return ResponseEntity.status(200).body(res);
    }

    @GetMapping("/get-contest-submission-of-a-user-paging/{contestId}/{userId}")
    public ResponseEntity<?> getContestSubmissionOfAUserPaging(
        @PathVariable("contestId") String contestId,
        @PathVariable String userId, Pageable pageable
    ) {
        log.info("getContestSubmissionPaging, contestId = " + contestId);
        pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by("createdAt").descending());
        Page<ContestSubmission> page = problemTestCaseService
            .findContestSubmissionByUserLoginIdAndContestIdPaging(pageable, userId, contestId);
        log.info("page {}", page);
        return ResponseEntity.status(200).body(page);
    }

    @GetMapping("/get-contest-problem-submission-detail-viewed-by-participant/{submissionId}")
    public ResponseEntity<?> getContestSubmissionDetailViewedByParticipant(
        @PathVariable("submissionId") UUID submissionId
    ) {
        log.info("get contest submission detail");
        ContestSubmissionEntity contestSubmission = problemTestCaseService.getContestSubmissionDetail(submissionId);
        return ResponseEntity.status(200).body(contestSubmission);
    }

    @GetMapping("/get-contest-infos-of-a-subsmission/{submissionId}")
    public ResponseEntity<?> getContestInfosOfASubmission(@PathVariable("submissionId") UUID submissionId) {
        ModelGetContestInfosOfSubmissionOutput res = problemTestCaseService.getContestInfosOfASubmission(submissionId);
        return ResponseEntity.ok().body(res);
    }

    @GetMapping("/get-contest-problem-submission-detail-viewed-by-manager/{submissionId}")
    public ResponseEntity<?> getContestSubmissionDetailViewedByManager(
        @PathVariable("submissionId") UUID submissionId
    ) {
        log.info("get contest submission detail");
        ContestSubmissionEntity contestSubmission = problemTestCaseService.getContestSubmissionDetail(submissionId);
        return ResponseEntity.status(200).body(contestSubmission);
    }


    @DeleteMapping("/delete-test-case/{testCaseId}")
    public ResponseEntity<?> deleteTestCase(@PathVariable("testCaseId") UUID testCaseId, Principal principal)
        throws MiniLeetCodeException {
        problemTestCaseService.deleteTestcase(testCaseId, principal.getName());
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }

    @GetMapping("/get-user-judged-problem-submission/{contestId}")
    public ResponseEntity<?> getUserJudgedProblemSubmission(@PathVariable String contestId) {
        List<ModelUserJudgedProblemSubmissionResponse> res = problemTestCaseService
            .getUserJudgedProblemSubmissions(contestId);
        return ResponseEntity.ok().body(res);
    }

    @GetMapping("/get-roles-user-approved-notapproved-in-contest/{userId}/{contestId}")
    public ResponseEntity<?> getRolesUserParrovedNotApprovedInContest(
        Principal principal, @PathVariable String userId,
        @PathVariable String contestId
    ) {
        ModelGetRolesOfUserInContestResponse res = problemTestCaseService.getRolesOfUserInContest(userId, contestId);
        return ResponseEntity.ok().body(res);
    }

    @GetMapping("/get-permissions-of-members-of-contest")
    public ResponseEntity<?> getPermissionsOfMemberOfContest() {
        List<String> perms = UserRegistrationContestEntity.getListPermissions();
        return ResponseEntity.ok().body(perms);
    }

    @GetMapping("/rerun-create-testcase-solution/{problemId}/{testCaseId}")
    public ResponseEntity<?> rerunCreateTestCaseSolution(
        Principal principal, @PathVariable String problemId,
        @PathVariable UUID testCaseId
    ) {
        log.info("rerunCreateTestCaseSolution problem " + problemId + " testCaseId " + testCaseId);
        ModelUploadTestCaseOutput res = problemTestCaseService.rerunCreateTestCaseSolution(problemId, testCaseId,
                                                                                           principal.getName());
        return ResponseEntity.ok().body(res);
    }

    @GetMapping("/get-submission-modes")
    public ResponseEntity<?> getSubmissionModes() {
        List<String> res = ContestProblem.getSubmissionModes();
        return ResponseEntity.ok().body(res);
    }

    @GetMapping("/get-user-contest-problem-roles/{problemId}")
    public ResponseEntity<?> getUserContestProblemRoles(Principal principal, @PathVariable String problemId) {
        List<ModelResponseUserProblemRole> res = problemTestCaseService.getUserProblemRoles(problemId);
        return ResponseEntity.ok().body(res);
    }

    @PostMapping("/add-contest-problem-role-to-user/")
    public ResponseEntity<?> addContestProblemRole(Principal principal, @RequestBody ModelUserProblemRole input) {
        try {
            boolean ok = problemTestCaseService.addUserProblemRole(principal.getName(), input);
            return ResponseEntity.ok().body(ok);
        } catch (Exception e) {
            if (e instanceof MiniLeetCodeException)
                return ResponseEntity.status(((MiniLeetCodeException) e).getCode()).body(e.getMessage());
            else
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping("/remove-contest-problem-role-to-user/")
    public ResponseEntity<?> removeContestProblemRole(Principal principal, @RequestBody ModelUserProblemRole input) {   
        try {
            boolean ok = problemTestCaseService.removeUserProblemRole(principal.getName(), input);
            return ResponseEntity.ok().body(ok);
        } catch (Exception e) {
            if (e instanceof MiniLeetCodeException)
                return ResponseEntity.status(((MiniLeetCodeException) e).getCode()).body(e.getMessage());
            else
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

//    @GetMapping("/public/ranking-programming-contest/{contestId}")
//    public ResponseEntity<?> getRankingContestPublic(@PathVariable("contestId") String contestId, Pageable pageable) {
//        pageable = Pageable.unpaged();
//        List<ContestSubmissionsByUser> page = problemTestCaseService.getRankingByContestIdNew(pageable, contestId,
//                                                                                              Constants.GetPointForRankingType.HIGHEST);
//        // log.info("ranking page {}", page);
//        return ResponseEntity.status(200).body(page);
//    }

    @PostMapping("/switch-judge-mode")
    public ResponseEntity<?> switchAllContestJudgeMode(@RequestParam("mode") String judgeMode) {

        problemTestCaseService.switchAllContestJudgeMode(judgeMode);

        return ResponseEntity.status(200).body("ok");
    }

    @PostMapping("/upload-excel-student-list-to-contest")
    public ResponseEntity<?> uploadExcelStudentListOfContest(
        Principal principal,
        @RequestParam("inputJson") String inputJson,
        @RequestParam("file") MultipartFile file
    ) {
        Gson gson = new Gson();
        ModelUploadExcelParticipantToContestInput modelUpload = gson.fromJson(
            inputJson, ModelUploadExcelParticipantToContestInput.class);
        List<String> uploadedUsers = new ArrayList();
        String contestId = modelUpload.getContestId();
        try (InputStream is = file.getInputStream()) {
            XSSFWorkbook wb = new XSSFWorkbook(is);
            XSSFSheet sheet = wb.getSheetAt(0);
            int lastRowNum = sheet.getLastRowNum();
            //System.out.println("uploadExcelStudentListOfQuizTest, lastRowNum = " + lastRowNum);
            for (int i = 1; i <= lastRowNum; i++) {
                Row row = sheet.getRow(i);
                Cell c = row.getCell(0);

                String userId = c.getStringCellValue();
                UserLogin u = userService.findById(userId);
                if (u == null) {
                    log.info("uploadExcelStudentListOfContest, user " + userId + " NOT EXISTS");
                    continue;
                }
                ModelAddUserToContest m = new ModelAddUserToContest();
                m.setContestId(contestId);
                m.setUserId(userId);
                m.setRole(UserRegistrationContestEntity.ROLE_PARTICIPANT);
                int cnt = problemTestCaseService.addUserToContest(m);
                //if(cnt == 1){
                uploadedUsers.add(userId);
                //}
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ResponseEntity.ok().body(uploadedUsers);
    }

    @PostMapping("/manager-submit-code-of-participant")
    public ResponseEntity<?> ManagerSubmitCodeOfParticipant(
        Principal principal,
        @RequestParam("inputJson") String inputJson,
        @RequestParam("file") MultipartFile file
    ) {
        Gson gson = new Gson();
        ModelInputManagerSubmitCodeOfParticipant model = gson.fromJson(
            inputJson,
            ModelInputManagerSubmitCodeOfParticipant.class);
        ContestEntity contestEntity = contestRepo.findContestByContestId(model.getContestId());
        String filename = file.getOriginalFilename();
        log.info("ManagerSubmitCodeOfParticipant, filename = " + file.getOriginalFilename());
        String[] s = filename.split("\\.");
        log.info("ManagerSubmitCodeOfParticipant, extract from filename, s.length = " + s.length);
        if (s.length < 2) {
            return ResponseEntity.ok().body("Filename " + filename + " Invalid");
        }
        String language = s[1].trim();
        if (language.equals("cpp")) {
            language = ContestSubmissionEntity.LANGUAGE_CPP;
        } else if (language.equals("java")) {
            language = ContestSubmissionEntity.LANGUAGE_JAVA;
        } else if (language.equals("py")) {
            language = ContestSubmissionEntity.LANGUAGE_PYTHON;
        }

        String[] s1 = s[0].split("_");
        log.info("ManagerSubmitCodeOfParticipant, extract from filename, s[0] = " + s[0] + " s1 = " + s1.length);
        if (s1.length < 2) {
            ModelContestSubmissionResponse resp = buildSubmissionResponseInvalidFilename(filename);
            return ResponseEntity.ok().body(resp);
        }
        String userId = s1[0].trim();
        String problemCode = s1[1].trim();
        String contestId = model.getContestId();
        String problemId = null;
        ContestProblem cp = contestProblemRepo.findByContestIdAndProblemRecode(contestId, problemCode);

        if (cp != null) {
            problemId = cp.getProblemId();
        } else {
            log.info("ManagerSubmitCodeOfParticipant, not found problem of code " + problemCode);
            ModelContestSubmissionResponse resp = buildSubmissionResponseProblemNotFound();
            return ResponseEntity.ok().body(resp);
        }
        if (!contestEntity.getStatusId().equals(ContestEntity.CONTEST_STATUS_RUNNING)) {
            ModelContestSubmissionResponse resp = buildSubmissionResponseTimeOut();
            return ResponseEntity.ok().body(resp);
        }

        List<UserRegistrationContestEntity> userRegistrations = userRegistrationContestRepo
            .findUserRegistrationContestEntityByContestIdAndUserIdAndStatusAndRoleId(model.getContestId(),
                                                                                     userId,
                                                                                     UserRegistrationContestEntity.STATUS_SUCCESSFUL,
                                                                                     UserRegistrationContestEntity.ROLE_PARTICIPANT);
        if (userRegistrations == null || userRegistrations.size() == 0) {
            ModelContestSubmissionResponse resp = buildSubmissionResponseNotRegistered();
            return ResponseEntity.ok().body(resp);

        }

        for (UserRegistrationContestEntity u : userRegistrations) {
            if (u.getPermissionId() != null
                && u.getPermissionId().equals(UserRegistrationContestEntity.PERMISSION_FORBIDDEN_SUBMIT)) {
                ModelContestSubmissionResponse resp = buildSubmissionResponseNoPermission();
                return ResponseEntity.ok().body(resp);
            }
        }

        int numOfSubmissions = contestSubmissionRepo
            .countAllByContestIdAndUserIdAndProblemId(model.getContestId(), userId, problemId);
        if (numOfSubmissions >= contestEntity.getMaxNumberSubmissions()) {
            ModelContestSubmissionResponse resp = buildSubmissionResponseReachMaxSubmission(contestEntity.getMaxNumberSubmissions());
            return ResponseEntity.ok().body(resp);
        }

        long submissionInterval = contestEntity.getMinTimeBetweenTwoSubmissions();
        if (submissionInterval > 0) {
            Date now = new Date();
            Long lastSubmitTime = cacheService.findUserLastProblemSubmissionTimeInCache(problemId, userId);
            if (lastSubmitTime != null) {
                long diffBetweenNowAndLastSubmit = now.getTime() - lastSubmitTime;
                if (diffBetweenNowAndLastSubmit < submissionInterval * 1000) {
                    ModelContestSubmissionResponse resp = buildSubmissionResponseNotEnoughTimeBetweenSubmissions(
                        submissionInterval);
                    return ResponseEntity.ok().body(resp);
                }
            }
            cacheService.addUserLastProblemSubmissionTimeToCache(problemId, userId);
        }

        try {
            StringBuilder source = new StringBuilder();
            InputStream inputStream = file.getInputStream();
            Scanner in = new Scanner(inputStream);
            while (in.hasNext()) {
                String line = in.nextLine();
                source.append(line).append("\n");
            }
            in.close();

            if (source.length() > contestEntity.getMaxSourceCodeLength()) {
                ModelContestSubmissionResponse resp = buildSubmissionResponseReachMaxSourceLength(
                    source.length(),
                    contestEntity.getMaxSourceCodeLength());
                return ResponseEntity.ok().body(resp);
            }
            ModelContestSubmission request = new ModelContestSubmission(model.getContestId(), problemId,
                                                                        source.toString(), language);
            ModelContestSubmissionResponse resp = null;
            if (contestEntity.getSubmissionActionType()
                             .equals(ContestEntity.CONTEST_SUBMISSION_ACTION_TYPE_STORE_AND_EXECUTE)) {
                if (cp.getSubmissionMode() != null &&
                    cp.getSubmissionMode().equals(ContestProblem.SUBMISSION_MODE_SOLUTION_OUTPUT)) {
                    resp = problemTestCaseService.submitContestProblemStoreOnlyNotExecute(
                        request,
                        userId,
                        principal.getName());
                } else {
                    resp = problemTestCaseService.submitContestProblemTestCaseByTestCaseWithFile(
                        request,
                        userId,
                        principal.getName());
                }
            } else {
                resp = problemTestCaseService.submitContestProblemStoreOnlyNotExecute(
                    request,
                    userId,
                    principal.getName());
            }
            log.info("ManagerSubmitCodeOfParticipant, submitted successfully");
            return ResponseEntity.status(200).body(resp);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.ok().body("OK");
    }

    //    @Secured("ROLE_TEACHER")
    @PostMapping(value = "/export-problem/{id}", consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE})
    public ResponseEntity<StreamingResponseBody> exportProblem(
        @PathVariable @NotBlank String id
    ) {
        StreamingResponseBody stream = outputStream -> problemTestCaseService.exportProblem(
            id,
            outputStream);

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE);
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + id + ".zip");

        return ResponseEntity.ok().headers(headers).body(stream);
    }

    @Secured("ROLE_TEACHER")
    @GetMapping("/get-all-my-problems")
    public List<ProblemEntity> getAllMyProblems(Principal owner) {
        return this.problemTestCaseService.getOwnerProblems(owner.getName());
    }

    @Secured("ROLE_TEACHER")
    @GetMapping("/get-all-shared-problems")
    public List<ProblemEntity> getAllSharedProblems(Principal owner) {
        return this.problemTestCaseService.getSharedProblems(owner.getName());
    }
}
