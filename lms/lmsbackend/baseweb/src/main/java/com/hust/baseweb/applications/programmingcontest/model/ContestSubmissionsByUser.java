package com.hust.baseweb.applications.programmingcontest.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ContestSubmissionsByUser {

    private List<ContestSubmissionsByUserCustom> mapProblemsToPoints;
    private String userId;
    private String fullname;
    private int totalPoint;
}
