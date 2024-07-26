package mangosiruu.nontoxicdiary.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import mangosiruu.nontoxicdiary.dto.*;
import mangosiruu.nontoxicdiary.entity.Challenge;
import mangosiruu.nontoxicdiary.entity.FoodCategory;
import mangosiruu.nontoxicdiary.entity.ToxicFood;
import mangosiruu.nontoxicdiary.entity.UserInfo;
import mangosiruu.nontoxicdiary.repository.ChallengeRepository;
import mangosiruu.nontoxicdiary.repository.FoodCategoryRepository;
import mangosiruu.nontoxicdiary.repository.ToxicFoodRepository;
import mangosiruu.nontoxicdiary.repository.UserInfoRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CalendarServiceImpl implements CalendarService {

    private final FoodCategoryRepository foodCategoryRepository;
    private final ToxicFoodRepository toxicFoodRepository;
    private final ChallengeRepository challengeRepository;
    private final UserInfoRepository userInfoRepository;
    private final ModelMapper modelMapper;

    @Override
    @Transactional
    public CalendarOutputDto saveToxicFoods(CalendarInputDto inputDto, Long userId) {
        UserInfo userInfo = userInfoRepository.findById(userId)
            .orElseThrow(
                () -> new IllegalArgumentException("존재하지 않는 사용자입니다. (id = " + userId + ")"));

        toxicFoodRepository.deleteByDate(inputDto.getDate(), userInfo);

        List<ToxicFood> toxicFoods = inputDto.getToxicFoods().stream()
            .filter(dto -> dto.getCount() > 0)
            .map(dto -> {
                FoodCategory category = foodCategoryRepository.findByFood(dto.getName())
                    .orElseThrow(
                        () -> new IllegalArgumentException(dto.getName() + " 해당 카테고리는 존재하지 않습니다."));
                return ToxicFood.builder()
                    .date(inputDto.getDate())
                    .userInfo(userInfo)
                    .category(category)
                    .count(dto.getCount())
                    .build();
            }).collect(Collectors.toList());

        toxicFoodRepository.saveAll(toxicFoods);

        List<ToxicFoodDto> toxicFoodDtos = toxicFoods.stream()
            .map(tf -> modelMapper.map(tf, ToxicFoodDto.class))
            .collect(Collectors.toList());

        return CalendarOutputDto.builder()
            .date(inputDto.getDate())
            .toxicFoods(toxicFoodDtos)
            .build();
    }

    @Override
    @Transactional(readOnly = true)
    public CalendarOutputDto getToxicFoods(LocalDate date, Long userId) {
        UserInfo userInfo = userInfoRepository.findById(userId)
            .orElseThrow(
                () -> new IllegalArgumentException("존재하지 않는 사용자입니다. (id = " + userId + ")"));

        List<ToxicFood> toxicFoods = toxicFoodRepository.findByDate(date, userInfo);

        List<ToxicFoodDto> toxicFoodDtos = toxicFoods.stream()
            .map(tf -> modelMapper.map(tf, ToxicFoodDto.class))
            .collect(Collectors.toList());

        return CalendarOutputDto.builder()
            .date(date)
            .toxicFoods(toxicFoodDtos)
            .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CalendarListOutputDto> getToxicFoodsByRange(LocalDate startDate, LocalDate endDate,
        String filterCategory, Long userId) {

        UserInfo userInfo = userInfoRepository.findById(userId)
            .orElseThrow(
                () -> new IllegalArgumentException("존재하지 않는 사용자입니다. (id = " + userId + ")"));

        LocalDate today = LocalDate.now();
        List<ToxicFood> toxicFoods;

        if (filterCategory.equals("전체")) {
            toxicFoods = toxicFoodRepository.findByDateBetween(startDate, endDate, userInfo);
        } else {
            toxicFoods = toxicFoodRepository.findByDateBetweenAndCategoryFood(startDate, endDate,
                filterCategory, userInfo);
        }

        List<LocalDate> datesInRange = startDate.datesUntil(endDate.plusDays(1)).toList();

        Map<LocalDate, List<ToxicFood>> groupedToxicFoods = toxicFoods.stream()
            .collect(Collectors.groupingBy(ToxicFood::getDate));

        List<CalendarListOutputDto> calendarListOutputDtos = datesInRange.stream()
            .map(date -> {
                List<ToxicFoodDto> toxicFoodDtos = groupedToxicFoods
                    .getOrDefault(date, new ArrayList<>()).stream()
                    .map(tf -> ToxicFoodDto.builder()
                        .name(tf.getCategory().getFood())
                        .count(tf.getCount())
                        .build())
                    .collect(Collectors.toList());

                boolean isChallengeSuccessful = determineChallengeSuccess(date, today,
                    groupedToxicFoods, userInfo);

                return CalendarListOutputDto.builder()
                    .date(date)
                    .toxicFoods(toxicFoodDtos)
                    .challengeSuccess(isChallengeSuccessful)
                    .build();
            })
            .collect(Collectors.toList());

        return calendarListOutputDtos;
    }

    private boolean determineChallengeSuccess(LocalDate date, LocalDate today,
        Map<LocalDate, List<ToxicFood>> groupedToxicFoods, UserInfo userInfo) {
        if (date.isAfter(today)) {
            return false;
        }

        List<Challenge> validChallenges = challengeRepository.findChallengesForDate(date, userInfo);
        if (validChallenges.isEmpty()) {
            return false;
        }

        Set<Long> toxicFoodCategoryIds = groupedToxicFoods.getOrDefault(date, new ArrayList<>())
            .stream()
            .map(tf -> tf.getCategory().getId())
            .collect(Collectors.toSet());

        return validChallenges.stream()
            .noneMatch(challenge -> toxicFoodCategoryIds.contains(challenge.getCategory().getId()));
    }
}
