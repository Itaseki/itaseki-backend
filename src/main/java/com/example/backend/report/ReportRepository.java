package com.example.backend.report;

import com.example.backend.community.domain.CommunityBoard;
import com.example.backend.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {
    Report findByUserAndCommunityBoard(User user, CommunityBoard communityBoard);
    List<Report> findAllByCommunityBoard(CommunityBoard board);
}
