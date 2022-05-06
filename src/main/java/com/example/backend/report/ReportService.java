package com.example.backend.report;

import com.example.backend.community.domain.CommunityBoard;
import com.example.backend.community.domain.CommunityComment;
import com.example.backend.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.embedded.undertow.UndertowServletWebServer;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ReportService {
    private final ReportRepository reportRepository;

    //controller 에서 Report 객체를 만들어 보내는 것 VS 여기에서 오버라이딩으로 각 경우별로 객체를 만들어서 저장하는 것
    public void saveReport(Report report){
        reportRepository.save(report);
    }

    public Boolean checkReportExistence(User user, CommunityBoard communityBoard){
        Report report = reportRepository.findByUserAndCommunityBoard(user, communityBoard);
        return report!=null;
    }

    public Boolean checkReportExistence(User user, CommunityComment comment){
        Report report=reportRepository.findByUserAndCommunityComment(user,comment);
        return report!=null;
    }

}