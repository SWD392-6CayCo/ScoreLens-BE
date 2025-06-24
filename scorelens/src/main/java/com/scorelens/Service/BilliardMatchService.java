package com.scorelens.Service;

import com.scorelens.DTOs.Request.*;
import com.scorelens.DTOs.Response.BilliardMatchResponse;
import com.scorelens.DTOs.Response.GameSetResponse;
import com.scorelens.Entity.*;
import com.scorelens.Enums.MatchStatus;
import com.scorelens.Enums.ResultStatus;
import com.scorelens.Exception.AppException;
import com.scorelens.Exception.ErrorCode;
import com.scorelens.Mapper.BilliardMatchMapper;
import com.scorelens.Repository.*;
import com.scorelens.Service.Interface.IBilliardMatchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
public class BilliardMatchService implements IBilliardMatchService {
    @Autowired
    private BilliardMatchRepository repository;
    @Autowired
    private BilliardTableRepo tableRepo;
    @Autowired
    private ModeRepository modeRepo;
    @Autowired
    private StaffRepository staffRepo;
    @Autowired
    private CustomerRepo customerRepo;
    @Autowired
    private TeamRepository teamRepo;
    @Autowired
    private GameSetRepository setRepo;
    @Autowired
    private PlayerRepo playerRepo;

    @Autowired
    private GameSetService setService;
    @Autowired
    private TeamService teamService;
    @Autowired
    private PlayerService playerService;

    @Autowired
    BilliardMatchMapper billiardMatchMapper;

