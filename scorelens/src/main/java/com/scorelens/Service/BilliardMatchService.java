package com.scorelens.Service;

import com.scorelens.DTOs.Request.BilliardMatchRequest;
import com.scorelens.DTOs.Response.BilliardMatchResponse;
import com.scorelens.Entity.BilliardMatch;
import com.scorelens.Repository.BilliardMatchRepository;
import com.scorelens.Service.Interface.IBilliardMatchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BilliardMatchService implements IBilliardMatchService {
        @Autowired
        private BilliardMatchRepository repository;

        @Override
        public BilliardMatchResponse getById(Integer id) {
            BilliardMatch match = repository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Match not found"));
            return toResponse(match);
        }

        @Override
        public BilliardMatchResponse createMatch(BilliardMatchRequest request) {
            BilliardMatch match = toEntity(request);
            return toResponse(repository.save(match));
        }

        @Override
        public BilliardMatchResponse updateMatch(Integer id, BilliardMatchRequest request) {
            Optional<BilliardMatch> optional = repository.findById(id);
            if (optional.isEmpty()) {
                throw new RuntimeException("Match not found");
            }
            BilliardMatch match = toEntity(request);
            match.setBilliardMatchID(id);
            return toResponse(repository.save(match));
        }

        @Override
        public Integer delete(Integer id) {
            if (!repository.existsById(id)) {
                throw new RuntimeException("Match not found");
            }
            repository.deleteById(id);
            return id;
        }

        // Mapping methods
        private BilliardMatchResponse toResponse(BilliardMatch match) {
            BilliardMatchResponse res = new BilliardMatchResponse();
            res.setBilliardMatchID(match.getBilliardMatchID());
            res.setBilliardTableID(match.getBillardTableID());
            res.setModeID(match.getModeID());
            res.setByStaff(match.getByStaff());
            res.setByCustomer(match.getByCustomer());
            res.setWinner(match.getWinner());
            res.setStartTime(match.getStartTime());
            res.setEndTime(match.getEndTime());
            res.setTotalRound(match.getTotalRound());
            res.setStatus(match.getStatus());
            res.setCode(match.getCode());
            return res;
        }

        private BilliardMatch toEntity(BilliardMatchRequest req) {
            BilliardMatch match = new BilliardMatch();
          //  match.setBilliardTableID(req.getBilliardTableID());
            match.setModeID(req.getModeID());
            match.setByStaff(req.getByStaff());
            match.setByCustomer(req.getByCustomer());
            match.setWinner(req.getWinner());
            match.setStartTime(req.getStartTime());
            match.setEndTime(req.getEndTime());
            match.setTotalRound(req.getTotalRound());
            match.setStatus(req.getStatus());
            match.setCode(req.getCode());
            return match;
        }
}
