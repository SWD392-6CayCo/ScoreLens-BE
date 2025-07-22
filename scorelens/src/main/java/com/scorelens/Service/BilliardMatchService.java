package com.scorelens.Service;

import com.scorelens.DTOs.Request.*;
import com.scorelens.DTOs.Response.BilliardMatchResponse;
import com.scorelens.Entity.*;
import com.scorelens.Enums.*;
import com.scorelens.Exception.AppException;
import com.scorelens.Exception.ErrorCode;
import com.scorelens.Mapper.BilliardMatchMapper;
import com.scorelens.Repository.*;
import com.scorelens.Service.Interface.IBilliardMatchService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@Slf4j
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
    private EventRepo eventRepo;
    @Autowired
    private NotificationRepo notificationRepo;

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

    @Autowired
    NotificationService notificationService;

    @Autowired
    RealTimeNotification realTimeNotification;

    @Autowired
    @Lazy
    EventProcessorService eventProcessorService;

    @Autowired
    BilliardTableService billiardTableService;


    @Override
    public BilliardMatchResponse getById(Integer id) {
        BilliardMatch match = repository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.MATCH_NOT_FOUND));
        return billiardMatchMapper.toBilliardMatchResponse(match);
    }

    @Override
    public BilliardMatchResponse getOnGoingMatch(String billiardTableID){
        //k check null, neu null thi van tra ve null
        return billiardMatchMapper.toBilliardMatchResponse(
                repository.findByTableAndOngoing(billiardTableID)
        );
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
    @Transactional
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
            if(!table.getStatus().equals(TableStatus.available)) {
                throw new AppException(ErrorCode.TABLE_NOT_AVAILABLE);
            }
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
            if(!table.getStatus().equals(TableStatus.available)) {
                throw new AppException(ErrorCode.TABLE_NOT_AVAILABLE);
            }
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
        if (match.getStatus().equals(MatchStatus.pending)) {
            match.setStatus(MatchStatus.ongoing);
        } else if (match.getStatus().equals(MatchStatus.completed) || match.getStatus().equals(MatchStatus.cancelled)) {
            throw new AppException(ErrorCode.MATCH_COMPLETED);
        }

        Team team = teamRepo.findById(request.getTeamID())
                .orElseThrow(() -> new AppException(ErrorCode.TEAM_NOT_FOUND));

        GameSet currentSet = match.getSets().stream()
                .filter(set -> set.getStatus() == MatchStatus.ongoing)
                .findFirst()
                .orElseGet(() -> {
                    GameSet pendingSet = match.getSets().stream()
                            .filter(set -> set.getStatus() == MatchStatus.pending)
                            .findFirst()
                            .orElseThrow(() -> new AppException(ErrorCode.SET_NOT_FOUND));

                    pendingSet.setStatus(MatchStatus.ongoing);
                    pendingSet.setStartTime(LocalDateTime.now());
                    return pendingSet;
                });

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

            String tmp = "Team " + team.getName() + " has win game set no " + currentSet.getGameSetNo();
            log.info(tmp);

//            //save noti
            notificationService.saveNotification(
                new NotificationRequest(
                        match.getBilliardMatchID(),
                        tmp,
                        NotificationType.score
                ));

            //push noti
            realTimeNotification.sendRealTimeNotification(
                    tmp,
                    WebSocketTopic.NOTI_MOBILE,
                    match.getBillardTable().getBillardTableID(),
                    WSFCMCode.WINNING_SET
            );

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

            String tmp = "Match " + match.getBilliardMatchID() + " has ended";
            log.info(tmp);

            notificationService.saveNotification(
                    new NotificationRequest(
                            match.getBilliardMatchID(),
                            tmp,
                            NotificationType.end
                    ));

            //push noti
            realTimeNotification.sendRealTimeNotification(
                    tmp,
                    WebSocketTopic.NOTI_MOBILE,
                    match.getBillardTable().getBillardTableID(),
                    WSFCMCode.WINNING_MATCH
            );


            //free matchID & gameSetID in queue
            eventProcessorService.resetMatchState(matchID);
            List<Integer> gameSetIDList = match.getSets()
                    .stream()
                    .map(GameSet::getGameSetID)
                    .toList();
            eventProcessorService.resetGameSetState(gameSetIDList);

            //free table
            billiardTableService.setAvailable(String.valueOf(match.getBillardTable().getBillardTableID()));


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
//                winningTeam = match.getTeams().stream()
//                        .max(Comparator.comparingInt(Team::getTotalScore))
//                        .orElse(null);
                winningTeam = null;

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
            }else{
                match.setWinner(null);
                // Update status cho tung team
                for (Team t : match.getTeams()) {
                    t.setStatus(ResultStatus.draw);
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
    public BilliardMatchResponse cancelMatch(Integer matchID, Integer teamID) {
        BilliardMatch match = repository.findById(matchID)
                .orElseThrow(() -> new AppException(ErrorCode.MATCH_NOT_FOUND));
        if (match.getStatus() == MatchStatus.pending) {
            match.setStatus(MatchStatus.cancelled);
            match.setEndTime(LocalDateTime.now());
            repository.save(match);
            for (GameSet gs : match.getSets()) {
                gs.setStatus(MatchStatus.cancelled);
                gs.setEndTime(LocalDateTime.now());
                setRepo.save(gs);
            }
        } else if (match.getStatus() == MatchStatus.ongoing) {
            Team forfeitedTeam = teamRepo.findById(teamID)
                    .orElseThrow(() -> new AppException(ErrorCode.TEAM_NOT_FOUND));
            GameSet currentSet = match.getSets().stream()
                    .filter(set -> set.getStatus() == MatchStatus.ongoing)
                    .findFirst()
                    .orElseThrow(() -> new AppException(ErrorCode.SET_NOT_FOUND));

            if (match.getTeams().size() == 2) {
                Team winningTeam = match.getTeams().stream()
                        .filter(t -> !t.equals(forfeitedTeam))
                        .findFirst()
                        .orElseThrow();

                // update teams
                for (Team team : match.getTeams()) {
                    for (GameSet set : match.getSets()) {
                        if (team.equals(winningTeam)) {
                            team.setStatus(ResultStatus.win);
                            if (set == currentSet) {
                                teamSetService.updateTeamSet(team.getTeamID(), set.getGameSetID(), team.getTotalScore());
                            } else if (set.getStatus().equals(MatchStatus.pending)) {
                                teamSetService.updateTeamSet(team.getTeamID(), set.getGameSetID(), set.getRaceTo());
                            }
                            int total = team.getTss().stream()
                                    .mapToInt(ts -> ts.getTotalScore() != null ? ts.getTotalScore() : 0)
                                    .sum();
                            team.setTotalScore(total);
                            teamRepo.save(team);
                            // player status
                            for (Player p : team.getPlayers()) {
                                p.setStatus(team.getStatus());
                                playerRepo.save(p);
                            }
                        }else{
                            team.setStatus(ResultStatus.lose);
                            if (set == currentSet) {
                                teamSetService.updateTeamSet(team.getTeamID(), set.getGameSetID(), team.getTotalScore());
                            } else if (set.getStatus().equals(MatchStatus.pending)) {
                                teamSetService.updateTeamSet(team.getTeamID(), set.getGameSetID(), 0);
                            }
                            int total = team.getTss().stream()
                                    .mapToInt(ts -> ts.getTotalScore() != null ? ts.getTotalScore() : 0)
                                    .sum();
                            team.setTotalScore(total);
                            teamRepo.save(team);
                            // player status
                            for (Player p : team.getPlayers()) {
                                p.setStatus(team.getStatus());
                                playerRepo.save(p);
                            }
                        }
                    }
                }

                // update sets
                for (GameSet set : match.getSets()) {
                    if (set == currentSet) {
                        currentSet.setStatus(MatchStatus.cancelled);
                        currentSet.setEndTime(LocalDateTime.now());
                        currentSet.setWinner(winningTeam.getName());
                        setRepo.save(currentSet);
                    }
                    if (set.getStatus() == MatchStatus.pending) {
                        set.setStatus(MatchStatus.cancelled);
                        set.setStartTime(LocalDateTime.now());
                        set.setEndTime(LocalDateTime.now());
                        set.setWinner(winningTeam.getName());
                        setRepo.save(set);
                    }
                }

                match.setWinner(winningTeam.getName());
                match.setEndTime(LocalDateTime.now());
                match.setStatus(MatchStatus.cancelled);
                repository.save(match);
            } else {
                // match > 2 team
            }
        }

        String tmp = "Match " + match.getBilliardMatchID() + " has been canceled";
        log.info(tmp);

//            //save noti
        notificationService.saveNotification(
                new NotificationRequest(
                        match.getBilliardMatchID(),
                        tmp,
                        NotificationType.special
                ));

        //push noti
        realTimeNotification.sendRealTimeNotification(
                tmp,
                WebSocketTopic.NOTI_MOBILE,
                match.getBillardTable().getBillardTableID(),
                WSFCMCode.NOTIFICATION
        );

        return billiardMatchMapper.toBilliardMatchResponse(match);
    }

    @Override
    public String completeMatch(Integer id) {
        BilliardMatch match = findMatchByID(id);
        match.setStatus(MatchStatus.completed);
        repository.save(match);
        return "Match with ID " + id + " has been completed";
    }

    @Override
    public List<BilliardMatchResponse> getFilter(MatchFilterRequest request) {
        List<BilliardMatch> matches = repository.findAll();
        List<BilliardMatch> filtered = new ArrayList<>();

        LocalDate filterDate = null;
        if (request.getDate() != null) {
            filterDate = request.getDate().toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();
        }
        String filterStatus = request.getStatus();
        Integer filterMode = request.getModeID();

        for (BilliardMatch match : matches) {
            boolean matchDate = true;
            boolean matchStatus = true;
            boolean matchMode = true;

            if (filterDate != null) {
                LocalDate startDate = match.getStartTime().toLocalDate();
                LocalDate endDate = match.getEndTime() != null ? match.getEndTime().toLocalDate() : null;

                matchDate = filterDate.equals(startDate) || (filterDate.equals(endDate));
            }
            if (filterStatus != null) {
                matchStatus = match.getStatus().name().equalsIgnoreCase(filterStatus);
            }
            if (filterMode != null) {
                matchMode = match.getMode() != null && filterMode.equals(match.getMode().getModeID());
            }

            if (matchDate && matchStatus && matchMode) {
                filtered.add(match);
            }
            if (filtered.isEmpty()) {
                throw new AppException(ErrorCode.MATCH_NOT_FOUND);
            }
        }
        return billiardMatchMapper.toBilliardMatchResponses(filtered);
    }
    public BilliardMatch findMatchByID(Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.MATCH_NOT_FOUND));
    }


    public String startMatch(int billiardMatchID){
        BilliardMatch m = findMatchByID(billiardMatchID);
        m.setStatus(MatchStatus.ongoing);
        repository.save(m);
        return "Match with ID " + billiardMatchID + " has been started";
    }
}