    @Override
    public BilliardMatchResponse getById(Integer id) {
        BilliardMatch match = repository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.MATCH_NOT_FOUND));
        return billiardMatchMapper.toBilliardMatchResponse(match);
    }

    @Override
    public List<BilliardMatchResponse> getByCustomer(String id){
        Customer customer = customerRepo.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.MODE_NOT_FOUND));
        List<BilliardMatch> matchs = repository.findByCustomer_CustomerID(id);
        return billiardMatchMapper.toBilliardMatchResponses(matchs);
    }

    @Override
    public List<BilliardMatchResponse> getByStaff(String id){
        Staff staff = staffRepo.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.MODE_NOT_FOUND));
        List<BilliardMatch> matchs = repository.findByStaff_StaffID(id);
        return billiardMatchMapper.toBilliardMatchResponses(matchs);
    }

    @Override
    public BilliardMatchResponse getByPlayerID(Integer id){
        Player player = playerRepo.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.PLAYER_NOT_FOUND));
        BilliardMatch match = repository.findByPlayerId(id);
        return billiardMatchMapper.toBilliardMatchResponse(match);
    }

    @Override
    public List<BilliardMatchResponse> getByCustomerID(String id) {
        Customer customer = customerRepo.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.MATCH_NOT_FOUND));
        List<BilliardMatch> matchs = repository.findByCustomerId(id);
        return billiardMatchMapper.toBilliardMatchResponses(matchs);
    }

    @Override
    public BilliardMatchResponse createMatch(BilliardMatchCreateRequest request) {
        BilliardMatch match = billiardMatchMapper.toBilliardMatch(request);
        if (request.getStaffID() == null && request.getCustomerID() == null) {
            throw new AppException(ErrorCode.ALL_NOT_NULL);
        }
        if (request.getStaffID() != null && request.getCustomerID() != null) {
            throw new AppException(ErrorCode.ALL_NOT_VALUE);
        }
        if (request.getStaffID() == null) {
            BilliardTable table = tableRepo.findById(request.getBilliardTableID())
                    .orElseThrow(() -> new AppException(ErrorCode.TABLE_NOT_FOUND));
            Mode mode = modeRepo.findById(request.getModeID())
                    .orElseThrow(() -> new AppException(ErrorCode.MODE_NOT_FOUND));
            Customer customer = customerRepo.findById(request.getCustomerID())
                    .orElseThrow(() -> new AppException(ErrorCode.MATCH_NOT_FOUND));
            match.setBillardTable(table);
            match.setMode(mode);
            match.setStaff(null);
            match.setCustomer(customer);
        }
        if (request.getCustomerID() == null) {
            BilliardTable table = tableRepo.findById(request.getBilliardTableID())
                    .orElseThrow(() -> new AppException(ErrorCode.TABLE_NOT_FOUND));
            Mode mode = modeRepo.findById(request.getModeID())
                    .orElseThrow(() -> new AppException(ErrorCode.MODE_NOT_FOUND));
            Staff staff = staffRepo.findById(request.getStaffID())
                    .orElseThrow(() -> new AppException(ErrorCode.MATCH_NOT_FOUND));

            match.setBillardTable(table);
            match.setMode(mode);
            match.setStaff(staff);
            match.setCustomer(null);
        }

        match.setTotalSet(request.getTotalSet());
        match.setStartTime(LocalDateTime.now());
        match.setEndTime(null);
        match.setWinner(null);
        match.setStatus(MatchStatus.pending);
        match.setCode(generateRandomCode());
        repository.save(match);
        for (int i = 1; i <= match.getTotalSet(); i++) {
            GameSetCreateRequest setRequest = new GameSetCreateRequest();
            setRequest.setBilliardMatchID(match.getBilliardMatchID());
            setRequest.setRaceTo(request.getRaceTo());
            GameSet gameSet = setService.createSetInMatch(i, setRequest);
            match.addSet(gameSet);
        }
        if (request.getTeamConfigs() == null || request.getTeamConfigs().isEmpty()) {
            throw new AppException(ErrorCode.TEAM_NOT_NULL);
        }
        for (BilliardMatchCreateRequest.TeamConfig team : request.getTeamConfigs()) {
            TeamCreateRequest teamRequest = new TeamCreateRequest();
            teamRequest.setBilliardMatchID(match.getBilliardMatchID());
            teamRequest.setName(team.getName());
            teamRequest.setTotalMember(team.getTotalMember());
            teamRequest.setMemberNames(team.getMemberNames());
            Team team1 = teamService.createTeam(teamRequest);
//            for (String name : team.getMemberNames()) {
//                PlayerCreateRequest playerCreateRequest = new PlayerCreateRequest();
//                playerCreateRequest.setName(name);
//                playerCreateRequest.setTeamID(team1.getTeamID());
//                Player player = playerService.createPlayer(playerCreateRequest);
//                team1.addPlayer(player);
//            }
            match.addTeam(team1);
        }
        return billiardMatchMapper.toBilliardMatchResponse(match);
    }

    private String generateRandomCode() {
        Random random = new Random();
        int number = 100000 + random.nextInt(900000); // ensures a 6-digit number
        return String.valueOf(number);
    }

    @Override
    public BilliardMatchResponse updateMatch(Integer id, BilliardMatchUpdateRequest request) {
        BilliardMatch match = repository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.MATCH_NOT_FOUND));
        match.setWinner(request.getWinner());
        if(request.getStatus().equals(MatchStatus.ongoing)) {
            match.setStatus(MatchStatus.ongoing);
        }else if(request.getStatus().equals(MatchStatus.completed)) {
            match.setStatus(MatchStatus.completed);
        }else {
            match.setStatus(MatchStatus.forfeited);
        }
        return billiardMatchMapper.toBilliardMatchResponse(repository.save(match));
    }

        @Override
        public Integer delete(Integer id) {
            if (!repository.existsById(id)) {
                throw new AppException(ErrorCode.MATCH_NOT_FOUND);
            }
            for(GameSet set : setRepo.findByBilliardMatch_BilliardMatchID(id)){
                setService.deleteByMatch(set.getGameSetID());
            }
            for(Team team : teamRepo.findByBilliardMatch_BilliardMatchID(id)){
                teamService.deleteByMatch(team.getTeamID());
            }
            repository.deleteById(id);
            return id;
        }
}
