package com.example.competitionmanagment.service;

import com.example.competitionmanagment.Mapper.HuntingMapper;
import com.example.competitionmanagment.Mapper.HuntingResponseMapper;
import com.example.competitionmanagment.dao.*;
import com.example.competitionmanagment.dto.hunting.HuntingDto;
import com.example.competitionmanagment.dto.hunting.HuntingDtoResponse;
import com.example.competitionmanagment.entity.*;
import com.example.competitionmanagment.service.serviceInterface.HuntingService;
import com.example.competitionmanagment.util.MySpecificException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class HuntingServiceImp implements HuntingService {

    @Autowired
    private HuntingRepository huntingRepository;

    @Autowired
    private RankingRepository rankingRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private CompetitionRepository competitionRepository;

    @Autowired
    private FishRepository fishRepository;

    @Override
    public Hunting addHunting(Hunting hunting) {

        memberRepository.findByNum(hunting.getUser().getNum()).orElseThrow(()-> new MySpecificException("member id not found"));
        Competition competition =  competitionRepository.findByCode(hunting.getCompetition().getCode()).orElseThrow(()->new MySpecificException("compettion not found"));
        fishRepository.findByName(hunting.getFish().getName()).orElseThrow(()->new MySpecificException("fish name is not available"));
        RandId randId = new RandId();
        randId.setCompetitoncode(hunting.getCompetition().getCode());
        randId.setMembernum(hunting.getUser().getNum());

        rankingRepository.findById(randId).orElseThrow(()->new MySpecificException("this member is not registred in that competition"));
        //here im checking if the today date is the exactly the date of competition
        LocalDate todayDate = LocalDate.now();
        System.out.println("here im checking the date value " +  todayDate);
        if(!competition.getDate().equals(todayDate) ){
            throw new MySpecificException("date isnt valide !!");
        }
        HuntingDto huntingDto = searchHunting(hunting.getUser().getNum(),hunting.getFish().getName());

        if(huntingDto == null){
            return huntingRepository.save(hunting);
        }else{
            hunting.setNumberOfFish(hunting.getNumberOfFish() + huntingDto.numberOfFish);
            hunting.setId(huntingDto.id);
            return huntingRepository.save(hunting);
        }
    }

    @Override
    public HuntingDto searchHunting(int num, String fishname) {
        User user = new User();
        user.setNum(num);
        Fish fish = new Fish();
        fish.setName(fishname);
        Hunting hunting = huntingRepository.findHuntingByUserAndFish(user,fish);
        HuntingDto huntingDto = HuntingMapper.HM.toDto(hunting);
        return huntingDto;

    }

    @Override
    public Hunting searchHuntingByHunting(String code) {
        return null;
    }

    @Override
    public List<Hunting> fetchHunting(String code) {

        Competition competition = new Competition();
        competition.setCode(code);

        return huntingRepository.findAllByCompetition(competition);

    }

    public List<Ranking> calulateScore(String CompetitionCode){

        List<Ranking> rankings = new ArrayList<>();
        List<Integer> memebers = iddd(CompetitionCode);
        List<Hunting> huntings = fetchHunting(CompetitionCode);
        List<HuntingDtoResponse> huntingDtoResponses = new ArrayList<>();

        for(Hunting H :huntings){
            HuntingDtoResponse huntingDtoResponse = HuntingResponseMapper.HRM.toDto(H);
            huntingDtoResponse.totalScoreForRaw =  huntingDtoResponse.fishScore * huntingDtoResponse.numberOfFish;
            huntingDtoResponses.add(huntingDtoResponse);
        }

        for(Integer id:memebers){
            String competitionCode = "";
            int totalScorePerId = 0;
            for(HuntingDtoResponse H : huntingDtoResponses){
                if(H.membernum == id ){
                    totalScorePerId = totalScorePerId + H.totalScoreForRaw;
                }else{
                }
                competitionCode = H.competitioncode;
            }

            Ranking ranking = new Ranking();
            ranking.setScore(totalScorePerId);
            RandId randId = new RandId();
            randId.setMembernum(id);
            randId.setCompetitoncode(competitionCode);
            ranking.setId(randId);

            rankings.add(ranking);

        }

        rankings.sort(Comparator.comparingInt(Ranking::getScore).reversed());

        for (int i = 0; i < rankings.size(); i++) {
            rankings.get(i).setRank(i + 1);
        }

        rankingRepository.saveAll(rankings);

        return rankings;
        
        
        
        
    }


    

    @Override
    public List<Ranking> calculateScoreWithoutSaving(String CompetitionCode) {
        List<Ranking> rankings = new ArrayList<>();
        List<Integer> members = iddd(CompetitionCode);
        List<Hunting> huntings = fetchHunting(CompetitionCode);
        List<HuntingDtoResponse> huntingDtoResponses = new ArrayList<>();

        for (Hunting h : huntings) {
            HuntingDtoResponse huntingDtoResponse = HuntingResponseMapper.HRM.toDto(h);
            huntingDtoResponse.totalScoreForRaw = huntingDtoResponse.fishScore * huntingDtoResponse.numberOfFish;
            huntingDtoResponses.add(huntingDtoResponse);
        }

        for (Integer id : members) {
            String competitionCodeForMember = "";
            int totalScorePerId = 0;
            for (HuntingDtoResponse h : huntingDtoResponses) {
                if (h.membernum == id) {
                    totalScorePerId += h.totalScoreForRaw;
                }
                competitionCodeForMember = h.competitioncode;
            }

            Ranking ranking = new Ranking();
            ranking.setScore(totalScorePerId);
            RandId randId = new RandId();
            randId.setMembernum(id);
            randId.setCompetitoncode(competitionCodeForMember);
            ranking.setId(randId);

            rankings.add(ranking);
        }

        rankings.sort(Comparator.comparingInt(Ranking::getScore).reversed());
        for (int i = 0; i < rankings.size(); i++) {
            rankings.get(i).setRank(i + 1);
        }

        return rankings;
    }

    @Override
    public List<Ranking> saveScores(List<Ranking> rankings) {
        return rankingRepository.saveAll(rankings);
    }


    @Override
    public List<Integer> iddd(String code) {
        return huntingRepository.FindMemberid(code);
    }


}
