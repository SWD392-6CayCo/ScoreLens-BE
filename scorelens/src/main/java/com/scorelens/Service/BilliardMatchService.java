package com.scorelens.Service;

import com.scorelens.DTOs.Request.*;
import com.scorelens.DTOs.Response.BilliardMatchResponse;
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
import java.util.*;
import java.util.stream.Collectors;

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
    private TeamSetService teamSetService;

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
            match.addTeam(team1);
        }
        for (GameSet gs : match.getSets()){
            for (Team t : match.getTeams()){
                TeamSet tss = teamSetService.createTeamSet(t.getTeamID(),gs.getGameSetID());
                t.addTeamSet(tss);
                gs.addTeamSet(tss);
            }
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
            teamSetService.deleteBySet(set.getGameSetID());
            setService.deleteByMatch(set.getGameSetID());
        }
        for(Team team : teamRepo.findByBilliardMatch_BilliardMatchID(id)){
            for(Player player : team.getPlayers()){
                playerService.deletePlayer(player.getPlayerID());
            }
            teamService.deleteByMatch(team.getTeamID());
        }
        repository.deleteById(id);
        return id;
    }

    @Override
    public void deleteAll(){
        List<BilliardMatch> matchs = repository.findAll();
        for (BilliardMatch match : matchs) {
            delete(match.getBilliardMatchID());
        }
    }

    @Override
    public BilliardMatchResponse updateScore(ScoreRequest request) {
        BilliardMatch match = repository.findById(request.getMatchID())
                .orElseThrow(() -> new AppException(ErrorCode.MATCH_NOT_FOUND));

        Team team = teamRepo.findById(request.getTeamID())
                .orElseThrow(() -> new AppException(ErrorCode.TEAM_NOT_FOUND));

        GameSet currentSet = match.getSets().stream()
                .filter(set -> set.getStatus() == MatchStatus.ongoing)
                .findFirst()
                .orElseThrow(() -> new AppException(ErrorCode.SET_NOT_FOUND));

        // Update team score
        int delta = Integer.parseInt(request.getDelta());
        team.setTotalScore(team.getTotalScore() + delta);
        teamRepo.save(team);

        // Check for set completion
        if (team.getTotalScore() == currentSet.getRaceTo()) {
            currentSet.setEndTime(LocalDateTime.now());
            currentSet.setStatus(MatchStatus.completed);
            currentSet.setWinner(team.getName());
            setRepo.save(currentSet);

            // Update team scores into TeamSet
            for (Team t : match.getTeams()) {
                teamSetService.updateTeamSet(t.getTeamID(), currentSet.getGameSetID(), t.getTotalScore());
                t.setTotalScore(0);
                teamRepo.save(t);
            }
        }
        // Check if match should end
        checkMatchEnd(request.getMatchID());
        return billiardMatchMapper.toBilliardMatchResponse(repository.save(match));
    }

    public void checkMatchEnd(Integer matchID) {
        BilliardMatch match = repository.findById(matchID)
                .orElseThrow(() -> new AppException(ErrorCode.MATCH_NOT_FOUND));

        boolean noPendingOrOngoing = match.getSets().stream()
                .noneMatch(set -> set.getStatus() == MatchStatus.pending || set.getStatus() == MatchStatus.ongoing);


        if (noPendingOrOngoing) {
            match.setEndTime(LocalDateTime.now());
            match.setStatus(MatchStatus.completed);

            // sum totalScore moi team tu teamSet
            for (Team t : match.getTeams()) {
                int total = t.getTss().stream()
                        .mapToInt(ts -> ts.getTotalScore() != null ? ts.getTotalScore() : 0)
                        .sum();
                t.setTotalScore(total);
                teamRepo.save(t);
            }

            // Dem luot thang cua tung team trong tung set
            Map<Team, Long> winCount = new HashMap<>();
            for (GameSet set : match.getSets()) {
                for (Team t : match.getTeams()) {
                    if (t.getName().equals(set.getWinner())) {
                        winCount.put(t, winCount.getOrDefault(t, 0L) + 1);
                    }
                }
            }

            // sort tu cao -> thap
            List<Map.Entry<Team, Long>> sorted = new ArrayList<>(winCount.entrySet());
            sorted.sort((a, b) -> Long.compare(b.getValue(), a.getValue()));

            Team winningTeam = null;
            if (sorted.size() >= 2 && sorted.get(0).getValue().equals(sorted.get(1).getValue())) {
                // draw -> cho team thang bang total score
                winningTeam = match.getTeams().stream()
                        .max(Comparator.comparingInt(Team::getTotalScore))
                        .orElse(null);
            } else if (!sorted.isEmpty()) {
                winningTeam = sorted.get(0).getKey();
            }

            if (winningTeam != null) {
                match.setWinner(winningTeam.getName());

                // Update status cho tung team
                for (Team t : match.getTeams()) {
                    if (t.equals(winningTeam)) {
                        t.setStatus(ResultStatus.win);
                    } else {
                        t.setStatus(ResultStatus.lose);
                    }
                    teamRepo.save(t);

                    // Update status cho tung player
                    for (Player p : t.getPlayers()) {
                        p.setStatus(t.getStatus());
                        playerRepo.save(p);
                    }
                }
            }
            repository.save(match);
        }
    }

    @Override
    public BilliardMatchResponse forfeit(Integer matchID, Integer teamID) {
        BilliardMatch match = repository.findById(matchID)
                .orElseThrow(() -> new AppException(ErrorCode.MATCH_NOT_FOUND));

        Team forfeitedTeam = teamRepo.findById(teamID)
                .orElseThrow(() -> new AppException(ErrorCode.TEAM_NOT_FOUND));

        GameSet currentSet = match.getSets().stream()
                .filter(set -> set.getStatus() == MatchStatus.ongoing)
                .findFirst()
                .orElseThrow(() -> new AppException(ErrorCode.SET_NOT_FOUND));

        List<Team> allTeams = match.getTeams();

        // forfeited team total score = 0 all set
        //                      status = lose
        for (GameSet set : match.getSets()) {
            teamSetService.updateTeamSet(forfeitedTeam.getTeamID(), set.getGameSetID(), 0);
        }
        forfeitedTeam.setStatus(ResultStatus.lose);
        teamRepo.save(forfeitedTeam);

        // player status
        for (Player p : forfeitedTeam.getPlayers()) {
            p.setStatus(ResultStatus.lose);
            playerRepo.save(p);
        }

        if (allTeams.size() == 2) {
            Team winningTeam = allTeams.stream()
                    .filter(t -> !t.equals(forfeitedTeam))
                    .findFirst()
                    .orElseThrow();

            // set current set
            teamSetService.updateTeamSet(winningTeam.getTeamID(), currentSet.getGameSetID(), winningTeam.getTotalScore());
            currentSet.setStatus(MatchStatus.forfeited);
            currentSet.setEndTime(LocalDateTime.now());
            currentSet.setWinner(winningTeam.getName());
            setRepo.save(currentSet);

            // update score cac set chua dau = raceTo
            for (GameSet set : match.getSets()) {
                if (set.getStatus() == MatchStatus.pending) {
                    set.setStatus(MatchStatus.forfeited);
                    set.setStartTime(LocalDateTime.now());
                    set.setEndTime(LocalDateTime.now());
                    set.setWinner(winningTeam.getName());
                    teamSetService.updateTeamSet(winningTeam.getTeamID(), set.getGameSetID(), set.getRaceTo());
                    setRepo.save(set);
                }
            }

            // sum totalScore winning team tu teamSet
            int total = winningTeam.getTss().stream()
                    .mapToInt(ts -> ts.getTotalScore() != null ? ts.getTotalScore() : 0)
                    .sum();
            winningTeam.setTotalScore(total);
            winningTeam.setStatus(ResultStatus.win);
            teamRepo.save(winningTeam);

            // player status
            for (Player p : winningTeam.getPlayers()) {
                p.setStatus(ResultStatus.win);
                playerRepo.save(p);
            }

            match.setWinner(winningTeam.getName());
            match.setEndTime(LocalDateTime.now());
            match.setStatus(MatchStatus.forfeited);
            repository.save(match);
        } else {
            // match > 2 team
        }
        return billiardMatchMapper.toBilliardMatchResponse(match);
    }

    @Override
    public BilliardMatchResponse cancel(Integer id) {
        BilliardMatch match = repository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.MATCH_NOT_FOUND));
        match.setStatus(MatchStatus.cancelled);
        repository.save(match);
        return billiardMatchMapper.toBilliardMatchResponse(match);
    }


    public String completeMatch(Integer id) {
        BilliardMatch match = repository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.MATCH_NOT_FOUND));
        match.setStatus(MatchStatus.completed);
        repository.save(match);
        return "Match with ID " + id + " has been completed";
    }
}
